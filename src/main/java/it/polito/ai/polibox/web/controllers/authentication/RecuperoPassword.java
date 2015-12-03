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
package it.polito.ai.polibox.web.controllers.authentication;

import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;

import javax.validation.Valid;

import it.polito.ai.polibox.web.controllers.SignInController;
import it.polito.ai.polibox.web.controllers.inputform.Email;
import it.polito.ai.polibox.web.controllers.inputform.validation.EmailValidator;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/authenticate/forgotten")
public class RecuperoPassword {
	@Autowired
	private AccountService accountService;
	
	
	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	private static final Logger LOGGER = Logger.getLogger(SignInController.class.getName());
	
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new EmailValidator());
    }
	
	@RequestMapping(method=RequestMethod.POST,
					produces=MediaType.APPLICATION_JSON)
	public @ResponseBody Response recupera(
			@Valid Email obj,
			BindingResult bindingResult
			){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		LOGGER.log(Level.DEBUG, "RecuperoPassword-log the application join into RecuperoPassword class and map correctly the post verb");
		User utente=new User();
		utente.setEmail(obj.getEmail());
		
		try {
			accountService.recuperaAccount(utente);
			response.setStatus(Status.OK);
			response.appendMessage(Status.OK, "an email has been sent with recovery istructions. check your email-box.");
		} catch (UserNotFoundException e) {
			response.setStatus(Status.BAD_PARAM);
			response.appendError(Status.FAIL,"user not found");
		}
		
		
		return response;
	} 
	
	
}
