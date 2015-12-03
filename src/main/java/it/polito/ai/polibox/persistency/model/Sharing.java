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

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="sharing")
@AssociationOverrides({
	@AssociationOverride(name="pk.userTarget",joinColumns=@JoinColumn(name="to_user_id")),
	@AssociationOverride(name="pk.sharingResource",joinColumns={
											@JoinColumn(name="resource_id", referencedColumnName="resource_id"),
									        @JoinColumn(name="from_user_id", referencedColumnName="user_id")
											})
})
									
public class Sharing implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6390595797845910751L;

	@EmbeddedId
	private SharingId pk=new SharingId();
	
	@ManyToOne
	@JoinColumn(name="permission")
	private SharingMode permission=new SharingMode();
	
	@Column(name="from_request_time")
	private Date fromRequestTime;
	
	@Column(name="request_accepted")
	private boolean requestAccepted;
	
	@Column(name="to_show_request")
	private boolean toShowRequest;
	
	@Column(name="to_accepted_time")
	private Date toAcceptedTime;
	
	
	public SharingId getPk() {
		return pk;
	}

	public void setPk(SharingId pk) {
		this.pk = pk;
	}

	public SharingMode getPermission() {
		return permission;
	}

	public void setPermission(SharingMode permission) {
		this.permission = permission;
	}

	

	public Date getFromRequestTime() {
		return fromRequestTime;
	}

	public void setFromRequestTime(Date fromRequestTime) {
		this.fromRequestTime = fromRequestTime;
	}

	public boolean isRequestAccepted() {
		return requestAccepted;
	}

	public void setRequestAccepted(boolean requestAccepted) {
		this.requestAccepted = requestAccepted;
	}

	public boolean isToShowRequest() {
		return toShowRequest;
	}

	public void setToShowRequest(boolean toShowRequest) {
		this.toShowRequest = toShowRequest;
	}

	public Date getToAcceptedTime() {
		return toAcceptedTime;
	}

	public void setToAcceptedTime(Date toAcceptedTime) {
		this.toAcceptedTime = toAcceptedTime;
	}

	@Transient
	public User getOwnerUser(){
		return pk.getSharingResource().getUser();
	}
	
	@Transient
	public User getTargetUser(){
		return pk.getUserTarget();
	}
	
	@Transient
	public Resource getResource(){
		return pk.getSharingResource().getResource();
	}
	
	
	
}
