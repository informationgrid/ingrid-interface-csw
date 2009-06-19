/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import java.util.Iterator;

import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.opengis.filter.And;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

public class IngridFilterVisitor extends DefaultFilterVisitor {

    public Object visit(And filter, Object data) {
    	return visit((BinaryLogicOperator)filter, data);
    }
    public Object visit(Or filter, Object data) {
    	return visit((BinaryLogicOperator)filter, data);
    }

    public Object visit(PropertyIsEqualTo filter, Object data) {
    	if (data instanceof FilterVisitorContext) {
    		FilterVisitorContext ctx = (FilterVisitorContext)data;
			
			// store the property name for later reference
    		Expression exp1 = filter.getExpression1();
    		if (exp1 instanceof PropertyName)
    			ctx.setProperty(FilterProperty.CURRENT_PROPERTY_NAME, ((PropertyName)exp1).getPropertyName());
    	}
    	data = super.visit(filter, data);
    	return data;
    }

    public Object visit(Literal expression, Object data) {
    	if (data instanceof FilterVisitorContext) {
    		FilterVisitorContext ctx = (FilterVisitorContext)data;
    		Object value = expression.getValue();
    		
    		// convert the value according to the current property
    		String curPropertyName = (String)ctx.getProperty(FilterProperty.CURRENT_PROPERTY_NAME);
    		if (curPropertyName != null) {
	    		value = PropertyValueConverter.convert(
	    				ctx.getProperty(FilterProperty.CURRENT_PROPERTY_NAME).toString(), expression.getValue());
    		}
    		ctx.appendQueryString(value.toString());
    	}
        return data;
    }

    public Object visit(PropertyName expression, Object data) {
    	if (data instanceof FilterVisitorContext) {
    		FilterVisitorContext ctx = (FilterVisitorContext)data;
    		
    		// map the property name to an InGrid property
    		String propertyName = null;
			try {
				propertyName = PropertyNameMapping.map(expression.getPropertyName(), ctx);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (propertyName != null && propertyName.length() > 0)
				ctx.appendQueryString(propertyName+":");
    	}
    	return data;
    }
    
    protected Object visit(BinaryLogicOperator op, Object data) {
    	String opStr = null;
		if (op instanceof And)
    		opStr = " AND ";
    	if (op instanceof Or)
    		opStr = " OR ";
    	
    	if (data instanceof FilterVisitorContext) {
    		FilterVisitorContext query = (FilterVisitorContext)data;
    		query.appendQueryString("(");
	    	if (op.getChildren() != null) {
	            Iterator<Filter> iterator = op.getChildren().iterator();
	            while (iterator.hasNext()) {
	            	iterator.next().accept(this, query);
	
	                if (iterator.hasNext()) {
	                	query.appendQueryString(opStr);
	                }
	            }
	        }
	    	query.appendQueryString(")");
    	}
        return data;
    }
}
