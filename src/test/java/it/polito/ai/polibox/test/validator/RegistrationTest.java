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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import it.polito.ai.polibox.web.controllers.inputform.Registration;
import it.polito.ai.polibox.web.controllers.inputform.validation.RegistrationValidator;

public class RegistrationTest {

	Errors errors;
	Registration registration;
	RegistrationValidator registrationValidator;
	
	public RegistrationTest() {
		registration=new Registration();
		registrationValidator=new RegistrationValidator();
		errors = new BeanPropertyBindingResult(registration, "registration");
	}

	
	@Test
	public void registrationValid1(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("prova1s3sd");
		registration.setRepassword("prova1s3sd");
		registration.setName("igor");
		registration.setSurname("deplano");
		registrationValidator.validate(registration, errors);
		assertEquals(0,errors.getErrorCount());
	}
	
	@Test
	public void registrationPasswordInvalid1(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("pr .ova1s3sd");
		registration.setRepassword("pr .ova1s3sd");
		registration.setName("igor");
		registration.setSurname("deplano");
		registrationValidator.validate(registration, errors);
		assertEquals(2,errors.getErrorCount());
	}
	@Test
	public void registrationPasswordInvalid2(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("pr .ova1s3sd");
		registration.setRepassword("pr ,.ova1s3sd");
		registration.setName("igor");
		registration.setSurname("deplano");
		registrationValidator.validate(registration, errors);
		assertEquals(3,errors.getErrorCount());
	}
	@Test
	public void registrationNameInvalid1(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("prova1s3sd");
		registration.setRepassword("prova1s3sd");
		registration.setName("igor .sad.,d");
		registration.setSurname("deplano");
		registrationValidator.validate(registration, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void registrationSurnameInvalid1(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("prova1s3sd");
		registration.setRepassword("prova1s3sd");
		registration.setName("igor");
		registration.setSurname("depl sd.. sd, ano");
		registrationValidator.validate(registration, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void registrationPasswordMismatch1(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("prova1s3sd");
		registration.setRepassword("prova1sws3sd");
		registration.setName("igor");
		registration.setSurname("deplano");
		registrationValidator.validate(registration, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void registrationPasswordMismatch2(){
		registration.setEmail("prova@prova.com");
		registration.setPassword("prova1s3sd");
		registration.setRepassword("prova1w3sd");
		registration.setName("igor");
		registration.setSurname("deplano");
		registrationValidator.validate(registration, errors);
		assertEquals(1,errors.getErrorCount());
	}
}
