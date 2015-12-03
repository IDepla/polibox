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

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.AssociationOverrides;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

@Entity
@Table(name="notification_device")
@AssociationOverrides({
	@AssociationOverride(name = "pk.device", 
		joinColumns = @JoinColumn(name = "device_id")),
	@AssociationOverride(name = "pk.notificationCode", 
		joinColumns = @JoinColumn(name = "notification_code")) })
public class DeviceNotification implements Serializable,NotificationOptionInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1932059423790021278L;

	@EmbeddedId
	private DeviceNotificationId pk=new DeviceNotificationId();
	
	@Column(name="enabled")
	private boolean enabled;

	public DeviceNotificationId getPk() {
		return pk;
	}

	@Transient
	public Device getDevice(){
		return pk.getDevice();
	}
	
	public void setDevice(Device device){
		pk.setDevice(device);
	}
	
	public void setPk(DeviceNotificationId pk) {
		this.pk = pk;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setNotificationCode(NotificationCode notificationCode){
		pk.setNotificationCode(notificationCode);
	}
	@Transient
	public int getNotificationCode(){
		return pk.getNotificationCode().getCode();
	}

	
	@Override
	public boolean equals(Object obj) {
		if( obj!=null && 
			obj instanceof DeviceNotification &&
			pk.getDevice()!=null &&
			pk.getNotificationCode()!= null &&
			((DeviceNotification)obj).getPk()!=null && 
			((DeviceNotification)obj).getPk().getDevice()==pk.getDevice() &&
			((DeviceNotification)obj).getPk().getNotificationCode()==pk.getNotificationCode()){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		//if(id != 0)
		//	return id;
		return 0;
	}
}
