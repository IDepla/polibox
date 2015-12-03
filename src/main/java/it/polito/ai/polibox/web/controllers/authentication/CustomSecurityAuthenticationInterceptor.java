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


import it.polito.ai.polibox.persistency.model.DeviceLogin;
import it.polito.ai.polibox.persistency.model.dao.DeviceLoginDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * questo filtro si occupa di autenticare ogni richiesta che viene fatta per il
 * servizio rest.
 * 
 * @author "Igor Deplano"
 * 
 */
@Component("customSecurityAuthenticationInterceptor")
public class CustomSecurityAuthenticationInterceptor implements HandlerInterceptor {
	
	@Autowired
	private DeviceLoginDao deviceLoginDao;
	
	public CustomSecurityAuthenticationInterceptor() {
	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		DeviceLogin d=deviceLoginDao.authenticateDevice(Integer.parseInt(request.getHeader("user")), 
				Integer.parseInt(request.getHeader("device")), 
				request.getHeader("password"));
		if(d!=null){
			if(d.getUser()==Integer.parseInt(request.getHeader("user")))
				return true;
		}
		return false;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	public DeviceLoginDao getDeviceLoginDao() {
		return deviceLoginDao;
	}

	public void setDeviceLoginDao(DeviceLoginDao deviceLoginDao) {
		this.deviceLoginDao = deviceLoginDao;
	}


	

	
	
}
