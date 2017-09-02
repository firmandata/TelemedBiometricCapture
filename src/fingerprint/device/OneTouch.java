package fingerprint.device;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import java.awt.Image;

public class OneTouch implements IFingerDevice {

    protected DPFPCapture mDPFPCapture;
    protected IFingerDeviceEvent mFingerDeviceEvent;
    
    public OneTouch() {
        mDPFPCapture = DPFPGlobal.getCaptureFactory().createCapture();
        
        mDPFPCapture.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                if (mFingerDeviceEvent != null) {
                    Image image = toBitmap(e.getSample());
                    if (image != null)
                        mFingerDeviceEvent.onFingerDeviceImageCaptured(image);
                }
            }
        });
        
        mDPFPCapture.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                if (mFingerDeviceEvent != null)
                    mFingerDeviceEvent.onFingerDeviceConnected();
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                if (mFingerDeviceEvent != null)
                    mFingerDeviceEvent.onFingerDeviceDisconnected();
            }
        });
    }
    
    @Override
    public void setListener(IFingerDeviceEvent fingerDeviceEvent) {
        mFingerDeviceEvent = fingerDeviceEvent;
    }
    
    @Override
    public boolean startCapture() {
        boolean isStart = false;
        
        if (!mDPFPCapture.isStarted()) {
            mDPFPCapture.startCapture();
            
            if (mFingerDeviceEvent != null)
                mFingerDeviceEvent.onFingerDeviceStartCapture();
            
            isStart = true;
        }
        
        return isStart;
    }
    
    @Override
    public boolean isCapturing() {
        return mDPFPCapture.isStarted();
    }
    
    @Override
    public boolean stopCapture() {
        boolean isStop = false;
        
        if (mDPFPCapture.isStarted()) {
            mDPFPCapture.stopCapture();
            
            if (mFingerDeviceEvent != null)
                mFingerDeviceEvent.onFingerDeviceStopCapture();
            
            isStop =  true;
        }
        
        return isStop;
    }
    
    protected Image toBitmap(DPFPSample dpfpSample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(dpfpSample);
    }

}
