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

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.DeviceNotification;
import it.polito.ai.polibox.persistency.model.DeviceNotificationId;
import it.polito.ai.polibox.persistency.model.NotificationCode;
import it.polito.ai.polibox.persistency.model.NotificationOptionInterface;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.DeviceDao;
import it.polito.ai.polibox.persistency.model.dao.DeviceNotificationDao;
import it.polito.ai.polibox.persistency.model.dao.NotificationCodeDao;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.exception.DeviceExistsException;
import it.polito.ai.polibox.persistency.model.service.DeviceService;
import it.polito.ai.polibox.security.authentication.AutenticationKeygen;
import it.polito.ai.polibox.service.MasterNotificationService;
import it.polito.ai.polibox.service.notification.message.DeviceCancellato;
import it.polito.ai.polibox.service.notification.message.DeviceCreato;
import it.polito.ai.polibox.service.notification.message.DeviceOpzioniNotificaModificate;
import it.polito.ai.polibox.service.notification.message.DeviceRinominato;

@Component
@Transactional
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DeviceDao deviceDao;
	
	@Autowired
	private DeviceNotificationDao deviceNotificationDao;

	@Autowired
	private EntityManager entityManager;
	

	@Autowired
	private NotificationCodeDao notificationCodeDao;
	
	@Autowired
	private MasterNotificationService masterNotificationService;
	
	


	public MasterNotificationService getMasterNotificationService() {
		return masterNotificationService;
	}


	public void setMasterNotificationService(
			MasterNotificationService masterNotificationService) {
		this.masterNotificationService = masterNotificationService;
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


	public DeviceDao getDeviceDao() {
		return deviceDao;
	}


	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}


	public DeviceNotificationDao getDeviceNotificationDao() {
		return deviceNotificationDao;
	}


	public void setDeviceNotificationDao(DeviceNotificationDao deviceNotificationDao) {
		this.deviceNotificationDao = deviceNotificationDao;
	}


	public EntityManager getEntityManager() {
		return entityManager;
	}


	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	
	
	public DeviceServiceImpl() {
	}


	public List<Device> getDeviceList(int userId) {
		User u=userDao.findOne(new Integer(userId));
		List<Device> list=deviceDao.getMyDevices(u);
		return list;
	}

	public NotificationOptionInterface[] getDeviceOptions(Device d){
		d=entityManager.merge(d);
		return d.getDeviceNotifications().toArray(new NotificationOptionInterface[0]);
	}

	public Device createDevice(String name, int userId) {
		User u=userDao.findOne(new Integer(userId));
		Device d=new Device();
		AutenticationKeygen autoAuth=new AutenticationKeygen(u);
		int i;
		d.setDevice_name(name);
		d.setOwner(u);
		d.setDeviceDeletable(true);
		u.getDevices().add(d);
		
		d.setRandomSalt(autoAuth.getRandomSalt());
		d.setAutoAuthenticationKey(autoAuth.getAutoAuthenticationKey());
		
		List<NotificationCode> notificationCodes=notificationCodeDao.findAll();
		NotificationCode nc;
		DeviceNotification dn;
		DeviceNotificationId dni;
		try{
			
			u=userDao.save(u);
			d=deviceDao.save(d);
			for(i=0;i<notificationCodes.size();i++){
				nc=notificationCodes.get(i);
		
				
				//device notification
				dn=new DeviceNotification();
				dn.setEnabled(true);
				dni=new DeviceNotificationId();
				dni.setDevice(d);
				dni.setNotificationCode(nc);
				dn.setPk(dni);
				dn=deviceNotificationDao.save(dn);
				d.getDeviceNotifications().add(dn);
		
			}
			masterNotificationService.notifica(new DeviceCreato(u, d));
		}finally{
		}
		
		return d;
	}


	public Device renameDevice(int device,String name, int userId)  throws DeviceExistsException{
		Device d=getDevice(device, userId);
		if(d.isDeviceDeletable()){
			d.setDevice_name(name);
			d=deviceDao.save(d);
			masterNotificationService.notifica(new DeviceRinominato(d.getOwner(), d));
		}
		return d;
	}


	public void deleteDevice(int device, int userId)  throws DeviceExistsException{
		Device d=getDevice(device, userId);
		if(d.isDeviceDeletable()){
			masterNotificationService.notifica(new DeviceCancellato(d.getOwner(), d));
			deviceDao.delete(d);
		}
	}


	public Device getDevice(int id, int userId) throws DeviceExistsException{
		User u=userDao.findOne(new Integer(userId));
		Device d=deviceDao.getDevice(u, id);
		if(d==null)
			throw new DeviceExistsException();
		return d;
	}


	public void modifyNotificationOption(int deviceId,int userId, int optionId, boolean optionValue) throws DeviceExistsException {
		Device d=getDevice(deviceId,userId);
		d=entityManager.merge(d);
		DeviceNotification[] dn=d.getDeviceNotifications().toArray(new DeviceNotification[0]);
		int i;
		
		for (i=0;i<dn.length;i++){
			if(dn[i].getPk().getNotificationCode().getCode()==optionId){
				dn[i].setEnabled(optionValue);
				dn[i]=entityManager.merge(dn[i]);
				return;
			}
		}
		masterNotificationService.notifica(new DeviceOpzioniNotificaModificate(d.getOwner(), d));
	}

	
}
