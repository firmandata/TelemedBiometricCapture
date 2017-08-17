package controllers;

import views.IndexLayout;
import views.BrowserLayout;

public class IndexController {
    
    protected IndexLayout mIndexView;
    protected BrowserLayout mBrowserView;
    
    public IndexController() {
        mIndexView = null;
        mBrowserView = null;
    }
    
    public void showIndexLayout() {
        if (mIndexView != null)
            mIndexView.dispose();
        
        mIndexView = IndexLayout.CreateLayout();
    }
    
    public void showLayout() {
        if (mBrowserView != null)
            mBrowserView.dispose();
        
        mBrowserView = BrowserLayout.CreateLayout();
    }
}
