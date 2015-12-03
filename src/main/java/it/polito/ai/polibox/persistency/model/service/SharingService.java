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
package it.polito.ai.polibox.persistency.model.service;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.Sharing;
import it.polito.ai.polibox.persistency.model.SharingMode;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.SharingDao;
import it.polito.ai.polibox.persistency.model.dao.SharingModeDao;
import it.polito.ai.polibox.persistency.model.exception.FileNotOwnedException;
import it.polito.ai.polibox.persistency.model.service.custom.ResourceShared;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.FullResource;

import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.stereotype.Service;

@Service
@PersistenceContext(type=PersistenceContextType.EXTENDED)
public interface SharingService extends FileService {
		
	public List<ResourceShared> getMySharedResourceRoot(User owner);
	
	public List<Resource> getSharedWithMeResource(User owner);
	
	public List<FullResource> getSharedWithMeFullResource(User owner);
	
	public Sharing shareResource(Resource resource, User owner,User shareTarget,SharingMode mode) throws FileNotOwnedException;
	
	public void modifySharingPermission(Sharing s, Integer mode);
	
	public void sharingAccept(Sharing s);
	
	public FileUploadWrapper createFile(	String name,
			String digest,
			int chunkNumber,
			String mime,
			int size, 
			User user,
			Resource parent,
			User owner);
	
	public void deleteSharing(Sharing s);
	
	public SharingDao getSharingDao();
	
	public List<Sharing> getSharing(Resource r);
	
	public void setSharingDao(SharingDao sharingDao);
	
	public SharingModeDao getSharingModeDao();
	
	public String buildNameFromParent(Resource parent,String childNameComplete);
	
	public void setSharingModeDao(SharingModeDao sharingModeDao);
	
	public boolean havePermissionResource(Resource r, User u);
	
	public boolean havePermissionWriteResource(Resource r, User u);
	
	public List<Sharing> getAcceptedSharing(Resource r);
	
	public Sharing getSharing(Resource r, User owner,User target);
	
	public List<Sharing> getSharing(Resource r, User target);
	
	public Sharing getFirstParentAcceptedSharedResourceByResourceAndTarget(Resource r, User target);
	
	public String trimUnknownPathFromResourceName(Resource r, User u);
}
