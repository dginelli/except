package it.unimib.util;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class FaultLocalizationTypeValidator implements IParameterValidator{
	public void validate(String name, String value)
		throws ParameterException {
			if(!value.equals("flacoco") && !value.equals("gzoltar")) {
				throw new ParameterException("Parameter "+name+" should be flacoco or gzoltar (found "+value+").");
			}
	}
}
