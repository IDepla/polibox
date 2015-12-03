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

import it.polito.ai.polibox.web.controllers.inputform.Directory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DirectoryValidator implements Validator {
	private Pattern pattern;
	private Matcher matcher;
	
	private static final String DIRECTORY_PATTERN="^/([A-Za-z0-9_-]+/)*[A-Za-z0-9_-]+";
	
	public DirectoryValidator() {
		pattern=Pattern.compile(DIRECTORY_PATTERN);
	}

	public boolean supports(Class<?> clazz) {
		return Directory.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		Directory directory=(Directory) target;
		if(directory == null){
			errors.reject("directory.notnull");
			return;
		}
		if(directory.getName().equalsIgnoreCase("")){
			errors.reject("directory.empty");
			return;
		}
		matcher=pattern.matcher(directory.getName());
		
		if(!matcher.matches())
			errors.reject("directory.invalid");
	}

}
