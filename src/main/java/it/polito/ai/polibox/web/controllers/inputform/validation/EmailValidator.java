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

import it.polito.ai.polibox.web.controllers.inputform.Email;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EmailValidator implements Validator {
	private Pattern pattern;
	private Matcher matcher;
	
	public static final String EMAIL_PATTERN="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.)([A-Za-z]{2,})$";
	
	public EmailValidator() {
		pattern=Pattern.compile(EMAIL_PATTERN);
	}
	
	public boolean supports(Class<?> clazz) {
		
		return Email.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		Email email=(Email) target;
		validaEmail(email.getEmail(), "email", errors);
	}

	
	protected void validaEmail(String target,String keyCode,Errors errors){
		if(target == null){
			errors.reject(keyCode+".notnull");
			return;
		}
		if(target.equalsIgnoreCase("")){
			errors.reject(keyCode+".empty");
			return;
		}
		matcher=pattern.matcher(target);
		
		if(!matcher.matches())
			errors.reject(keyCode+".invalid");
	}
}
