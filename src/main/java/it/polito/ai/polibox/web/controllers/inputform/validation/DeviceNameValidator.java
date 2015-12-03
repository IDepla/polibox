/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Igor Deplano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package it.polito.ai.polibox.web.controllers.inputform.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.polito.ai.polibox.web.controllers.inputform.DeviceName;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DeviceNameValidator implements Validator {
	private Pattern pattern;
	private Matcher matcher;
	private static final String NAME_PATTERN="^[-A-Za-z0-9_\\s]+";

	public DeviceNameValidator() {
		pattern=Pattern.compile(NAME_PATTERN);
	}

	public boolean supports(Class<?> clazz) {
		return DeviceName.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		DeviceName deviceName=(DeviceName) target;
		
		//ValidationUtils.rejectIfEmpty(errors, "name", "deviceName.invalid");
		if(deviceName == null){
			errors.reject("deviceName.notnull");
			return;
		}
		if(deviceName.getName() == null){
			errors.reject("deviceName.notnull");
			return;
		}
		if(deviceName.getName().equalsIgnoreCase("")){
			errors.reject("deviceName.empty");
			return;
		}
		matcher=pattern.matcher(deviceName.getName());
		
		if(!matcher.matches())
			errors.reject("deviceName.invalid");
	}

}
