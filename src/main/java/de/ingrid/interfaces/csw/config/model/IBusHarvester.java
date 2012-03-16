package de.ingrid.interfaces.csw.config.model;

import java.util.List;

/**
 * Container for configuration properties for a
 * de.ingrid.interfaces.csw.harvest.impl.IBusHarvester instance.
 * 
 * @author ingo@wemove.com
 */
public class IBusHarvester extends ClassConfiguration {

	/**
	 * The communication xml file path
	 */
	private String communicationXml;

	/**
	 * List of request definitions
	 */
	private List<RequestDefinition> requestDefinitions;

	public String getCommunicationXml() {
		return this.communicationXml;
	}

	public void setCommunicationXml(String communicationXml) {
		this.communicationXml = communicationXml;
	}

	public List<RequestDefinition> getRequestDefinitions() {
		return this.requestDefinitions;
	}

	public void setRequestDefinitions(List<RequestDefinition> requestDefinitions) {
		this.requestDefinitions = requestDefinitions;
	}

	@Override
	public String getClassName() {
		return de.ingrid.interfaces.csw.harvest.impl.IBusHarvester.class.getName();
	}
}
