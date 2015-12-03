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
import javax.persistence.Table;

@Entity
@Table(name="resource_version_in_device")
@AssociationOverrides({
	@AssociationOverride(name="pk.resourceVersion",joinColumns={
			@JoinColumn(name="resource_id", referencedColumnName="resource_id"),
	        @JoinColumn(name="version", referencedColumnName="version")}),
	@AssociationOverride(name="pk.device",joinColumns=@JoinColumn(name="device_id"))
})
public class ResourceVersionInDevice implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4263807460772304638L;

	@EmbeddedId
	private ResourceVersionInDeviceId pk=new ResourceVersionInDeviceId();

	
	@Column(name="last_sync")
	private Date lastSync;

	@Column(name="version",insertable=false, updatable=false)
	private int version;

	public ResourceVersionInDeviceId getPk() {
		return pk;
	}

	public void setPk(ResourceVersionInDeviceId pk) {
		this.pk = pk;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}
	
	
}
