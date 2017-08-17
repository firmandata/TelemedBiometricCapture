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
                        if (newState == Worker.State.SUCCEEDED) {
                            //mWebEngine.executeScript("profile_set_title(\"Hallo\")");
                        }
                        Logger.getLogger(BrowserView.class.getName()).log(Level.INFO, newState.toString());
                    }
                });
                mWebEngine.load(url);

                ObservableList<Node> children = stackPane.getChildren();
                children.add(mWebView);

                jfxPanel.setScene(scene);
            }
        });
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
}
