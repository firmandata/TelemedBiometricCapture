package fingerprint.device;

import fingerprint.device.IFingerDeviceEvent;

public interface IFingerDevice {
    void setListener(final IFingerDeviceEvent fingerDeviceEvent);
    boolean startCapture();
    boolean isCapturing();
    boolean stopCapture();
}
