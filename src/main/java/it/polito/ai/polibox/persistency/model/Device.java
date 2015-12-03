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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name="device")
public class Device implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 570321607093342706L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="device_name",length=45,nullable=false)
	private String device_name;
	
	@Column(name="file_list_digest",length=45,nullable=false)
	private String fileListDigest;
	
	@Column(name="last_ping")
	private Date lastPing;
	
	@Column(name="last_login")
	private Date lastLogin;
	
	@Column(name="last_complete_sync")
	private Date lastCompleteSync;
	
	@Column(name="device_time_sync")
	private Date devicetimeSync;
	
	@Column(name="device_deletable")
	private boolean deviceDeletable;
	
	@Column(name="random_salt",length=45,nullable=false)
	private String randomSalt;
	
	@Column(name="auto_authentication_key",length=61,nullable=false)
	private String autoAuthenticationKey;
	
	@Column(name="last_auto_authentication_timestamp")
	private Date lastAutoAuthenticationTimestamp;
	
	@Column(name="device_max_space")
	private int deviceMaxSpace;
	
	@OneToMany(
			mappedBy="pk.device"			
			)
	private Set<ResourceVersionInDevice> resourceVersionInDevice=new HashSet<ResourceVersionInDevice>();
	
	@OneToMany(mappedBy="pk.device")
	private Set<DeviceNotification> deviceNotifications=new HashSet<DeviceNotification>();
	
	@ManyToOne
	@JoinColumn(name="owner",
				nullable=false,
				referencedColumnName="id")
	private User owner;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}



	public String getFileListDigest() {
		return fileListDigest;
	}

	public void setFileListDigest(String fileListDigest) {
		this.fileListDigest = fileListDigest;
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

	public Date getDevicetimeSync() {
		return devicetimeSync;
	}

	public void setDevicetimeSync(Date devicetimeSync) {
		this.devicetimeSync = devicetimeSync;
	}

	public boolean isDeviceDeletable() {
		return deviceDeletable;
	}

	public void setDeviceDeletable(boolean deviceDeletable) {
		this.deviceDeletable = deviceDeletable;
	}


	public String getRandomSalt() {
		return randomSalt;
	}

	public void setRandomSalt(String randomSalt) {
		this.randomSalt = randomSalt;
	}

	public String getAutoAuthenticationKey() {
		return autoAuthenticationKey;
	}

	public void setAutoAuthenticationKey(String autoAuthenticationKey) {
		this.autoAuthenticationKey = autoAuthenticationKey;
	}

	public Date getLastAutoAuthenticationTimestamp() {
		return lastAutoAuthenticationTimestamp;
	}

	public void setLastAutoAuthenticationTimestamp(
			Date lastAutoAuthenticationTimestamp) {
		this.lastAutoAuthenticationTimestamp = lastAutoAuthenticationTimestamp;
	}

	public int getDeviceMaxSpace() {
		return deviceMaxSpace;
	}

	public void setDeviceMaxSpace(int deviceMaxSpace) {
		this.deviceMaxSpace = deviceMaxSpace;
	}

	public Set<ResourceVersionInDevice> getResourceVersionInDevice() {
		return resourceVersionInDevice;
	}

	public void setResourceVersionInDevice(
			Set<ResourceVersionInDevice> resourceVersionInDevice) {
		this.resourceVersionInDevice = resourceVersionInDevice;
	}

	public Set<DeviceNotification> getDeviceNotifications() {
		return deviceNotifications;
	}

	public void setDeviceNotifications(Set<DeviceNotification> deviceNotifications) {
		this.deviceNotifications = deviceNotifications;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null && 
			obj instanceof Device && 
			((Device) obj).getId()==this.getId()){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if(id != 0)
			return id;
		return 0;
	}
}
