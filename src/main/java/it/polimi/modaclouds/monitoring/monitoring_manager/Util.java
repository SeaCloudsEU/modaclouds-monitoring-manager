/**
 * Copyright 2014 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.monitoring.monitoring_manager;

import it.polimi.csparqool.FunctionArgs;
import it.polimi.modaclouds.monitoring.dcfactory.DCConfig;
import it.polimi.modaclouds.qos_models.schema.Action;
import it.polimi.modaclouds.qos_models.schema.MonitoredTarget;
import it.polimi.modaclouds.qos_models.schema.MonitoringMetricAggregation;
import it.polimi.modaclouds.qos_models.schema.MonitoringRule;

import java.util.List;

public class Util {

	public static String getOutputValueVariable(MonitoringRule rule) {
		if (!isAggregatedMetric(rule))
			return QueryVars.INPUT;
		return QueryVars.OUTPUT;
	}

	public static String getOutputResourceIdVariable(MonitoringRule rule)
			throws RuleInstallationException {
		String targetVar;
		if (isAggregatedMetric(rule)
				&& !getTargetClass(rule).equals(getGroupingClass(rule))) {
			String groupingClassVariable = getGroupingClassVariable(rule);
			if (groupingClassVariable==null) return null;
			targetVar = groupingClassVariable + "Id";
		} else {
			targetVar = QueryVars.RESOURCE_ID;
		}
		return targetVar;
	}

	public static String getAggregateFunction(MonitoringRule rule) {
		if (!isAggregatedMetric(rule))
			return null;
		String aggregateFunction = rule.getMetricAggregation()
				.getAggregateFunction();
		return aggregateFunction;
	}

	public static String getGroupingClassVariable(MonitoringRule rule)
			throws RuleInstallationException {
		if (!isGroupedMetric(rule))
			return null;
		String groupingClass = getGroupingClass(rule);
		String targetClass = getTargetClass(rule);
		if (groupingClass==null) {
			return null;
		}
		if (groupingClass.equals(targetClass)) {
			return QueryVars.RESOURCE;
		}
		return "?" + groupingClass;
	}

	public static String getTargetClass(MonitoringRule rule)
			throws RuleInstallationException {
		List<MonitoredTarget> targets = getMonitoredTargets(rule);
		String targetClass = null;
		for (MonitoredTarget t : targets) {
			if (targetClass != null) {
				if (!targetClass.equals(t.getClazz()))
					throw new RuleInstallationException(
							"Monitored targets must belong to the same class");
			} else
				targetClass = t.getClazz();
		}
		return targetClass;
	}

	public static String getGroupingClass(MonitoringRule rule) {
		if (isAggregatedMetric(rule))
			return rule.getMetricAggregation().getGroupingClass();
		else
			return null;
	}

	public static boolean isAggregatedMetric(MonitoringRule rule) {
		return rule.getMetricAggregation() != null;
	}
	
	public static boolean isGroupedMetric(MonitoringRule rule) {
		return getGroupingClass(rule)!=null;
	}

	public static List<MonitoredTarget> getMonitoredTargets(MonitoringRule rule) {
		List<MonitoredTarget> targets = rule.getMonitoredTargets()
				.getMonitoredTargets();
		return targets;
	}

	public static String getParameterValue(String parameterName,
			MonitoringMetricAggregation metricAggregation) {
		for (it.polimi.modaclouds.qos_models.schema.Parameter par : metricAggregation
				.getParameters()) {
			if (par.getName().equals(parameterName))
				return par.getValue();
		}
		return null;
	}

	public static String getParameterValue(String parameterName, Action action) {
		for (it.polimi.modaclouds.qos_models.schema.Parameter par : action
				.getParameters()) {
			if (par.getName().equals(parameterName))
				return par.getValue();
		}
		return null;
	}

	public static String[] getAggregateFunctionArgs(MonitoringRule rule) {
		String aggregateFunction = Util.getAggregateFunction(rule);
		String[] args = new String[FunctionArgs
				.getNumberOfArgs(aggregateFunction)];
		args[FunctionArgs.getArgIdx(aggregateFunction,
				FunctionArgs.INPUT_VARIABLE)] = QueryVars.INPUT;
		if (rule.getMetricAggregation().getParameters() != null) {
			List<it.polimi.modaclouds.qos_models.schema.Parameter> rulePars = rule
					.getMetricAggregation().getParameters();
			for (it.polimi.modaclouds.qos_models.schema.Parameter p : rulePars) {
				int index = FunctionArgs.getArgIdx(aggregateFunction,
						p.getName());
				args[index] = p.getValue().toString();
			}
		}
		return args;
	}

	public static void addParameters(DCConfig dc,
			List<it.polimi.modaclouds.qos_models.schema.Parameter> parameters) {
		for (it.polimi.modaclouds.qos_models.schema.Parameter p : parameters) {
			dc.addParameter(p.getName(), p.getValue());
		}

	}

	public static boolean softEquals(String name1, String name2) {
		return name1.toLowerCase().equals(name2.toLowerCase());
	}

	public static String getGroupingClassIdVariable(MonitoringRule rule)
			throws RuleInstallationException {
		return getGroupingClassVariable(rule) + "Id";
	}

	public static String getOutputTimestampVariable(MonitoringRule rule) {
		if (isAggregatedMetric(rule)) return QueryVars.TIMESTAMP;
		return QueryVars.INPUT_TIMESTAMP;
	}

	

}
