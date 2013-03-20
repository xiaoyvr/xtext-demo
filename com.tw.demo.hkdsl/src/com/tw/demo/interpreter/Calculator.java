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
import com.tw.demo.hKDsl.Expression;
import com.tw.demo.hKDsl.FunctionCall;
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
}
