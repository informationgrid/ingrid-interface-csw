/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
	
	private Analyzer fAnalyzer = null;

    /**
     * @param term
     * @return filtered term
     * @throws IOException
     */
    public String filterTerm(String term) throws IOException {
        String result = "";

        // always use same analyzer, NOT new instance ! Is called in mapping process !
        Analyzer myAnalyzer = getAnalyzer();
        TokenStream ts = myAnalyzer.tokenStream(null, new StringReader(term));
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

        while (ts.incrementToken()) {
            String t = charTermAttribute.toString();
            result = result + " " + t;
        }
        return result.trim();
    }

	/** METHOD WILL BE INJECTED BY SPRING TO RETURN NEW INSTANCE OF ANALYZER !
	 * In non spring environment we return new default analyzer (German).
	 */
	public Analyzer createAnalyzer() {
		return new GermanAnalyzer(Version.LUCENE_36, new HashSet());
	}

	/** Return existing analyzer. If not created yet, then create one ! */
	public Analyzer getAnalyzer() {
		if (fAnalyzer == null) {
			fAnalyzer = createAnalyzer();
		}
		
		return fAnalyzer;
	}
}
