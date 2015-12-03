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
import java.sql.Blob;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="resource_version_chunk")
@AssociationOverrides({
	@AssociationOverride(name="pk.resourceVersion",joinColumns={
			@JoinColumn(name="resource_id", referencedColumnName="resource_id"),
	        @JoinColumn(name="version", referencedColumnName="version")}),
	@AssociationOverride(name="pk.number",joinColumns=@JoinColumn(name="chunk_number"))
})
public class ResourceChunk implements Serializable{

	
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3022993641942784028L;

	@EmbeddedId
	private ResourceChunkId pk;
	
	@Column(name="digest",length=129)
	private String digest;
	
	@Column(name="data",columnDefinition="MEDIUMBLOB")
	private Blob data;
	
	@Column(name="real_size")
	private int size;
	
	public ResourceChunk() {
		pk= new ResourceChunkId();
	}
	
	public ResourceChunk(Resource r, int version, int chunkNumber){
		pk.getResourceVersion().getPk().setResource(r);
		pk.getResourceVersion().getPk().setVersion(version);
		pk.setNumber(chunkNumber);
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	public ResourceChunkId getPk() {
		return pk;
	}

	public void setPk(ResourceChunkId pk) {
		this.pk = pk;
	}
	

	@Override
	public boolean equals(Object obj) {
		return this.getPk().equals(obj);
	}
}
