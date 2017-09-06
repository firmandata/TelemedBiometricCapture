package views;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import controllers.JavaScriptController;
import java.awt.BorderLayout;
import javafx.application.Platform;
import javax.swing.JPanel;

public class jxBrowserView implements IBrowserView {
    
    protected final Browser mBrowser;
    
    protected JavaScriptController mJavaScriptController;
    
    protected views.BrowserView.PageStateListener mPageStateListener;
    
    public jxBrowserView(final JavaScriptController javaScriptController, final JPanel jPanel, final String url) {
        mJavaScriptController = javaScriptController;
        
        mBrowser = new Browser();
        mBrowser.addScriptContextListener(new ScriptContextAdapter() {
            @Override
            public void onScriptContextCreated(ScriptContextEvent scriptContextEvent) {
                Browser browser = scriptContextEvent.getBrowser();
                JSValue jsValue = browser.executeJavaScriptAndReturnValue("window");
                jsValue.asObject().setProperty("app", mJavaScriptController);
            }
        });
        
        BrowserView browserView = new BrowserView(mBrowser);
        
        jPanel.add(browserView, BorderLayout.CENTER);
        
        mBrowser.loadURL(url);
    }
    
    @Override
    public void setPageStateListener(final views.BrowserView.PageStateListener pageStateListener) {
        mPageStateListener = pageStateListener;
    }
    
    @Override
    public void redirect(final String url) {
        mBrowser.loadURL(url);
    }
    
    @Override
    public void reload() {
        mBrowser.reload();
    }
    
    @Override
    public void executeScript(final String script) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mBrowser.executeJavaScript(script);
            }
        });
    }
    
    @Override
    public void executeScript(final String script, final views.BrowserView.ExecuteScriptListener executeScriptListener) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                JSValue jsValue = mBrowser.executeJavaScriptAndReturnValue(script);
                if (executeScriptListener != null)
                    executeScriptListener.onExecutedScript(jsValue);
            }
        });
    }
    
    @Override
    public void openDocument(final String url) {
        
    }
}
