/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Filters a string with the {@link GermanAnalyzer}. This class is used in
 * javascript based mapping of a IDF record to a lucene document.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class LuceneTools {

	// use German Analyzer, see INGRID-2246
	private static GermanAnalyzer fAnalyzer = new GermanAnalyzer(Version.LUCENE_36, new HashSet());
//    private static ClassicAnalyzer fAnalyzer = new ClassicAnalyzer(Version.LUCENE_36);

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

}
