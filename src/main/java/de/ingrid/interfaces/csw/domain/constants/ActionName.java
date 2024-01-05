/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

import java.util.Hashtable;
import java.util.Map;

import de.ingrid.interfaces.csw.domain.exceptions.CSWInvalidParameterValueException;

public enum ActionName {

    INSERT {
        @Override
        public String toString() {
            return "Insert";
        }
    },
    UPDATE {
        @Override
        public String toString() {
            return "Update";
        }
    },
    DELETE {
        @Override
        public String toString() {
            return "Delete";
        }
    };

    /**
     * Action name to Action mapping
     */
    private static Map<String, ActionName> nameMapping = null;
    static {
        nameMapping = new Hashtable<String, ActionName>();
        for (ActionName action : ActionName.values()) {
            nameMapping.put(action.toString(), action);
        }
    }

    public static ActionName getByName(String name) throws CSWInvalidParameterValueException {
        ActionName action = nameMapping.get(name);
        if (action == null) {
            throw new CSWInvalidParameterValueException("The action '"+name+"' is unknown.", name);
        }
        return action;
    }
}
