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

import it.polito.ai.polibox.web.controllers.inputform.Login;

import org.springframework.validation.Errors;

public class LoginValidator extends EmailValidator {
	
	private Pattern passwordPattern;
	private Matcher passwordMatcher;
	public static final int PASSWORD_MAX_LENGTH=20;
	public static final int PASSWORD_MIN_LENGTH=4; 

	
	private static final String PASSWORD_PATTERN="^([A-Za-z\\s0-9]+)$";
	
	public LoginValidator() {
		super();
		passwordPattern=Pattern.compile(PASSWORD_PATTERN);
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Login.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Login login=(Login) target;
		validaEmail(login.getEmail(), "email", errors);
		validaPassword(login.getPassword(),"password", errors);
	}

	protected void validaPassword(String target,String keyCode,Errors errors){
		if(target == null){
			errors.reject(keyCode+".notnull");
			return;
		}
		if(target.length()<PASSWORD_MIN_LENGTH){
			errors.reject(keyCode+".tootiny");
			return;
		}
		
		if(target.length()>PASSWORD_MAX_LENGTH){
			errors.reject(keyCode+".toobig");
			return;
		}
		
		passwordMatcher=passwordPattern.matcher(target);
		if(!passwordMatcher.matches())
			errors.reject(keyCode+".invalid");
	}
	
}
