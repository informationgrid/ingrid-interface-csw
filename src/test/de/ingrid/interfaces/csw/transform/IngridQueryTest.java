/*
 * Created on 04.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;

import java.io.StringReader;

import de.ingrid.interfaces.csw.transform.IngridQueryToString;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

import junit.framework.TestCase;



/**
 * @author rschaefer
 *
 */
public class IngridQueryTest extends TestCase {
    
    
    public void testIngridQuery() throws Exception {
        
        IngridQuery query = null;
        
//        query = new IngridQuery();
//        
//        query.addTerm(new TermQuery(0, "weidenbach"));
//        
//        query.addTerm(new TermQuery(0, "karte"));
        
        //query = parse("ort:weidenbach OR karte");
        
        //query = parse("abstract:arte* OR title:Test");
        
        //query = parse("abstract:arte OR title:Test");
       
        //query = parse("ort:Halle OR land:germany");
       
         //query = parse("fische ort:halle NOT (saale OR Hufeisensee)");
        
         //query = parse("ort:Halle AND t0:1990");
         
        //TODO should 'AND NOT'/'OR NOT' be possible?
         //query = parse("ort:Halle AND NOT t0:1990");
        //query = parse("ort:Halle OR NOT t0:1990");
        
         //query = parse("ort:Halle NOT t0:1990");
        
         //query = parse(" (NOT anyText:hufeisensee AND anyText:fische AND area:halle)");
         
         // query = parse("(NOT (anyText:saale OR anyText:hufeisensee) AND anyText:fische AND area:halle)");
        
     
          //query = parse("(anyText:blabla OR ( NOT (anyText:saale OR anyText:hufeisensee) AND anyText:fische))");
          
         // query = parse("(anyText:blabla OR (anyText:kraken AND area:darmstadt  NOT (anyText:saale OR anyText:hufeisensee)))");
          
          
          //query = parse("ort:Halle NOT t0:1990");
          
          //query = parse("ort:Halle !t0:1990");
          
          
          //query.addField(new FieldQuery(IngridQuery.AND, "source", "map"));
     
          
         //query = parse("NOT (t0:1990 OR t0:2000)");
        
         query = parse("t0:1990 OR (t0:2000 AND title:boden)");
        
         //query = parse("NOT t0:1990");
        
         //query = parse("ort:Hal* ort:Darmstadt");
         
         //query = parse("((denominator:>50 AND denominator:<100) OR (denominator:>=10 AND denominator:<=15))");
        
        //query = parse("fische bla bo nkl hjkjk izi ort:Halle land:BRD (zeit:1970 OR aktuell:monatlich) ");
        
        // query = parse("(zeit:1970 OR aktuell:täglich)  fische OR ort:Halle land:BRD ");
        
        //TODO meaning of square brackets?
         //query = parse("west:[ ]");
        
        //query = parse("x1:-31.0");
        
        //TODO negative coordinates (integers)   
         //query = parse("x1:-31");
         
        
        
         //query = parse("(FIELD1:10 OR FIELD1:20) AND (STATUS:VALID)");
        
      
//        query = new IngridQuery();
//        
//         query.addTerm(new TermQuery(IngridQuery.AND, "fische"));
//
//         //query.addTerm(new TermQuery(IngridQuery.AND, "seegurke"));
//         
//         query.addField(new FieldQuery(IngridQuery.AND, "ort", "halle"));
//         
//         //query.addField(new FieldQuery(IngridQuery.AND, "land:BRD"));
//         
//         
//         ClauseQuery clauseQuery = new ClauseQuery(IngridQuery.NOT);
//         
//         //query.addClause(clauseQuery);
//         
//         clauseQuery.addTerm(new TermQuery(IngridQuery.OR, "saale"));
//         
//         clauseQuery.addTerm(new TermQuery(IngridQuery.OR, "hufeisensee"));
//         
//         //clauseQuery.addField(new FieldQuery(IngridQuery.AND, "ort:FFM"));
//         
//         query.addClause(clauseQuery);
//         
       
        
//        String strDesc = query.getDescription();
//        
//        System.out.println("query strDesc: " + strDesc);
        
        
        IngridQueryToString ingridQueryToString = new IngridQueryToString();
        
        //String strExp = query.toLogExp();
        
        //System.out.println("query strExp: " + strExp);
       
        
        System.out.println("query ingridQueryToString: " + ingridQueryToString.transform(query));
        
       // System.out.println("query ingridQuery as string: " + query.toString());
        
        
//        assertEquals("query contains no fields.", 1, query.getFields().length);
//        
//        assertEquals("The expected field 'x1:-31.0' does not exist.", "x1:-31.0", query.getFields()[0].toString());
        
    }

    
    /**
     * 
     * @param q
     * @return The parsed {@link IngridQuery}
     * @throws ParseException
     */
    private IngridQuery parse(String q) throws ParseException {
        QueryStringParser parser = new QueryStringParser(new StringReader(q));
        IngridQuery query = parser.parse();
        assertNotNull(query);

        return query;
    }
    
}
