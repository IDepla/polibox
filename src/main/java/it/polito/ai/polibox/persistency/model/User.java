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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="user", 
	   uniqueConstraints={
		@UniqueConstraint(columnNames="email")
		}
)
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5491849821327515479L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="email",length=255,nullable=false,unique=true)
	private String email;

	@Column(length=61,nullable=false)
	private String password;
	
	@Column(length=45)
	private String name;
	
	@Column(length=45)
	private String surname;
	
	@Column(name="enabled")
	private boolean enabled;
	
	@Column(length=45)
	private String company;
	
	@Column(length=45)
	private String position;
	
	@Column(length=45)
	private String mobile;
	
	@Column(name="last_login")
	private Date lastLogin;
	
	@Column(name="sign_in_time")
	private Date signInTime;
	
	@Column(name="last_action_time")
	private Date lastActionTime;
	
	@Column(nullable=false)
	private boolean deleted;
	
	@OneToMany(
			cascade=CascadeType.ALL,
			mappedBy="owner"
			)
	private Set<Device> devices=new HashSet<Device>();
	
	
	@OneToMany(
			cascade=CascadeType.ALL,
			mappedBy="pk.user"
			)
	@OrderBy("notification_code ASC")
	private Set<EmailNotification> emailNotification=new HashSet<EmailNotification>();
	
	@OneToMany(
			cascade=CascadeType.ALL,
			mappedBy="user"
			)
	@OrderBy("notification_code ASC")
	private Set<Notification> notification=new HashSet<Notification>();
	
	@OneToMany(
			cascade=CascadeType.ALL,
			mappedBy="pk.userTarget"
			)	
	private Set<Sharing> sharedWithYouResources=new HashSet<Sharing>();
	
	@OneToMany( 
			mappedBy="pk.user"
			)
	private Set<ResourceOwners> ownedResources=new HashSet<ResourceOwners>();
	
	@OneToMany(
			mappedBy="creator" 
			)
	private Set<ResourceVersion> mineVersions=new HashSet<ResourceVersion>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getSignInTime() {
		return signInTime;
	}

	public void setSignInTime(Date signInTime) {
		this.signInTime = signInTime;
	}

	public Date getLastActionTime() {
		return lastActionTime;
	}

	public void setLastActionTime(Date lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}

	public Set<EmailNotification> getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(Set<EmailNotification> emailNotification) {
		this.emailNotification = emailNotification;
	}

	public Set<Notification> getNotification() {
		return notification;
	}

	public void setNotification(Set<Notification> notification) {
		this.notification = notification;
	}


	

	public Set<Sharing> getSharedWithYouResources() {
		return sharedWithYouResources;
	}

	public void setSharedWithYouResources(Set<Sharing> sharedWithYouResources) {
		this.sharedWithYouResources = sharedWithYouResources;
	}

	public Set<ResourceOwners> getOwnedResources() {
		return ownedResources;
	}

	public void setOwnedResources(Set<ResourceOwners> ownedResources) {
		this.ownedResources = ownedResources;
	}

	public Set<ResourceVersion> getMineVersions() {
		return mineVersions;
	}

	public void setMineVersions(Set<ResourceVersion> mineVersions) {
		this.mineVersions = mineVersions;
	}
	
	
}
