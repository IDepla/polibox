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
package it.polito.ai.polibox.persistency.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.CascadeType;

@Entity
@Table(name="notification_code")
public class NotificationCode {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="code")
	private int code;
	@Column(name="description",length=45,nullable=false)
	private String description;
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="pk.notificationCode")
	private Set<EmailNotification> emailNotifications=new HashSet<EmailNotification>();
	
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="pk.notificationCode")
	private Set<DeviceNotification> deviceNotifications=new HashSet<DeviceNotification>();


	@OneToMany(mappedBy="notificationCode",fetch=FetchType.LAZY)
	private Set<Notification> notifications=new HashSet<Notification>();

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
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

	public Set<EmailNotification> getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(Set<EmailNotification> emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	public Set<DeviceNotification> getDeviceNotifications() {
		return deviceNotifications;
	}

	public void setDeviceNotifications(Set<DeviceNotification> deviceNotifications) {
		this.deviceNotifications = deviceNotifications;
	}
}
