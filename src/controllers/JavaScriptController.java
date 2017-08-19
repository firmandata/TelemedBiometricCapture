package controllers;

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
    
    public void requestTemplate(String[] imagesBase64) {
        mJavaScriptListener.onRequestTemplate(imagesBase64);
    }
    
    public void requestIdentify(String templateBase64) {
        mJavaScriptListener.onRequestIdentify(templateBase64);
    }
    
    public interface JavaScriptListener {
        void onRequestFingerCaptureStatus();
        void onRequestFingerCaptureStart();
        void onRequestFingerCaptureStop();
        void onRequestFingerImage();
        void onRequestTemplate(String[] imagesBase64);
        void onRequestIdentify(String templateBase64);
    }
}
