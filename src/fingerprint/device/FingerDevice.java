package fingerprint.device;

import constants.Constant;

public class FingerDevice implements IFingerDevice {

    protected IFingerDevice mFingerDevice;
    protected boolean mCapturing;
    
    public FingerDevice(final int fingerSDK) {
        if (fingerSDK == Constant.FINGER_SDK_NEUROTEC) {
            mFingerDevice = new Neurotec();
        } else {
            mFingerDevice = new OneTouch();
        }
    }
    
    public IFingerDevice getInstance() {
        return mFingerDevice;
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
}
