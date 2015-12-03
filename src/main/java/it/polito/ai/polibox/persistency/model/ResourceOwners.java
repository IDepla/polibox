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
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="resource_owners")
@AssociationOverrides({
	@AssociationOverride(name="pk.user",joinColumns=@JoinColumn(name="user_id")),
	@AssociationOverride(name="pk.resource",joinColumns=@JoinColumn(name="resource_id"))
})
public class ResourceOwners implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3374197379695172212L;

	@EmbeddedId
	private ResourceOwnersId pk=new ResourceOwnersId();
	
	@Column(name="`from`")
	private Date from;
	
	@OneToMany(
			cascade=CascadeType.ALL,
			mappedBy="pk.sharingResource"
			)
	private List<Sharing> sharing;
	
	public ResourceOwnersId getPk() {
		return pk;
	}

	public void setPk(ResourceOwnersId pk) {
		this.pk = pk;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public void setResource(Resource resource){
		pk.setResource(resource);
	}
	
	@Transient
	public Resource getResource(){
		return pk.getResource();
	}
	
	
	public void setUser(User user){
		pk.setUser(user);
	}
	
	@Transient
	public User getUser(){
		return pk.getUser();
	}

	public List<Sharing> getSharing() {
		return sharing;
	}

	public void setSharing(List<Sharing> sharing) {
		this.sharing = sharing;
	}
	
	
	
}
