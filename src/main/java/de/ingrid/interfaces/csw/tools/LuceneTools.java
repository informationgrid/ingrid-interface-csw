/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;

/**
 * @author joachim
 *
 */
public class LuceneTools {

    private static GermanAnalyzer fAnalyzer = new GermanAnalyzer(new String[0]);
    
    
    /**
     * @param term
     * @return filtered term
     * @throws IOException
     */
    public static String filterTerm(String term) throws IOException {
        String result = "";

        TokenStream ts = fAnalyzer.tokenStream(null, new StringReader(term));
        Token token = ts.next();
        while (null != token) {
            result = result + " " + token.termText();
            token = ts.next();
        }

        return result.trim();
    }
    
    
}
