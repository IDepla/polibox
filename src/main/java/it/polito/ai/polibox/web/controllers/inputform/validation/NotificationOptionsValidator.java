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

import it.polito.ai.polibox.web.controllers.inputform.NotificationOption;
import it.polito.ai.polibox.web.controllers.inputform.NotificationOptions;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class NotificationOptionsValidator implements Validator{
	private Matcher numberMatcher;
	private Pattern numberPattern;
	private static final String NUMBER_PATTERN="^([0-9]+)$";
	public NotificationOptionsValidator() {
		numberPattern=Pattern.compile(NUMBER_PATTERN);
	}

	public boolean supports(Class<?> clazz) {
		return NotificationOptions.class.equals(clazz) || NotificationOption.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		NotificationOption[] list=new NotificationOption[1];
		if(target instanceof NotificationOptions)
			list=((NotificationOptions) target).getList();
		else if(target instanceof NotificationOption)
			list[0]=(NotificationOption) target;
		int i;
		for(i=0;i<list.length;i++){
			numberMatcher=numberPattern.matcher(list[i].getCode());
			if(!numberMatcher.matches())
				errors.reject("code.invalid");
		}
		
	}

}
