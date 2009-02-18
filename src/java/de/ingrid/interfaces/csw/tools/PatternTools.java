/*----------------------------------------------------------------------------*
 *          @@@@@      @@@       @@@@@                                        *
 *      @@@@@@@@@@@    @@@@    @@@@@@@@        @                              *
 *     @@@@@@@@@@@@    @@@@   @@@@@@@@@     @@@@                              *
 *    @@@@@            @@@@  @@@@           @@@@                              *
 *   @@@@@             @@@@  @@@@@        @@@@@@@@@   @@@@@@@@      @@@@@@@   *
 *   @@@@    @@@@@@@   @@@@   @@@@@@@     @@@@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *   @@@@   @@@@@@@@   @@@@    @@@@@@@@     @@@@    @@@@    @@@   @@@@        *
 *   @@@@    @@@@@@@   @@@@      @@@@@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *   @@@@@      @@@@   @@@@         @@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *    @@@@@     @@@@   @@@@   @     @@@@    @@@@    @@@@      @   @@@@        *
 *     @@@@@@@@@@@@@   @@@@   @@@@@@@@@@    @@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *       @@@@@@@@@@@   @@@@   @@@@@@@@       @@@@@@   @@@@@@@@@     @@@@@@@   *
 *                            Neue Wege mit GIS                               *
 *                                                                            *
 * Fraunhoferstr. 5                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schaefer                                            *
 * Erstellungsdatum : 28.05.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     : methods for testing and creating patterns and formats   *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * aenderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 * 10.01.2006 |1.0      |rschaefer | method toValidDateFormat improvements    *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
 */

package de.ingrid.interfaces.csw.tools;

import java.util.StringTokenizer;

//import org.apache.lucene.document.NumericField;


/**
 * This class supplies methods for testing and creating patterns and formats (e.g. version numbers, dates etc.).
 * 
 * @author rschaefer
 */
public final class PatternTools {

		
	/**
	 * the hyphen or minus
	 */
	private static final String HYPHEN = "-";
	
	
	/**
	 * 
	 */
	//private static final int ONE = 1;
	
	/**
	 * 
	 */
	private static final int TWO = 2;
	
	/**
	 * 
	 */
	//private static final int THREE = 3;
	
	/**
	 * 
	 */
	private static final int FOUR = 4;
	
	/**
	 * 
	 */
	//private static final int FIVE = 5;
	
	/**
	 * 
	 */
	private static final int SIX = 6;
	
	/**
	 * 
	 */
	//private static final int SEVEN = 7;
	
	/**
	 * 
	 */
	private static final int EIGHT = 8;
	
	/**
	 * 
	 */
	//private static final int NINE = 9;
	
	/**
	 * 
	 */
	private static final int TEN = 10;
	
	
	/**
	 * constructor
	 */
	private PatternTools() { }
	
	
	/** 
	 * This method tests the pattern of the version numbers of the catalogue 
	 * service.
	 * Required format of the version is three non-negative numbers of the form x.y.z;
	 * y and z shall not exceed 99.
	 * 
	 * @param version String
	 * @return isVersionFormatValid boolean
	 */
	public static boolean isVersionFormatValid(final String version) {

		boolean isVersionFormatValid = false;

		//TODO regexp fuer x > 99?
		if (version.matches("[0-9][.][0-9][.][0-9]") ||
				 version.matches("[0-9][0-9][.][0-9][.][0-9]") ||
				 version.matches("[0-9][0-9][.][0-9][0-9][.][0-9]") ||
				  version.matches("[0-9][0-9][.][0-9][.][0-9][0-9]") ||
				  version.matches("[0-9][.][0-9][0-9][.][0-9]") ||
				  version.matches("[0-9][.][0-9][0-9][.][0-9][0-9]") ||
			      version.matches("[0-9][.][0-9][.][0-9][0-9]") ||
				  version.matches("[0-9][0-9][.][0-9][0-9][.][0-9][0-9]")) {

			isVersionFormatValid = true;
		}

		return isVersionFormatValid;

	}

