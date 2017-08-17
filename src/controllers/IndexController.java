package controllers;

import com.digitalpersona.onetouch.DPFPCaptureFeedback;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import java.awt.Image;
import javax.swing.SwingUtilities;

import views.IndexLayout;

public class IndexController {
    
    protected IndexLayout mIndexView;
    protected DPFPCapture mDPFPCapture;
    
    public IndexController() {
        mIndexView = null;
        mDPFPCapture = DPFPGlobal.getCaptureFactory().createCapture();
    }
    
    public void showLayout() {
        if (mIndexView != null)
            mIndexView.dispose();
        
        initLayout();
    }
    
    protected void initLayout() {
        mIndexView = IndexLayout.CreateLayout();
        mIndexView.setLayoutListener(new IndexLayout.LayoutListener() {
            @Override
            public void onLayoutShown() {
                initCapture();
                startCapture();
            }

            @Override
            public void onLayoutHidden() {
                stopCapture();
            }
        });
    }
    
    protected void initCapture() {
        mDPFPCapture.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mIndexView.setStatus("The fingerprint was captured.");
                        processCapture(e.getSample());
                    }
                });
            }
        });
        
        mDPFPCapture.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mIndexView.setStatus("The fingerprint reader was connected.");
                    }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mIndexView.setStatus("The fingerprint reader was disconnected.");
                    }
                });
            }
        });
        
        mDPFPCapture.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mIndexView.setStatus("The fingerprint reader was touched.");
                    }
                });
            }
            
            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mIndexView.setStatus("The finger was removed from the fingerprint reader.");
                    }
                });
            }
        });
        
        mDPFPCapture.addImageQualityListener(new DPFPImageQualityAdapter() {
            @Override
            public void onImageQuality(final DPFPImageQualityEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (e.getFeedback().equals(DPFPCaptureFeedback.CAPTURE_FEEDBACK_GOOD))
                            mIndexView.setStatus("The quality of the fingerprint is good.");
                        else
                            mIndexView.setStatus("The quality of the fingerprint is poor.");
                    }
                });
            }
        });
    }
    
    protected void startCapture() {
        mDPFPCapture.startCapture();
        
        mIndexView.setStatus("Fingerprint reader is ready start.");
    }
    
    protected void stopCapture() {
        mDPFPCapture.stopCapture();
        
        mIndexView.setStatus("Fingerprint reader is stopped.");
    }
    
    protected void processCapture(DPFPSample dpfpSample) {
        mIndexView.setCaptured(toBitmap(dpfpSample));
    }
    
    protected Image toBitmap(DPFPSample dpfpSample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(dpfpSample);
    }
}
