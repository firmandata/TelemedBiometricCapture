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
import helpers.ImageHelper;
import java.awt.Image;
import javax.swing.SwingUtilities;

import views.IndexLayout;

public class IndexController implements JavaScriptController.JavaScriptListener {
    
    protected IndexLayout mIndexView;
    protected DPFPCapture mDPFPCapture;
    protected JavaScriptController mJavaScriptController;
    
    protected DPFPSample mDPFPSample;
    
    public IndexController() {
        mIndexView = null;
        mDPFPCapture = DPFPGlobal.getCaptureFactory().createCapture();
        mJavaScriptController = new JavaScriptController(this);
    }
    
    public void showLayout() {
        if (mIndexView != null)
            mIndexView.dispose();
        
        initLayout();
    }
    
    protected void initLayout() {
        mIndexView = IndexLayout.CreateLayout();
        mIndexView.setJavaScriptController(mJavaScriptController);
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
    
    protected boolean startCapture() {
        if (!mDPFPCapture.isStarted())
            mDPFPCapture.startCapture();
        
        mIndexView.setStatus("Fingerprint reader is ready start.");
        
        return true;
    }
    
    protected boolean stopCapture() {
        if (mDPFPCapture.isStarted())
            mDPFPCapture.stopCapture();
        
        mIndexView.setStatus("Fingerprint reader is stopped.");
        
        return true;
    }
    
    protected void processCapture(DPFPSample dpfpSample) {
        mIndexView.setResponseFingerCaptured(toBitmap(dpfpSample));
        mDPFPSample = dpfpSample;
    }
    
    protected Image toBitmap(DPFPSample dpfpSample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(dpfpSample);
    }
    
    
    // -----------------------------
    // -- JAVASCRIPT COMMUNICATOR --
    // -----------------------------
    
    @Override
    public void onRequestFingerCaptureStatus() {
        mIndexView.setResponseFingerCaptureStatus(mDPFPCapture.isStarted());
    }

    @Override
    public void onRequestFingerCaptureStart() {
        boolean isStarted = startCapture();
        mIndexView.setResponseFingerCaptureStart(isStarted);
    }

    @Override
    public void onRequestFingerCaptureStop() {
        boolean isStopped = stopCapture();
        mIndexView.setResponseFingerCaptureStop(isStopped);
    }

    @Override
    public void onRequestFingerImage() {
        if (mDPFPSample != null) {
            mIndexView.setResponseFingerImageBase64(toBitmap(mDPFPSample));
        } else {
            mIndexView.setResponseFingerImageBase64(null);
        }
    }

    @Override
    public void onRequestTemplate(String[] imagesBase64) {
        mIndexView.setResponseTemplateBase64(null);
    }

    @Override
    public void onRequestIdentify(String templateBase64) {
        mIndexView.setResponseIdentify(null);
    }
}