	/** 
	 * This method transforms a string into a valid date string.
	 * @param dateStr String
	 * @param index boolean flag that shows if lucene index format is required
	 * @return validDate String
	 * @throws Exception e
	 */
	public static String toValidDateFormat(final String dateStr, final boolean index)
			throws Exception {

		String date = dateStr;
		
		String validDate = "";
		String year = null;
		String month = null;
		String day = null;

		date = date.trim();

		boolean dateHasHyphens = false;

		if (date.indexOf(HYPHEN) == -1) {

			dateHasHyphens = false;

			//System.out.println("PatternTools - toValidDateFormat: Date has no hyphens.");	 

			if (date.length() > EIGHT) {

				//System.out.println("PatternTools - toValidDateFormat - WARNING: date.length > 8.");
				//System.out.println("Removing needless characters.");

				date = date.substring(0, EIGHT);
			
			} else if (date.length() == FOUR) {
				
				System.out.println("PatternTools - toValidDateFormat - WARNING: date not complete.");
				//append 0101 if date not complete
				//TODO append 1231 in some cases?
				date = date + "0101";
			
			} else if (date.length() == SIX) {
				
				System.out.println("PatternTools - toValidDateFormat - WARNING: date not complete.");
				//append 01 if date not complete
				//TODO append 31 in some cases?
				date = date + "01";
			
			}
			
			

			if (index) {

				return date;

			}

		} else {

			dateHasHyphens = true;

			//System.out.println("PatternTools - toValidDateFormat: Date has hyphens.");	

			if (date.length() > TEN) {

				//System.out.println("PatternTools - toValidDateFormat - WARNING: date.length > 10.");
				//System.out.println("Removing needless characters.");

				date = date.substring(0, TEN);
			}

		}

		if (dateHasHyphens) {

			StringTokenizer stringTokenizer = new StringTokenizer(date, HYPHEN);

			year = stringTokenizer.nextToken();

			if (year.length() != FOUR) {
				System.err.println("PatternTools toValidDateFormat year: " +
						 year);
			}

			month = stringTokenizer.nextToken();

			if (month.length() != TWO) {
				System.err.println("PatternTools toValidDateFormat month: " +
						 month);

			}

			day = stringTokenizer.nextToken();
			
			
			if (day.length() != TWO) {
				System.err.println("PatternTools toValidDateFormat day: " +
						 day);

			}
			

		} else { // no hyphens in date

			year = date.substring(0, FOUR);

			month = date.substring(FOUR, SIX);

			day = date.substring(SIX, EIGHT);

		}

		if (day.length() != 2) {

			if (day.length() > 2) {
			
				day = day.substring(0, 2);
			
			} else {
				
				System.err.println("PatternTools toValidDateFormat day: " + day);
			
			}

		}
		//nur fuer Lucene
		if (index) {

			validDate = year + month + day;

		} else {

			validDate = year + HYPHEN + month + HYPHEN + day;

		}

		/*
		 System.out.println("PatternTools toValidDateFormat date: " + date);
		 System.out.println("PatternTools toValidDateFormat validDate: " + validDate);
		 */

		return validDate;
	}

//	/**
//	 * @param cooWithoutComma
//	 * @return
//	 * @throws Exception
//	 */
//	private static String handleCoordWithoutComma(final String cooWithoutComma)
//			throws Exception {
//
//		String coordWithoutComma = cooWithoutComma;
//		
//		if (coordWithoutComma.length() == 1) {
//			//eine Null vorne anhaengen
//			coordWithoutComma = ZEROSTR + coordWithoutComma;
//			//zwei Nullen hinten anhaengen
//			coordWithoutComma = coordWithoutComma + ZEROSTR + ZEROSTR;
//		} else if (coordWithoutComma.length() == 2) {
//			//nur zwei Nullen hinten anhaengen
//			coordWithoutComma = coordWithoutComma + ZEROSTR + ZEROSTR;
//		} else {
//
//			throw new CSWInvalidParameterValueException(
//					"Value of coordinate is not valid: " + coordWithoutComma,
//					"gml:coordinates");
//		}
//
//		return coordWithoutComma;
//
//	}

//	private static String handleCoordBeforeComma(String coordBeforeComma)
//			throws Exception {
//
//		if (coordBeforeComma.length() == 1) {
//			//eine Null vorne anhaengen
//			coordBeforeComma = ZEROSTR + coordBeforeComma;
//		} else if (coordBeforeComma.length() != 2) {
//
//			throw new CSWInvalidParameterValueException(
//					"Value of coordinate is not valid: " + coordBeforeComma,
//					"gml:coordinates");
//		}
//
//		return coordBeforeComma;
//
//	}

//	private static String handleCoordAfterComma(String coordAfterComma) {
//
//		if (coordAfterComma.length() == 1) {
//			//eine Null anhaengen
//			coordAfterComma = coordAfterComma + ZEROSTR;
//		} else {
//			//nur zwei Ziffern vorne lassen, Rest abschneiden	
//			coordAfterComma = coordAfterComma.substring(0, 2);
//		}
//
//		return coordAfterComma;
//
//	}

//	/**
//	 * This method transforms geographic coordinates into 
//	 * a format for the lucene index (String --> String)
//	 * @param coord String
//	 * @return validCoord String
//	 * @throws Exception e
//	 */
//	public static String toLuceneIndexCoordFormat(final String coord)
//			throws Exception {
//
//		if (coord == null) { 
//			
//			return "";
//		}
//
//		try {
//			
//			return NumericField.numberToString(Double.valueOf(coord.trim()));
//		
//		} catch (NumberFormatException e) {
//		
//			return "";
//		}
//	}

	/**
	 * This method tests if field is a coordinate field
	 * @param field String
	 * @return isCoordField boolean
	 */
	public static boolean isCoordField(final String field) {

		boolean isCoordField = false;

		if (field.equals("x1") || field.equals("x2") || field.equals("y1") ||
				 field.equals("y2")) {

			isCoordField = true;

		}

		return isCoordField;
	}

	/**
	 * This method tests if field is a date field
	 * @param field String
	 * @return isDateField boolean
	 */
	public static boolean isDateField(final String field) {

		boolean isDateField = false;

		if (field.equals("creationDate") || field.equals("modified") ||
				 field.equals("t1") || field.equals("t2")) {

			isDateField = true;

		}

		return isDateField;
	}

}