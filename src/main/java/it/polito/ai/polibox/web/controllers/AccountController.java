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


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.NotificationCodeDao;
import it.polito.ai.polibox.persistency.model.exception.LoginInvalidException;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.web.controllers.inputform.AccountStrict;
import it.polito.ai.polibox.web.controllers.inputform.NotificationOptions;
import it.polito.ai.polibox.web.controllers.inputform.PersonalDetails;
import it.polito.ai.polibox.web.controllers.inputform.validation.AccountStrictValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.NotificationOptionsValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.PersonalDetailsValidator;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;
import it.polito.ai.polibox.web.controllers.result.NotificationOptionWrapper;
import it.polito.ai.polibox.web.controllers.result.UserAccountDetailsWrapper;
import it.polito.ai.polibox.web.controllers.result.UserPersonalDetailWrapper;
import it.polito.ai.polibox.web.controllers.session.constants.SessionAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private NotificationCodeDao notificationCodeDao;
	

	
	
	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public NotificationCodeDao getNotificationCodeDao() {
		return notificationCodeDao;
	}

	public void setNotificationCodeDao(NotificationCodeDao notificationCodeDao) {
		this.notificationCodeDao = notificationCodeDao;
	}

	public AccountController() {
	}
	
	@InitBinder("account")
    protected void initAccountBinder(WebDataBinder binder) {
		binder.setValidator(new AccountStrictValidator());
    }
	
	@InitBinder("personalDetails")
    protected void initPersonalBinder(WebDataBinder binder) {
		binder.setValidator(
				new PersonalDetailsValidator()
				);
    }
	
	@InitBinder("options")
    protected void initNotificationBinder(WebDataBinder binder) {
		binder.setValidator(
				new NotificationOptionsValidator()
				);
    }
	
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody Response getAccountDetails(HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try {
			User user=new User();
			user.setId(userId.intValue());
			user=accountService.getUserAccountDetails(user);
			UserAccountDetailsWrapper ud=new UserAccountDetailsWrapper(user);
			ud.setSize(accountService.getSpaceUsed(user));
			response.setStatus(Status.OK);
			response.getResult().add(ud);
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"user not found");
		}
		return response;
	}
	

	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody Response setAccountDetails(@Valid AccountStrict account,
													BindingResult bindingResult,HttpSession session
													){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try {
			if(account.getPassword()==null || account.getPassword().isEmpty()){
				response.setStatus(Status.OK);
				return response;
			}
			User user=new User();
			user.setId(userId.intValue());
			user.setEmail(account.getEmail());
			user.setPassword(account.getNewPassword());
			accountService.setUserAccount(user,account.getPassword());
			response.setStatus(Status.OK);
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"user not found");
		} catch (LoginInvalidException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"wrong password");
		}
		return response;
	}
	
	@RequestMapping(value="/details",method=RequestMethod.GET)
	public @ResponseBody Response getAccountPersonalDetails(HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try {
			User user=new User();
			user.setId(userId.intValue());
			user=accountService.getUserAccountDetails(user);
			UserPersonalDetailWrapper ud=new UserPersonalDetailWrapper(user);
			response.setStatus(Status.OK);
			response.getResult().add(ud);
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"user not found");
		}
		return response;
	}
	
	@RequestMapping(value="/details",method=RequestMethod.POST)
	public @ResponseBody Response setAccountPersonalDetails(@Valid PersonalDetails personalDetails,
															BindingResult bindingResult,
															HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try {
			User user=new User();
			user.setId(userId.intValue());
			user.setCompany(personalDetails.getCompany());
			user.setMobile(personalDetails.getMobile());
			user.setPosition(personalDetails.getPosition());
			user.setName(personalDetails.getName());
			user.setSurname(personalDetails.getSurname());
			user=accountService.setUserAccountDetails(user);
			response.setStatus(Status.OK);
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"user not found");
		}
		return response;
	}
	
	@RequestMapping(value="/notification/email",method=RequestMethod.GET)
	public @ResponseBody Response getEmailNotificationDetails(HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User user=new User();
		user.setId(userId.intValue());
		try {
			NotificationOptionWrapper nOw=new NotificationOptionWrapper(
													accountService.getEmailNotificationOptionList(user));

			response.setStatus(Status.OK);
			response.getResult().add(nOw);
		} catch (Exception e) {
			response.setStatus(Status.FAIL);
		}
		return response;
	}

	@RequestMapping(value="/notification/email",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response setEmailNotificationDetails(@Valid @RequestBody NotificationOptions options,
																BindingResult bindingResult,
																HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User user=new User();
		user.setId(userId.intValue());
		try {
			accountService.setEmailNotificationOptionList(user, options.getList());
			response.setStatus(Status.OK);
		} catch (Exception e) {
			response.setStatus(Status.FAIL);
		}
		return response;
	}	
	
	
	

}
