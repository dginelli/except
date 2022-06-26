package it.unimib.util;

import it.unimib.model.FailureInfo;
import it.unimib.model.StackTracePOI;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.notification.Failure;

public final class FailureAnalyzer {
	
	private static final String JDK_CLASS_PREFIX = "jdk.";
	private static final String JAVA_CLASS_PREFIX = "java.";
	private static final String SUN_REFLECT = "sun.reflect";
	private static final String JUNIT_CLASS_PREFIX = "org.junit.";
	private static final String JUNIT_FRAMEWORK_PREFIX = "junit.framework.";
	private static final String STAMP_PROJECT = "eu.stamp_project";
	private static final String EXCALIBUR = "it.unimib";
	
	private FailureAnalyzer() {}

	public static FailureInfo analyzeFailure(eu.stamp_project.testrunner.runner.Failure failure,
											  String testClassName, String classTestToExecute) {
		if (failure == null) {
			return null;
		}

		FailureInfo failureInfo = new FailureInfo(testClassName, classTestToExecute);
		failureInfo.setExceptionType(failure.fullQualifiedNameOfException);
		failureInfo.setExceptionMessage(failure.messageOfFailure);

		String stackTrace = failure.stackTrace;

		String[] stackTraceArray = stackTrace.split("\n");

		for (int i = 0; i < stackTraceArray.length; i++) {
			stackTraceArray[i] = stackTraceArray[i].replace("at ", "").trim();
		}

		for (int i = 1; i < stackTraceArray.length; i++) {

			/*
			 *  To be excluded: jdk.internal.reflect.NativeMethodAccessorImpl
			 *  To be excluded: java.lang.reflect.Method
			 *  To be excluded: sun.reflect
			 *  To be excluded: org.junit
			 *  To be excluded: junit.framework
			 *  To be excluded: eu.stamp_project
			 * 	To be excluded: it.unimib
			 *  To be excluded: test case class
			 */
			if (!stackTraceArray[i].startsWith(JDK_CLASS_PREFIX) &&
					!stackTraceArray[i].startsWith(JAVA_CLASS_PREFIX) &&
					!stackTraceArray[i].startsWith(SUN_REFLECT) &&
					!stackTraceArray[i].startsWith(JUNIT_CLASS_PREFIX) &&
					!stackTraceArray[i].startsWith(JUNIT_FRAMEWORK_PREFIX) &&
					!stackTraceArray[i].startsWith(STAMP_PROJECT) &&
					//!stackTraceArray[i].startsWith(EXCALIBUR) &&
					!(stackTraceArray[i].contains(classTestToExecute) ||
							stackTraceArray[i].contains(testClassName))) {

				String classNameContent = stackTraceArray[i].substring(0, stackTraceArray[i].indexOf("("));
				String className = classNameContent;

				if (classNameContent.contains("/")) {
					className = classNameContent.substring(classNameContent.indexOf("/") + 1);
				}

				int position = StringUtils.lastIndexOf(className, ".");
				className = className.substring(0, position);

				String lineAndFileContent = stackTraceArray[i].substring(
						stackTraceArray[i].indexOf("(") + 1, stackTraceArray[i].indexOf(")"));

				String lineNumber = lineAndFileContent.substring(lineAndFileContent.indexOf(":") + 1);
				String fileName = lineAndFileContent.substring(0, lineAndFileContent.indexOf(":"));

				String[] stackTraceArraySplit = stackTraceArray[i].split("\\.");
				String stackTraceArraySplitMethodPart = stackTraceArraySplit[stackTraceArraySplit.length-2];
				String methodName = stackTraceArraySplitMethodPart.substring(0, stackTraceArraySplitMethodPart.indexOf("("));

				failureInfo.addStackTracePOI(
						new StackTracePOI(className, methodName, fileName, Integer.parseInt(lineNumber))
				);
			}
		}

		failureInfo.removeStackTracePOI(failureInfo.getStackTracePOIList().size());

		return failureInfo;
	}
	
	public static FailureInfo analyzeFailure(Failure failure, String classTestToExecute) {
		
		if (failure == null) {
			return null;
		}
		
		FailureInfo failureInfo = new FailureInfo();
		failureInfo.setExceptionType(failure.getException().getClass().getCanonicalName());
		failureInfo.setExceptionMessage(failure.getMessage());
		
		StackTraceElement[] stackTraceElements = failure.getException().getStackTrace();
		
		for (int i = 0; i < stackTraceElements.length; i++) {

			/*
			 *  To be excluded: jdk.internal.reflect.NativeMethodAccessorImpl
			 *  To be excluded: java.lang.reflect.Method
			 *  To be excluded: sun.reflect
			 *  To be excluded: org.junit
			 *  To be excluded: junit.framework
			 *  To be excluded: eu.stamp_project
			 * 	To be excluded: it.unimib
			 *  To be excluded: test case class
			 */
			String className = stackTraceElements[i].getClassName();
			System.out.println("class name: " + className);

			if (!className.startsWith(JDK_CLASS_PREFIX) &&
					!className.startsWith(JAVA_CLASS_PREFIX) &&
					!className.startsWith(SUN_REFLECT) &&
					!className.startsWith(JUNIT_CLASS_PREFIX) &&
					!className.startsWith(JUNIT_FRAMEWORK_PREFIX) &&
					!className.startsWith(EXCALIBUR) &&
					!className.startsWith(classTestToExecute)) {
				failureInfo.addStackTracePOI(
						new StackTracePOI(
							stackTraceElements[i].getClassName(),
							stackTraceElements[i].getMethodName(),
							stackTraceElements[i].getFileName(),
							stackTraceElements[i].getLineNumber()
						)
					);
			}
		}
		
		failureInfo.removeStackTracePOI(failureInfo.getStackTracePOIList().size());
		
		return failureInfo;
	}
}
