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
package it.polito.ai.polibox.persistency.model.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.DeviceNotification;
import it.polito.ai.polibox.persistency.model.DeviceNotificationId;
import it.polito.ai.polibox.persistency.model.EmailNotification;
import it.polito.ai.polibox.persistency.model.EmailNotificationId;
import it.polito.ai.polibox.persistency.model.NotificationCode;
import it.polito.ai.polibox.persistency.model.NotificationOptionInterface;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.DeviceDao;
import it.polito.ai.polibox.persistency.model.dao.DeviceNotificationDao;
import it.polito.ai.polibox.persistency.model.dao.EmailNotificationDao;
import it.polito.ai.polibox.persistency.model.dao.NotificationCodeDao;
import it.polito.ai.polibox.persistency.model.dao.NotificationDao;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.exception.AccountCreationException;
import it.polito.ai.polibox.persistency.model.exception.LoginInvalidException;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.security.authentication.AutenticationKeygen;
import it.polito.ai.polibox.service.MasterNotificationService;
import it.polito.ai.polibox.service.notification.message.AccountCreato;
import it.polito.ai.polibox.service.notification.message.AccountEmailModificato;
import it.polito.ai.polibox.service.notification.message.AccountOpzioniNotificaEmail;
import it.polito.ai.polibox.service.notification.message.AccountPasswordModificato;
import it.polito.ai.polibox.service.notification.message.AccountPersonalDetailModificato;
import it.polito.ai.polibox.service.notification.message.AccountRecuperato;
import it.polito.ai.polibox.web.controllers.inputform.NotificationOption;

