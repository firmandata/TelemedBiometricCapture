package controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JavaScriptController {
    
    protected JavaScriptListener mJavaScriptListener;
    
    public JavaScriptController(final JavaScriptListener javaScriptListener) {
        mJavaScriptListener = javaScriptListener;
    }
    
    public void requestFingerCaptureStatus() {
        mJavaScriptListener.onRequestFingerCaptureStatus();
    }
    
    public void requestFingerCaptureStart() {
        mJavaScriptListener.onRequestFingerCaptureStart();
    }
    
    public void requestFingerCaptureStop() {
        mJavaScriptListener.onRequestFingerCaptureStop();
    }
    
    public void requestFingerImage() {
        mJavaScriptListener.onRequestFingerImage();
    }
    
    public void requestTemplate(String jsonData) {
        try {
            JSONObject jsonRoot = new JSONObject(jsonData);
            
            int[] fingerIndexPositions = new int[] { };
            String[] fingerBase64Images = new String[] { };
            
            if (jsonRoot.has("fingers")) {
                JSONArray jsonFingers = jsonRoot.getJSONArray("fingers");
                
                int fingerLength = jsonFingers.length();
            
                fingerIndexPositions = new int[fingerLength];
                fingerBase64Images = new String[fingerLength];

                for (int jsonFingerIdx = 0; jsonFingerIdx < fingerLength; jsonFingerIdx++) {
                    JSONObject jsonFinger = jsonFingers.getJSONObject(jsonFingerIdx);
                    if (jsonFinger.has("index"))
                        fingerIndexPositions[jsonFingerIdx] = jsonFinger.getInt("index");
                    if (jsonFinger.has("imageBase64"))
                        fingerBase64Images[jsonFingerIdx] = jsonFinger.getString("imageBase64");
                }
            }
            
            mJavaScriptListener.onRequestTemplate(fingerIndexPositions, fingerBase64Images);
        } catch (JSONException ex) {
            
        }
    }
    
    public void templateAdd(int id, String templateBase64) {
        mJavaScriptListener.onTemplateAdd(id, templateBase64);
    }
    
    public void templateDelete(int id) {
        mJavaScriptListener.onTemplateDelete(id);
    }
    
    public void templateIdentify(String templateBase64) {
        mJavaScriptListener.onTemplateIdentify(templateBase64);
    }
    
    public interface JavaScriptListener {
        void onRequestFingerCaptureStatus();
        void onRequestFingerCaptureStart();
        void onRequestFingerCaptureStop();
        void onRequestFingerImage();
        void onRequestTemplate(int[] fingerIndexPositions, String[] fingerBase64Images);
        void onTemplateAdd(int id, String templateBase64);
        void onTemplateDelete(int id);
        void onTemplateIdentify(String templateBase64);
    }
}
