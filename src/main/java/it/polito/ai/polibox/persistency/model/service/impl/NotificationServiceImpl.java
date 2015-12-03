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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.polito.ai.polibox.persistency.model.Notification;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.DeviceNotificationDao;
import it.polito.ai.polibox.persistency.model.dao.EmailNotificationDao;
import it.polito.ai.polibox.persistency.model.dao.NotificationCodeDao;
import it.polito.ai.polibox.persistency.model.dao.NotificationDao;
import it.polito.ai.polibox.persistency.model.dao.ResourceDao;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.service.NotificationService;
import it.polito.ai.polibox.service.notification.NotificationMessageInterface;

@Component
@Transactional
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private DeviceNotificationDao deviceNotificationDao;
	
	@Autowired
	private EmailNotificationDao emailNotificationDao;
	
	@Autowired
	private NotificationCodeDao notificationCodeDao;
	
	@Autowired
	private EntityManager entityManager; 
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ResourceDao resourceDao;
	
	
	public NotificationDao getNotificationDao() {
		return notificationDao;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public DeviceNotificationDao getDeviceNotificationDao() {
		return deviceNotificationDao;
	}

	public void setDeviceNotificationDao(DeviceNotificationDao deviceNotificationDao) {
		this.deviceNotificationDao = deviceNotificationDao;
	}

	public EmailNotificationDao getEmailNotificationDao() {
		return emailNotificationDao;
	}

	public void setEmailNotificationDao(EmailNotificationDao emailNotificationDao) {
		this.emailNotificationDao = emailNotificationDao;
	}

	public NotificationCodeDao getNotificationCodeDao() {
		return notificationCodeDao;
	}

	public void setNotificationCodeDao(NotificationCodeDao notificationCodeDao) {
		this.notificationCodeDao = notificationCodeDao;
	}

	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public ResourceDao getResourceDao() {
		return resourceDao;
	}

	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	
	public NotificationServiceImpl() {	}


	
	
	


	public List<Notification> getNotifications(User u,Date day) {
		return notificationDao.getNotificationByDate(u, day);
	}

	public Notification create(NotificationMessageInterface msg, User u) {
		Notification n=new Notification();
		n.setCreationTime(new Date());
		n.setDescription(msg.getMessage());
		n.setSent(true);
		n.setUser(u);
		n.setNotificationCode(notificationCodeDao.getOne(msg.getCode()));
		n=notificationDao.save(n);
		return n;
	}

	public List<String> getEmailsToNotify(NotificationMessageInterface msg) {
		String s=msg.getEmailsToNotify();
		if(s!=null && !s.isEmpty()){
			Query q=entityManager.createNativeQuery(msg.getEmailsToNotify());
			@SuppressWarnings("unchecked")
			List<String> l=q.getResultList();
			return l;
		}
		return new ArrayList<String>();
	}

	public List<Integer> getDevicesToNotify(NotificationMessageInterface msg) {
		String s=msg.getEmailsToNotify();
		if(s!=null && !s.isEmpty()){
			Query q=entityManager.createNativeQuery(msg.getDeviceToNotifyRealtime());
			@SuppressWarnings("unchecked")
			List<Integer> l=q.getResultList();
			return l;
		}
		return new ArrayList<Integer>();
	}

	

	
	
}
