package helpers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemHelper {
    // Used to identify the windows platform.
    private static final String WIN_ID = "Windows";
    // The default system browser under windows.
    private static final String WIN_PATH = "rundll32";
    // The flag to display a url.
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    // The default browser under unix.
    private static final String UNIX_PATH = "netscape";
    // The flag to display a url.
    private static final String UNIX_FLAG = "-remote openURL";
    
    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name");
        if ( os != null && os.startsWith(WIN_ID))
            return true;
        else
            return false;
    }
    
    public static boolean openBrowser(final String url) {
        boolean result = false;
        
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
                result = true;
            } catch (Exception ex) {
                System.err.println("Could not invoke browser (" + url + ")");
                ex.printStackTrace();
            }
        } else {
            System.err.println("Could not invoke browser (" + url + ")" +
                System.getProperty("line.separator") +
                "Browsing is not supported on this computer.");
        }
        
//        boolean windows = isWindowsPlatform();
//        String cmd = null;
//        try {
//            if (windows) {
//                // cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
//                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
//                Process process = Runtime.getRuntime().exec(cmd);
//                result = true;
//            } else {
//                // Under Unix, Netscape has to be running for the "-remote"
//                // command to work.  So, we try sending the command and
//                // check for an exit value.  If the exit command is 0,
//                // it worked, otherwise we need to start the browser.
//                // cmd = 'netscape -remote openURL(http://www.java-tips.org)'
//                cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
//                Process process = Runtime.getRuntime().exec(cmd);
//                try {
//                    // wait for exit code -- if it's 0, command worked,
//                    // otherwise we need to start the browser up.
//                    int exitCode = process.waitFor();
//                    if (exitCode != 0) {
//                        // Command failed, start up the browser
//                        // cmd = 'netscape http://www.java-tips.org'
//                        cmd = UNIX_PATH + " "  + url;
//                        process = Runtime.getRuntime().exec(cmd);
//                        result = true;
//                    }
//                } catch(InterruptedException ex) {
//                    Logger.getLogger(SystemHelper.class.getName()).log(Level.SEVERE, "Error bringing up browser, cmd='" + cmd + "'", ex);
//                }
//            }
//        } catch(IOException ex) {
//            // couldn't exec browser
//            Logger.getLogger(SystemHelper.class.getName()).log(Level.SEVERE, "Could not invoke browser, command=" + cmd, ex);
//        }
        
        return result;
    }
}
