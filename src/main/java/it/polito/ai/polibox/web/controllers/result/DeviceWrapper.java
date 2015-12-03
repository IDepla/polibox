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

import java.util.Date;

import it.polito.ai.polibox.persistency.model.Device;
import it.polito.ai.polibox.persistency.model.NotificationOptionInterface;

public class DeviceWrapper {
	private int id;
	private String name;
	private boolean deletable;
	private Date lastPing;
	private Date lastLogin;
	private Date lastCompleteSync;
	private NotificationOptionWrapper notification;

	public NotificationOptionWrapper getNotification() {
		return notification;
	}

	public void setNotification(NotificationOptionWrapper notification) {
		this.notification = notification;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public DeviceWrapper(){
	}
	
	public DeviceWrapper(Device d,NotificationOptionInterface[] notifiche) {
		id=d.getId();
		name=d.getDevice_name();
		deletable=d.isDeviceDeletable();
		lastLogin=d.getLastLogin();
		lastPing=d.getLastPing();
		lastCompleteSync=d.getLastCompleteSync();
		notification=new NotificationOptionWrapper(notifiche);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public Date getLastPing() {
		return lastPing;
	}

	public void setLastPing(Date lastPing) {
		this.lastPing = lastPing;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastCompleteSync() {
		return lastCompleteSync;
	}

	public void setLastCompleteSync(Date lastCompleteSync) {
		this.lastCompleteSync = lastCompleteSync;
	}

}
