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
package it.polito.ai.polibox.persistency.model.service.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceOwners;
import it.polito.ai.polibox.persistency.model.ResourceOwnersId;
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
import it.polito.ai.polibox.persistency.model.service.FileService;
import it.polito.ai.polibox.service.MasterNotificationService;
import it.polito.ai.polibox.service.notification.message.DirectoryCancellata;
import it.polito.ai.polibox.service.notification.message.DirectoryCreata;
import it.polito.ai.polibox.service.notification.message.FileCancellato;
import it.polito.ai.polibox.service.notification.message.FileCreato;
import it.polito.ai.polibox.service.notification.message.FileModificato;
import it.polito.ai.polibox.service.notification.message.FileRinominato;
import it.polito.ai.polibox.service.notification.message.RisorsaRipristinataDaCestino;
import it.polito.ai.polibox.service.notification.message.RisorsaSpostataInCestino;
import it.polito.ai.polibox.web.controllers.result.ChunkItemWrapper;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.FullResource;

@Component
@Transactional
@Primary
public class FileServiceImpl implements FileService{

	@Autowired
	protected UserDao userDao;
	
	@Autowired
	protected EntityManager entityManager;
	
	@Autowired
	protected DeviceDao deviceDao;
	
	@Autowired
	protected ResourceDao resourceDao;
	
	@Autowired
	protected NotificationDao notificationDao;
	
	@Autowired
	protected ResourceChunkDao resourceChunkDao;
	
	@Autowired
	protected ResourceOwnersDao resourceOwnersDao;
	
	@Autowired
	protected ResourceVersionDao resourceVersionDao;
	
	@Autowired
	protected ResourceVersionInDeviceDao resourceVersionInDeviceDao;
	

	@Autowired
	protected MasterNotificationService masterNotificationService;
	
	public FileServiceImpl() {
		
	}
	
	public MasterNotificationService getMasterNotificationService() {
		return masterNotificationService;
	}

	public void setMasterNotificationService(
			MasterNotificationService masterNotificationService) {
		this.masterNotificationService = masterNotificationService;
	}


	public boolean isOwnerResource(Resource r, User u){
		List<User> list=getResourceOwners(r);
		for (User user : list) {
			if(user.getId()==u.getId()){
				return true;
			}
		}
		return false;
	}
	
	public List<User> getResourceOwners(Resource r){
		List<User> list = resourceOwnersDao.getResourceOwners(r);
		
		return list;
	}
	
	public List<ResourceOwners> getOwnersByResource(Resource r){
		return resourceOwnersDao.getOwners(r);
	}
	
	public Resource getResource(int resourceId) throws FileNotFoundException{
		Resource r=resourceDao.findOne(resourceId);
		if(r==null)
			throw new FileNotFoundException();
		return r;
	}
	
	public boolean isCreatorResourceVersion(int resourceId, int resourceVersion, int userId){
		if(resourceVersionDao.isCreator(resourceId, resourceVersion, userId) == null)
			return false;
		return true;
	}
	
	public int getDirectorySize(Resource r,User u){
		if (r.isDirectory()){
			List<Resource> list=resourceDao.getResourceChild(r.getName(),u.getId());
			int size=0,ind;
			ResourceVersion last;
			for(ind=0;ind<list.size();ind++){
				if(!list.get(ind).isDirectory()){
					last=resourceVersionDao.getLastResourceVersion(list.get(ind));
					size+=last.getSize();
				}
			}
			return size;
		}
		return -1;
	}
	
	public String rendiUnicoIlNomeDiUnaRisorsa(String name,User u){
		List<Resource> list=resourceDao.getResourceByName(name, u.getId());
		if(list!=null && list.size()>0){
			int index=name.lastIndexOf("\\.");
			if(index>0){
				name=name.substring(0,index)+""+Math.round(Math.random()*10000)+name.substring(index);
			}else{
				name=name.concat(""+Math.round(Math.random()*10000));
			}
		}
		name=name.replaceAll("\\s", "_");
		return name;
	}
	
	public Resource rinominaFile(Resource r, User u,String newName){
		String oldName=r.getName();
		//System.out.println(oldName+"   -->"+(oldName.lastIndexOf("/")+1));
		//System.out.println(newName+"   -->"+(newName.lastIndexOf("/")+1));
		String oldPath=oldName.substring(0, (oldName.lastIndexOf("/")+1));
		String newPath=oldName.substring(0,(newName.lastIndexOf("/")+1));
		if(oldPath.equals(newPath)){
			newName=rendiUnicoIlNomeDiUnaRisorsa(newName, u);
			//System.out.println(newName+"   is my new name unique");
			r.setName(newName);
			r.setLastModify(new Date());
			if(r.isDirectory()){//ho da modificare tutti i figli di questa directory.
				List<Integer> list=resourceDao.getResourceChild(r.getId(), u.getId());
				List<Resource> childs=resourceDao.findAll(list);
				String tmpName;
				for (Resource resource : childs) {
					tmpName=resource.getName().replaceFirst(oldName, newName);
					resource.setName(tmpName);
					resource.setLastModify(new Date());
					resourceDao.save(resource);
				}
			}
			r=resourceDao.save(r);//salvo il mio file
			masterNotificationService.notifica(new FileRinominato(u, r, oldName));
			
		}
		return r;
	}
	
