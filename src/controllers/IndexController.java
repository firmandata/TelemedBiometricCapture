package controllers;

import views.IndexLayout;

public class IndexController {
    
    protected IndexLayout mIndexView;
    
    public IndexController() {
        mIndexView = null;
    }
    
    public void showLayout() {
        if (mIndexView != null)
            mIndexView.dispose();
        
        mIndexView = IndexLayout.CreateLayout();
    }
}
