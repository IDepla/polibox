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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="resource")
public class Resource implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5861494953003270526L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="name",length=1024,nullable=false)
	private String name;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Column(name="last_modify")
	private Date lastModify;
	
	@Column(name="writing_lock")
	private boolean writingLock; 
	
	@Column(name="is_directory")
	private boolean isDirectory;
	
	@OneToMany(mappedBy="pk.resource")
	private Set<ResourceOwners> owners=new HashSet<ResourceOwners>();
	
	@OneToMany(mappedBy="pk.resource")
	private Set<ResourceVersion> resourceVersions=new HashSet<ResourceVersion>();
	
	public int getId() {
		return id;
	}

	public Date getLastModify() {
		return lastModify;
	}

	public void setLastModify(Date lastModify) {
		this.lastModify = lastModify;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isWritingLock() {
		return writingLock;
	}

	public void setWritingLock(boolean writingLock) {
		this.writingLock = writingLock;
	}

	public Set<ResourceOwners> getOwners() {
		return owners;
	}

	public void setOwners(Set<ResourceOwners> owners) {
		this.owners = owners;
	}


	public Set<ResourceVersion> getResourceVersions() {
		return resourceVersions;
	}

	public void setResourceVersions(Set<ResourceVersion> resourceVersions) {
		this.resourceVersions = resourceVersions;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	
	/**
	 * da usare solo con oggetti recuperati dal db
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Resource){
			Resource t=(Resource) obj;
			if(id==t.getId())
				return true;
		}
		return false;
	}
}
