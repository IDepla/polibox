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
import it.polito.ai.polibox.persistency.model.Sharing;

import java.util.ArrayList;
import java.util.List;

public class SharingResponseWrapper {

	public FileShortInfo getFile() {
		return file;
	}

	public void setFile(FileShortInfo file) {
		this.file = file;
	}

	public List<ResourceSharingWrapper> getSharing() {
		return sharing;
	}

	public void setSharing(List<ResourceSharingWrapper> sharing) {
		this.sharing = sharing;
	}

	private FileShortInfo file;
	private List<ResourceSharingWrapper> sharing;
	
	public SharingResponseWrapper(Resource r,List<Sharing> s,boolean owned){
		this();
		file=new FileShortInfo(r, owned);
		if(s.size()>0){
			int i;
			for(i=0;i<s.size();i++){
				sharing.add(new ResourceSharingWrapper(s.get(i)));
			}
		}
	}
	
	public SharingResponseWrapper() {
		file=new FileShortInfo();
		sharing=new ArrayList<ResourceSharingWrapper>();
	}
	


	public class FileShortInfo{
		private int id;
		private String name;
		private boolean directory;
		private boolean owned;
		
		public FileShortInfo(Resource r,boolean own){
			id=r.getId();
			int index=r.getName().lastIndexOf("/");
			name=r.getName().substring(index+1);
			directory=r.isDirectory();
			owned=own;
		}
		
		public FileShortInfo() {
			name="";
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

		public boolean isDirectory() {
			return directory;
		}

		public void setDirectory(boolean directory) {
			this.directory = directory;
		}

		public boolean isOwned() {
			return owned;
		}

		public void setOwned(boolean owned) {
			this.owned = owned;
		}
	}
	

}
