package controllers;

import java.awt.Image;

import constants.Config;
import constants.Constant;
import fingerprint.device.FingerDevice;
import fingerprint.device.IFingerDeviceEvent;
import fingerprint.device.Neurotec;

import views.IndexLayout;

public class IndexController implements JavaScriptController.JavaScriptListener {
    
    protected IndexLayout mIndexView;
    protected FingerDevice mFingerDevice;
    
    protected Image mImage;
    
    public IndexController() {
        mIndexView = null;
        mFingerDevice = new FingerDevice(Config.FINGER_SDK);
        if (Config.FINGER_SDK == Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH)
            mFingerDevice.setNeurotecService(Config.NEUROTECT_SERVICE_HOST, Config.NEUROTECT_SERVICE_PORT);
    }
    
    public void showLayout() {
        if (mIndexView != null)
            mIndexView.dispose();
        
        initLayout();
        initFingerDevice();
    }
    
    protected void initLayout() {
        mIndexView = IndexLayout.CreateLayout(new JavaScriptController(this));
        mIndexView.setLayoutListener(new IndexLayout.LayoutListener() {
            @Override
            public void onLayoutShown() {
                //startCapture();
            }

            @Override
            public void onLayoutHidden() {
                //stopCapture();
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
            public void onFingerDeviceImageCaptured(final Image image, String templateBase64) {
                mIndexView.setResponseFingerCaptured(image, templateBase64);
                mIndexView.setStatus("The fingerprint was captured.");
            }

            @Override
            public void onFingerDeviceImageCaptureFailed(String message) {
                mIndexView.setResponseFingerCaptureFailed(message);
                mIndexView.setStatus(message);
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
        Thread thread = new Thread(){
            @Override
            public void run() {
                mIndexView.setResponseFingerCaptureStatus(mFingerDevice.isCapturing());
            }
        };
        thread.start();
    }

    @Override
    public void onRequestFingerCaptureStart() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                boolean isStarted = startCapture();
                mIndexView.setResponseFingerCaptureStart(isStarted);
            }
        };
        thread.start();
    }

    @Override
    public void onRequestFingerCaptureStop() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                boolean isStopped = stopCapture();
                mIndexView.setResponseFingerCaptureStop(isStopped);
            }
        };
        thread.start();
    }

    @Override
    public void onRequestFingerImage() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                mIndexView.setResponseFingerImageBase64(mImage);
            }
        };
        thread.start();
    }

    @Override
    public void onRequestTemplate(int[] fingerIndexPositions, String[] fingerBase64Images) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                mFingerDevice.createTemplateFromImages(fingerIndexPositions, fingerBase64Images, new Neurotec.CreateTemplateListener() {
                    @Override
                    public void onTemplateCreateSuccess(String templateBase64) {
                        mIndexView.setResponseTemplateBase64(templateBase64);
                    }

                    @Override
                    public void onTemplateCreateFailed(String message) {
                        mIndexView.setResponseTemplateFailed(message);
                    }
                });
            }
        };
        thread.start();
    }

    @Override
    public void onTemplateAdd(int id, String templateBase64) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                mFingerDevice.templateAdd(id, templateBase64, new Neurotec.TemplateAddListener() {
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
        };
        thread.start();
    }

    @Override
    public void onTemplateDelete(int id) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                mFingerDevice.templateDelete(id, new Neurotec.TemplateDeleteListener() {
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
        };
        thread.start();
    }

    @Override
    public void onTemplateIdentify(String templateBase64) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                mFingerDevice.templateIdentify(templateBase64, new Neurotec.TemplateIdentifyListener() {
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
        };
        thread.start();
    }
}
