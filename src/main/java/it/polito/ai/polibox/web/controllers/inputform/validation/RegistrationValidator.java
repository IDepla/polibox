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

import it.polito.ai.polibox.web.controllers.inputform.Registration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;

public class RegistrationValidator extends LoginValidator {
	private Pattern textPattern;
	private Matcher textMatcher;
	public static final int TEXT_MAX_LENGTH=44;
	public static final int TEXT_MIN_LENGTH=1; 

	
	private static final String TEXT_PATTERN="^([A-Za-z\\s\\']+)$";
	
	
	public RegistrationValidator() {
		super();
		textPattern=Pattern.compile(TEXT_PATTERN);
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Registration.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Registration reg=(Registration) target;
		validaEmail(reg.getEmail(),"email", errors);
		validaPassword(reg.getPassword(),"password", errors);
		validaPassword(reg.getRepassword(),"repassword", errors);
		textValid(reg.getName(),"name",errors);
		textValid(reg.getSurname(),"surname",errors);
		
		if(!reg.getPassword().equals(reg.getRepassword())){
			errors.reject("passwords.mustbeequal");
		}
		
	}

	protected void textValid(String target,String keyCode,Errors errors){
		if(target == null){
			errors.reject(keyCode+".notnull");
			return;
		}
		if(target.length()<TEXT_MIN_LENGTH){
			errors.reject(keyCode+".tootiny");
			return;
		}
		
		if(target.length()>TEXT_MAX_LENGTH){
			errors.reject(keyCode+".toobig");
			return;
		}
		
		textMatcher=textPattern.matcher(target);
		if(!textMatcher.matches())
			errors.reject(keyCode+".invalid");
	}
	
}
