package views;

import com.sun.javafx.application.PlatformImpl;
import controllers.JavaScriptController;
import helpers.SystemHelper;
import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javax.swing.JPanel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;

public class BrowserWebView implements IBrowserView {
    
    protected Stage mStage;
    protected JFXPanel mJFXPanel;
    
    protected WebView mWebView;
    protected WebEngine mWebEngine;
    
    protected WebView mWebViewPopup;
    protected WebEngine mWebEnginePopup;
    
    protected JavaScriptController mJavaScriptController;
    
    protected BrowserView.PageStateListener mPageStateListener;
    
    public BrowserWebView(final JavaScriptController javaScriptController, final JPanel jPanel, final String url) {
        mJavaScriptController = javaScriptController;
        
        mJFXPanel = new JFXPanel();  
        createScene(mJFXPanel, url);  

        jPanel.add(mJFXPanel, BorderLayout.CENTER);
    }
    
    protected void createScene(final JFXPanel jfxPanel, final String url) {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                // ---------------
                // Initialize view
                // ---------------
                mStage = new Stage();
                mStage.setResizable(true);
                
                StackPane stackPane = new StackPane();
                stackPane.setAlignment(Pos.CENTER);
                
                Scene scene = new Scene(stackPane);
                mStage.setScene(scene);

                // ---------------
                // WebEngine web page view
                // -----------------------
                mWebView = new WebView();
                mWebView.setContextMenuEnabled(false);
                
                mWebEngine = mWebView.getEngine();
                mWebEngine.setJavaScriptEnabled(true);
                mWebEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                        if (mPageStateListener != null) {
                            String url = mWebEngine.getLocation();
                            if (newState == Worker.State.READY) {
                            } else if (newState == Worker.State.SCHEDULED)
                                mPageStateListener.onPageStateScheduled(url);
                            else if (newState == Worker.State.RUNNING)
                                mPageStateListener.onPageStateRunning(url);
                            else if (newState == Worker.State.SUCCEEDED) {
                                executeScript("window", new BrowserView.ExecuteScriptListener() {
                                    @Override
                                    public void onExecutedScript(Object result) {
                                        // Attach app variable to page
                                        JSObject jsObject = (JSObject) result;
                                        jsObject.setMember("app", mJavaScriptController);

                                        // Send ready flag to app_ready() function
                                        executeScript("app_ready()");
                                    }
                                });

                                mPageStateListener.onPageStateSucceeded(url);
                            } else if (newState == Worker.State.CANCELLED) {
                                
                            } else if (newState == Worker.State.FAILED)
                                mPageStateListener.onPageStateFailed(url);
                        }
                    }
                });
                mWebEngine.load(url);

                // ------------
                // Add to scene
                // ------------
                ObservableList<Node> children = stackPane.getChildren();
                children.add(mWebView);

                jfxPanel.setScene(scene);
                
                // ------------------
                // Popup page handler
                // ------------------
                mWebEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
                    @Override
                    public WebEngine call(PopupFeatures popupFeatures) {
                        return mWebEnginePopup;
                    }
                });
                
                mWebViewPopup = new WebView();
                mWebViewPopup.setContextMenuEnabled(false);
                
                mWebEnginePopup = mWebViewPopup.getEngine();
                mWebEnginePopup.setJavaScriptEnabled(false);
                mWebEnginePopup.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                        if (oldState != Worker.State.CANCELLED && newState == Worker.State.SCHEDULED) {
                            mWebEnginePopup.getLoadWorker().cancel();
                            if (mPageStateListener != null) {
                                String url = mWebEnginePopup.getLocation();
                                if (url != null)
                                    mPageStateListener.onPagePopupOpen(url);
                            }
                        }
                    }
                });
            }
        });
    }
    
    @Override
    public void setPageStateListener(final BrowserView.PageStateListener pageStateListener) {
        mPageStateListener = pageStateListener;
    }
    
    @Override
    public void redirect(final String url) {
        if (mWebEngine != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mWebEngine.load(url);
                }
            });
        }
    }
    
    @Override
    public void reload() {
        if (mWebEngine != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mWebEngine.reload();
                }
            });
        }
    }
    
    @Override
    public void executeScript(final String script) {
        executeScript(script, null);
    }
    
    @Override
    public void executeScript(final String script, final BrowserView.ExecuteScriptListener executeScriptListener) {
        if (mWebEngine != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object result = mWebEngine.executeScript(script);
                        if (executeScriptListener != null)
                            executeScriptListener.onExecutedScript(result);
                    } catch (netscape.javascript.JSException ex) {
                        Logger.getLogger(BrowserWebView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(BrowserWebView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }
    
    @Override
    public void openDocument(final String url) {
        SystemHelper.openBrowser(url);
    }
}
