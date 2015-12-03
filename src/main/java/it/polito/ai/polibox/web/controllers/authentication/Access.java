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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polito.ai.polibox.web.controllers.response.Response;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Autenticazione automatica.
 * 
 * @author "Igor Deplano"
 *
 */
@Controller
@RequestMapping("/authenticate")
public class Access {

	@RequestMapping(method=RequestMethod.GET,value="/access/granted")
	public @ResponseBody Response  accessGranted(){
		Response rsp=new Response();
		
		rsp.appendMessage("200", "access granted");
		rsp.setStatus("200");
		return rsp; 
	}

	
	@RequestMapping(method=RequestMethod.GET,value="/access/denied")
	public @ResponseBody Response  accessDenied(){
		Response rsp=new Response();
		
		rsp.appendError("400","access denied");
		rsp.setStatus("400");
		return rsp; 
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/login")
	public @ResponseBody Response  unauthorized(HttpServletRequest req,
												HttpServletResponse res){
		Response rsp=new Response();
		
		res.setStatus(401);
		rsp.appendError("401","login required");
		rsp.setStatus("401");
		return rsp; 
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/ping")
	public void ping(HttpServletRequest req,
										HttpServletResponse res){
		res.setStatus(200);
	}
}
