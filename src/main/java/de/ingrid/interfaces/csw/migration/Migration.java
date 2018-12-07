/*-
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.IBusHarvesterConfiguration;
import de.ingrid.interfaces.csw.tools.FileUtils;

@Service
public class Migration {

    private static Log log = LogFactory.getLog( Migration.class );

    @Autowired
    ConfigurationProvider cProvider = null;

    public void migrate() throws IOException {

        //check for legacy config files in application root, migrate to instances directory
        Path legacyConfigPath = Paths.get( cProvider.getInstancesPath().getParentFile().getAbsolutePath(), "config.xml");
        if (Files.exists( legacyConfigPath )) {
            Files.createDirectories( cProvider.getInstancesPath().toPath() );
            log.info( "Move legacy config.xml to instances directory." );
            Files.move( legacyConfigPath, Paths.get( cProvider.getInstancesPath().getAbsolutePath(), "config.xml" ), StandardCopyOption.REPLACE_EXISTING);
        }
        
        //check for legacy index directory, migrate to data directory
        Path legacyIndexPath = Paths.get( cProvider.getInstancesPath().getParentFile().getAbsolutePath(), "index");
        if (Files.exists( legacyIndexPath )) {
            Files.createDirectories( cProvider.getIndexPath().getParentFile().toPath() );
            log.info( "Move legacy index directory to data directory." );
            Files.move( legacyIndexPath, cProvider.getIndexPath().toPath());
        }

        //check for legacy cache directory, migrate to data directory
        Path legacyCachePath = Paths.get( cProvider.getInstancesPath().getParentFile().getAbsolutePath(), "cache");
        if (Files.exists( legacyCachePath )) {
            Files.createDirectories( cProvider.getRecordCachePath().getParentFile().toPath() );
            log.info( "Move legacy cache directory to data directory." );
            Files.move( legacyCachePath, cProvider.getRecordCachePath().toPath());
        }

        //check for legacy scheduling pattern file, migrate to data directory
        Path legacySchedulingPattern = Paths.get( cProvider.getInstancesPath().getParent(), "scheduling.pattern" );
        if (Files.exists( legacySchedulingPattern)) {
            log.info( "Move legacy scheduling pattern file to instance directory." );
            Files.move( legacySchedulingPattern, Paths.get( cProvider.getInstancesPath().getAbsolutePath(), "scheduling.pattern"));
        }
        
        
        // check for instance directories in application root
        // move existing instance directories to instances directory
        Path instancesPath = Paths.get( cProvider.getInstancesPath().getAbsolutePath().toString() );
        if (!Files.exists( cProvider.getInstancesPath().toPath() )) {
            log.info( "Create instances path." );
            Files.createDirectories( cProvider.getInstancesPath().toPath() );
        }

        Configuration conf = cProvider.reloadConfiguration();
        for (HarvesterConfiguration hc : conf.getHarvesterConfigurations()) {
            String wd = hc.getWorkingDirectory();
            if (wd == null || wd.contains( instancesPath.toString() )) {
                continue;
            }
            log.info( "Instance directory '" + wd + "' found outside instances directory '" + instancesPath + "'." );
            Path source = new File( wd ).toPath();
            Path dest = Paths.get( instancesPath.toString(), FileUtils.encodeFileName( hc.getName() ) );
            log.info( "Move '" + source + "' to '" + dest + "'." );
            Files.move( source, dest );

            hc.setWorkingDirectory( dest.toString() );

            String oldPath = hc.getCacheConfiguration().getCachePath().getAbsolutePath();
            hc.getCacheConfiguration().setCachePath( new File( oldPath.replace( wd, dest.toString() ) ) );

            if (hc instanceof IBusHarvesterConfiguration) {
                oldPath = ((IBusHarvesterConfiguration) hc).getCommunicationXml();
                ((IBusHarvesterConfiguration) hc).setCommunicationXml( oldPath.replace( wd, dest.toString() ) );
            }
        }
        cProvider.write( conf );

        cProvider.reloadConfiguration();

    }
}
