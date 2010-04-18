/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.data;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.RangeQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.IDataTypes;

/**
 * Global STATIC data and utility methods for SEARCH !
 * 
 * @author Martin Maidhof
 */
public class UtilsSearch {

	private final static Log log = LogFactory.getLog(UtilsSearch.class);

	public final static String DETAIL_VALUES_SEPARATOR = ", ";

	public static String queryToString(IngridQuery q) {
		String qStr = "";
		boolean isFirstTerm = true;

		try {
			TermQuery[] terms = q.getTerms();
			for (int i = 0; i < terms.length; i++) {
				if (!terms[i].isRequred()) {
					qStr = qStr.concat(" OR ").concat(terms[i].getTerm());
				} else {
					if (isFirstTerm) {
						qStr = qStr.concat(terms[i].getTerm());
						isFirstTerm = false;
					} else {
						qStr = qStr.concat(" ").concat(terms[i].getTerm());
					}
				}
			}
			ClauseQuery[] clauses = q.getClauses();
			for (int i = 0; i < clauses.length; i++) {
				if (!clauses[i].isRequred()) {
					qStr = qStr.concat(" OR (").concat(queryToString(clauses[i])).concat(")");
				} else {
					qStr = qStr.concat(" (").concat(queryToString(clauses[i])).concat(")");
				}

			}

			FieldQuery[] fields = q.getFields();
			for (int i = 0; i < fields.length; i++) {
				qStr = qStr.concat(" ").concat(buildFieldQueryStr(fields[i]));
			}
			
			RangeQuery[] rangeQueries = q.getRangeQueries();
			for (RangeQuery rq : rangeQueries) {
				if (!rq.isRequred()) {
					qStr = qStr.concat(" OR ").concat(rq.getRangeName()+":["+rq.getRangeFrom()+ " TO " + rq.getRangeTo() + "]");
				} else {
					qStr = qStr.concat(" ").concat(rq.getRangeName()+":["+rq.getRangeFrom()+ " TO " + rq.getRangeTo() + "]");
				}
			}
			

			FieldQuery[] datatypes = q.getDataTypes();
			for (int i = 0; i < datatypes.length; i++) {
				qStr = qStr.concat(" ").concat(buildFieldQueryStr(datatypes[i]));
			}

			String ranking = q.getRankingType();
			if (ranking != null) {
				qStr = qStr.concat(" ranking:").concat(ranking);
			}

			if (hasPositiveDataType(q, IDataTypes.SNS)) {
				FieldQuery[] fq = getField(q, Topic.REQUEST_TYPE);
				int rType;
				if (fq != null && fq.length > 0) {
					rType = Integer.parseInt(fq[0].getFieldValue());
				} else {
					rType = q.getInt(Topic.REQUEST_TYPE);
				}
				if (rType == Topic.ANNIVERSARY_FROM_TOPIC) {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(ANNIVERSARY_FROM_TOPIC)");
				} else if (rType == Topic.EVENT_FROM_TOPIC) {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(EVENT_FROM_TOPIC)");
				} else if (rType == Topic.SIMILARTERMS_FROM_TOPIC) {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(SIMILARTERMS_FROM_TOPIC)");
				} else if (rType == Topic.TOPIC_FROM_TERM) {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(TOPIC_FROM_TERM)");
				} else if (rType == Topic.TOPIC_FROM_TEXT) {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(TOPIC_FROM_TEXT)");
				} else if (rType == Topic.SIMILARLOCATIONS_FROM_TOPIC) {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(SIMILARLOCATIONS_FROM_TOPIC)");
				} else {
					qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
							.concat("(UNKNOWN)");
				}
			}

			// remaining keys in IngridQuery
/*			Iterator iterator = q.keySet().iterator();
			qStr = qStr.concat(" /MAP->");
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Object value = q.get(key);
				if (value == null) {
					value = "null";
				}
				qStr = qStr.concat(" ").concat(key.toString()).concat(":");

				if (value instanceof String[]) {
					qStr = qStr.concat("[");
					String[] valueArray = (String[]) value;
					for (int i = 0; i < valueArray.length; i++) {
						if (i != 0) {
							qStr = qStr.concat(", ");
						}
						qStr = qStr.concat(valueArray[i]);
					}
					qStr = qStr.concat("]");
				} else if (value instanceof ArrayList) {
					qStr = qStr.concat("[");
					ArrayList valueList = (ArrayList) value;
					for (int i = 0; i < valueList.size(); i++) {
						if (i != 0) {
							qStr = qStr.concat(", ");
						}
						qStr = qStr.concat(valueList.get(i).toString());
					}
					qStr = qStr.concat("]");
				} else {
					qStr = qStr.concat(value.toString());
				}
			} */

		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems transforming IngridQuery to String, string so far = " + qStr, ex);
			}
		}

		return qStr;

		/*
		 * StringBuffer qStr = new StringBuffer(); qStr.append(query);
		 * qStr.append(", ");
		 * 
		 * FieldQuery[] fields = query.getDataTypes(); for (int i = 0; i <
		 * fields.length; i++) { qStr.append(" "); qStr.append(fields[i]);
		 * qStr.append("/required:"); qStr.append(fields[i].isRequred());
		 * qStr.append("/prohibited:"); qStr.append(fields[i].isProhibited()); }
		 * 
		 * return qStr.toString();
		 */
	}

	private static String buildFieldQueryStr(FieldQuery field) {
		String result = "";
		if (field.isRequred() && !field.isProhibited()) {
			result = result.concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
		} else if (!field.isRequred() && field.isProhibited()) {
			result = result.concat("-").concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
		} else if (field.isRequred() && field.isProhibited()) {
			result = result.concat("+-").concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
		} else {
			result = result.concat("?").concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
		}
		return result;
	}

	public static boolean hasPositiveDataType(IngridQuery q, String datatype) {
		String[] dtypes = q.getPositiveDataTypes();
		for (int i = 0; i < dtypes.length; i++) {
			if (dtypes[i].equalsIgnoreCase(datatype)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasNegativeDataType(IngridQuery q, String datatype) {
		String[] dtypes = q.getNegativeDataTypes();
		for (int i = 0; i < dtypes.length; i++) {
			if (dtypes[i].equalsIgnoreCase(datatype)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all fields with a specific field name.
	 * 
	 * @param q
	 *            The query.
	 * @param fieldName
	 *            The fieldname.
	 * @return The Array of resulting fields.
	 */
	public static FieldQuery[] getField(IngridQuery q, String fieldName) {
		FieldQuery[] fields = q.getFields();
		ArrayList<FieldQuery> resultFields = new ArrayList<FieldQuery>();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getFieldName().equals(fieldName)) {
				resultFields.add(fields[i]);
			}
		}
		return (FieldQuery[]) resultFields.toArray(new FieldQuery[resultFields.size()]);
	}

}
