/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.filter.ogc1_1_0.BinaryComparisonOpType;
import de.ingrid.interfaces.csw.domain.filter.ogc1_1_0.ComparisonOpsType;
import de.ingrid.interfaces.csw.domain.filter.ogc1_1_0.FilterType;
import de.ingrid.interfaces.csw.domain.filter.ogc1_1_0.LiteralType;
import de.ingrid.interfaces.csw.domain.filter.ogc1_1_0.PropertyNameType;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class FilterTest extends TestCase {

	public static final String FILTER = "src/test/resources/filter.xml";

	/**
	 * Test filter parsing
	 * @throws Exception
	 */
	public void testParseFilter() throws Exception {
		File requestFile = new File(FILTER);
		String filterStr = new Scanner(requestFile).useDelimiter("\\A").next();
		Document filterDoc = StringUtils.stringToDocument(filterStr);

		final JAXBContext context = JAXBContext.newInstance(FilterType.class);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final JAXBElement<FilterType> filterEl = unmarshaller.unmarshal(filterDoc, FilterType.class);
		FilterType filter = filterEl.getValue();

		ComparisonOpsType compOps = filter.getComparisonOps().getValue();
		assertEquals(BinaryComparisonOpType.class, compOps.getClass());
		assertEquals("PropertyIsEqualTo", filter.getComparisonOps().getName().getLocalPart());

		BinaryComparisonOpType binComp = (BinaryComparisonOpType)compOps;

		JAXBElement<?> propertyNameEl = this.getChild(binComp.getExpression(), "PropertyName");
		PropertyNameType propertyName = (PropertyNameType)propertyNameEl.getValue();
		String propertyNameValue = propertyName.getContent().get(0).toString();
		assertEquals("Title", propertyNameValue);

		JAXBElement<?> literalEl = this.getChild(binComp.getExpression(), "Literal");
		LiteralType literal = (LiteralType)literalEl.getValue();
		String literalValue = literal.getContent().get(0).toString();
		assertEquals("Wasser", literalValue);
	}

	private JAXBElement<?> getChild(List<JAXBElement<?>> children, String name) {
		JAXBElement<?> result = null;
		for (JAXBElement<?> child : children) {
			if (child.getName().getLocalPart().equals(name)) {
				result = child;
				break;
			}
		}
		return result;
	}
}
