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
package it.polito.ai.polibox.web.controllers.result;

import java.util.ArrayList;

import java.util.List;


import it.polito.ai.polibox.persistency.model.NotificationCode;

public class NotificationCodeWrapper {

	private List<Notifica> list;


	public NotificationCodeWrapper() {	
		list=new ArrayList<NotificationCodeWrapper.Notifica>();
	}
	
	public NotificationCodeWrapper(NotificationCode notificationCode) {	
		this();
		list.add(new Notifica(notificationCode.getCode(), notificationCode.getDescription()));
	}
	
	public NotificationCodeWrapper(List<NotificationCode> notificationCodes) {	
		this();
		int i;
		for(i=0;i<notificationCodes.size();i++){
			list.add(new Notifica(notificationCodes.get(i).getCode(),notificationCodes.get(i).getDescription()));
		}
	}
	
	
	public List<Notifica> getList() {
		return list;
	}

	public void setList(List<Notifica> list) {
		this.list = list;
	}


	class Notifica{
		private int code;
		private String description;
		public Notifica(int code, String description) {
			this.code=code;
			this.description=description;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
	}
}
