package fingerprint.device;

import java.awt.Image;

public interface IFingerDeviceEvent {
    void onFingerDeviceConnected();
    void onFingerDeviceStartCapture();
    void onFingerDeviceImageCaptured(Image image);
    void onFingerDeviceStopCapture();
    void onFingerDeviceDisconnected();
}
