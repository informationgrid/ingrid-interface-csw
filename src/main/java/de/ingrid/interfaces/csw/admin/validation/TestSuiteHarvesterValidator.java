package de.ingrid.interfaces.csw.admin.validation;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.ingrid.interfaces.csw.admin.command.IBusHarvesterCommandObject;
import de.ingrid.interfaces.csw.admin.command.TestSuiteHarvesterCommandObject;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;

public class TestSuiteHarvesterValidator {

    @Service
    public static class TestSuiteHarvesterValidatorStep1 extends AbstractValidator<TestSuiteHarvesterCommandObject> {

        final private static Log log = LogFactory.getLog(TestSuiteHarvesterValidatorStep1.class);

        public final Errors validate(final Errors errors) {
            rejectIfEmptyOrWhitespace(errors, "workingDirectory");
            try {
                File workingDir = new File(((String) errors.getFieldValue("workingDirectory")));
                if (!workingDir.exists() && !workingDir.mkdirs()) {
                    errors.rejectValue("workingDirectory", "harvester.workingDirectory.couldnotcreate");
                }
                if (!workingDir.canWrite()) {
                    errors.rejectValue("workingDirectory", "harvester.workingDirectory.cannotwrite");
                }
            } catch (Exception e) {
                log.error("Error creating wrking directory", e);
                errors.rejectValue("workingDirectory", "harvester.workingDirectory.error");
            }

            return errors;
        }
    }

    @Service
    public static class IBusHarvesterValidatorStep2 extends AbstractValidator<IBusHarvesterCommandObject> {

        public final Errors validate(final Errors errors) {
            rejectIfEmptyOrWhitespace(errors, "iBusIp");
            rejectIfEmptyOrWhitespace(errors, "iBusPort");
            rejectIfEmptyOrWhitespace(errors, "iBusProxyId");
            rejectIfEmptyOrWhitespace(errors, "clientProxyId");

            return errors;
        }
    }

    @Service
    public static class IBusHarvesterValidatorStep4 extends AbstractValidator<RequestDefinition> {

        public final Errors validate(final Errors errors) {
            rejectIfEmptyOrWhitespace(errors, "queryString");
            rejectIfEmptyOrWhitespace(errors, "timeout");
            rejectIfEmptyOrWhitespace(errors, "pause");
            rejectIfEmptyOrWhitespace(errors, "recordsPerCall");
            rejectIfEmptyOrWhitespace(errors, "plugId");

            return errors;
        }
    }

}