	public Resource createDirectory(String name, User user){
		Resource resource=new Resource();
		ResourceOwners owner=new ResourceOwners();
		ResourceOwnersId roi=new ResourceOwnersId();
			resource.setWritingLock(false);
			resource.setDirectory(true);
			resource.setLastModify(new Date());
			name=rendiUnicoIlNomeDiUnaRisorsa(name, user);
			resource.setName(name);
			
			roi.setResource(resource);
			roi.setUser(user);
			owner.setPk(roi);
			owner.setFrom(new Date());
			
			resource.getOwners().add(owner);
			
			resource=resourceDao.save(resource);
			resourceOwnersDao.save(owner);
		
		masterNotificationService.notifica(new DirectoryCreata(user, resource));
		return resource;
	}
	
	//creo il mio file
	public FileUploadWrapper createFile(	String name,
								String digest,
								int chunkNumber,
								String mime,
								int size, 
								User user){
		Resource resource=new Resource();
		
		FileUploadWrapper fuw=new FileUploadWrapper();
		
		ResourceVersionId resVersId=new ResourceVersionId();
		ResourceVersion newVersion = new ResourceVersion();
		
		int i;
		
		Integer id=resourceDao.getFileByName(name, user.getId());
		if(id==null){//create from scratch: file non esiste
			ResourceOwners owner=new ResourceOwners();
			resource.setDirectory(false);
			name=rendiUnicoIlNomeDiUnaRisorsa(name, user);
			resource.setName(name);
			resource.setLastModify(new Date());
			owner.setFrom(new Date());
			owner.setUser(user);
			owner.setResource(resource);
			resource.getOwners().add(owner);
			resource=resourceDao.save(resource);
			resourceOwnersDao.save(owner);

			resVersId.setVersion(1);
			masterNotificationService.notifica(new FileCreato(user, resource));
		}else{//is a new version recupera i digest: il file esiste
			resource=resourceDao.findOne(id);
			resource.setLastModify(new Date());
			resource=entityManager.merge(resource);
			ResourceVersion resourceVersion=resourceVersionDao.getLastResourceVersion(resource);
			resource.setLastModify(new Date());
			resVersId.setVersion(resourceVersion.getPk().getVersion()+1);//se esiste già il prossimo upload deve salire di versione.
			
			List<ResourceChunk> lrc=resourceChunkDao.getResourceChunkList(resource, 
																			resourceVersion.getPk().getVersion(),
																			resourceVersion.getChunkNumber());
			for(i=0;i<lrc.size();i++){
				fuw.getChunks().add(new ChunkItemWrapper(lrc.get(i).getPk().getNumber(),lrc.get(i).getDigest()));
			}
			masterNotificationService.notifica(new FileModificato(user, resource));
		}
		newVersion.setChunkNumber(chunkNumber);
		newVersion.setCreationTime(new Date());
		newVersion.setDeleted(false);
		newVersion.setMime(mime);
		newVersion.setSize(size);
		newVersion.setCreator(user);
		newVersion.setDigest(digest);
		resVersId.setResource(resource);
		newVersion.setPk(resVersId);
		newVersion=resourceVersionDao.save(newVersion);
		
		
		fuw.setDigest(newVersion.getDigest());
		fuw.setMime(newVersion.getMime());
		fuw.setName(resource.getName());
		fuw.setDirectory(resource.isDirectory());
		fuw.setId(resource.getId());
		fuw.setVersion(newVersion.getPk().getVersion());
		fuw.setSize(newVersion.getSize());
		fuw.setChunkNumber(newVersion.getChunkNumber());
		fuw.setCreationTime(newVersion.getCreationTime());
		return fuw;
	}
	/**
	 * cancella ricorsivamente i file
	 */
	public int deleteResource(Resource r, User u) 
			throws FileNotOwnedException{ 
		if(isOwnerResource(r, u)){
			r.setLastModify(new Date());
			r=entityManager.merge(r);
			if(r.isDeleted() && r.isDirectory()){
				List<Resource> list=resourceDao.getResourceChild(r.getName(),u.getId());
				int i;
				for(i=0;i<list.size();i++){
					resourceDao.delete(list.get(i));	
				}
				resourceDao.delete(r);
				masterNotificationService.notifica(new DirectoryCancellata(u, r));
			}else if(r.isDeleted()){
				resourceDao.delete(r);
				masterNotificationService.notifica(new FileCancellato(u, r));
			}
			return 1;
		}
		throw new FileNotOwnedException();
	}
	
