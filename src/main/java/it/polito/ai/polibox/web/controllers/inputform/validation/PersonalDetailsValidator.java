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

import it.polito.ai.polibox.web.controllers.inputform.PersonalDetails;

import org.springframework.validation.Errors;

public class PersonalDetailsValidator extends RegistrationValidator{

	private Pattern mobilePattern;
	private Matcher mobileMatcher;
	private Pattern companyPattern;
	private Matcher companyMatcher;
	public static final int TEXT_MAX_LENGTH=44;
	public static final int TEXT_MIN_LENGTH=1; 
	
	private static final String MOBILE_PATTERN="^((\\+)?[0-9\\s]*)$";
	private static final String COMPANY_PATTERN="^([A-Za-z0-9\\s\\']+)$";
	
	public PersonalDetailsValidator() {
		super();
		mobilePattern=Pattern.compile(MOBILE_PATTERN);
		companyPattern=Pattern.compile(COMPANY_PATTERN);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PersonalDetails.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		PersonalDetails reg=(PersonalDetails) target;
		textValid(reg.getName(),"name",errors);
		textValid(reg.getSurname(),"surname",errors);
		companyValid(reg.getCompany(),"company",errors);
		mobileValid(reg.getMobile(),"mobile",errors);
		textValid(reg.getPosition(),"position",errors);
	}
	
	protected void mobileValid(String target,String keyCode,Errors errors){
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
		
		mobileMatcher=mobilePattern.matcher(target);
		if(!mobileMatcher.matches())
			errors.reject(keyCode+".invalid");
	}
	
	protected void companyValid(String target,String keyCode,Errors errors){
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
		
		companyMatcher=companyPattern.matcher(target);
		if(!companyMatcher.matches())
			errors.reject(keyCode+".invalid");
	}
}
