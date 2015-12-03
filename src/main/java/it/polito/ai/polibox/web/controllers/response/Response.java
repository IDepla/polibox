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
package it.polito.ai.polibox.web.controllers.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.ObjectError;


public class Response {

	private String status;
	private List<Error> errors;
	private List<Message> messages;
	private List<Object> result;
	private String redirect;
	
	public Response() {
		messages=new ArrayList<Message>();
		errors=new ArrayList<Error>();
		result=new ArrayList<Object>();
	}
	

	public void appendAllErrors(List<ObjectError> list){
		int i=0;
		for (i=0;i<list.size();i++){
			errors.add(new Error(list.get(i)));
		}
		
	}
	
	public void appendError(String code,String message){
		errors.add(new Error(code,message));
	}
	
	public void appendMessage(String code,String message){
		messages.add(new Message(code,message));
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

	public List<Error> getErrors() {
		return errors;
	}



	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}



	public List<Message> getMessages() {
		return messages;
	}



	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}



	public List<Object> getResult() {
		return result;
	}
	public void setResult(List<Object> result) {
		this.result = result;
	}
	public void setResult(Object[] result) {
		int i;
		for(i=0;i<result.length;i++){
			this.result.add(result[i]);
		}
	}
	public String getRedirect() {
		return redirect;
	}
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	

	
	class Message{
		String message;
		String code;
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public Message(String code, String message) {
			setMessage(message);
			setCode(code);
		}
		public Message() {
		
		}
	}
	
	class Error{
		String message;
		String code;
		public Error() {
			
		}
		public Error(ObjectError error) {
			setCode(error.getCode());
			setMessage(error.getDefaultMessage());
		}
		public Error(String code, String message){
			setCode(code);
			setMessage(message);
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
	}	
	
}