	public int moveFromTrashResource(Resource r, User u) throws FileNotOwnedException, FileNotFoundException{
		if(r==null){
			throw new FileNotFoundException();
		}
		if(isOwnerResource(r, u)){
			if(r.isDirectory()){
				List<Resource> list=resourceDao.getResourceChild(r.getName(),u.getId());
				int i;
				for(i=0;i<list.size();i++){
					list.get(i).setLastModify(new Date());
					list.get(i).setDeleted(false);
					masterNotificationService.notifica(new RisorsaRipristinataDaCestino(u, r));
				}
				resourceDao.save(list);
			}
			r.setLastModify(new Date());
			r=entityManager.merge(r);
			r.setDeleted(false);
			resourceDao.save(r);
			masterNotificationService.notifica(new RisorsaRipristinataDaCestino(u, r));
			return 1;
		}
		throw new FileNotOwnedException();
	}
	
	public int moveToTrashResource(Resource r, User u) throws FileNotOwnedException, FileNotFoundException{ 
		
		if(r==null){
			throw new FileNotFoundException();
		}
		if(isOwnerResource(r, u)){
			if(r.isDirectory()){
				List<Resource> list=resourceDao.getResourceChild(r.getName(),u.getId());
				int i;
				for(i=0;i<list.size();i++){
					list.get(i).setLastModify(new Date());
					list.get(i).setDeleted(true);
					masterNotificationService.notifica(new RisorsaSpostataInCestino(u, r));
				}
				resourceDao.save(list);
			}
			r=entityManager.merge(r);
			r.setLastModify(new Date());
			r.setDeleted(true);
			resourceDao.save(r);
			masterNotificationService.notifica(new RisorsaSpostataInCestino(u, r));
			return 1;
		}
		throw new FileNotOwnedException();
	}
	public int moveToTrashResource(int[] id, User user) throws FileNotOwnedException, FileNotFoundException{
		int i;
		Resource r;
		for(i=0;i<id.length;i++){
			r=resourceDao.findOne(id[i]);
			moveToTrashResource(r, user);
		}
		return 1;
	}
	
	public ResourceChunk uploadResourceChunk(ResourceChunk r){
		return resourceChunkDao.save(r);
	}
	
	public List<ResourceChunk> downloadResource(ResourceVersion rvid){ 
		List<ResourceChunk> list=resourceChunkDao.getResourceChunkList(rvid.getPk().getResource(), 
																	   rvid.getPk().getVersion(),
																	   rvid.getChunkNumber());
		return list;
	}
	
	
	public ResourceVersion getResourceVersion(ResourceVersionId rvid){
		ResourceVersion rv=resourceVersionDao.findOne(rvid);
		return rv;
	}

	
	public List<Resource> getMyResource(String basePath, User owner){
		List<Resource> list;
		List<Integer> idList=resourceDao.getYourResources(owner.getId(), basePath);
		list=resourceDao.findAll(idList);
		return list;
	}
	
	public List<FullResource> getAllMyFullResource(User owner){
		List<FullResource> finalList=new ArrayList<FullResource>();
		List<Resource> list=resourceDao.getAllYourResourcesWithDeleted(owner.getId());
		for (Resource resource : list) {
			FullResource a=new FullResource(resource,owner);
			if(!resource.isDirectory()){
				ResourceVersion rv=resourceVersionDao.getLastResourceVersion(resource);
				a.setChunkNumber(rv.getChunkNumber());
				a.setCreationTime(rv.getCreationTime());
				a.setDigest(rv.getDigest());
				a.setMime(rv.getMime());
				a.setSize(rv.getSize());
				a.setVersion(rv.getPk().getVersion());
			}
			finalList.add(a);
		}
		return finalList;
	}
	
	public List<Resource> getTrashResource(User owner){
		List<Resource> list=resourceDao.getYourTrash(owner.getId());
		return list;
	}
	
	public List<Resource> getResourceChild(Resource r){
		List<User> owners=getResourceOwners(r);
		if(owners.size()>0){
			List<Integer> list=resourceDao.getResourceChild(r.getId(), owners.get(0).getId());
			List<Resource> finalList=resourceDao.findAll(list);
			return finalList;
		}
		return new ArrayList<Resource>();
	}
	
	/**
	 * 
	 * promuove una versione di un file all'ultima versione
	 */
	
