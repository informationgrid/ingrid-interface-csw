/*
 * Created on 04.10.2005
 *
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.ArrayList;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;


/**
 * @author rschaefer
 */
public final class SessionParameters {	
	public static final int MAXRECORDS = 10;
	public static final int STARTPOSITION = 1;
	private boolean operationIsGetCap = false;
	private boolean operationIsGetRecs = false;
	private boolean operationIsGetRecById = false;
	private boolean operationIsDescRec = false;
	private int maxRecords = MAXRECORDS;
	private String resultType = "hits";
	private String outputFormat = "text/xml";
	private String outputSchema = "csw:ogccore";
	private String typeNames = null;
	private boolean typeNameIsDataset = false;
	private boolean typeNameIsDatasetcollection = false;
	private boolean typeNameIsService = false;
	private boolean typeNameIsApplication = false;
	private int startPosition = STARTPOSITION;
	private int numberOfRecordsMatched = 0;
	private int numberOfRecordsReturned = 0;
	private String version = null; 
	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * the sorting: default is by
	 * metadata element name (ascending)
	 */
	private  String sortBy = "metadata_element_name:A";

	/**
	 * stores the OGC Filter element
	 */
	private Element soapElementFilter = null;

	//also needed for GetRecordById requests

	/**
	 * TODO elementSetName default value for GetRecordById should be summary
	 */
	private  String elementSetName = "full";

	/**
	 * for the id string of GetRecById
	 *
	 */
	private String ids = null;

	/**
	 * holds all ids from the id string of GetRecById
	 */
	private ArrayList idsList = null;


	/**
	 * @return Returns the elementSetName.
	 */
	public  String getElementSetName() {
		return elementSetName;
	}

	/**
	 * @param elemSetName The elementSetName to set.
	 */
	public  void setElementSetName(final String elemSetName) {
		this.elementSetName = elemSetName;
	}

	/**
	 * @return Returns the maxRecords.
	 */
	public  int getMaxRecords() {
		return maxRecords;
	}

	/**
	 * @param maxRecs The maxRecords to set.
	 */
	public  void setMaxRecords(final int maxRecs) {
		this.maxRecords = maxRecs;
	}

	/**
	 * @return Returns the startPosition.
	 */
	public  int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param startPos The startPosition to set.
	 */
	public  void setStartPosition(final int startPos) {
		this.startPosition = startPos;
	}

	/**
	 * @return Returns the outputFormat.
	 */
	public  String getOutputFormat() {
		return outputFormat;
	}

	/**
	 * @param outputForm The outputFormat to set.
	 */
	public  void setOutputFormat(final String outputForm) {
		this.outputFormat = outputForm;
	}

	/**
	 * @return Returns the outputSchema.
	 */
	public  String getOutputSchema() {
		return outputSchema;
	}

	/**
	 * @param outputSch The outputSchema to set.
	 */
	public  void setOutputSchema(final String outputSch) {
		this.outputSchema = outputSch;
	}

	/**
	 * @return Returns the resultType.
	 */
	public  String getResultType() {
		return resultType;
	}

	/**
	 * @param resultT  The resultType to set.
	 */
	public  void setResultType(final String resultT) {
		this.resultType = resultT;
	}

	/**
	 * @return Returns the sortBy.
	 */
	public  String getSortBy() {
		return sortBy;
	}

	/**
	 * @param sort The sortBy to set.
	 */
	public  void setSortBy(final String sort) {
		this.sortBy = sort;
	}

	/**
	 * @return Returns the typeNames.
	 */
	public  String getTypeNames() {
		return typeNames;
	}

	/**
	 * @param typeN The typeNames to set.
	 */
	public  void setTypeNames(final String typeN) {
		this.typeNames = typeN;
	}

	/**
	 * @return Returns the soapElementFilter.
	 */
	public Element getSoapElementFilter() {
		return soapElementFilter;
	}

	/**
	 * @param soapElemFilter The soapElementFilter to set.
	 */
	public void setSoapElementFilter(final Element soapElemFilter) {
		this.soapElementFilter = soapElemFilter;
	}

	/**
	 * @return Returns the ids.
	 */
	public String getIds() {
		return ids;
	}

	/**
	 * @param string The ids to set.
	 */
	public void setIds(final String string) {
		this.ids = string;
	}

	/**
	 * @return Returns the typeNameIsApplication.
	 */
	public boolean isTypeNameIsApplication() {
		return typeNameIsApplication;
	}

