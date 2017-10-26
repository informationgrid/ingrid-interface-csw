package de.ingrid.interfaces.csw.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
