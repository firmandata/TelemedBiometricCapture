package fingerprint.device;

import java.awt.Image;

public interface IFingerDeviceEvent {
    void onFingerDeviceConnected();
    void onFingerDeviceStartCapture();
    void onFingerDeviceImageCaptured(Image image, String templateBase64);
    void onFingerDeviceImageCaptureFailed(String message);
    void onFingerDeviceStopCapture();
    void onFingerDeviceDisconnected();
}
