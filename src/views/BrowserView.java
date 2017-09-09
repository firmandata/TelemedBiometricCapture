package views;

import constants.Constant;
import controllers.JavaScriptController;
import javax.swing.JPanel;

public class BrowserView implements IBrowserView {

    protected IBrowserView mBrowserView;
    
    public BrowserView(final int browserProvider, final JavaScriptController javaScriptController, final JPanel jPanel, final String uri) {
        if (browserProvider == Constant.BROWSER_PROVIDER_WEBVIEW) {
            mBrowserView = new BrowserWebView(javaScriptController, jPanel, uri);
        } else if (browserProvider == Constant.BROWSER_PROVIDER_JXBROWSER) {
            mBrowserView = new jxBrowserView(javaScriptController, jPanel, uri);
        }
    }
    
    @Override
    public void setPageStateListener(final PageStateListener pageStateListener) {
        mBrowserView.setPageStateListener(pageStateListener);
    }

    @Override
    public void redirect(final String url) {
        mBrowserView.redirect(url);
    }

    @Override
    public void reload() {
        mBrowserView.reload();
    }

    @Override
    public void executeScript(final String script) {
        mBrowserView.executeScript(script);
    }

    @Override
    public void executeScript(final String script, final ExecuteScriptListener executeScriptListener) {
        mBrowserView.executeScript(script, executeScriptListener);
    }

    @Override
    public void openDocument(final String url) {
        mBrowserView.openDocument(url);
    }
    
    public interface PageStateListener {
        void onPageStateScheduled(String url);
        void onPageStateRunning(String url);
        void onPageStateSucceeded(String url);
        void onPageStateFailed(String url);
        void onPagePopupOpen(String url);
    }
    
    public interface ExecuteScriptListener {
        void onExecutedScript(Object result);
    }
}
