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

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.User;

import java.util.Date;

public class FullResource{

	private int id;
	
	private int version;
	
	private String name;
	
	private boolean deleted;
	
	private boolean writingLock; 
	
	private boolean isDirectory;
	
	private Date serverLastModify;
	
	private String digest;
	
	private boolean toSynchronize;
	
	private int chunkNumber;
	
	private int size;
	
	private String mime;
	
	private Date creationTime;
	
	private int owner;
	
	private int parent;
	
	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public FullResource() {
		
	}
	
	public FullResource(FileUploadWrapper f,User o){
		id=f.getId();
		mime=f.getMime();
		digest=f.getDigest();
		name=f.getName();
		setServerLastModify(f.getCreationTime());
		chunkNumber=f.getChunkNumber();
		version=f.getVersion();
		size=f.getSize();
		isDirectory=false;
		deleted=false;
		owner=o.getId();
	}
	
	public FullResource(Resource r){
		setId(r.getId());
		setName(r.getName());
		setDeleted(r.isDeleted());
		setDirectory(r.isDirectory());
		setWritingLock(r.isWritingLock());
		setServerLastModify(r.getLastModify());
	}

	public FullResource(Resource r,User u){
		this(r);
		setOwner(u.getId());
	}
	
	public FullResource(ResourceVersion rv,User u){
		this(rv.getPk().getResource(),u);
		setVersion(rv.getPk().getVersion());
		setDigest(rv.getDigest());
		setSize(rv.getSize());
		setMime(rv.getMime());
		setChunkNumber(rv.getChunkNumber());
	}
	
	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isToSynchronize() {
		return toSynchronize;
	}

	public void setToSynchronize(boolean toSynchronize) {
		this.toSynchronize = toSynchronize;
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

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}



	public Date getServerLastModify() {
		return serverLastModify;
	}

	public void setServerLastModify(Date serverLastModify) {
		this.serverLastModify = serverLastModify;
	}

	public int getId() {
		return id;
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

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
}
