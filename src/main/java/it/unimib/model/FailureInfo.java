package it.unimib.model;

import java.util.ArrayList;
import java.util.List;

public class FailureInfo {

	private String failingTestClass;
	private String failingTestMethod;
	private String exceptionType;
	private String exceptionMessage;
	private List<StackTracePOI> stackTracePOIList;

	public FailureInfo() {}
	
	public FailureInfo(String failingTestClass, String failingTestMethod) {
		this.stackTracePOIList = new ArrayList<>();
		this.failingTestClass = failingTestClass;
		this.failingTestMethod = failingTestMethod;
	}

	public String getFailingTestClass() {
		return failingTestClass;
	}

	public void setFailingTestClass(String failingTestClass) {
		this.failingTestClass = failingTestClass;
	}

	public String getFailingTestMethod() {
		return failingTestMethod;
	}

	public void setFailingTestMethod(String failingTestMethod) {
		this.failingTestMethod = failingTestMethod;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	public void addStackTracePOI(StackTracePOI stackTracePOI) {
		this.stackTracePOIList.add(stackTracePOI);
	}
	
	public void removeStackTracePOI(int position) {
		if (stackTracePOIList != null && position >= 0 && position < stackTracePOIList.size()) {
			stackTracePOIList.remove(position);
		}
	}
	
	public List<StackTracePOI> getStackTracePOIList() {
		return this.stackTracePOIList;
	}

	@Override
	public String toString() {
		return "FailureInfo{" +
				"failingTestClass='" + failingTestClass + '\'' +
				", failingTestMethod='" + failingTestMethod + '\'' +
				", exceptionType='" + exceptionType + '\'' +
				", exceptionMessage='" + exceptionMessage + '\'' +
				", stackTracePOIList=" + stackTracePOIList +
				'}';
	}
}
