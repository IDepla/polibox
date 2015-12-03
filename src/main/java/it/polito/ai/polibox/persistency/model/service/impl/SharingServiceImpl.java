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

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceOwners;
import it.polito.ai.polibox.persistency.model.ResourceOwnersId;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.ResourceVersionId;
import it.polito.ai.polibox.persistency.model.Sharing;
import it.polito.ai.polibox.persistency.model.SharingId;
import it.polito.ai.polibox.persistency.model.SharingMode;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.dao.SharingDao;
import it.polito.ai.polibox.persistency.model.dao.SharingModeDao;
import it.polito.ai.polibox.persistency.model.exception.FileNotOwnedException;
import it.polito.ai.polibox.persistency.model.service.SharingService;
import it.polito.ai.polibox.persistency.model.service.custom.ResourceShared;
import it.polito.ai.polibox.service.notification.message.CondivisioneAccettata;
import it.polito.ai.polibox.service.notification.message.CondivisioneCancellata;
import it.polito.ai.polibox.service.notification.message.CondivisioneModificata;
import it.polito.ai.polibox.service.notification.message.CondivisioneRichiesta;
import it.polito.ai.polibox.service.notification.message.FileCreato;
import it.polito.ai.polibox.service.notification.message.FileModificato;
import it.polito.ai.polibox.web.controllers.result.ChunkItemWrapper;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.FullResource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class SharingServiceImpl extends FileServiceImpl implements SharingService{

	@Autowired
	protected SharingDao sharingDao;
	
	@Autowired
	protected SharingModeDao sharingModeDao;
	
	public SharingServiceImpl() {
		super();
	}
	public SharingDao getSharingDao() {
		return sharingDao;
	}

	public void setSharingDao(SharingDao sharingDao) {
		this.sharingDao = sharingDao;
	}

	public SharingModeDao getSharingModeDao() {
		return sharingModeDao;
	}

	public void setSharingModeDao(SharingModeDao sharingModeDao) {
		this.sharingModeDao = sharingModeDao;
	}

	
	private boolean internalPermissionResource(Resource r,User u){
		List<Sharing> list = sharingDao.getSharingByResourceAndTarget(r, u);
		int i;
		for(i=0;i<list.size();i++){
			if(list.get(i).isRequestAccepted()){
				return true;
			}
		}
		return false;
	}
	private boolean internalPermissionWriteResource(Resource r,User u){
		List<Sharing> list = sharingDao.getSharingByResourceAndTarget(r, u);
		int i;
		for(i=0;i<list.size();i++){
			if(list.get(i).isRequestAccepted()){
				if(list.get(i).getPermission().getId()==2)
					return true;
			}
		}
		return false;
	}
	
	private List<Resource> getParentResource(Resource r){//questo mi serve perchè il permesso posso avercelo sul genitore.
		List<User> owners=resourceOwnersDao.getResourceOwners(r);
		User owner=owners.get(0);
		String resourceName=r.getName();
		String[] exploded=resourceName.split("\\/");
		List<Resource> list=new ArrayList<Resource>();
		int i;
		String path="";
		for(i=1;i<exploded.length;i++){
			path=path+"/"+exploded[i];
//			System.out.println("parent resource: "+path);
			list.addAll(resourceDao.getResourceByName(path, owner.getId()));
		}
		return list;
	}
	
	public Sharing getFirstParentAcceptedSharedResourceByResourceAndTarget(Resource r, User target){
		List<Resource> list=getParentResource(r);
		for (Resource resource : list) {
			List<Sharing> s= sharingDao.getSharingByResourceAndTarget(resource, target);
			if(s!=null && s.size()>0){
				for (Sharing sharing : s) {
					if(sharing.isRequestAccepted())
						return sharing;
				}
			}
		}
		return null;
	}
	
	public String buildNameFromParent(Resource parent,String childNameComplete){
		String resourceName=parent.getName();
		String target="";
		int index=childNameComplete.indexOf("/",1);
		if(index>0){
			target=resourceName.concat(childNameComplete.substring(index));
		}
		return target;
	}
	
	public String trimUnknownPathFromResourceName(Resource r, User u){
		Sharing sss=getFirstParentAcceptedSharedResourceByResourceAndTarget(r, u);
		int lastIndex=sss.getResource().getName().lastIndexOf("/");
		String parentPath="";
		if(lastIndex>0){
			parentPath=sss.getResource().getName().substring(0,lastIndex);
		}
		String displayedName=r.getName().replaceFirst(parentPath,"");
		if(displayedName.length()==0)
			displayedName=r.getName().substring(r.getName().lastIndexOf("/"));
		return displayedName;
	}
	
	//creo il mio file
		public FileUploadWrapper createFile(
									String name,
									String digest,
									int chunkNumber,
									String mime,
									int size, 
									User targetUser,
									Resource parentResource,
									User ownerUser){
			Resource resource=new Resource();
			
			FileUploadWrapper fuw=new FileUploadWrapper();
			
			ResourceVersionId resVersId=new ResourceVersionId();
			ResourceVersion newVersion = new ResourceVersion();
			
			int i;
			String nome=buildNameFromParent(parentResource, name);
			
			Integer id=resourceDao.getFileByName(nome, ownerUser.getId());
			if(id==null){//create from scratch: file non esiste
				ResourceOwners owner=new ResourceOwners();
				resource.setDirectory(false);
				name=rendiUnicoIlNomeDiUnaRisorsa(nome, ownerUser);
				resource.setName(name);
				resource.setLastModify(new Date());
				owner.setFrom(new Date());
				owner.setUser(ownerUser);
				owner.setResource(resource);
				resource.getOwners().add(owner);
				resource=resourceDao.save(resource);
				resourceOwnersDao.save(owner);

				resVersId.setVersion(1);
				masterNotificationService.notifica(new FileCreato(ownerUser, resource));
			}else{//is a new version recupera i digest: il file esiste
				resource=resourceDao.findOne(id);
				resource.setDeleted(false);//se era cancellato ora non lo è più
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
				masterNotificationService.notifica(new FileModificato(ownerUser, resource));
			}
			newVersion.setChunkNumber(chunkNumber);
			newVersion.setCreationTime(new Date());
			newVersion.setDeleted(false);
			newVersion.setMime(mime);
			newVersion.setSize(size);
			newVersion.setCreator(targetUser);
			newVersion.setDigest(digest);
			resVersId.setResource(resource);
			newVersion.setPk(resVersId);
			newVersion=resourceVersionDao.save(newVersion);
			
			
			fuw.setDigest(newVersion.getDigest());
			fuw.setMime(newVersion.getMime());
			fuw.setName(trimUnknownPathFromResourceName(resource, targetUser));
			fuw.setDirectory(resource.isDirectory());
			fuw.setId(resource.getId());
			fuw.setVersion(newVersion.getPk().getVersion());
			fuw.setSize(newVersion.getSize());
			fuw.setChunkNumber(newVersion.getChunkNumber());
			fuw.setCreationTime(newVersion.getCreationTime());
			return fuw;
		}
	
	
	public boolean havePermissionResource(Resource r, User u){
		List<Resource> list= getParentResource(r);
		for (Resource resource : list) {
			if(internalPermissionResource(resource, u))
				return true;
		}
		return false;
	}
	
	public boolean havePermissionWriteResource(Resource r, User u){
		List<Resource> list= getParentResource(r);
		for (Resource resource : list) {
			if(internalPermissionWriteResource(resource, u))
				return true;
		}
		return false;
	}
	
		
	public List<ResourceShared> getMySharedResourceRoot(User owner){
		List<ResourceShared> list=new ArrayList<ResourceShared>();
		String query="SELECT  resource_id,min(IF(request_accepted=0,0,1)) "+
						"FROM sharing "+
						"where " +
						"from_user_id=:userId "+
						"group by resource_id";
		Query q=entityManager.createNativeQuery(query);
		q.setParameter("userId", owner.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> result=q.getResultList();
		ResourceShared rs;
		for(Object[] obj : result){
			rs=new ResourceShared();
			rs.setPending(((BigInteger) obj[1]).intValue());
			rs.setResource(resourceDao.findOne((Integer) obj[0]));
			list.add(rs);
	    }
		return list;
	}
	
	public List<Resource> getSharedWithMeResource(User owner){
		List<Resource> list= new ArrayList<Resource>();
		List<Sharing> sharingList=sharingDao.getResourceSharedByTargetUser(owner);
		int i;
		for(i=0;i<sharingList.size();i++){
			list.add(sharingList.get(i).getResource());
		}
		return list;
	}
	/**
	 * restituisce tutte le risorse che sono condivise con te 
	 * e restituisce anche tutti i figli.
	 */
	public List<FullResource> getSharedWithMeFullResource(User owner){
		List<FullResource> finalList=new ArrayList<FullResource>();
		List<Sharing> sharingList=sharingDao.getResourceSharedByTargetUser(owner);
		for (Sharing sh : sharingList) {
			if(!sh.getResource().isDeleted()){
				FullResource a=new FullResource(sh.getResource(),sh.getOwnerUser());
				int index=a.getName().lastIndexOf("/");//levo la base che non posso vedere
				if(!sh.getResource().isDirectory()){
					ResourceVersion rv=resourceVersionDao.getLastResourceVersion(sh.getResource());
					a.setChunkNumber(rv.getChunkNumber());
					a.setCreationTime(rv.getCreationTime());
					a.setDigest(rv.getDigest());
					a.setMime(rv.getMime());
					a.setSize(rv.getSize());
					a.setVersion(rv.getPk().getVersion());
					a.setParent(a.getId());
					finalList.add(a);
				}else{
					finalList.add(a);
					//recupero i figli di questa directory condivisa.
					List<Resource> childs=resourceDao.getResourceChild(a.getName(), sh.getOwnerUser().getId());
					for (Resource resource : childs) {
						FullResource b=new FullResource(resource, sh.getOwnerUser());
						if(!resource.isDirectory()){
							ResourceVersion rv=resourceVersionDao.getLastResourceVersion(resource);
							b.setChunkNumber(rv.getChunkNumber());
							b.setCreationTime(rv.getCreationTime());
							b.setDigest(rv.getDigest());
							b.setMime(rv.getMime());
							b.setSize(rv.getSize());
							b.setVersion(rv.getPk().getVersion());
						}
						if(index>0){
							b.setName(b.getName().substring(index));//ho tolto il path dal nome.
						}
						b.setParent(a.getId());
						finalList.add(b);
					}
				}
				if(index>0){
					a.setName(a.getName().substring(index));//ho tolto il path dal nome.
				}
			}
		}
		return finalList;
	}
	
	public Sharing shareResource(Resource resource, User owner,User shareTarget,SharingMode mode) throws FileNotOwnedException{
		if(isOwnerResource(resource, owner)){
			Sharing share=new Sharing();
			ResourceOwnersId roid=new ResourceOwnersId();
			roid.setResource(resource);
			roid.setUser(owner);
			ResourceOwners ro=resourceOwnersDao.findOne(roid);
			share.getPk().setUserTarget(shareTarget);
			share.getPk().setSharingResource(ro);
			
			share.setPermission(mode);
			share.setRequestAccepted(false);
			share.setFromRequestTime(new Date());
			share.setToShowRequest(true);
			
			share=sharingDao.save(share);
			ro.getSharing().add(share);
			shareTarget=entityManager.merge(shareTarget);
			shareTarget.getSharedWithYouResources().add(share);
			
			userDao.save(shareTarget);
			masterNotificationService.notifica(new CondivisioneRichiesta(owner, shareTarget, resource));
			return share;
		}
		throw new FileNotOwnedException();
	}
	
	public void modifySharingPermission(Sharing s, Integer mode){
		SharingMode sm=sharingModeDao.findOne(mode);
		s.setPermission(sm);
		sharingDao.save(s);
		masterNotificationService.notifica(
				new CondivisioneModificata(s.getOwnerUser(), 
											s.getTargetUser(), 
											s.getResource()));
	}
	public void sharingAccept(Sharing s){
		s.setRequestAccepted(true);
		s.setToAcceptedTime(new Date());
		sharingDao.save(s);
		masterNotificationService.notifica(
				new CondivisioneAccettata(s.getOwnerUser(), 
											s.getTargetUser(), 
											s.getResource()));
	}
	public void deleteSharing(Sharing s){
		sharingDao.delete(s);
		masterNotificationService.notifica(
				new CondivisioneCancellata(s.getOwnerUser(),
											s.getTargetUser(), 
											s.getResource()));
	}
	
	public List<Sharing> getSharing(Resource r){
		List<Sharing> list=sharingDao.getSharingByResource(r);
		return list;
	}
	
	public List<Sharing> getAcceptedSharing(Resource r){
		List<Sharing> list=sharingDao.getAcceptedSharingByResource(r);
		return list;
	}
	
	public Sharing getSharing(Resource r, User owner,User target){
		try{
			SharingId id=new SharingId();
			ResourceOwnersId rid=new ResourceOwnersId();
			rid.setUser(owner);
			rid.setResource(r);
			ResourceOwners ro=resourceOwnersDao.findOne(rid);
			id.setUserTarget(target);
			id.setSharingResource(ro);
			Sharing s=sharingDao.findOne(id);
			return s;
		}catch(Exception e){
			return null;
		}
	}
	
	public List<Sharing> getSharing(Resource r, User target){
		try{
			return sharingDao.getSharingByResourceAndTarget(r, target);
		}catch(Exception e){
			return null;
		}
	}
}
