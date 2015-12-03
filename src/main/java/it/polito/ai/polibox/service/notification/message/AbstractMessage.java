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
package it.polito.ai.polibox.service.notification.message;

import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.service.notification.NotificationMessageInterface;

import java.util.Date;

public abstract class AbstractMessage implements 
							NotificationMessageInterface
							{
	protected int id;
	protected int target;
	protected int code;
	protected Date time;
	protected User u;
	protected Object o;
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getTarget() {
		return target;
	}


	public void setTarget(int target) {
		this.target = target;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}


	public String getMessage() {
		return messageGenerator();
	}


	public Object getPossibleObject(){
		return o;
	}
	
	public User getUser(){
		return u;
	}
	
	protected abstract String messageGenerator();
	
	
	/**
	 * di default è solo i device dell'utente e le email dell'utente
	 */
	public String getDeviceToNotifyRealtime() {
		String s="select d.id " +
				"from device d, notification_device nd " +
				"where " +
				"d.owner="+u.getId()+" and " +
				"nd.device_id=d.id and " +
				"nd.notification_code="+code+" and " +
				"nd.enabled=true";
		return s;
	}

	public String getEmailsToNotify() {
		String s="select u.email " +
				"from user u, email_notification en " +
				"where " +
				"en.user_id="+u.getId()+" and " +
				"en.notification_code="+code+" and " +
				"en.enabled=true and " +
				"en.user_id=u.id";
		return s;
	}
	
	public Object getData() {
		return new DataContainer(getId(),getCode(),getMessage(),getTime());
	}
	public String getEvent() {
		return ""+this.code;
	}
	public String getCommand() {
		return "";
	}
	
	public class DataContainer{
		private int id;
		private int code;
		private String description;
		private Date creationTime;

		public DataContainer( String msg, Date time) {
			description=msg;
			creationTime=time;
			if(time == null)
				creationTime=new Date();
		}
		
		public DataContainer(int id,int code, String msg, Date time) {
			this(msg, time);
			this.id=id;
			this.code=code;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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

		public Date getCreationTime() {
			return creationTime;
		}

		public void setCreationTime(Date creationTime) {
			this.creationTime = creationTime;
		}

		
	}
}
