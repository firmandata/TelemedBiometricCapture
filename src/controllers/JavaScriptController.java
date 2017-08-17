package controllers;

public class JavaScriptController {
    
    protected JavaScriptListener mJavaScriptListener;
    
    public JavaScriptController(final JavaScriptListener javaScriptListener) {
        mJavaScriptListener = javaScriptListener;
    }
    
    public boolean isStartedCapture() {
        return mJavaScriptListener.isStartedCapture();
    }
    
    public void startCapture() {
        mJavaScriptListener.onStartCapture();
    }
    
    public void stopCapture() {
        mJavaScriptListener.onStopCapture();
    }
    
    public interface JavaScriptListener {
        void onStartCapture();
        void onStopCapture();
        boolean isStartedCapture();
    }
}
