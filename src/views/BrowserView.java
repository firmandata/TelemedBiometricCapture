package views;

import com.sun.javafx.application.PlatformImpl;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class BrowserView {
    
    protected Stage mStage;
    protected WebView mWebView;
    protected JFXPanel mJFXPanel;
    protected WebEngine mWebEngine;
    
    protected PageStateListener mPageStateListener;
    
    public BrowserView(final JPanel jPanel, final String url) {
        mJFXPanel = new JFXPanel();  
        createScene(mJFXPanel, url);  

        jPanel.add(mJFXPanel, BorderLayout.CENTER);
    }
    
    protected void createScene(final JFXPanel jfxPanel, final String url) {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                mStage = new Stage();
                mStage.setResizable(true);
                
                StackPane stackPane = new StackPane();
                stackPane.setAlignment(Pos.CENTER);
                
                Scene scene = new Scene(stackPane);
                mStage.setScene(scene);

                mWebView = new WebView();
                mWebView.setContextMenuEnabled(false);
                
                mWebEngine = mWebView.getEngine();
                mWebEngine.setJavaScriptEnabled(true);
                mWebEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                        if (mPageStateListener != null) {
                            String url = mWebEngine.getLocation();
                            if (newState == Worker.State.READY)
                                mPageStateListener.onPageStateReady(url);
                            else if (newState == Worker.State.SCHEDULED)
                                mPageStateListener.onPageStateScheduled(url);
                            else if (newState == Worker.State.RUNNING)
                                mPageStateListener.onPageStateRunning(url);
                            else if (newState == Worker.State.SUCCEEDED)
                                mPageStateListener.onPageStateSucceeded(url);
                            else if (newState == Worker.State.CANCELLED)
                                mPageStateListener.onPageStateCancelled(url);
                            else if (newState == Worker.State.FAILED)
                                mPageStateListener.onPageStateFailed(url);
                        }
                    }
                });
                mWebEngine.load(url);

                ObservableList<Node> children = stackPane.getChildren();
                children.add(mWebView);

                jfxPanel.setScene(scene);
            }
        });
    }
    
    public void setPageStateListener(final PageStateListener pageStateListener) {
        mPageStateListener = pageStateListener;
    }
    
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
    
    public void executeScript(final String script) {
        executeScript(script, null);
    }
    
    public void executeScript(final String script, final ExecuteScriptListener executeScriptListener) {
        if (mWebEngine != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object result = mWebEngine.executeScript(script);
                        if (executeScriptListener != null)
                            executeScriptListener.onExecutedScript(result);
                    } catch (netscape.javascript.JSException ex) {
                        Logger.getLogger(BrowserView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(BrowserView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }
    
    public interface PageStateListener {
        void onPageStateReady(String url);
        void onPageStateScheduled(String url);
        void onPageStateRunning(String url);
        void onPageStateSucceeded(String url);
        void onPageStateCancelled(String url);
        void onPageStateFailed(String url);
    }
    
    public interface ExecuteScriptListener {
        void onExecutedScript(Object result);
    }
}