	public ResourceVersion promuoviVersione(Resource r, int version, User u) {
		ResourceVersionId rvid=new ResourceVersionId();
		rvid.setResource(r);
		rvid.setVersion(version);
		r.setLastModify(new Date());
		resourceDao.save(r);
		ResourceVersion versioneDaPromuovere=getResourceVersion(rvid);
		ResourceVersion ultimaVersione=resourceVersionDao.getLastResourceVersion(r);
		/**
		 * qua praticamente faccio una copia della versione e la reinserisco con il nuovo versione
		 */
		if(versioneDaPromuovere!=null && ultimaVersione!=null){
			List<ResourceChunk> listaChunkVersioneDaPromuovere=downloadResource(versioneDaPromuovere);
			List<ResourceChunk> listaChunkUltimaVersione=downloadResource(ultimaVersione);
			
			ResourceVersion nuova=new ResourceVersion();
			ResourceVersionId nuovoId=new ResourceVersionId();
			nuovoId.setResource(ultimaVersione.getPk().getResource());
			nuovoId.setVersion(ultimaVersione.getPk().getVersion()+1);//incremento la versione
			nuova.setPk(nuovoId);
			nuova.setChunkNumber(versioneDaPromuovere.getChunkNumber());
			nuova.setCreationTime(new Date());
			nuova.setCreator(userDao.findOne(u.getId()));
			nuova.setDeleted(versioneDaPromuovere.isDeleted());
			nuova.setDigest(versioneDaPromuovere.getDigest());
			nuova.setMime(versioneDaPromuovere.getMime());
			nuova.setSize(versioneDaPromuovere.getSize());
			nuova=resourceVersionDao.save(nuova);//ora devo salvare versione.
			
			ResourceChunk resourceChunk,newResourceChunk;
			int i;
			for (i=0;i<listaChunkVersioneDaPromuovere.size();i++) {
				resourceChunk=listaChunkVersioneDaPromuovere.get(i);
				if(!resourceChunk.getDigest().equals(listaChunkUltimaVersione.get(i).getDigest())){
					newResourceChunk=new ResourceChunk();
					newResourceChunk.setPk(resourceChunk.getPk());
					newResourceChunk.getPk().setResourceVersion(nuova);
					newResourceChunk.setData(resourceChunk.getData());
					newResourceChunk.setDigest(resourceChunk.getDigest());
					newResourceChunk.setSize(resourceChunk.getSize());
					newResourceChunk=resourceChunkDao.save(newResourceChunk);
				}
			}
			return nuova;
		}
		return null;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public DeviceDao getDeviceDao() {
		return deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	public ResourceDao getResourceDao() {
		return resourceDao;
	}

	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	public NotificationDao getNotificationDao() {
		return notificationDao;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public ResourceChunkDao getResourceChunkDao() {
		return resourceChunkDao;
	}

	public void setResourceChunkDao(ResourceChunkDao resourceChunkDao) {
		this.resourceChunkDao = resourceChunkDao;
	}

	public ResourceOwnersDao getResourceOwnersDao() {
		return resourceOwnersDao;
	}

	public void setResourceOwnersDao(ResourceOwnersDao resourceOwnersDao) {
		this.resourceOwnersDao = resourceOwnersDao;
	}

	public ResourceVersionDao getResourceVersionDao() {
		return resourceVersionDao;
	}

	public void setResourceVersionDao(ResourceVersionDao resourceVersionDao) {
		this.resourceVersionDao = resourceVersionDao;
	}

	public ResourceVersionInDeviceDao getResourceVersionInDeviceDao() {
		return resourceVersionInDeviceDao;
	}

	public void setResourceVersionInDeviceDao(
			ResourceVersionInDeviceDao resourceVersionInDeviceDao) {
		this.resourceVersionInDeviceDao = resourceVersionInDeviceDao;
	}

	public List<Resource> getResourcesById(Integer[] id){
		List<Resource> list=new ArrayList<Resource>();
		int i;
		for(i=0;i<id.length;i++){
			list.add(resourceDao.findOne(id[i]));
		}
		return list;
	}
	public Resource moveResource(Resource destination, Resource source, User owner){
		if(destination.getName().equals(source.getName()))
			return null;
		String oldName=source.getName();
		String newName=destination.getName();
		int index=source.getName().lastIndexOf("/");
		newName+=source.getName().substring(index);
		source.setName(newName);
		if(source.isDirectory()){//ho da modificare tutti i figli di questa directory.
			List<Integer> list=resourceDao.getResourceChild(source.getId(), owner.getId());
			List<Resource> childs=resourceDao.findAll(list);
			String tmpName;
			for (Resource resource : childs) {
				tmpName=resource.getName().replaceFirst(oldName, newName);
				resource.setName(tmpName);
				resource.setLastModify(new Date());
				resourceDao.save(resource);
			}
		}
		source.setLastModify(new Date());
		source=resourceDao.save(source);
		return source;
	}



	
}
