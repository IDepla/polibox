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
package it.polito.ai.polibox.web.handlers;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.DeviceDao;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.session.constants.SessionAttribute;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component("ajaxAuthenticationSuccessHandler")
public class AuthenticationSuccessHandler extends
		SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DeviceDao deviceDao;
	
	
	public DeviceDao getDeviceDao() {
		return deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public AuthenticationSuccessHandler() {
		
	}

	public AuthenticationSuccessHandler(String defaultTargetUrl) {
		super(defaultTargetUrl);
		
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		HttpSession session;
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf8");

	    ObjectMapper mapper = new ObjectMapper();
	    
        Response rsp=new Response();
		rsp.setStatus("200");
		rsp.appendMessage("200", "authentication success");
		User user;
		try {
			user = userDao.findByEmail(authentication.getName());
			user.setLastLogin(new Date());
			user=userDao.save(user);
			session=request.getSession();
			session.setAttribute(SessionAttribute.LOGGED_USER, new Integer(user.getId()));
			String key=(String) request.getAttribute("identification_key");
			Device device=null;
			if(key==null){
				device=deviceDao.getBrowserDevice(user);
			}else{
				device=deviceDao.getDeviceByUserAndKey(user, key);
			}
			if(device==null) throw new UserNotFoundException();
			session.setAttribute(SessionAttribute.LOGGED_DEVICE, new Integer(device.getId()));
		} catch (UserNotFoundException e) {
			
			rsp.appendError("400", "very strange it was authenticated but not retrieved");
		}
		
		
        
		response.getWriter().write(mapper.writeValueAsString(rsp));
        response.flushBuffer();
	}
}
