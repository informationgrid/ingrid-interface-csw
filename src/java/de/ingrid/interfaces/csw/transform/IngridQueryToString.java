/*
 * Created on 10.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParserConstants;

/**
 * transforms an IngridQuery into a query string
 * @author rschaefer
 */
public class IngridQueryToString {
    
    
    /**
     * does the transforming of an InGrid Query into a string
     * except for datatypes (anything else?)
     * @param ingridQuery IngridQuery
     * @return queryString String
     */
    public final String transform(final IngridQuery ingridQuery) {
        
        String queryString = null;
        
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        //buffer.append(" terms: ");
        appendToString(buffer, ingridQuery.getTerms());
        //buffer.append(" fields: ");
        appendToString(buffer, ingridQuery.getFields());
       // buffer.append(" wildcards (terms): ");
        appendToString(buffer, ingridQuery.getWildCardTermQueries());
       //buffer.append(" wildcards (fields): ");
        appendToString(buffer, ingridQuery.getWildCardFieldQueries());
        //buffer.append(" ranges: ");
        appendToString(buffer, ingridQuery.getRangeQueries());
        
        //buffer.append(" clauses: ");
        IngridQuery[] clauses = ingridQuery.getClauses();
        for (int i = 0; i < clauses.length; i++) {
           
            buffer.append(getOpString(clauses[i]) + " ");
            
           // buffer.append(clauses[i].getDescription());
            buffer.append(transform(clauses[i]));

        }
        buffer.append(")");
        
        queryString = buffer.toString();
        
        
       
        return  queryString;
    }
    
    
    /**
     * appends all ingrid queries / terms to a Stringbuffer
     * @param buffer StringBuffer
     * @param terms IngridQuery[]
     */
    private void appendToString(final StringBuffer buffer, final IngridQuery[] terms) {
        
        for (int i = 0; i < terms.length; i++) {
            
           String strOp = getOpString(terms[i]);
           
           buffer.append(" " + strOp + " ");
            
            
            buffer.append(terms[i].getContent());
            buffer.append(" ");
        }
    }

    
//    private String getOpString(final int operation) {
//        
//        String strOp = null;
//        
//        switch (operation) {
//        
//         case -1: strOp = "NOT";
//                 break;
//         case 0: strOp = "AND";
//                 break;
//         case 1: strOp = "OR";
//                 break;
//         default : strOp = "AND";
//        
//        } 
//        
//        System.out.println("IngridQueryToString operation: " + operation);
//        
//        return strOp;
//    }
    

 /**
  * returns the operator of the term as a string
 * @param opTerm IngridQuery
 * @return strOp String
 */
private String getOpString(final IngridQuery opTerm) {
        
        String strOp = null;
        
        if (opTerm.isProhibited()) {
        	
        	strOp = "NOT";
        	
        } else if (opTerm.isRequred()) {
        	
        	strOp = "AND";
        	
        } else {
        	
        	strOp = "OR";
        }
        
        
        
        
        return strOp;
    }
    
    
}
