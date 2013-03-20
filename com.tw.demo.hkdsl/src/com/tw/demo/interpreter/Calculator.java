package com.tw.demo.interpreter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.common.collect.ImmutableMap;
import com.tw.demo.hKDsl.Div;
import com.tw.demo.hKDsl.Expression;
import com.tw.demo.hKDsl.Minus;
import com.tw.demo.hKDsl.Multi;
import com.tw.demo.hKDsl.NumberLiteral;
import com.tw.demo.hKDsl.Plus;

public class Calculator {
	private PolymorphicDispatcher<Object> dispatcher = PolymorphicDispatcher.createForSingleTarget("internalEvaluate", 2, 2, this);
	public Object evaluate(Expression expr) {
		return dispatcher.invoke(expr, ImmutableMap.<String,Object>of());
	}
	
	protected BigDecimal internalEvaluate(Expression e, ImmutableMap<String,Object> values) { 
		throw new UnsupportedOperationException(e.toString());
	}
	
	protected BigDecimal internalEvaluate(NumberLiteral e, ImmutableMap<String,Object> values) { 
		return e.getValue();
	}
	
	protected BigDecimal internalEvaluate(Plus plus, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(plus.getLeft(),values).add(evaluateAsBigDecimal(plus.getRight(),values));
	}
		
	protected BigDecimal internalEvaluate(Minus minus, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(minus.getLeft(),values).subtract(evaluateAsBigDecimal(minus.getRight(),values));
	}
	protected BigDecimal internalEvaluate(Div div, ImmutableMap<String,Object> values) {
		BigDecimal left = evaluateAsBigDecimal(div.getLeft(),values);
		BigDecimal right = evaluateAsBigDecimal(div.getRight(),values);
		return left.divide(right,20,RoundingMode.HALF_UP);
	}
	protected BigDecimal internalEvaluate(Multi multi, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(multi.getLeft(),values).multiply(evaluateAsBigDecimal(multi.getRight(),values));
	}
	
	private BigDecimal evaluateAsBigDecimal(Expression obj, ImmutableMap<String,Object> values) {
		BigDecimal invoke = (BigDecimal)dispatcher.invoke(obj, values);
		return invoke;
	}
	
}
