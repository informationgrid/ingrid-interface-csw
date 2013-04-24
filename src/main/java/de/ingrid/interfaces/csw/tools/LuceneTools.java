/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Filters a string with the injected analyzer. This class is used in
 * javascript based mapping of a IDF record to a lucene document.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
@Service
public class LuceneTools {

    private static Analyzer fAnalyzer;

    /**
     * @param term
     * @return filtered term
     * @throws IOException
     */
    public static String filterTerm(String term) throws IOException {
        String result = "";

        TokenStream ts = fAnalyzer.tokenStream(null, new StringReader(term));
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

        while (ts.incrementToken()) {
            String t = charTermAttribute.toString();
            result = result + " " + t;
        }
        return result.trim();
    }

    /** Injects default analyzer via autowiring ! */
    @Autowired
    public void setAnalyzer(Analyzer analyzer) {
    	fAnalyzer = analyzer;
	}
}
