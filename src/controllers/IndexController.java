package controllers;

import com.neurotec.biometrics.NTemplate;
import java.awt.Image;

import constants.Config;
import fingerprint.device.FingerDevice;
import fingerprint.device.IFingerDeviceEvent;
import fingerprint.device.Neurotec;
import org.apache.commons.codec.binary.Base64;

import views.IndexLayout;

public class IndexController implements JavaScriptController.JavaScriptListener {
    
    protected IndexLayout mIndexView;
    protected FingerDevice mFingerDevice;
    protected JavaScriptController mJavaScriptController;
    
    protected Image mImage;
    
    public IndexController() {
        mIndexView = null;
        mFingerDevice = new FingerDevice(Config.FINGER_SDK);
        mJavaScriptController = new JavaScriptController(this);
    }
    
    public void showLayout() {
        if (mIndexView != null)
            mIndexView.dispose();
        
        initLayout();
        initFingerDevice();
    }
    
    protected void initLayout() {
        mIndexView = IndexLayout.CreateLayout();
        mIndexView.setJavaScriptController(mJavaScriptController);
        mIndexView.setLayoutListener(new IndexLayout.LayoutListener() {
            @Override
            public void onLayoutShown() {
                startCapture();
            }

            @Override
            public void onLayoutHidden() {
                stopCapture();
            }
        });
    }
    
    protected void initFingerDevice() {
        mFingerDevice.setListener(new IFingerDeviceEvent() {
            @Override
            public void onFingerDeviceConnected() {
                mIndexView.setStatus("The fingerprint reader was connected.");
            }

            @Override
            public void onFingerDeviceStartCapture() {
                mIndexView.setStatus("The fingerprint reader is started listening.");
            }

            @Override
            public void onFingerDeviceImageCaptured(final Image image) {
                mIndexView.setResponseFingerCaptured(image);
                mIndexView.setStatus("The fingerprint was captured.");
            }

            @Override
            public void onFingerDeviceStopCapture() {
                mIndexView.setStatus("The fingerprint reader is stopped listening.");
            }

            @Override
            public void onFingerDeviceDisconnected() {
                mIndexView.setStatus("The fingerprint reader was disconnected.");
            }
        });
    }
    
    protected boolean startCapture() {
        return mFingerDevice.startCapture();
    }
    
    protected boolean stopCapture() {
        return mFingerDevice.stopCapture();
    }
    
    
    // -----------------------------
    // -- JAVASCRIPT COMMUNICATOR --
    // -----------------------------
    
    @Override
    public void onRequestFingerCaptureStatus() {
        mIndexView.setResponseFingerCaptureStatus(mFingerDevice.isCapturing());
    }

    @Override
    public void onRequestFingerCaptureStart() {
        boolean isStarted = startCapture();
        mIndexView.setResponseFingerCaptureStart(isStarted);
    }

    @Override
    public void onRequestFingerCaptureStop() {
        boolean isStopped = stopCapture();
        mIndexView.setResponseFingerCaptureStop(isStopped);
    }

    @Override
    public void onRequestFingerImage() {
        mIndexView.setResponseFingerImageBase64(mImage);
    }

    @Override
    public void onRequestTemplate(int[] fingerIndexPositions, String[] fingerBase64Images) {
        Neurotec neurotec = (Neurotec) mFingerDevice.getInstance();
        neurotec.createTemplateFromImages(fingerIndexPositions, fingerBase64Images, new Neurotec.CreateTemplateListener() {
            @Override
            public void onTemplateCreateSuccess(NTemplate template) {
                byte[] templateBytes = template.save().toByteArray();
                String base64Encoded = Base64.encodeBase64String(templateBytes);
                mIndexView.setResponseTemplateBase64(base64Encoded);
            }

            @Override
            public void onTemplateCreateFailed(String message) {
                mIndexView.setResponseTemplateFailed(message);
            }
        });
    }

    @Override
    public void onTemplateAdd(int id, String templateBase64) {
        Neurotec neurotec = (Neurotec) mFingerDevice.getInstance();
        neurotec.templateAdd(id, templateBase64, new Neurotec.TemplateAddListener() {

            @Override
            public void onTemplateAddSuccess(int id) {
                mIndexView.setResponseTemplateAdd(id);
            }

            @Override
            public void onTemplateAddFailed(String message) {
                mIndexView.setResponseTemplateAddFailed(message);
            }
        });
    }

    @Override
    public void onTemplateDelete(int id) {
        Neurotec neurotec = (Neurotec) mFingerDevice.getInstance();
        neurotec.templateDelete(id, new Neurotec.TemplateDeleteListener() {

            @Override
            public void onTemplateDeleteSuccess(int id) {
                mIndexView.setResponseTemplateDelete(id);
            }

            @Override
            public void onTemplateDeleteFailed(String message) {
                mIndexView.setResponseTemplateDeleteFailed(message);
            }
        });
    }

    @Override
    public void onTemplateIdentify(String templateBase64) {
        Neurotec neurotec = (Neurotec) mFingerDevice.getInstance();
        neurotec.templateIdentify(templateBase64, new Neurotec.TemplateIdentifyListener() {

            @Override
            public void onTemplateIdentifySuccess(Neurotec.TemplateIdentifyResult[] templateIdentifyResults) {
                mIndexView.setResponseTemplateIdentify(templateIdentifyResults);
            }

            @Override
            public void onTemplateIdentifyFailed(String message) {
                mIndexView.setResponseTemplateIdentifyFailed(message);
            }
        });
    }
}
