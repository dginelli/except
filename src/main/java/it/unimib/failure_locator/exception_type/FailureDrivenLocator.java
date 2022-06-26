package it.unimib.failure_locator.exception_type;

import java.util.List;

import it.unimib.model.FailureInfo;
import it.unimib.model.SuspiciousLocation;
import spoon.Launcher;

public abstract class FailureDrivenLocator {
	
	protected Launcher launcher;
	
	public FailureDrivenLocator(String programSourceCodePath) {
		launcher = new Launcher();
		launcher.addInputResource(programSourceCodePath);
		launcher.getEnvironment().setCommentEnabled(false);
		launcher.buildModel();
	}
	
	public abstract List<SuspiciousLocation> getSuspiciousLocations(FailureInfo failureInfo);
}
