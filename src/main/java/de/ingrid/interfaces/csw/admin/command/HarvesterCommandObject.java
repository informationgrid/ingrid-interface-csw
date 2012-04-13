package de.ingrid.interfaces.csw.admin.command;

public class HarvesterCommandObject {

    private String name;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
