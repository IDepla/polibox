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
package it.polito.ai.polibox.web.controllers;



import javax.validation.Valid;

import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.exception.AccountCreationException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.web.controllers.inputform.Registration;
import it.polito.ai.polibox.web.controllers.inputform.validation.RegistrationValidator;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/account/registrazione")
public class SignInController {

	@Autowired
	private AccountService accountService;
	
	
	private static final Logger LOGGER = Logger.getLogger(SignInController.class.getName());
	
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new RegistrationValidator());
    }
	
		
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody Response registrazione(
			@Valid Registration obj,
			BindingResult bindingResult,
			Model model
			){
		Response rsp=new Response();
		if(bindingResult.hasErrors()){
			rsp.setStatus(Status.BAD_PARAM);
			rsp.appendAllErrors(bindingResult.getAllErrors());
            return rsp;
        }
		LOGGER.log(Level.DEBUG, "SignIn-log the application join into SignIn class and map correctly the post verb");
		User utente=new User();
		utente.setEmail(obj.getEmail());
		utente.setName(obj.getName());
		utente.setSurname(obj.getSurname());
		utente.setPassword(obj.getPassword());
		
		
		try {
			accountService.createAccount(utente);
			//response construction
			rsp.setStatus(Status.OK);
			rsp.appendMessage("200", "account created an email has been sent into your email box, follow the instruction to activate the account");
		} catch (AccountCreationException e) {
			rsp.setStatus(Status.FAIL);
			rsp.appendError(Status.BAD_PARAM, "account cannot be created");
		}
		
		return rsp;
	}


	public AccountService getAccountService() {
		return accountService;
	}


	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}



}
