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
package it.polito.ai.polibox.service.impl;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.service.FileService;
import it.polito.ai.polibox.service.ZipBuilderService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZipBuilderServiceImpl implements ZipBuilderService{

	@Autowired
	private FileService fileService;
	
	public ZipBuilderServiceImpl() {
		
	}
	
	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public String getName(String fullPath){
		int index=fullPath.lastIndexOf("/");
		if(index>-1){
			return fullPath.substring(index+1);
		}
		return fullPath;
	}
	public String getPath(String fullPath){
		int index=fullPath.lastIndexOf("/");
		if(index>0){
			return fullPath.substring(0,index);
		}
		return "/";
	}
	public String trimParent(String parent, String child){
		return child.replaceFirst(parent, "");
	};
	
	public File prepareZip(Resource r) throws IOException, SQLException{
		FileOutputStream output = null;
		ZipOutputStream out = null;
		try {
			String tail=""+Math.round(Math.random()*10000);
			File result=File.createTempFile("zip", tail);		
			output=new FileOutputStream(result);
			out=new ZipOutputStream(output);
			
			if(r.isDirectory()){
				List<Resource> array=fileService.getResourceChild(r);
				for (Resource resource : array) {
					if(!resource.isDirectory()){
						addZipEntry(out, resource, r.getName()); 
					}
				}
			}else{
				addZipEntry(out, r, getPath(r.getName()));
			}
			return result;
		}finally{
			if(out!=null){
				out.close();
			}
			if(output!=null){
				output.close();
			}
		}
	}

	protected void addZipEntry(ZipOutputStream out, Resource resource, String parent) throws IOException, SQLException{
		ResourceVersion lastVersion=fileService.getResourceVersionDao().getLastResourceVersion(resource);
		ZipEntry zipEntry = new ZipEntry(trimParent(parent, resource.getName()));
		out.putNextEntry(zipEntry);

		List<ResourceChunk> set=fileService.downloadResource(lastVersion);
		List<Blob> lb=new ArrayList<Blob>();
		int i;
	
		for(i=0;i<set.size();i++){
			lb.add(set.get(i).getData());
		}
	
		for(i=0;i<lb.size();i++){
			//out.write(Base64.decodeBase64(lb.get(i).getBytes(1, (int) lb.get(i).length())));
			out.write(lb.get(i).getBytes(1, (int) lb.get(i).length()));
		}
		out.closeEntry();
	}
}
