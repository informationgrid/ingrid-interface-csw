/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Filters a string with the injected analyzer. This class is used in
 * javascript based mapping of a IDF record to a lucene document.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class LuceneTools {

    /**
     * @param term
     * @return filtered term
     * @throws IOException
     */
    public String filterTerm(String term) throws IOException {
        String result = "";

        Analyzer myAnalyzer = getAnalyzer();
        TokenStream ts = myAnalyzer.tokenStream(null, new StringReader(term));
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

        while (ts.incrementToken()) {
            String t = charTermAttribute.toString();
            result = result + " " + t;
        }
        return result.trim();
    }

	/** METHOD WILL BE INJECTED BY SPRING TO RETURN NEW INSTANCE OF INJECTED ANALYZER !
	 * In non spring environment we return new default analyzer (German).
	 */
	public Analyzer getAnalyzer() {
		return new GermanAnalyzer(Version.LUCENE_36, new HashSet());
	}
}
