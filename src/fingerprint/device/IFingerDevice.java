package fingerprint.device;

public interface IFingerDevice {
    void setListener(final IFingerDeviceEvent fingerDeviceEvent);
    boolean startCapture();
    boolean isCapturing();
    boolean stopCapture();
    void createTemplateFromImages(final int[] fingerIndexPositions, final String[] fingerBase64Images, final Neurotec.CreateTemplateListener createTemplateListener);
    void templateAdd(final int id, final String templateBase64, final Neurotec.TemplateAddListener templateAddListener);
    void templateDelete(final int id, final Neurotec.TemplateDeleteListener templateDeleteListener);
    void templateIdentify(final String templateBase64, final Neurotec.TemplateIdentifyListener templateIdentifyListener);
}