@Component
@Transactional
public class AccountServiceImpl implements AccountService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private EmailNotificationDao emailNotificationDao;
	
	@Autowired
	private DeviceNotificationDao deviceNotificationDao;
	
	@Autowired
	private NotificationCodeDao notificationCodeDao;
	
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private MasterNotificationService masterNotificationService;
	
	
	public MasterNotificationService getMasterNotificationService() {
		return masterNotificationService;
	}

	public void setMasterNotificationService(
			MasterNotificationService masterNotificationService) {
		this.masterNotificationService = masterNotificationService;
	}

	public AccountServiceImpl() {}

	public int getSpaceUsed(User user){
		Query q=entityManager.createNativeQuery("SELECT sum(rvc.real_size) as size "+ 
									"FROM resource_version_chunk rvc, resource_owners ro "+
									"where "+ 
									"ro.user_id=:utente and "+
									"ro.resource_id= rvc.resource_id");
		BigDecimal i=(BigDecimal)q.setParameter("utente", user.getId()).getSingleResult();
		
		if(i!=null){
			return i.intValue();
		}
		return 0;
	}
	@Transactional
	public int createAccount(User newUser) throws AccountCreationException  {
		newUser.setPassword(AutenticationKeygen.encodePassword(newUser.getPassword()));
		
		AutenticationKeygen auto=new AutenticationKeygen(newUser);
		Date today=new Date();
		int i;
		//device creation
		
		Device web=new Device();
		web.setOwner(newUser);
		web.setDevice_name("Browser");
		web.setDeviceDeletable(false);
		web.setLastCompleteSync(today);
		
		web.setRandomSalt(auto.getRandomSalt());
		web.setAutoAuthenticationKey(auto.getAutoAuthenticationKey());
		
		//user completion
		newUser.getDevices().add(web);
		newUser.setDeleted(false);
		newUser.setSignInTime(today);
		newUser.setLastActionTime(today);
		newUser.setCompany("");
		newUser.setMobile("");
		newUser.setPosition("");
		//notification configuration
		List<NotificationCode> notificationCodes=notificationCodeDao.findAll();
		NotificationCode nc;
		EmailNotification en;
		EmailNotificationId eni;
		DeviceNotification dn;
		DeviceNotificationId dni;
	
		try{
		
			newUser=userDao.save(newUser);
			web=deviceDao.save(web);
			for(i=0;i<notificationCodes.size();i++){
				nc=notificationCodes.get(i);
				//email notification
				en=new EmailNotification();
				en.setEnabled(true);
				eni=new EmailNotificationId();
				eni.setNotificationCode(nc);
				eni.setUser(newUser);
				en.setPk(eni);
				en=emailNotificationDao.save(en);
				newUser.getEmailNotification().add(en);
				
				//device notification
				dn=new DeviceNotification();
				dn.setEnabled(true);
				dni=new DeviceNotificationId();
				dni.setDevice(web);
				dni.setNotificationCode(nc);
				dn.setPk(dni);
				dn=deviceNotificationDao.save(dn);
				web.getDeviceNotifications().add(dn);
				
			}
			

			masterNotificationService.notifica(new AccountCreato(newUser));
		}catch (EntityExistsException e){
			throw new AccountCreationException();
		}finally{
		}
		
		return 0;
	}
	
	public User setUserAccount(User user,String oldPassword) throws UserNotFoundException, LoginInvalidException {
		User utente;
		String encodedPassword;
		try{
			utente=userDao.findOne(user.getId());
			encodedPassword=AutenticationKeygen.encodePassword(oldPassword);
			if(!AutenticationKeygen.getEncoder().matches(encodedPassword, utente.getPassword())){
				throw new LoginInvalidException("wrong password");
			}
			utente=entityManager.merge(utente);
			
			if(user.getEmail()!=null){
				utente.setEmail(user.getEmail());
				masterNotificationService.notifica(new AccountEmailModificato(user));
			}
			if(user.getPassword()!=null){
				encodedPassword=AutenticationKeygen.encodePassword(user.getPassword());
				utente.setPassword(encodedPassword);
				masterNotificationService.notifica(new AccountPasswordModificato(user));
			}
			utente.setLastActionTime(new Date());
			utente=userDao.saveAndFlush(utente);
			return utente;
		}finally{
			entityManager.clear();
		}
	}
	
	public User setUserAccountDetails(User user) throws UserNotFoundException {
		User utente;
		boolean b=false;
		try{
			utente=userDao.findOne(user.getId());
			utente=entityManager.merge(utente);
			if(user.getCompany()!=null){
				utente.setCompany(user.getCompany());
				b=true;
			}
			if(user.getMobile()!=null){
				utente.setMobile(user.getMobile());
				b=true;
			}
			if(user.getPosition()!=null){
				utente.setPosition(user.getPosition());
				b=true;
			}
			if(user.getName()!=null){
				utente.setName(user.getName());
				b=true;
			}
			if(user.getSurname()!=null){
				utente.setSurname(user.getSurname());
				b=true;
			}
			utente.setLastActionTime(new Date());
			utente=userDao.saveAndFlush(utente);
			if(b==true){
				masterNotificationService.notifica(new AccountPersonalDetailModificato(utente));
			}
			return utente;
		}finally{
			entityManager.clear();
		}
	}


	public NotificationOptionInterface[] setEmailNotificationOptionList(User user, NotificationOption[]  options) {
		User utente;
		Set<EmailNotification> set;
		Iterator<EmailNotification> it;
		int i;
		boolean b=false;
		try{
			utente=userDao.findOne(user.getId());
			utente=entityManager.merge(utente);
			set=utente.getEmailNotification();
			it=set.iterator();
			EmailNotification em;
			NotificationOption no;
			while(it.hasNext()){
				em=it.next();
				for(i=0;i<options.length;i++){
					no=options[i];
					if(em.getNotificationCode()==no.getNotificationCode()){
						em.setEnabled(no.isEnabled());
						emailNotificationDao.saveAndFlush(em);
						b=true;
					}
				}
				
			}
			utente.setLastActionTime(new Date());
			userDao.saveAndFlush(utente);
			if(b==true){
				masterNotificationService.notifica(new AccountOpzioniNotificaEmail(utente));
			}
			return null;
		}finally{
			entityManager.clear();
		}
	}


	

	//TODO
	public int recuperaAccount(User oldUser)  throws UserNotFoundException{
		
		
		masterNotificationService.notifica(new AccountRecuperato(oldUser));
		return 0;
	}

	
	
	public int deleteAccount(User user)  throws UserNotFoundException{
		User account=userDao.findOne(user.getId());
		account.setDeleted(true);
		userDao.saveAndFlush(account);
		return 0;
	}

	public int activateAccount(User user)  throws UserNotFoundException{
		User account=userDao.findOne(user.getId());
		account.setEnabled(true);
		userDao.saveAndFlush(account);
		return 0;
	}
	
	public User getUserAccountDetails(User user) throws UserNotFoundException {
		user=userDao.findOne(user.getId());
		return user;
	}


	public NotificationOptionInterface[] getEmailNotificationOptionList(User user) {
		try{
			user=userDao.findOne(user.getId()); 
			user=entityManager.merge(user);
			return user.getEmailNotification().toArray(new NotificationOptionInterface[0]);
		}finally{
			entityManager.clear();
		}
	}



	public NotificationCodeDao getNotificationCodeDao() {
		return notificationCodeDao;
	}

	public void setNotificationCodeDao(NotificationCodeDao notificationCodeDao) {
		this.notificationCodeDao = notificationCodeDao;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public NotificationDao getNotificationDao() {
		return notificationDao;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public EmailNotificationDao getEmailNotificationDao() {
		return emailNotificationDao;
	}

	public void setEmailNotificationDao(EmailNotificationDao emailNotificationDao) {
		this.emailNotificationDao = emailNotificationDao;
	}

	public DeviceNotificationDao getDeviceNotificationDao() {
		return deviceNotificationDao;
	}

	public void setDeviceNotificationDao(DeviceNotificationDao deviceNotificationDao) {
		this.deviceNotificationDao = deviceNotificationDao;
	}

	

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public DeviceDao getDeviceDao() {
		return deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}



}
