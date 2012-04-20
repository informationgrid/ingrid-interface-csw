package de.ingrid.interfaces.csw.config.model.communication;


public class CommunicationServerSecurity {

    private String keystore;
    
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getKeystore() {
        return keystore;
    }
    
}
