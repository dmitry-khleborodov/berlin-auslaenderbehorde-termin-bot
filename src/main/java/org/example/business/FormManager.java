package org.example.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.PersonalInfoFormTO;
import org.example.model.VisaFormTO;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.io.InputStream;

import static org.example.utils.DriverManager.initDriverHeadless;

public class FormManager {

    public final static Logger logger = LogManager.getLogger(FormManager.class);

    private FormManager() {

    }

    public static PersonalInfoFormTO readPersonalInfoFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = PersonalInfoFormTO.class.getResourceAsStream("/DEFAULT_PERSONAL_INFO_FORM.json");
        try {
            return mapper.readValue(is, PersonalInfoFormTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static VisaFormTO readVisaInfoFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = VisaFormTO.class.getResourceAsStream("/DEFAULT_VISA_APPLICATION_FORM.json");
        try {
            return mapper.readValue(is, VisaFormTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isResidenceTitleInfoVerified(VisaFormTO visaFormTO) {
        logger.info("Verifying form: {}", visaFormTO);
        String serviceType = visaFormTO.getServiceType();
        Boolean isResidencePermitPresent = visaFormTO.getResidencePermitPresent();
        String residencePermitId = visaFormTO.getResidencePermitId();

        if (serviceType.equals("Apply for a residence title")) {
            if (isResidencePermitPresent == null) {
                return false;
            }

            if (isResidencePermitPresent && residencePermitId == null) {
                return false;
            }

            if (!isResidencePermitPresent && residencePermitId != null) {
                return false;
            }
        }

        if (serviceType.equals("Extend a residence title")) {

            if (residencePermitId == null) {
                return false;
            }
        }

        return true;
    }

    public static void startForm(PersonalInfoFormTO personalInfoFormTO, VisaFormTO visaFormTO) {
        logger.info("PersonalInfoFormTO: {}", personalInfoFormTO);
        logger.info("VisaFormTO: {}", visaFormTO);
        RemoteWebDriver remoteWebDriver = initDriverHeadless();
        TerminFinder terminFinder = new TerminFinder(personalInfoFormTO, visaFormTO, remoteWebDriver);
        terminFinder.startScanning();
    }

}