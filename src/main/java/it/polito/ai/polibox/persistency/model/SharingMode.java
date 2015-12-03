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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="sharing_mode")
public class SharingMode implements Serializable{

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDefault_flag() {
		return default_flag;
	}

	public void setDefault_flag(boolean default_flag) {
		this.default_flag = default_flag;
	}

	public Set<Sharing> getSharing() {
		return sharing;
	}

	public void setSharing(Set<Sharing> sharing) {
		this.sharing = sharing;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6787734281185140146L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="description",length=45,nullable=false)
	private String description;
	
	@Column(name="default")
	private boolean default_flag;
	
	@OneToMany(
			mappedBy="permission", 
			fetch=FetchType.LAZY
			) 
	private Set<Sharing> sharing=new HashSet<Sharing>();
}
