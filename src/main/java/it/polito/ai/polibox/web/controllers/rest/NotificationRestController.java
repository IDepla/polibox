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
package it.polito.ai.polibox.web.controllers.rest;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.dao.NotificationCodeDao;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.persistency.model.service.DeviceService;
import it.polito.ai.polibox.persistency.model.service.NotificationService;
import it.polito.ai.polibox.service.MasterNotificationService;
import it.polito.ai.polibox.service.notification.message.DeviceCollegato;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(produces={MediaType.APPLICATION_JSON})
public class NotificationRestController {
	@Autowired
	private NotificationCodeDao notificationCodeDao;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private MasterNotificationService masterNotificationService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private AccountService accountService;
	
	public NotificationRestController() {}
	
	
	@RequestMapping(value="/rest/notifications/realtime",method=RequestMethod.GET,produces={"text/event-stream"})
	public void realTimeNotifications(
			HttpServletRequest req,
			HttpServletResponse res){
		 // set content type
        res.setContentType("text/event-stream");
        res.setCharacterEncoding("UTF-8");
        res.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
        res.setHeader(HttpHeaders.PRAGMA, "no-cache");

//        System.out.println("header device:"+req.getHeader("device")+"|name:"+req.getHeader("user")+"|");
        
		AsyncContext ac = req.startAsync();
//		System.out.println("timeout del context="+ac.getTimeout());
	     masterNotificationService.getRealTimeNotification().pushContext(
	    		 new Integer(req.getHeader("device")),ac); 
	     
	     
	     try {
	    	int u=Integer.parseInt(req.getHeader("user"));
			int d=Integer.parseInt(req.getHeader("device"));
			Device device=deviceService.getDevice(d, u);
			masterNotificationService.notifica(new DeviceCollegato(device.getOwner(),device));
		} catch (Exception e) {
			ac.complete();
			masterNotificationService.getRealTimeNotification().removeContext(new Integer(req.getHeader("device")), 
					ac);
		} 
	}


	public NotificationCodeDao getNotificationCodeDao() {
		return notificationCodeDao;
	}


	public void setNotificationCodeDao(NotificationCodeDao notificationCodeDao) {
		this.notificationCodeDao = notificationCodeDao;
	}


	public NotificationService getNotificationService() {
		return notificationService;
	}


	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}


	public MasterNotificationService getMasterNotificationService() {
		return masterNotificationService;
	}


	public void setMasterNotificationService(
			MasterNotificationService masterNotificationService) {
		this.masterNotificationService = masterNotificationService;
	}


	public DeviceService getDeviceService() {
		return deviceService;
	}


	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}


	public AccountService getAccountService() {
		return accountService;
	}


	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	
}
