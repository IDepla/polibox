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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class ResourceChunkId  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6193577322782394282L;
	
	@ManyToOne
	private ResourceVersion resourceVersion;
	
	@Column(name="chunk_number")
	private int number;
	
	public ResourceChunkId() {
		resourceVersion=new ResourceVersion();
	}
	


	public ResourceVersion getResourceVersion() {
		return resourceVersion;
	}



	public void setResourceVersion(ResourceVersion resourceVersion) {
		this.resourceVersion = resourceVersion;
	}



	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * da usare solo con roba presa dal db
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj!=null){
			if(obj instanceof ResourceChunkId){
				ResourceChunkId t=(ResourceChunkId) obj;
				if(resourceVersion.equals(t.getResourceVersion()) && number==t.getNumber())
					return true;
			}else if(obj instanceof ResourceChunk){
				ResourceChunk t=(ResourceChunk) obj;
				return this.equals(t.getPk());
			}
		}
		return false;
	}
	
	

}
