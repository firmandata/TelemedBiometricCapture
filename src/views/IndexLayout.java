package views;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Image;

import constants.Config;
import constants.Constant;
import controllers.JavaScriptController;
import fingerprint.device.Neurotec;
import helpers.ImageHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IndexLayout extends javax.swing.JFrame {

    protected BrowserView mBrowserView;
    protected LayoutListener mLayoutListener;
    
    /**
     * Creates new form MainFrame
     */
    public IndexLayout() {
        // set maximize
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        // Initialize
        initComponents();
        
        // Layout listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (mLayoutListener != null)
                    mLayoutListener.onLayoutShown();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                if (mLayoutListener != null)
                    mLayoutListener.onLayoutHidden();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        jPanelBottom = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Telemed");

        jPanelMain.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanelMain.setLayout(new java.awt.BorderLayout());

        jLabelStatus.setText("Telemed Biometric Capture");

        javax.swing.GroupLayout jPanelBottomLayout = new javax.swing.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottomLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBottomLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    public void initWebView(final JavaScriptController javaScriptController) {
        mBrowserView = new BrowserView(Config.BROWSER_PROVIDER, javaScriptController, jPanelMain, Config.TELEMED_URL);
        mBrowserView.setPageStateListener(new BrowserView.PageStateListener() {
            @Override
            public void onPageStateScheduled(String url) {

            }

            @Override
            public void onPageStateRunning(String url) {

            }

            @Override
            public void onPageStateSucceeded(String url) {

            }

            @Override
            public void onPageStateFailed(String url) {

            }

            @Override
            public void onPagePopupOpen(String url) {
                mBrowserView.openDocument(url);
            }
        });
    }
    
    public void setLayoutListener(final LayoutListener layoutListener) {
        mLayoutListener = layoutListener;
    }
    
    public static IndexLayout CreateLayout(final JavaScriptController javaScriptController) {
        LayoutCreator layoutCreator = new LayoutCreator(javaScriptController);
        
        try {
            SwingUtilities.invokeAndWait(layoutCreator);
        } catch (InterruptedException ex) {
            Logger.getLogger(IndexLayout.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(IndexLayout.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return layoutCreator.getLayout();
    }
    
    protected static class LayoutCreator implements Runnable {
        protected IndexLayout mLayout;
        protected JavaScriptController mJavaScriptController;
        
        public LayoutCreator(final JavaScriptController javaScriptController) {
            mJavaScriptController = javaScriptController;
        }
        
        @Override
        public void run() {
            mLayout = new IndexLayout();
            mLayout.setVisible(true);
            mLayout.initWebView(mJavaScriptController);
        }
        
        public IndexLayout getLayout() {
            return mLayout;
        }
    }
    
    public BrowserView getBrowserView() {
        return mBrowserView;
    }
    
    public void setStatus(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelStatus.setText(status);
            }
        });
    }
    
    public void setResponseFingerCaptureStatus(final boolean isStarted) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_finger_capture_status(" + String.valueOf(isStarted) + ")");
            }
        });
    }
    
    public void setResponseFingerCaptureStart(final boolean isStarted) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_finger_capture_start(" + String.valueOf(isStarted) + ")");
            }
        });
    }
    
    public void setResponseFingerCaptureStop(final boolean isStopped) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_finger_capture_stop(" + String.valueOf(isStopped) + ")");
            }
        });
    }
    
    public void setResponseFingerCaptured(final Image image, final String templateBase64) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String imageString = ImageHelper.jpegToBase64(image); 
                if (imageString != null && mBrowserView != null)
                    mBrowserView.executeScript("app_response_finger_captured(\"" + imageString + "\", \"JPEG\", \"" + templateBase64 + "\")");
            }
        });
    }
    
    public void setResponseFingerCaptureFailed(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_finger_capture_failed(\"" + message + "\")");
            }
        });
    }
    
    public void setResponseFingerImageBase64(final Image image) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String imageString = ImageHelper.jpegToBase64(image); 
                if (imageString != null && mBrowserView != null)
                    mBrowserView.executeScript("app_response_finger_image(\"" + imageString + "\", \"JPEG\")");
            }
        });
    }
    
    public void setResponseTemplateBase64(final String template) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template(\"" + template + "\")");
            }
        });
    }
    
    public void setResponseTemplateFailed(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_failed(\"" + message + "\")");
            }
        });
    }
    
    public void setResponseTemplateAdd(final int id) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_add(" + String.valueOf(id) + ")");
            }
        });
    }
    
    public void setResponseTemplateAddFailed(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_add_failed(\"" + message + "\")");
            }
        });
    }
    
    public void setResponseTemplateDelete(final int id) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_delete(" + String.valueOf(id) + ")");
            }
        });
    }
    
    public void setResponseTemplateDeleteFailed(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_delete_failed(\"" + message + "\")");
            }
        });
    }
    
    public void setResponseTemplateIdentify(final Neurotec.TemplateIdentifyResult[] templateIdentifyResults) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String identifyResult = "";
                if (templateIdentifyResults != null) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (Neurotec.TemplateIdentifyResult templateIdentifyResult : templateIdentifyResults) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", templateIdentifyResult.getId());
                            jsonObject.put("score", templateIdentifyResult.getScore());
                            jsonArray.put(jsonObject);
                        }
                        identifyResult = jsonArray.toString();
                    } catch (JSONException ex) {
                        
                    }
                }
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_identify('" + identifyResult + "')");
            }
        });
    }
    
    public void setResponseTemplateIdentifyFailed(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (mBrowserView != null)
                    mBrowserView.executeScript("app_response_template_identify_failed(\"" + message + "\")");
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelMain;
    // End of variables declaration//GEN-END:variables

    public interface LayoutListener {
        void onLayoutShown();
        void onLayoutHidden();
    }
}
