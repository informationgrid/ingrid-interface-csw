/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.ingrid.interfaces.csw.admin.command.HarvesterCommandObject;

@Service
public class HarvesterValidator extends AbstractValidator<HarvesterCommandObject> {

    public final Errors validate(final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, "name");
        rejectIfEmptyOrWhitespace(errors, "type");

        return errors;
    }
}
