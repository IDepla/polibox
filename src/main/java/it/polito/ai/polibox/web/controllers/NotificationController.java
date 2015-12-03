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


import java.util.Calendar;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.Notification;
import it.polito.ai.polibox.persistency.model.NotificationCode;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.NotificationCodeDao;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.persistency.model.service.DeviceService;
import it.polito.ai.polibox.persistency.model.service.NotificationService;
import it.polito.ai.polibox.service.MasterNotificationService;
import it.polito.ai.polibox.service.notification.message.DeviceCollegato;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;
import it.polito.ai.polibox.web.controllers.result.NotificationCodeWrapper;
import it.polito.ai.polibox.web.controllers.result.NotificationWrapper;
import it.polito.ai.polibox.web.controllers.session.constants.SessionAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotificationController {

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
	
	public NotificationController() {
	}
	
	

	public DeviceService getDeviceService() {
		return deviceService;
	}



	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}



	public MasterNotificationService getMasterNotificationService() {
		return masterNotificationService;
	}



	public void setMasterNotificationService(
			MasterNotificationService masterNotificationService) {
		this.masterNotificationService = masterNotificationService;
	}



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



	public NotificationService getNotificationService() {
		return notificationService;
	}



	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}



	@RequestMapping(value="/notification/codes", method=RequestMethod.GET)
	public @ResponseBody Response getNotificationCodes(){
		List<NotificationCode> list=notificationCodeDao.findAll();
		NotificationCodeWrapper lista=new NotificationCodeWrapper(list);
		Response response=new Response();
		response.setResult(lista.getList().toArray());
		return response;
	} 
	
	@RequestMapping(value="/notifications/{year}/{month}/{day}",method=RequestMethod.GET)
	public @ResponseBody Response getNotifications(@PathVariable("year") int year,
													@PathVariable("month") int month,
													@PathVariable("day") int day,
													HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=new User();
		u.setId(userId);
		try {
			int i;
			Calendar d=Calendar.getInstance();
			d.set(Calendar.DAY_OF_MONTH, day);
			d.set(Calendar.MONTH, month);
			d.set(Calendar.YEAR, year);
			u=accountService.getUserAccountDetails(u);
			List<Notification> list=notificationService.getNotifications(u,d.getTime());
			if(list!=null){
				for(i=0;i<list.size();i++){
					response.getResult().add(new NotificationWrapper(list.get(i)));
				}
			}
			response.setStatus(Status.OK);
		} catch (UserNotFoundException e) {
			response.appendError(Status.FAIL, e.getMessage());
		}
		return response;
	} 
	
	@RequestMapping(value="/notifications/realtime",method=RequestMethod.GET,produces={"text/event-stream"})
	public void realTimeNotifications(
			HttpServletRequest req,
			HttpServletResponse res,
			HttpSession session){
		 // set content type
        res.setContentType("text/event-stream");
        res.setCharacterEncoding("UTF-8");
        res.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
        res.setHeader(HttpHeaders.PRAGMA, "no-cache");

		AsyncContext ac = req.startAsync();
//		System.out.println("timeout del context="+ac.getTimeout());
	     masterNotificationService.getRealTimeNotification().pushContext(
	    		 (Integer)session.getAttribute(SessionAttribute.LOGGED_DEVICE),ac); 
	     
	     
	     try {
	    	int u=((Integer)session.getAttribute(SessionAttribute.LOGGED_USER)).intValue();
			int d=((Integer)session.getAttribute(SessionAttribute.LOGGED_DEVICE)).intValue();
			Device device=deviceService.getDevice(d, u);
			masterNotificationService.notifica(new DeviceCollegato(device.getOwner(),device));
		} catch (Exception e) {
			ac.complete();
			masterNotificationService.getRealTimeNotification().removeContext(
					(Integer)session.getAttribute(SessionAttribute.LOGGED_DEVICE), 
					ac);
		} 
	}
}
