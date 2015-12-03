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

import it.polito.ai.polibox.persistency.model.Sharing;

import java.util.Date;

public class ResourceSharingWrapper{
	private int permission;
	private Date fromRequestTime;
	private boolean requestAccepted;
	private boolean toShowRequest;
	private Date toAcceptedTime;
	private int owner;
	private int target;
	private String ownerName;
	private String targetName;
	private int resource;
	private String resourceName;
	
	public ResourceSharingWrapper(Sharing s) {
		resource=s.getResource().getId();
		int index=s.getResource().getName().lastIndexOf("/");
		if(index>0){
			resourceName=s.getResource().getName().substring(index);
		}else{
			resourceName=s.getResource().getName();
		}
		permission=s.getPermission().getId();
		fromRequestTime=s.getFromRequestTime();
		requestAccepted=s.isRequestAccepted();
		toShowRequest=s.isToShowRequest();
		toAcceptedTime=s.getToAcceptedTime();
		owner=s.getOwnerUser().getId();
		target=s.getTargetUser().getId();
		ownerName=s.getOwnerUser().getEmail();
		targetName=s.getTargetUser().getEmail();
	}
	
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
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
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public int getTarget() {
		return target;
	}
	public void setTarget(int target) {
		this.target = target;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}
}