	/**
	 * @param typeNameIsAppl The typeNameIsApplication to set.
	 */
	public void setTypeNameIsApplication(final boolean typeNameIsAppl) {
		this.typeNameIsApplication = typeNameIsAppl;
	}

	/**
	 * @return Returns the typeNameIsDataset.
	 */
	public boolean isTypeNameIsDataset() {
		return typeNameIsDataset;
	}

	/**
	 * @param typeNameIsDatas The typeNameIsDataset to set.
	 */
	public void setTypeNameIsDataset(final boolean typeNameIsDatas) {
		this.typeNameIsDataset = typeNameIsDatas;
	}

	/**
	 * @return Returns the typeNameIsDatasetcollection.
	 */
	public boolean isTypeNameIsDatasetcollection() {
		return typeNameIsDatasetcollection;
	}

	/**
	 * @param typeNameIsDatasetcoll The typeNameIsDatasetcollection to set.
	 */
	public void setTypeNameIsDatasetcollection(final boolean typeNameIsDatasetcoll) {
		this.typeNameIsDatasetcollection = typeNameIsDatasetcoll;
	}

	/**
	 * @return Returns the typeNameIsService.
	 */
	public boolean isTypeNameIsService() {
		return typeNameIsService;
	}

	/**
	 * @param typeNameIsServ The typeNameIsService to set.
	 */
	public void setTypeNameIsService(final boolean typeNameIsServ) {
		this.typeNameIsService = typeNameIsServ;
	}

	/**
	 * @return Returns the idsList.
	 */
	public ArrayList getIdsList() {
		return idsList;
	}

	/**
	 * @param idsLst The idsList to set.
	 */
	public void setIdsList(final ArrayList idsLst) {
		this.idsList = idsLst;
	}

	/**
	 * @return Returns the operationIsDescRec.
	 */
	public boolean isOperationIsDescRec() {
		return operationIsDescRec;
	}

	/**
	 * @param opIsDescRec The operationIsDescRec to set.
	 */
	public void setOperationIsDescRec(final boolean opIsDescRec) {
		this.operationIsDescRec = opIsDescRec;
	}

	/**
	 * @return Returns the operationIsGetCap.
	 */
	public boolean isOperationIsGetCap() {
		return operationIsGetCap;
	}

	/**
	 * @param opIsGetCap The operationIsGetCap to set.
	 */
	public void setOperationIsGetCap(final boolean opIsGetCap) {
		this.operationIsGetCap = opIsGetCap;
	}

	/**
	 * @return Returns the operationIsGetRecById.
	 */
	public boolean isOperationIsGetRecById() {
		return operationIsGetRecById;
	}

	/**
	 * @param opIsGetRecById The operationIsGetRecById to set.
	 */
	public void setOperationIsGetRecById(final boolean opIsGetRecById) {
		this.operationIsGetRecById = opIsGetRecById;
	}

	/**
	 * @return Returns the operationIsGetRecs.
	 */
	public boolean isOperationIsGetRecs() {
		return operationIsGetRecs;
	}

	/**
	 * @param opIsGetRecs The operationIsGetRecs to set.
	 */
	public void setOperationIsGetRecs(final boolean opIsGetRecs) {
		this.operationIsGetRecs = opIsGetRecs;
	}

	/**
	 * @return Returns the numberOfRecordsMatched.
	 */
	public int getNumberOfRecordsMatched() {
		return numberOfRecordsMatched;
	}

	/**
	 * @param numberOfRecordsMatch The numberOfRecordsMatched to set.
	 */
	public void setNumberOfRecordsMatched(final int numberOfRecordsMatch) {
		this.numberOfRecordsMatched = numberOfRecordsMatch;
	}

	/**
	 * @return Returns the numberOfRecordsReturned.
	 */
	public int getNumberOfRecordsReturned() {
		return numberOfRecordsReturned;
	}

	/**
	 * @param numberOfRecordsReturn The numberOfRecordsReturned to set.
	 */
	public void setNumberOfRecordsReturned(final int numberOfRecordsReturn) {
		this.numberOfRecordsReturned = numberOfRecordsReturn;
	}
	
	public String getRequestOperation() {
		if (operationIsGetCap) {
			return ClientRequestParameters.GETCAPABILITIES;
		} else if (operationIsGetRecs) {
			return ClientRequestParameters.GETRECORDS;
		} else if (operationIsGetRecById) {
			return ClientRequestParameters.GETRECORDBYID;
		} else if (operationIsDescRec) {
			return ClientRequestParameters.DESCRIBERECORD;
		} else {
			return null;
		}
	}
	
}
