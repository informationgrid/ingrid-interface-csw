/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model.impl;

import java.io.File;

/**
 * Configuration a
 * de.ingrid.interfaces.csw.harvest.impl.RecordCache instance.
 * 
 * @author ingo@wemove.com
 */
public class RecordCacheConfiguration extends ClassConfigurationBase {

	private File cachePath;

	public void setCachePath(File cachePath) {
		this.cachePath = cachePath;
	}

	public File getCachePath() {
		return this.cachePath;
	}

	@Override
	public String getClassName() {
		return de.ingrid.interfaces.csw.harvest.impl.RecordCache.class.getName();
	}
}
