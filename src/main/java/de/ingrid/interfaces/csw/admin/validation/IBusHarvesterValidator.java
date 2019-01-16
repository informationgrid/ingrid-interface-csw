/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.interfaces.csw.admin.validation;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.ingrid.interfaces.csw.admin.command.IBusHarvesterCommandObject;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;

public class IBusHarvesterValidator {

    @Service
    public static class IBusHarvesterValidatorStep1 extends AbstractValidator<IBusHarvesterCommandObject> {

        final private static Log log = LogFactory.getLog(IBusHarvesterValidatorStep1.class);
        
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
