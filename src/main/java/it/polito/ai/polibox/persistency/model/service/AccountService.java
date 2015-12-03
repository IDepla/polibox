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
package it.polito.ai.polibox.persistency.model.service;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import it.polito.ai.polibox.persistency.model.NotificationOptionInterface;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.exception.AccountCreationException;
import it.polito.ai.polibox.persistency.model.exception.LoginInvalidException;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.web.controllers.inputform.NotificationOption;

import org.springframework.stereotype.Service;

@Service
@PersistenceContext(type=PersistenceContextType.EXTENDED)
public interface AccountService {

	public UserDao getUserDao();
	public int createAccount(User newUser) throws AccountCreationException;
	public int recuperaAccount(User oldUser) throws UserNotFoundException;
	public int deleteAccount(User user)  throws UserNotFoundException;
	public int activateAccount(User user) throws UserNotFoundException;
	
	public User getUserAccountDetails(User user)throws UserNotFoundException;
	public NotificationOptionInterface[] getEmailNotificationOptionList(User user);
	
	public int getSpaceUsed(User user);
	
	public User setUserAccount(User user,String oldPassword) throws UserNotFoundException, LoginInvalidException;
	public User setUserAccountDetails(User user)throws UserNotFoundException;
	public NotificationOptionInterface[] setEmailNotificationOptionList(User user,  NotificationOption[] options);
}
