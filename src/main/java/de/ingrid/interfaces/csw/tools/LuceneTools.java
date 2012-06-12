/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.ClassicAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Filters a string with the {@link ClassicAnalyzer}. This class is used in
 * javascript based mapping of a IDF record to a lucene document.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class LuceneTools {

    private static ClassicAnalyzer fAnalyzer = new ClassicAnalyzer(Version.LUCENE_36);

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
