package fingerprint.device;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OneTouch implements IFingerDevice {

    protected DPFPCapture mDPFPCapture;
    protected IFingerDeviceEvent mFingerDeviceEvent;
    
    protected Socket mSocket;
    protected BufferedReader mSocketIn;
    protected PrintWriter mSocketOut;
    
    public OneTouch() {
        mDPFPCapture = DPFPGlobal.getCaptureFactory().createCapture();
        
        mDPFPCapture.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                if (mFingerDeviceEvent != null) {
                    Image image = toBitmap(e.getSample());
                    if (image != null)
                        mFingerDeviceEvent.onFingerDeviceImageCaptured(image);
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
    
    @Override
    public void setListener(IFingerDeviceEvent fingerDeviceEvent) {
        mFingerDeviceEvent = fingerDeviceEvent;
    }
    
    @Override
    public boolean startCapture() {
        boolean isStart = false;
        
        if (!mDPFPCapture.isStarted()) {
            mDPFPCapture.startCapture();
            
            if (mFingerDeviceEvent != null)
                mFingerDeviceEvent.onFingerDeviceStartCapture();
            
            isStart = true;
        }
        
        return isStart;
    }
    
    @Override
    public boolean isCapturing() {
        return mDPFPCapture.isStarted();
    }
    
    @Override
    public boolean stopCapture() {
        boolean isStop = false;
        
        if (mDPFPCapture.isStarted()) {
            mDPFPCapture.stopCapture();
            
            if (mFingerDeviceEvent != null)
                mFingerDeviceEvent.onFingerDeviceStopCapture();
            
            isStop =  true;
        }
        
        return isStop;
    }
    
    protected Image toBitmap(DPFPSample dpfpSample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(dpfpSample);
    }

    @Override
    public void createTemplateFromImages(int[] fingerIndexPositions, String[] fingerBase64Images, Neurotec.CreateTemplateListener createTemplateListener) {
        Socket socket = null;
        try {
            // -- Create socket --
            socket = new Socket("127.0.0.1", 9050);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
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
            socketOut.close();
            
            // -- Receive message --
            String receiveJsonMessage = socketIn.readLine();
            socketIn.close();
            
            String commandId = null;
            boolean status = false;
            String message = null;
            String templateBase64 = null;
            
            try {
                JSONObject JSONObjectReceive = new JSONObject(receiveJsonMessage);
                commandId = JSONObjectReceive.getString("commandId");
                status = JSONObjectReceive.getBoolean("status");
                message = JSONObjectReceive.getString("message");
                templateBase64 = JSONObjectReceive.getString("data");
            } catch (JSONException ex) {
                
            }
            
            if (createTemplateListener != null) {
                if (status)
                    createTemplateListener.onTemplateCreateSuccess(templateBase64);
                else
                    createTemplateListener.onTemplateCreateFailed(message);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(OneTouch.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (socket != null) {
                try {
                    // -- Close socket --
                    socket.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    @Override
    public void templateAdd(int id, String templateBase64, Neurotec.TemplateAddListener templateAddListener) {
        Socket socket = null;
        try {
            // -- Create socket --
            socket = new Socket("127.0.0.1", 9050);
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
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
            socketOut.close();
            
            // -- Receive message --
            String receiveJsonMessage = socketIn.readLine();
            socketIn.close();
            
            String commandId = null;
            boolean status = false;
            String message = null;
            int resultId = 0;
            
            try {
                JSONObject JSONObjectReceive = new JSONObject(receiveJsonMessage);
                commandId = JSONObjectReceive.getString("commandId");
                status = JSONObjectReceive.getBoolean("status");
                message = JSONObjectReceive.getString("message");
                resultId = JSONObjectReceive.getInt("data");
            } catch (JSONException ex) {
                
            }
            
            if (templateAddListener != null) {
                if (status)
                    templateAddListener.onTemplateAddSuccess(resultId);
                else
                    templateAddListener.onTemplateAddFailed(message);;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(OneTouch.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (socket != null) {
                try {
                    // -- Close socket --
                    socket.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    @Override
    public void templateDelete(int id, Neurotec.TemplateDeleteListener templateDeleteListener) {
        
    }

    @Override
    public void templateIdentify(String templateBase64, Neurotec.TemplateIdentifyListener templateIdentifyListener) {
        
    }
}
