package fingerprint.device;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import helpers.ImageHelper;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OneTouch implements IFingerDevice {

    protected DPFPCapture mDPFPCapture;
    protected IFingerDeviceEvent mFingerDeviceEvent;
    
    protected String mNeurotecServiceHost;
    protected int mNeurotecServicePort;
    
    public OneTouch() {
        mDPFPCapture = DPFPGlobal.getCaptureFactory().createCapture();
        
        mDPFPCapture.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                if (mFingerDeviceEvent != null) {
                    final Image image = toBitmap(e.getSample());
                    if (image != null) {
                        createTemplateFromImages(new int[] { -1 }, new String[]  { ImageHelper.jpegToBase64(image) }, new Neurotec.CreateTemplateListener() {
                            @Override
                            public void onTemplateCreateSuccess(String templateBase64, String[] imagesBinaryBase64, int[] qualities) {
                                String imageBinaryBase64 = null;
                                if (imagesBinaryBase64 != null) {
                                    if (imagesBinaryBase64.length > 0)
                                        imageBinaryBase64 = imagesBinaryBase64[0];
                                }
                                int quality = 0;
                                if (qualities != null) {
                                    if (qualities.length > 0)
                                        quality = qualities[0];
                                }
                                mFingerDeviceEvent.onFingerDeviceImageCaptured(templateBase64, image, ImageHelper.base64ToImage(imageBinaryBase64), quality);
                            }

                            @Override
                            public void onTemplateCreateFailed(String message) {
                                mFingerDeviceEvent.onFingerDeviceImageCaptureFailed(message);
                            }
                        });
                    }
                    else
                        mFingerDeviceEvent.onFingerDeviceImageCaptureFailed("Failed to create bitmap from sample.");
                }
            }
        });
        
        mDPFPCapture.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                if (mFingerDeviceEvent != null)
                    mFingerDeviceEvent.onFingerDeviceConnected();
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                if (mFingerDeviceEvent != null)
                    mFingerDeviceEvent.onFingerDeviceDisconnected();
            }
        });
    }
    
    public void setNeurotecService(final String host, final int port) {
        mNeurotecServiceHost = host;
        mNeurotecServicePort = port;
    }
    
    @Override
    public void setListener(IFingerDeviceEvent fingerDeviceEvent) {
        mFingerDeviceEvent = fingerDeviceEvent;
    }
    
    @Override
    public boolean startCapture() {
        boolean isStart = true;
        
        if (!mDPFPCapture.isStarted()) {
            try {
                mDPFPCapture.startCapture();
            
                if (mFingerDeviceEvent != null)
                    mFingerDeviceEvent.onFingerDeviceStartCapture();

                isStart = true;
            } catch (Exception ex) {
                isStart = false;
            }
        }
        
        return isStart;
    }
    
    @Override
    public boolean isCapturing() {
        return mDPFPCapture.isStarted();
    }
    
    @Override
    public boolean stopCapture() {
        boolean isStop = true;
        
        if (mDPFPCapture.isStarted()) {
            try {
                mDPFPCapture.stopCapture();
                
                if (mFingerDeviceEvent != null)
                    mFingerDeviceEvent.onFingerDeviceStopCapture();
                
                isStop =  true;
            } catch (Exception ex) {
                isStop =  false;
            }
        }
        
        return isStop;
    }
    
    protected Image toBitmap(DPFPSample dpfpSample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(dpfpSample);
    }

    @Override
    public void createTemplateFromImages(int[] fingerIndexPositions, String[] fingerBase64Images, Neurotec.CreateTemplateListener createTemplateListener) {
        Socket socket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        
        try {
            // -- Create socket --
            socket = new Socket(mNeurotecServiceHost, mNeurotecServicePort);
            socketOut = new PrintWriter(socket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // -- Send message --
            String sendJsonMessage = null;
            
            try {
                JSONObject JSONObjectSend = new JSONObject();
                JSONObjectSend.put("type", "createTemplateFromImages");
                JSONObjectSend.put("commandId", String.valueOf(1));
                
                JSONObject JSONObjectCommand = new JSONObject();
                JSONArray JSONArrayFingers = new JSONArray();
                for (int fingerBase64ImageIdx = 0; fingerBase64ImageIdx < fingerBase64Images.length; fingerBase64ImageIdx++) {
                    JSONObject JSONArrayFinger = new JSONObject();
                    JSONArrayFinger.put("index", fingerIndexPositions[fingerBase64ImageIdx]);
                    JSONArrayFinger.put("imageBase64", fingerBase64Images[fingerBase64ImageIdx]);
                    JSONArrayFingers.put(JSONArrayFinger);
                }
                JSONObjectCommand.put("fingers", JSONArrayFingers);
                
                JSONObjectSend.put("command", JSONObjectCommand);
                
                sendJsonMessage = JSONObjectSend.toString();
            } catch (JSONException ex) {
            }
            
            socketOut.println(sendJsonMessage);
            
            // -- Receive message --
            String receiveJsonMessage = socketIn.readLine();
            
            String commandId = null;
            boolean status = false;
            String message = null;
            String templateBase64 = null;
            String[] imagesBinaryBase64 = null;
            int[] qualities = null;
            
            try {
                JSONObject JSONObjectReceive = new JSONObject(receiveJsonMessage);
                if (JSONObjectReceive.has("commandId"))
                    commandId = JSONObjectReceive.getString("commandId");
                if (JSONObjectReceive.has("status"))
                    status = JSONObjectReceive.getBoolean("status");
                if (JSONObjectReceive.has("message"))
                    message = JSONObjectReceive.getString("message");
                if (JSONObjectReceive.has("data")) {
                    JSONObject JSONObjectReceiveData = JSONObjectReceive.getJSONObject("data");
                    if (JSONObjectReceiveData.has("templateBase64"))
                        templateBase64 = JSONObjectReceiveData.getString("templateBase64");
                    if (JSONObjectReceiveData.has("imagesBinaryBase64")) {
                        JSONArray JSONArrayImageBinaryBase64 = JSONObjectReceiveData.getJSONArray("imagesBinaryBase64");
                        int JSONArrayImagesBinaryBase64Length = JSONArrayImageBinaryBase64.length();
                        imagesBinaryBase64 = new String[JSONArrayImagesBinaryBase64Length];
                        for (int JSONArrayImageBinaryBase64Idx = 0; JSONArrayImageBinaryBase64Idx < JSONArrayImagesBinaryBase64Length; JSONArrayImageBinaryBase64Idx++)
                            imagesBinaryBase64[JSONArrayImageBinaryBase64Idx] = JSONArrayImageBinaryBase64.getString(JSONArrayImageBinaryBase64Idx);
                    }
                    if (JSONObjectReceiveData.has("qualities")) {
                        JSONArray JSONArrayQuality = JSONObjectReceiveData.getJSONArray("qualities");
                        int JSONArrayQualityLength = JSONArrayQuality.length();
                        qualities = new int[JSONArrayQualityLength];
                        for (int JSONArrayQualityIdx = 0; JSONArrayQualityIdx < JSONArrayQualityLength; JSONArrayQualityIdx++)
                            qualities[JSONArrayQualityIdx] = JSONArrayQuality.getInt(JSONArrayQualityIdx);
                    }
                }
            } catch (JSONException ex) {
                message = ex.getMessage();
            }
            
            if (createTemplateListener != null) {
                if (status)
                    createTemplateListener.onTemplateCreateSuccess(templateBase64, imagesBinaryBase64, qualities);
                else
                    createTemplateListener.onTemplateCreateFailed(message);
            }
            
            // -- Send close message --
            socketOut.println("SOCKET_CLOSE");
            
        } catch (IOException ex) {
            if (createTemplateListener != null)
                createTemplateListener.onTemplateCreateFailed(ex.getMessage());
        } finally {
            // -- Close socket --
            try {
                if (socketOut != null)
                    socketOut.close();
                if (socketIn != null)
                    socketIn.close();
                if (socket != null)
                    socket.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void templateAdd(int id, String templateBase64, Neurotec.TemplateAddListener templateAddListener) {
        Socket socket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        
        try {
            // -- Create socket --
            socket = new Socket(mNeurotecServiceHost, mNeurotecServicePort);
            socketOut = new PrintWriter(socket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // -- Send message --
            String sendJsonMessage = null;
            
            try {
                JSONObject JSONObjectSend = new JSONObject();
                JSONObjectSend.put("type", "templateAdd");
                JSONObjectSend.put("commandId", String.valueOf(1));
                
                JSONObject JSONObjectCommand = new JSONObject();
                JSONObjectCommand.put("id", id);
                JSONObjectCommand.put("templateBase64", templateBase64);
                
                JSONObjectSend.put("command", JSONObjectCommand);
                
                sendJsonMessage = JSONObjectSend.toString();
            } catch (JSONException ex) {
            }
            
            socketOut.println(sendJsonMessage);
            
            // -- Receive message --
            String receiveJsonMessage = socketIn.readLine();
            
            String commandId = null;
            boolean status = false;
            String message = null;
            int resultId = 0;
            
            try {
                JSONObject JSONObjectReceive = new JSONObject(receiveJsonMessage);
                if (JSONObjectReceive.has("commandId"))
                    commandId = JSONObjectReceive.getString("commandId");
                if (JSONObjectReceive.has("status"))
                    status = JSONObjectReceive.getBoolean("status");
                if (JSONObjectReceive.has("message"))
                    message = JSONObjectReceive.getString("message");
                if (JSONObjectReceive.has("data"))
                    resultId = JSONObjectReceive.getInt("data");
            } catch (JSONException ex) {
                message = ex.getMessage();
            }
            
            if (templateAddListener != null) {
                if (status)
                    templateAddListener.onTemplateAddSuccess(resultId);
                else
                    templateAddListener.onTemplateAddFailed(message);
            }
            
            // -- Send close message --
            socketOut.println("SOCKET_CLOSE");
            
        } catch (IOException ex) {
            if (templateAddListener != null)
                templateAddListener.onTemplateAddFailed(ex.getMessage());
        } finally {
            // -- Close socket --
            try {
                if (socketOut != null)
                    socketOut.close();
                if (socketIn != null)
                    socketIn.close();
                if (socket != null)
                    socket.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void templateDelete(int id, Neurotec.TemplateDeleteListener templateDeleteListener) {
        Socket socket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        
        try {
            // -- Create socket --
            socket = new Socket(mNeurotecServiceHost, mNeurotecServicePort);
            socketOut = new PrintWriter(socket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // -- Send message --
            String sendJsonMessage = null;
            
            try {
                JSONObject JSONObjectSend = new JSONObject();
                JSONObjectSend.put("type", "templateDelete");
                JSONObjectSend.put("commandId", String.valueOf(1));
                
                JSONObject JSONObjectCommand = new JSONObject();
                JSONObjectCommand.put("id", id);
                
                JSONObjectSend.put("command", JSONObjectCommand);
                
                sendJsonMessage = JSONObjectSend.toString();
            } catch (JSONException ex) {
            }
            
            socketOut.println(sendJsonMessage);
            
            // -- Receive message --
            String receiveJsonMessage = socketIn.readLine();
            
            String commandId = null;
            boolean status = false;
            String message = null;
            int resultId = 0;
            
            try {
                JSONObject JSONObjectReceive = new JSONObject(receiveJsonMessage);
                if (JSONObjectReceive.has("commandId"))
                    commandId = JSONObjectReceive.getString("commandId");
                if (JSONObjectReceive.has("status"))
                    status = JSONObjectReceive.getBoolean("status");
                if (JSONObjectReceive.has("message"))
                    message = JSONObjectReceive.getString("message");
                if (JSONObjectReceive.has("data"))
                    resultId = JSONObjectReceive.getInt("data");
            } catch (JSONException ex) {
                message = ex.getMessage();
            }
            
            if (templateDeleteListener != null) {
                if (status)
                    templateDeleteListener.onTemplateDeleteSuccess(resultId);
                else
                    templateDeleteListener.onTemplateDeleteFailed(message);
            }
            
            // -- Send close message --
            socketOut.println("SOCKET_CLOSE");
            
        } catch (IOException ex) {
            if (templateDeleteListener != null)
                templateDeleteListener.onTemplateDeleteFailed(ex.getMessage());
        } finally {
            // -- Close socket --
            try {
                if (socketOut != null)
                    socketOut.close();
                if (socketIn != null)
                    socketIn.close();
                if (socket != null)
                    socket.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void templateIdentify(String templateBase64, Neurotec.TemplateIdentifyListener templateIdentifyListener) {
        Socket socket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        
        try {
            // -- Create socket --
            socket = new Socket(mNeurotecServiceHost, mNeurotecServicePort);
            socketOut = new PrintWriter(socket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // -- Send message --
            String sendJsonMessage = null;
            
            try {
                JSONObject JSONObjectSend = new JSONObject();
                JSONObjectSend.put("type", "templateIdentify");
                JSONObjectSend.put("commandId", String.valueOf(1));
                
                JSONObject JSONObjectCommand = new JSONObject();
                JSONObjectCommand.put("templateBase64", templateBase64);
                
                JSONObjectSend.put("command", JSONObjectCommand);
                
                sendJsonMessage = JSONObjectSend.toString();
            } catch (JSONException ex) {
            }
            
            socketOut.println(sendJsonMessage);
            
            // -- Receive message --
            String receiveJsonMessage = socketIn.readLine();
            
            String commandId = null;
            boolean status = false;
            String message = null;
            Neurotec.TemplateIdentifyResult[] templateIdentifyResults = new Neurotec.TemplateIdentifyResult[] { };
            
            try {
                JSONObject JSONObjectReceive = new JSONObject(receiveJsonMessage);
                if (JSONObjectReceive.has("commandId"))
                    commandId = JSONObjectReceive.getString("commandId");
                if (JSONObjectReceive.has("status"))
                    status = JSONObjectReceive.getBoolean("status");
                if (JSONObjectReceive.has("message"))
                    message = JSONObjectReceive.getString("message");
                if (JSONObjectReceive.has("data")) {
                    List<Neurotec.TemplateIdentifyResult> templateIdentifyResultList = new ArrayList<Neurotec.TemplateIdentifyResult>();
                    JSONArray JSONArrayResults = JSONObjectReceive.getJSONArray("data");
                    for (int JSONArrayResultIdx = 0; JSONArrayResultIdx < JSONArrayResults.length(); JSONArrayResultIdx++) {
                        JSONObject JSONObjectResult = JSONArrayResults.getJSONObject(JSONArrayResultIdx);
                        Neurotec.TemplateIdentifyResult templateIdentifyResult = new Neurotec.TemplateIdentifyResult();
                        if (JSONObjectResult.has("id"))
                            templateIdentifyResult.setId(JSONObjectResult.getInt("id"));
                        if (JSONObjectResult.has("score"))
                            templateIdentifyResult.setScore(JSONObjectResult.getInt("score"));
                        templateIdentifyResultList.add(templateIdentifyResult);
                    }
                    templateIdentifyResults = new Neurotec.TemplateIdentifyResult[templateIdentifyResultList.size()];
                    templateIdentifyResultList.toArray(templateIdentifyResults);
                }
            } catch (JSONException ex) {
                message = ex.getMessage();
            }
            
            if (templateIdentifyListener != null) {
                if (status)
                    templateIdentifyListener.onTemplateIdentifySuccess(templateIdentifyResults);
                else
                    templateIdentifyListener.onTemplateIdentifyFailed(message);
            }
            
            // -- Send close message --
            socketOut.println("SOCKET_CLOSE");
            
        } catch (IOException ex) {
            if (templateIdentifyListener != null)
                templateIdentifyListener.onTemplateIdentifyFailed(ex.getMessage());
        } finally {
            // -- Close socket --
            try {
                if (socketOut != null)
                    socketOut.close();
                if (socketIn != null)
                    socketIn.close();
                if (socket != null)
                    socket.close();
            } catch (IOException ex) {
            }
        }
    }
}
