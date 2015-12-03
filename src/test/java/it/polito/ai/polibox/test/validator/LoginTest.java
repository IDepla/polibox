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
import it.polito.ai.polibox.web.controllers.inputform.Login;
import it.polito.ai.polibox.web.controllers.inputform.validation.LoginValidator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class LoginTest {
	Login login;
	LoginValidator loginValidator;
	Errors errors;
	
	public LoginTest() {
		// TODO Auto-generated constructor stub
	}
	@Before
	public void init(){
		login=new Login();
		loginValidator=new LoginValidator();
		errors = new BeanPropertyBindingResult(login, "login");
	}
	
	@Test
	public void loginValid1(){
		login.setEmail("prova@prova.com");
		login.setPassword("prova1s3sd");
		loginValidator.validate(login, errors);
		assertEquals(0,errors.getErrorCount());
	}
	
	@Test
	public void passwordTooTiny(){
		login.setEmail("prova@prova.com");
		login.setPassword("pro");
		loginValidator.validate(login, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void emailInvalid1(){
		login.setEmail("provam.@prova.com");
		login.setPassword("prova1s3sd");
		loginValidator.validate(login, errors);
		assertEquals(1,errors.getErrorCount());
	}

	@Test
	public void emailInvalid2(){
		login.setEmail("thessd@");
		login.setPassword("prova1s3sd");
		loginValidator.validate(login, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void emailInvalid3(){
		login.setEmail("thessd@sadasd,asd");
		login.setPassword("prova1s3sd");
		loginValidator.validate(login, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void emailInvalid4(){
		login.setEmail("thessd@sadasd asd");
		login.setPassword("prova1s3sd");
		loginValidator.validate(login, errors);
		assertEquals(1,errors.getErrorCount());
	}
	
	@Test
	public void passwordTooBig1(){
		login.setEmail("thessd@sadasdasd.it");
		login.setPassword("prova1s3sdprova1s3sdprova1s3sdprova1s3sdprova1s3sdprova1s3sd");
		loginValidator.validate(login, errors);
		assertEquals(1,errors.getErrorCount());
	}
}
