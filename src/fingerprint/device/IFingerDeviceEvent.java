package fingerprint.device;

import java.awt.Image;

public interface IFingerDeviceEvent {
    void onFingerDeviceConnected();
    void onFingerDeviceStartCapture();
    void onFingerDeviceImageCaptured(String templateBase64, Image image);
    void onFingerDeviceImageCaptureFailed(String message);
    void onFingerDeviceStopCapture();
    void onFingerDeviceDisconnected();
}
