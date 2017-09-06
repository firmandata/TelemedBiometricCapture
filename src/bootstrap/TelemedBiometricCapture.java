package bootstrap;

import constants.Config;
import constants.Constant;
import helpers.LibraryManager;
import helpers.FingersTools;
import controllers.IndexController;
import controllers.NeurotecServiceController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelemedBiometricCapture {
    
    public static void main(String[] args) {
        boolean isLicensed = false;
        
        if (Config.FINGER_SDK == Constant.FINGER_SDK_NEUROTEC) {
            LibraryManager.initLibraryPath();

            List<String> requiredLicenses = new ArrayList<String>();
            requiredLicenses.add("Biometrics.FingerExtraction");
            requiredLicenses.add("Devices.FingerScanners");

            try {
                isLicensed = FingersTools.getInstance().obtainLicenses(requiredLicenses);
            } catch (IOException ex) {

            }
        } else {
            isLicensed = true;
        }        
        
        if (isLicensed) {
            if (!Config.RUN_AS_SERVICE) {
                IndexController indexController = new IndexController();
                indexController.showLayout();
            } else {
                try {
                    NeurotecServiceController neurotectServiceController = new NeurotecServiceController();
                    neurotectServiceController.start(Config.NEUROTECT_SERVICE_PORT);
                } catch (IOException ex) {
                    Logger.getLogger(NeurotecServiceController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
