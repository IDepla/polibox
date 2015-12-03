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

import java.io.FileNotFoundException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.stereotype.Service;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceOwners;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.ResourceVersionId;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.DeviceDao;
import it.polito.ai.polibox.persistency.model.dao.NotificationDao;
import it.polito.ai.polibox.persistency.model.dao.ResourceChunkDao;
import it.polito.ai.polibox.persistency.model.dao.ResourceDao;
import it.polito.ai.polibox.persistency.model.dao.ResourceOwnersDao;
import it.polito.ai.polibox.persistency.model.dao.ResourceVersionDao;
import it.polito.ai.polibox.persistency.model.dao.ResourceVersionInDeviceDao;
import it.polito.ai.polibox.persistency.model.dao.UserDao;
import it.polito.ai.polibox.persistency.model.exception.FileNotOwnedException;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.FullResource;

@Service
@PersistenceContext(type=PersistenceContextType.EXTENDED)
public interface FileService {
	
	public boolean isOwnerResource(Resource r, User u);
	
	public List<ResourceOwners> getOwnersByResource(Resource r);
	
	public Resource getResource(int resourceId) throws FileNotFoundException;
	
	public int getDirectorySize(Resource r,User u);
	
	public List<User> getResourceOwners(Resource r);
	
	public boolean isCreatorResourceVersion(int resourceId, int resourceVersion, int userId);
	
	public Resource createDirectory(String name, User user);
	
	public Resource rinominaFile(Resource r, User u,String newName);
	
	public FileUploadWrapper createFile(	String name,
			String digest,
			int chunkNumber,
			String mime,
			int size, 
			User user);
	
	public int deleteResource(Resource r, User u) throws FileNotOwnedException;
	
	public int moveToTrashResource(Resource r, User u) throws FileNotOwnedException, FileNotFoundException;
	
	public int moveFromTrashResource(Resource r, User u) throws FileNotOwnedException, FileNotFoundException;
	
	public int moveToTrashResource(int[] id, User user) throws FileNotOwnedException, FileNotFoundException;
	
	public ResourceChunk uploadResourceChunk(ResourceChunk rc);
	
	public List<ResourceChunk> downloadResource(ResourceVersion rvid);
	
	public ResourceVersion getResourceVersion(ResourceVersionId rvid);
	
	public List<Resource> getMyResource(String basePath, User owner);
	
	public List<FullResource> getAllMyFullResource(User owner);
	
	public List<Resource> getTrashResource(User owner);
	
	public List<Resource> getResourceChild(Resource r);
	
	public UserDao getUserDao();

	public void setUserDao(UserDao userDao);

	public EntityManager getEntityManager();

	public void setEntityManager(EntityManager entityManager) ;

	public DeviceDao getDeviceDao();

	public void setDeviceDao(DeviceDao deviceDao);

	public ResourceDao getResourceDao();

	public void setResourceDao(ResourceDao resourceDao);

	public NotificationDao getNotificationDao();

	public void setNotificationDao(NotificationDao notificationDao);

	public ResourceChunkDao getResourceChunkDao();

	public void setResourceChunkDao(ResourceChunkDao resourceChunkDao);

	public ResourceOwnersDao getResourceOwnersDao();

	public void setResourceOwnersDao(ResourceOwnersDao resourceOwnersDao);

	public ResourceVersionDao getResourceVersionDao();

	public void setResourceVersionDao(ResourceVersionDao resourceVersionDao);

	public ResourceVersionInDeviceDao getResourceVersionInDeviceDao();

	public void setResourceVersionInDeviceDao(ResourceVersionInDeviceDao resourceVersionInDeviceDao);
	
	public ResourceVersion promuoviVersione(Resource r, int version, User u);
	
	public List<Resource> getResourcesById(Integer[] id);
	
	public Resource moveResource(Resource destination, Resource source, User owner);
}
