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

import it.polito.ai.polibox.persistency.model.ReadNotificationOptionInterface;

import java.util.ArrayList;
import java.util.List;

public class NotificationOptionWrapper {
	
	private List<NotificationOption> list;
	
	
	public List<NotificationOption> getList() {
		return list;
	}

	public void setList(List<NotificationOption> list) {
		this.list = list;
	}

	public NotificationOptionWrapper() {
		list=new ArrayList<NotificationOptionWrapper.NotificationOption>();
	}
	
	public NotificationOptionWrapper(ReadNotificationOptionInterface[] options) {
		this();
		int i;
		for(i=0;i<options.length;i++){
			list.add(new NotificationOption(options[i].getNotificationCode(),
					options[i].isEnabled()));
		}
	}
	class NotificationOption{
		private int code;
		private boolean option;
		public NotificationOption(int code, boolean option) {
			this.code=code;
			this.option=option;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public boolean isOption() {
			return option;
		}
		public void setOption(boolean option) {
			this.option = option;
		}	
	}
}
