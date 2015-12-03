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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PropertiesWrapper {

	private String name;
	private int size;
	private int version;
	private int resource;
	private List<Owner> owner;
	private List<ResourceSharingWrapper> shared;
	private List<Version> history;
	
	public List<ResourceSharingWrapper> getShared() {
		return shared;
	}

	public void setShared(List<ResourceSharingWrapper> shared) {
		this.shared = shared;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Owner> getOwner() {
		return owner;
	}

	public void setOwner(List<Owner> owner) {
		this.owner = owner;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	

	

	public List<Version> getHistory() {
		return history;
	}

	public void setHistory(List<Version> history) {
		this.history = history;
	}

	public void addOwner(String name, String email, Date from){
		Owner o=new Owner();
		o.setEmail(email);
		o.setName(name);
		o.setFrom(from);
		owner.add(o);
	}

	public void addShare(Sharing s){
		shared.add(new ResourceSharingWrapper(s));
	}
	
	public void addVersion(int version,int size, Date from,String uploader){
		Version v=new Version();
		v.setFrom(from);
		v.setSize(size);
		v.setUploader(uploader);
		v.setVersion(version);
		history.add(v);
	}
	
	public PropertiesWrapper() {
		history= new ArrayList<PropertiesWrapper.Version>();
		shared=new ArrayList<ResourceSharingWrapper>();
		owner=new ArrayList<PropertiesWrapper.Owner>();
	}
	
	public class Owner{
		private String name;
		private String email;
		private Date from;
		public Owner() {
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public Date getFrom() {
			return from;
		}
		public void setFrom(Date from) {
			this.from = from;
		}
		
	}

	
	public class Version{
		private int version;
		private int size;
		private Date from;
		private String uploader;
		public Version() {
			
		}
		public int getVersion() {
			return version;
		}
		public void setVersion(int version) {
			this.version = version;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		public Date getFrom() {
			return from;
		}
		public void setFrom(Date from) {
			this.from = from;
		}
		public String getUploader() {
			return uploader;
		}
		public void setUploader(String uploader) {
			this.uploader = uploader;
		}
	}
}
