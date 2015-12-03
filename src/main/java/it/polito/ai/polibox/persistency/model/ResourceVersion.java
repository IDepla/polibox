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

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;


@Entity
@Table(name="resource_version")
@AssociationOverrides({
	@AssociationOverride(name="pk.version",joinColumns=@JoinColumn(name="version")),
	@AssociationOverride(name="pk.resource",joinColumns=@JoinColumn(name="resource_id"))
})
public class ResourceVersion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8890668381517346851L;
	@EmbeddedId
	private ResourceVersionId pk=new ResourceVersionId();
	
	@Column(name="digest",length=129,nullable=false)
	private String digest;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Column(name="chunk_number")
	private int chunkNumber;
	
	@Column(name="size")
	private int size;
	
	@Column(name="mime",length=255)
	private String mime;
	
	@Column(name="creation_time")
	private Date creationTime;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User creator=new User();
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="pk.resourceVersion")
	private Set<ResourceVersionInDevice> resourceVersionInDevices = new HashSet<ResourceVersionInDevice>();
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="pk.resourceVersion")
	private Set<ResourceChunk> resourceChunkes= new HashSet<ResourceChunk>();



	public ResourceVersionId getPk() {
		return pk;
	}


	public void setPk(ResourceVersionId pk) {
		this.pk = pk;
	}


	public String getDigest() {
		return digest;
	}


	public void setDigest(String digest) {
		this.digest = digest;
	}


	public boolean isDeleted() {
		return deleted;
	}


	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


	public int getChunkNumber() {
		return chunkNumber;
	}


	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public String getMime() {
		return mime;
	}


	public void setMime(String mime) {
		this.mime = mime;
	}


	public Date getCreationTime() {
		return creationTime;
	}


	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}


	public User getCreator() {
		return creator;
	}


	public void setCreator(User creator) {
		this.creator = creator;
	}


	public Set<ResourceVersionInDevice> getResourceVersionInDevices() {
		return resourceVersionInDevices;
	}


	public void setResourceVersionInDevices(
			Set<ResourceVersionInDevice> resourceVersionInDevices) {
		this.resourceVersionInDevices = resourceVersionInDevices;
	}


	public Set<ResourceChunk> getResourceChunkes() {
		return resourceChunkes;
	}


	public void setResourceChunkes(Set<ResourceChunk> resourceChunkes) {
		this.resourceChunkes = resourceChunkes;
	} 
	/**
	 * da usare solo con oggetti presi dal database
	 */
	@Override
	public boolean equals(Object obj) {
		return this.getPk().equals(obj);
	}
}
