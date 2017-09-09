package views;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import controllers.JavaScriptController;
import java.awt.BorderLayout;
import javafx.application.Platform;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class jxBrowserView implements IBrowserView {
    
    protected final Browser mBrowser;
    
    protected JavaScriptController mJavaScriptController;
    
    protected views.BrowserView.PageStateListener mPageStateListener;
    
    public jxBrowserView(final JavaScriptController javaScriptController, final JPanel jPanel, final String url) {
        mJavaScriptController = javaScriptController;
        
        mBrowser = new Browser();
        mBrowser.setAudioMuted(false);
        mBrowser.setZoomEnabled(false);
        mBrowser.addScriptContextListener(new ScriptContextAdapter() {
            @Override
            public void onScriptContextCreated(ScriptContextEvent scriptContextEvent) {
                Browser browser = scriptContextEvent.getBrowser();
                JSValue jsValue = browser.executeJavaScriptAndReturnValue("window");
                jsValue.asObject().setProperty("app", mJavaScriptController);
            }
        });
        mBrowser.addLoadListener(new LoadAdapter() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
                if (startLoadingEvent.isMainFrame()) {
                    if (mPageStateListener != null)
                        mPageStateListener.onPageStateScheduled(startLoadingEvent.getValidatedURL());
                }
            }
            
            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
                if (provisionalLoadingEvent.isMainFrame()) {
                    if (mPageStateListener != null)
                        mPageStateListener.onPageStateRunning(provisionalLoadingEvent.getURL());
                }
            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
                if (finishLoadingEvent.isMainFrame()) {
                    // Send ready flag to app_ready() function
                    executeScript("app_ready()");
                    
                    if (mPageStateListener != null)
                        mPageStateListener.onPageStateSucceeded(finishLoadingEvent.getValidatedURL());
                }
            }
            
            @Override
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
                if (failLoadingEvent.isMainFrame()) {
                    if (mPageStateListener != null)
                        mPageStateListener.onPageStateFailed(failLoadingEvent.getValidatedURL());
                }
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
        mBrowser.executeJavaScript(script);
    }
    
    @Override
    public void executeScript(final String script, final views.BrowserView.ExecuteScriptListener executeScriptListener) {
        JSValue jsValue = mBrowser.executeJavaScriptAndReturnValue(script);
        if (executeScriptListener != null)
            executeScriptListener.onExecutedScript(jsValue);
    }
    
    @Override
    public void openDocument(final String url) {
        
    }
}
