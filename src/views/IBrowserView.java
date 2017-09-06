package views;

public interface IBrowserView {
    void setPageStateListener(BrowserView.PageStateListener pageStateListener);
    void redirect(String url);
    void reload();
    void executeScript(String script);
    void executeScript(String script, BrowserView.ExecuteScriptListener executeScriptListener);
    void openDocument(String url);
}
