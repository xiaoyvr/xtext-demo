package com.tw.demo.interpreter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.tw.demo.hKDsl.DeclaredParameter;
import com.tw.demo.hKDsl.Definition;
import com.tw.demo.hKDsl.Div;
import com.tw.demo.hKDsl.EQ;
import com.tw.demo.hKDsl.Expression;
import com.tw.demo.hKDsl.FunctionCall;
import com.tw.demo.hKDsl.GE;
import com.tw.demo.hKDsl.GT;
import com.tw.demo.hKDsl.IfStatement;
import com.tw.demo.hKDsl.LE;
import com.tw.demo.hKDsl.LT;
import com.tw.demo.hKDsl.Minus;
import com.tw.demo.hKDsl.Multi;
import com.tw.demo.hKDsl.NumberLiteral;
import com.tw.demo.hKDsl.Param;
import com.tw.demo.hKDsl.Plus;

public class Calculator {
	private PolymorphicDispatcher<Object> dispatcher = PolymorphicDispatcher.createForSingleTarget("internalEvaluate", 2, 2, this);
	public Object evaluate(Expression expr) {
		return evaluate(expr, ImmutableMap.<String,Object>of());
	}
	
	private Object evaluate(Expression expr, ImmutableMap<String,Object> values) {
		return dispatcher.invoke(expr, values);
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
	
	protected Object internalEvaluate(FunctionCall fc, ImmutableMap<String,Object> values) {
		Definition def = fc.getFunc();
		Map<String,Object> params = Maps.newHashMap();
		for (int i=0; i<fc.getArgs().size();i++) {
			DeclaredParameter declaredParameter = def.getArgs().get(i);
			Object evaluate = evaluate(fc.getArgs().get(i), values);
			params.put(declaredParameter.getName(), evaluate);
		}
		return evaluate(def.getExpr(),ImmutableMap.copyOf(params));
	}
	
	protected Object internalEvaluate(Param param, ImmutableMap<String,Object> values) {
		return values.get(param.getParam().getName());
	}
	
	private BigDecimal evaluateAsBigDecimal(Expression obj, ImmutableMap<String,Object> values) {
		BigDecimal invoke = (BigDecimal)dispatcher.invoke(obj, values);
		return invoke;
	}
	
	protected boolean internalEvaluate(GT greaterThan, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(greaterThan.getLeft(),values).compareTo(evaluateAsBigDecimal(greaterThan.getRight(),values)) > 0;		
	}
	
	protected boolean internalEvaluate(LT lessThan, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(lessThan.getLeft(),values).compareTo(evaluateAsBigDecimal(lessThan.getRight(),values)) < 0;		
	}
	
	protected boolean internalEvaluate(EQ equal, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(equal.getLeft(),values).compareTo(evaluateAsBigDecimal(equal.getRight(),values)) == 0;		
	}
	protected boolean internalEvaluate(GE greaterOrEqual, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(greaterOrEqual.getLeft(),values).compareTo(evaluateAsBigDecimal(greaterOrEqual.getRight(),values)) >= 0;
	}
	protected boolean internalEvaluate(LE lessOrEqual, ImmutableMap<String,Object> values) {
		return evaluateAsBigDecimal(lessOrEqual.getLeft(),values).compareTo(evaluateAsBigDecimal(lessOrEqual.getRight(),values)) >= 0;
	}
	protected BigDecimal internalEvaluate(IfStatement ifStatement, ImmutableMap<String,Object> values) {
		boolean cond = evaluateAsBoolean(ifStatement.getCond(), values);
		return cond? 
				evaluateAsBigDecimal(ifStatement.getTruePart(),values) :
				evaluateAsBigDecimal(ifStatement.getFalsePart(),values);
	}
	
	private boolean evaluateAsBoolean(Expression obj, ImmutableMap<String,Object> values) {
		boolean invoke = (Boolean)dispatcher.invoke(obj, values);
		return invoke;
	}

}
