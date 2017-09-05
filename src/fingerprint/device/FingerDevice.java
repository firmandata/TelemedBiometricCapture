package fingerprint.device;

import constants.Constant;

public class FingerDevice implements IFingerDevice {

    protected int mFingerSDK;
    protected IFingerDevice mFingerDevice;
    protected boolean mCapturing;
    
    public FingerDevice(final int fingerSDK) {
        mFingerSDK = fingerSDK;
        if (mFingerSDK == Constant.FINGER_SDK_NEUROTEC) {
            mFingerDevice = new Neurotec();
        } else {
            mFingerDevice = new OneTouch();
        }
    }
    
    public void setNeurotecService(final String host, final int port) {
        if (mFingerSDK == Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH) {
            ((OneTouch) mFingerDevice).setNeurotecService(host, port);
        }
    }
    
    @Override
    public void setListener(IFingerDeviceEvent fingerDeviceEvent) {
        mFingerDevice.setListener(fingerDeviceEvent);
    }
    
    @Override
    public boolean startCapture() {
        return mFingerDevice.startCapture();
    }

    @Override
    public boolean isCapturing() {
        return mFingerDevice.isCapturing();
    }

    @Override
    public boolean stopCapture() {
        return mFingerDevice.stopCapture();
    }

    @Override
    public void createTemplateFromImages(int[] fingerIndexPositions, String[] fingerBase64Images, Neurotec.CreateTemplateListener createTemplateListener) {
        mFingerDevice.createTemplateFromImages(fingerIndexPositions, fingerBase64Images, createTemplateListener);
    }

    @Override
    public void templateAdd(int id, String templateBase64, Neurotec.TemplateAddListener templateAddListener) {
        mFingerDevice.templateAdd(id, templateBase64, templateAddListener);
    }

    @Override
    public void templateDelete(int id, Neurotec.TemplateDeleteListener templateDeleteListener) {
        mFingerDevice.templateDelete(id, templateDeleteListener);
    }

    @Override
    public void templateIdentify(String templateBase64, Neurotec.TemplateIdentifyListener templateIdentifyListener) {
        mFingerDevice.templateIdentify(templateBase64, templateIdentifyListener);
    }
}
