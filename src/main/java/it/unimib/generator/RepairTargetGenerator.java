package it.unimib.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

import it.unimib.failure_locator.exception_type.ArrayIndexOutOfBoundsExceptionFailureDrivenLocator;
import it.unimib.failure_locator.exception_type.FailureDrivenLocator;
import it.unimib.failure_locator.exception_type.IllegalArgumentExceptionFailureDrivenLocator;
import it.unimib.failure_locator.exception_type.NullPointerExceptionFailureDrivenLocator;
import it.unimib.failure_locator.exception_type.StringIndexOutOfBoundsExceptionFailureDrivenLocator;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;
import it.unimib.model.SuspiciousLocation;
import it.unimib.model.SuspiciousLocation.LocationType;
import it.unimib.model.RepairTarget.GuessedFault;

import static it.unimib.util.Constants.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION;
import static it.unimib.util.Constants.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION;
import static it.unimib.util.Constants.NULL_POINTER_EXCEPTION;
import static it.unimib.util.Constants.ILLEGAL_ARGUMENT_EXCEPTION;
import static it.unimib.util.Constants.MAX_SUSPICIOUSNESS_SCORE;
import static it.unimib.util.Constants.SUSPICIOUSNESS_DECREASING_FACTOR;

public class RepairTargetGenerator {
	
	public static Map<FailureInfo, List<RepairTarget>> getRepairTargets(List<FailureInfo> failuresInformationList, String programSourceCodePath) {
		Map<FailureInfo, List<RepairTarget>> repairTargetForFailureMap = new HashMap<>();

		for (FailureInfo failureInfo : failuresInformationList) {
			List<RepairTarget> repairTargetList = generateRepairTargets(failureInfo, programSourceCodePath);

			if (repairTargetList != null) {
				RepairTargetGenerator.resetMaxSuspiciousnessScore();
				repairTargetList.forEach(System.out::println);
				repairTargetForFailureMap.put(failureInfo, repairTargetList);
			}
		}

		return repairTargetForFailureMap;
	}
	
	private static List<RepairTarget> generateRepairTargets(FailureInfo failureInfo, String programSourceCodePath) {
		
		FailureDrivenLocator failureDrivenLocator = null;
		List<SuspiciousLocation> suspiciousLocationList = null;
		List<RepairTarget> repairTargetList = null;
		
		if (failureInfo.getExceptionType().equals(ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION)) {
			failureDrivenLocator = new ArrayIndexOutOfBoundsExceptionFailureDrivenLocator(programSourceCodePath);
			suspiciousLocationList = failureDrivenLocator.getSuspiciousLocations(failureInfo);
		} else if (failureInfo.getExceptionType().equals(STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION)) {
			failureDrivenLocator = new StringIndexOutOfBoundsExceptionFailureDrivenLocator(programSourceCodePath);
			suspiciousLocationList = failureDrivenLocator.getSuspiciousLocations(failureInfo);
		} else if (failureInfo.getExceptionType().equals(NULL_POINTER_EXCEPTION)) {
			failureDrivenLocator = new NullPointerExceptionFailureDrivenLocator(programSourceCodePath);
			suspiciousLocationList = failureDrivenLocator.getSuspiciousLocations(failureInfo);
		} else if (failureInfo.getExceptionType().equals(ILLEGAL_ARGUMENT_EXCEPTION)) {
			failureDrivenLocator = new IllegalArgumentExceptionFailureDrivenLocator(programSourceCodePath);
			suspiciousLocationList = failureDrivenLocator.getSuspiciousLocations(failureInfo);
		} else {
			return null;
		}
		
		if (suspiciousLocationList != null) {
			repairTargetList = new ArrayList<RepairTarget>();
    		
    		for (SuspiciousLocation suspiciousLocation : suspiciousLocationList) {
        		repairTargetList.add(new RepairTarget(
    				suspiciousLocation, 
    				formulateHypothesis(suspiciousLocation.getLocationType()),
    				0
        		));
        	}
    	}
		
		Map<Integer, Double> suspMapping = new HashMap<Integer, Double>();
		
		if (repairTargetList == null) {
			return null;
		}
		
		repairTargetList.forEach((e) -> {
			if (!suspMapping.containsKey(e.getSuspiciousLocation().getLineNumber())) {
				suspMapping.put(e.getSuspiciousLocation().getLineNumber(), MAX_SUSPICIOUSNESS_SCORE);
				MAX_SUSPICIOUSNESS_SCORE = MAX_SUSPICIOUSNESS_SCORE - SUSPICIOUSNESS_DECREASING_FACTOR;
				MAX_SUSPICIOUSNESS_SCORE = Math.floor(MAX_SUSPICIOUSNESS_SCORE * 100) / 100;
			}
		});
		
		for (int i = 0; i < repairTargetList.size(); i++) {
			repairTargetList.get(i).setSuspiciousnessScore(suspMapping.get(repairTargetList.get(i).getSuspiciousLocation().getLineNumber()));
		}

		repairTargetList.sort(Comparator.comparingDouble(RepairTarget::getSuspiciousnessScore).reversed());

		return repairTargetList;
	}
	
	private static GuessedFault formulateHypothesis(LocationType locationType) {
		
		switch(locationType) {
			case ARRAY_VARIABLE:
				return GuessedFault.ARRAY_VARIABLE_IS_WRONG;
			case ARRAY_INDEX:
				return GuessedFault.ARRAY_INDEX_IS_WRONG;
			case INDEX_USED_FOR_ARRAY_INITIALIZATION:
				return GuessedFault.ARRAY_INDEX_IS_WRONG;
			case ARRAY_INDEX_VARIABLE_ASSIGNMENT:
				return GuessedFault.ARRAY_INDEX_INITIALIZATION_IS_WRONG;
			case ARRAY_INITIALIZATION:
				return GuessedFault.ARRAY_INITIALIZATION_IS_WRONG;
			case ARRAY_INITIALIZATION_ASSIGNMENT:
				return GuessedFault.ARRAY_INITIALIZATION_IS_WRONG;
			case BEFORE_ARRAY_ACCESS:
				return GuessedFault.MISSING_CONDITION;
			case VARIABLE_ACCESS:
				return GuessedFault.VARIABLE_IS_WRONG;
			case BEFORE_VARIABLE_ACCESS:
				return GuessedFault.MISSING_CONDITION;
			case VARIABLE_DECLARATION:
				return GuessedFault.VARIABLE_INITIALIZATION_IS_WRONG;
			case VARIABLE_ASSIGNMENT:
				return GuessedFault.VARIABLE_ASSIGNMENT_IS_WRONG;
			case METHOD_INVOCATION:
				return GuessedFault.PARAMETER_VALUE_IS_WRONG;
			case INDEX_USED_FOR_VARIABLE_ACCESS:
				return GuessedFault.STRING_INDEX_IS_WRONG;
			case METHOD_PARAMETER:
				return GuessedFault.METHOD_PARAMETER_IS_WRONG;
		}
		
		return null;
	}

	public static void resetMaxSuspiciousnessScore() {
		MAX_SUSPICIOUSNESS_SCORE = 2.00;
	}
}
