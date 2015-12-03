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

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.exception.DeviceExistsException;
import it.polito.ai.polibox.persistency.model.service.DeviceService;
import it.polito.ai.polibox.web.controllers.inputform.DeviceName;
import it.polito.ai.polibox.web.controllers.inputform.NotificationOption;
import it.polito.ai.polibox.web.controllers.inputform.validation.DeviceNameValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.NotificationOptionsValidator;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;
import it.polito.ai.polibox.web.controllers.result.DeviceWrapper;
import it.polito.ai.polibox.web.controllers.result.NotificationOptionWrapper;
import it.polito.ai.polibox.web.controllers.session.constants.SessionAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/device")
public class DeviceController {

	@Autowired
	private DeviceService deviceService;
	
	public DeviceController() {
		
	}
	@InitBinder("deviceName")
    protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new DeviceNameValidator());
    }
	@InitBinder("options")
    protected void initOptionsBinder(WebDataBinder binder) {
		binder.setValidator(new NotificationOptionsValidator());
    }

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody Response list(HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		
		List<it.polito.ai.polibox.persistency.model.Device> list=deviceService.getDeviceList(userId);
 		int i;
 		for(i=0;i<list.size();i++){
 			response.getResult().add(new DeviceWrapper(list.get(i),deviceService.getDeviceOptions(list.get(i))));
 		}
		response.setStatus(Status.OK);
		return response;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody Response add( @Valid DeviceName deviceName,
										BindingResult bindingResult,
										HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		it.polito.ai.polibox.persistency.model.Device d=deviceService.createDevice(deviceName.getName(), userId);
		DeviceWrapper dw=new DeviceWrapper(d,deviceService.getDeviceOptions(d));
		response.getResult().add(dw);
		response.setStatus(Status.OK);
		return response;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public @ResponseBody Response rename(@PathVariable("id") int id,
			 								@Valid DeviceName deviceName,
											HttpSession session,
											BindingResult bindingResult){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try{
			it.polito.ai.polibox.persistency.model.Device d=deviceService.renameDevice(id,deviceName.getName(), userId);
			DeviceWrapper dw=new DeviceWrapper(d,deviceService.getDeviceOptions(d));
			response.getResult().add(dw);
			response.setStatus(Status.OK);
		}catch(DeviceExistsException e){
			response.appendError(Status.BAD_PARAM, e.getMessage());
			response.setStatus(Status.FAIL);
		}
		return response;
	}
		
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public @ResponseBody Response remove(@PathVariable("id") int id,
							HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try{
			deviceService.deleteDevice(id, userId);
			response.setStatus(Status.OK);
		}catch(DeviceExistsException e){
			response.appendError(Status.BAD_PARAM, e.getMessage());
			response.setStatus(Status.FAIL);
		}
		return response;
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public void download(@PathVariable("id") int id,//TODO
							HttpSession session){
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		
	}
	
	@RequestMapping(value="/{id}/notifications", method=RequestMethod.GET)
	public @ResponseBody Response getNotificationsDevice(@PathVariable("id") int id,
										HttpSession session){
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);

		User user=new User();
		user.setId(userId.intValue());
		try {
			it.polito.ai.polibox.persistency.model.Device d=deviceService.getDevice(id, userId);
			NotificationOptionWrapper nOw=new NotificationOptionWrapper(deviceService.getDeviceOptions(d));
			response.setStatus(Status.OK);
			response.getResult().add(nOw);
		} catch (Exception e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"notification codes not found");
			response.appendError("798",e.getMessage());
		}
		return response;
	}
	
		
	@RequestMapping(value="/{id}/notifications",method=RequestMethod.PUT, consumes={"application/json"})
	public @ResponseBody Response modifyNotificationsDevice(@Valid @RequestBody NotificationOption options,
															BindingResult bindingResult,
															@PathVariable("id") int id,
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
			deviceService.modifyNotificationOption(id, userId, options.getNotificationCode(), options.isEnabled());
			response.setStatus(Status.OK);
		} catch (Exception e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"notification codes not found");
			response.appendError("798",e.getMessage());
		}
		return response;
	}
}
