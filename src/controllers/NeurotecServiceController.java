package controllers;

import com.neurotec.biometrics.NTemplate;
import fingerprint.device.Neurotec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NeurotecServiceController {
    
    public NeurotecServiceController() {
        
    }
    
    public void start(final int port) throws IOException {
        Neurotec neurotec = new Neurotec();
        ServerSocket serverSocket = new ServerSocket(port);

        try {
            System.out.println("Starting on port : " + String.valueOf(port));
            while (true) {
                new Handler(serverSocket.accept(), neurotec).start();
            }
        } finally {
            serverSocket.close();
        }
    }
    
    private static class Handler extends Thread {
        protected Socket mSocket;
        protected Neurotec mNeurotec;
        protected BufferedReader mSocketIn;
        protected PrintWriter mSocketOut;

        public Handler(final Socket socket, final Neurotec neurotec) {
            mSocket = socket;
            mNeurotec = neurotec;
        }

        @Override
        public void run() {
            try {
                mSocketIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mSocketOut = new PrintWriter(mSocket.getOutputStream(), true);

                String json = mSocketIn.readLine();
                
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.has("type")) {
                        String type = jsonObject.getString("type");
                        
                        String commandId = null;
                        if (jsonObject.has("commandId"))
                            commandId = jsonObject.getString("commandId");
                        
                        JSONObject command = new JSONObject();
                        if (jsonObject.has("command"))
                            command = jsonObject.getJSONObject("command");
                        
                        if (type.equals("createTemplateFromImages")) {
                            // -- Create template from images --
                            int[] fingerIndexPositions = new int[] {};
                            String[] fingerBase64Images = new String[] {};
                            if (command.has("fingers")) {
                                JSONArray jsonArray = command.getJSONArray("fingers");
                                
                                int fingerLength = jsonArray.length();
                                fingerIndexPositions = new int[fingerLength];
                                fingerBase64Images = new String[fingerLength];
                                
                                for (int jsonArrayIdx = 0; jsonArrayIdx < fingerLength; jsonArrayIdx++) {
                                    JSONObject fingerObject = jsonArray.getJSONObject(jsonArrayIdx);
                                    if (fingerObject.has("index") && fingerObject.has("imageBase64")) {
                                        fingerIndexPositions[jsonArrayIdx] = fingerObject.getInt("index");
                                        fingerBase64Images[jsonArrayIdx] = fingerObject.getString("imageBase64");
                                    }
                                }
                            }
                            createTemplateFromImages(commandId, fingerIndexPositions, fingerBase64Images);
                            
                        } else if (type.equals("templateAdd")) {
                            // -- Add template --
                            int id = 0;
                            if (command.has("id"))
                                id = command.getInt("id");
                            
                            String templateBase64 = null;
                            if (command.has("templateBase64"))
                                templateBase64 = command.getString("templateBase64");
                            
                            templateAdd(commandId, id, templateBase64);
                            
                        } else if (type.equals("templateDelete")) {
                            // -- Delete template --
                            int id = 0;
                            if (command.has("id"))
                                id = command.getInt("id");
                            
                            templateDelete(commandId, id);
                            
                        } else if (type.equals("templateIdentify")) {
                            // -- Identify template --
                            String templateBase64 = null;
                            if (command.has("templateBase64"))
                                templateBase64 = command.getString("templateBase64");
                            
                            templateIdentify(commandId, templateBase64);
                        }
                    }
                } catch (JSONException ex) {
                    
                }
                
                String closeMessage = mSocketIn.readLine();
                
            } catch (IOException ex) {
                Logger.getLogger(NeurotecServiceController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    mSocket.close();
                } catch (IOException ex) {
                }
            }
        }
        
        protected void createTemplateFromImages(final String commandId, final int[] fingerIndexPositions, final String[] fingerBase64Images) {
            mNeurotec.createTemplateFromImages(fingerIndexPositions, fingerBase64Images, new Neurotec.CreateTemplateListener() {
                @Override
                public void onTemplateCreateSuccess(String templateBase64) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", true);
                        result.put("message", (String) null);
                        result.put("data", templateBase64);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }

                @Override
                public void onTemplateCreateFailed(String message) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", false);
                        result.put("message", message);
                        result.put("data", (String) null);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }
            });
        }
        
        protected void templateAdd(final String commandId, final int id, final String templateBase64) {
            mNeurotec.templateAdd(id, templateBase64, new Neurotec.TemplateAddListener() {
                @Override
                public void onTemplateAddSuccess(int id) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", true);
                        result.put("message", (String) null);
                        result.put("data", id);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }

                @Override
                public void onTemplateAddFailed(String message) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", false);
                        result.put("message", message);
                        result.put("data", (String) null);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }
            });
        }
        
        protected void templateDelete(final String commandId, final int id) {
            mNeurotec.templateDelete(id, new Neurotec.TemplateDeleteListener() {

                @Override
                public void onTemplateDeleteSuccess(int id) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", true);
                        result.put("message", (String) null);
                        result.put("data", id);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }

                @Override
                public void onTemplateDeleteFailed(String message) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", false);
                        result.put("message", message);
                        result.put("data", (String) null);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }
            });
        }
        
        protected void templateIdentify(final String commandId, final String templateBase64) {
            mNeurotec.templateIdentify(templateBase64, new Neurotec.TemplateIdentifyListener() {

                @Override
                public void onTemplateIdentifySuccess(Neurotec.TemplateIdentifyResult[] templateIdentifyResults) {
                    String response = null;
                    
                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", true);
                        result.put("message", (String) null);
                        
                        JSONArray dataResults = new JSONArray();
                        for (Neurotec.TemplateIdentifyResult templateIdentifyResult : templateIdentifyResults) {
                            JSONObject dataResult = new JSONObject();
                            dataResult.put("id", templateIdentifyResult.getId());
                            dataResult.put("score", templateIdentifyResult.getScore());
                            dataResults.put(dataResult);
                        }
                        result.put("data", dataResults);
                        
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }

                @Override
                public void onTemplateIdentifyFailed(String message) {
                    String response = null;

                    try {
                        JSONObject result = new JSONObject();
                        result.put("commandId", commandId);
                        result.put("status", false);
                        result.put("message", message);
                        result.put("data", (String) null);
                        response = result.toString();
                    } catch (JSONException ex) {

                    }

                    mSocketOut.println(response);
                }
            });
        }
    }
}
