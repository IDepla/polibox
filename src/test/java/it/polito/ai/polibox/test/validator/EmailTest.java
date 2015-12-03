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
package it.polito.ai.polibox.test.validator;

import static org.junit.Assert.*;
import it.polito.ai.polibox.web.controllers.inputform.Email;
import it.polito.ai.polibox.web.controllers.inputform.validation.EmailValidator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


public class EmailTest {
	Email email;
	EmailValidator emailValidator;
	Errors errors;
	
	
	
	@Before
	public void init(){
		email=new Email();
		emailValidator=new EmailValidator();
		errors = new BeanPropertyBindingResult(email, "email");
	}
	
	@Test
	public void emailValid1(){
		email.setEmail("the.moloch@gmail.com");
		emailValidator.validate(email, errors);
		assertEquals(0,errors.getErrorCount());
	
		
	}
	@Test
	public void emailValid2(){
	
		email.setEmail("asdasfsaf@sa.sadasdu");
		emailValidator.validate(email, errors);
		assertEquals(0,errors.getErrorCount());
		
	}
	
	@Test
	public void emailNotNull(){
		email.setEmail(null);
		emailValidator.validate(email, errors);
		assertEquals(1,errors.getErrorCount());
		
	}
	
	@Test
	public void emailNotEmpty(){
		email.setEmail("");
		emailValidator.validate(email, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void emailInvalid1(){
		email.setEmail("asdasfsaf");
		emailValidator.validate(email, errors);
		assertEquals(1,errors.getErrorCount());
		
	}
	@Test
	public void emailInvalid2(){
		
		email.setEmail("asdasfsaf@s");
		emailValidator.validate(email, errors);
		assertEquals(1,errors.getErrorCount());
		
	}
	@Test
	public void emailInvalid3(){
		
		email.setEmail("asdasfsaf@");
		emailValidator.validate(email, errors);
		assertEquals(1,errors.getErrorCount());
		

	}
}
