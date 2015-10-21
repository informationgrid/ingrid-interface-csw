package de.ingrid.interfaces.csw.admin.command;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class AdminManager {
    
    /**
     * Read the override configuration and convert the clear text password to a bcrypt-hash,
     * which is used and needed by the base-webapp v3.6.1 
     */
    private static void migratePassword() {
        try {
            InputStream is = new FileInputStream( "conf/config.properties" );
            Properties props = new Properties();
            props.load( is );
            String oldPassword = props.getProperty( "ingrid.admin.password" );
            is.close();
            
            props.setProperty( "plugdescription.IPLUG_ADMIN_PASSWORD", BCrypt.hashpw(oldPassword, BCrypt.gensalt()) );
            
            OutputStream os = new FileOutputStream( "conf/config.override.properties" );
            props.store( os, "Override configuration written by the application" );
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Read the override configuration and write a new bcrypt-hash password.
     */
    private static void resetPassword(String newPassword) {
        try {
            InputStream is = new FileInputStream( "conf/config.override.properties" );
            Properties props = new Properties();
            props.load( is );
            props.setProperty( "ingrid.admin.password", BCrypt.hashpw(newPassword, BCrypt.gensalt()) );
            
            OutputStream os = new FileOutputStream( "conf/config.override.properties" );
            props.store( os, "Override configuration written by the application" );
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String command = args[0];
            switch (command) {
            case "migrate_password":
                migratePassword();
                break;
            case "reset_password":
                if (args.length == 2) {
                    String newPassword = args[1];
                    resetPassword( newPassword );
                } else {
                    printUsage();
                }
                break;
            default:
                printUsage();
            }
        } else {
            printUsage();
        }
    }
    
    private static void printUsage() {
        System.out.println( "Valid commands are: migrate_password, reset_password <newPassword>" );
    }
}
