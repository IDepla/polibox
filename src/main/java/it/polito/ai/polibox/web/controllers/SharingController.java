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
package it.polito.ai.polibox.web.controllers;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.Sharing;
import it.polito.ai.polibox.persistency.model.SharingMode;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.exception.AccountCreationException;
import it.polito.ai.polibox.persistency.model.exception.FileNotOwnedException;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.persistency.model.service.SharingService;
import it.polito.ai.polibox.persistency.model.service.custom.ResourceShared;
import it.polito.ai.polibox.web.controllers.inputform.Directory;
import it.polito.ai.polibox.web.controllers.inputform.FriendFileInput;
import it.polito.ai.polibox.web.controllers.inputform.ResourceInput;
import it.polito.ai.polibox.web.controllers.inputform.SharingInput;
import it.polito.ai.polibox.web.controllers.inputform.SharingInputCreation;
import it.polito.ai.polibox.web.controllers.inputform.validation.DirectoryValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.FileInputValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.SharingInputCreationValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.SharingInputValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.UploadChunkValidator;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;
import it.polito.ai.polibox.web.controllers.result.DirectoryWrapper;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.ItemListWrapper;
import it.polito.ai.polibox.web.controllers.result.ItemWrapper;
import it.polito.ai.polibox.web.controllers.result.ResourceSharingWrapper;
import it.polito.ai.polibox.web.controllers.result.SharingItemWrapper;
import it.polito.ai.polibox.web.controllers.result.SharingModeWrapper;
import it.polito.ai.polibox.web.controllers.result.SharingResponseWrapper;
import it.polito.ai.polibox.web.controllers.session.constants.SessionAttribute;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class SharingController {
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private SharingService sharingService;
	
	public SharingController() {
	}
	
	public SharingService getSharingService() {
		return sharingService;
	}

	public void setSharingService(SharingService sharingService) {
		this.sharingService = sharingService;
	}

	@InitBinder("dir")
    protected void initDirBinder(WebDataBinder binder) {
		binder.setValidator(new DirectoryValidator());
    }
	
	@InitBinder("file")
    protected void initFileBinder(WebDataBinder binder) {
		binder.setValidator(new FileInputValidator());
    }
	@InitBinder("mode")
    protected void initSharingBinder(WebDataBinder binder) {
		binder.setValidator(new SharingInputValidator());
    }
	
	
	@InitBinder("chunk")
    protected void initChunkBinder(WebDataBinder binder) {
		binder.setValidator(
				new UploadChunkValidator()
				);
    }

	@InitBinder("sharingInputCreation")
    protected void initSharingCreationBinder(WebDataBinder binder) {
		binder.setValidator(new SharingInputCreationValidator());
    }
	
	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	
	
	
	/**
	 * recupera tutti i modi di condivisione
	 * @return
	 */
	@RequestMapping(value="/sharing/modes",method=RequestMethod.GET)
	public @ResponseBody Response getSharingModes(){
		Response resp=new Response();
		List<SharingMode> list=sharingService.getSharingModeDao().findAll();
		int i;
		for(i=0;i<list.size();i++){
			resp.getResult().add(new SharingModeWrapper(list.get(i)));
		}
		return resp;
	}
	
	/**
	 * se sei l'owner restituiscono con chi hai condiviso i file che sia accettato o no
	 * se non sei l'owner restituisce la lista delle persone che hanno accettato lo sharing
	 * @return
	 */
	@RequestMapping(value="/sharing/file/{id}",method=RequestMethod.GET)
	public @ResponseBody Response getSharingFile(@PathVariable("id") int fileId,
												 HttpSession session){
		Response resp=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=accountService.getUserDao().findOne(userId);
		Resource r;
		try {
			r = sharingService.getResource(fileId);
			if(u!=null && r!=null){
				List<Sharing> list;
				if(sharingService.isOwnerResource(r, u)){
					list=sharingService.getSharing(r);
					resp.getResult().add(new SharingResponseWrapper(r,list,true));
				}else if(sharingService.havePermissionResource(r, u)){
					list=sharingService.getAcceptedSharing(r);
					resp.getResult().add(new SharingResponseWrapper(r,list,false));
				}
				resp.setStatus(Status.OK);
			}else{
				resp.setStatus(Status.BAD_PARAM);
				resp.appendError(Status.BAD_PARAM, "error in params");
			}
		} catch (FileNotFoundException e) {
			resp.setStatus(Status.FAIL);
			resp.appendError(Status.FAIL, "error");
		}
		
		return resp;
	}
	

	
	/**
	 * solo se sei l'owner, modifica i permessi di uno sharing
	 * @param fileId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sharing/file/{id}/{target}",method=RequestMethod.PUT,consumes={"application/json"})
	public @ResponseBody Response changeSharingFile(@PathVariable("id") int fileId,
													@PathVariable("target") int target,
													@Valid @RequestBody SharingInput mode,
													BindingResult bindingResult,
													HttpSession session
													){
		Response resp=new Response();
		if(bindingResult.hasErrors()){
			resp.setStatus(Status.BAD_PARAM);
			resp.appendAllErrors(bindingResult.getAllErrors());
            return resp;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=accountService.getUserDao().findOne(userId);
		User ut=accountService.getUserDao().findOne(target);
		Resource r;
		try {
			r = sharingService.getResource(fileId);
			if(u!=null && r!=null && ut!=null){
				if(sharingService.isOwnerResource(r, u)){
					Sharing s=sharingService.getSharing(r, u, ut);
					if(s!=null){
						try{
							sharingService.modifySharingPermission(s, mode.getMode());
							resp.setStatus(Status.OK);
						}catch(Exception e){
							resp.setStatus(Status.FAIL);
							resp.appendError(Status.FAIL,"sharing not found");
						}
					}
				}
			}else{
				resp.setStatus(Status.BAD_PARAM);
				resp.appendError(Status.BAD_PARAM, "error in params");
			}
		} catch (FileNotFoundException e1) {
			resp.setStatus(Status.FAIL);
			resp.appendError(Status.FAIL,"sharing not found");
		}
	
		return resp;
	}
	
	/**
	 * solo se sei il target
	 * @return
	 */
	@RequestMapping(value="/sharing/file/{id}/{from}/accept",method=RequestMethod.PUT)
	public @ResponseBody Response acceptSharingRequest( @PathVariable("id") int fileId,
														@PathVariable("from") int from,
														 HttpSession session){
		Response resp=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User toUser=accountService.getUserDao().findOne(userId);
		User fromUser=accountService.getUserDao().findOne(from);
		Resource r;
		try {
			r = sharingService.getResource(fileId);
			if(toUser!=null && r!=null && fromUser!=null){
				Sharing s=sharingService.getSharing(r, fromUser, toUser);
				if(s!=null){
					try{
						sharingService.sharingAccept(s);
						resp.setStatus(Status.OK);
					}catch(Exception e){
						resp.setStatus(Status.FAIL);
						resp.appendError(Status.FAIL,"sharing not found");
					}
				}
			}
		} catch (FileNotFoundException e1) {
			resp.setStatus(Status.FAIL);
			resp.appendError(Status.FAIL,"sharing not found");
		}
		
		return resp;
	}
	
	/**
	 * solo se sei l'owner
	 * @param fileId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sharing/file/{id}/{target}",method=RequestMethod.DELETE)
	public @ResponseBody Response deleteSharingFile(@PathVariable("id") int fileId,
													@PathVariable("target") int target,
													 HttpSession session){
		Response resp=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=accountService.getUserDao().findOne(userId);
		User ut=accountService.getUserDao().findOne(target);
		Resource r;
		try {
			r = sharingService.getResource(fileId);
			if(u!=null && r!=null && ut!=null){
				if(sharingService.isOwnerResource(r, u)){
					Sharing s=sharingService.getSharing(r, u, ut);
					if(s!=null){
						try{
							sharingService.deleteSharing(s);
							resp.setStatus(Status.OK);
						}catch(Exception e){
							resp.setStatus(Status.FAIL);
							resp.appendError(Status.FAIL,"sharing not found");
						}
					}
				}
			}else{
				resp.setStatus(Status.BAD_PARAM);
				resp.appendError(Status.BAD_PARAM, "error in params");
			}
		} catch (FileNotFoundException e1) {
			resp.setStatus(Status.FAIL);
			resp.appendError(Status.FAIL,e1.getMessage());
		}
		
		return resp;
	}

	/**
	 * restituisce le richieste pendenti verso te
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sharing/file/pending",method=RequestMethod.GET)
	public @ResponseBody Response getPendingSharedWithYouFiles(HttpSession session){
		Response resp=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		List<Sharing> list= sharingService.getSharingDao().getPendingShareByTargetUserId(userId);
		int i;
		for(i=0;i<list.size();i++){
			resp.getResult().add(new ResourceSharingWrapper(list.get(i)));
		}
		resp.setStatus(Status.OK);
		return resp;
	}
	
	/**
	 * restituisce i file che sono stati condivisi con te
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sharedwithyou/files",method=RequestMethod.GET)
	public @ResponseBody Response getSharedWithYouFiles(@Valid Directory dir,
														 BindingResult bindingResult,
														 HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=accountService.getUserDao().findOne(userId);
		if(dir.getName().isEmpty()||dir.getName().equalsIgnoreCase("/")){ // se non do il path allora recupero i file condivisi con me.
			List<Resource> s=sharingService.getSharedWithMeResource(u);
			ItemListWrapper listWrapper=new ItemListWrapper(s);//ora devo trimmerare il path, deve rimanere solo il nome.
			int index;
			for (ItemWrapper item : listWrapper.getItem()) {
				index=item.getName().lastIndexOf("/");
				if(index>0){
					item.setName(item.getName().substring(index));//ho tolto il path dal nome.
				}
			}
			response.setResult(listWrapper.getItem().toArray());
		}else{//altrimenti devo andare a controllare se ho i permessi per recuperarli
			Resource parent;
			try {
				parent = sharingService.getResource(dir.getId());
				if(sharingService.havePermissionResource(parent, u)){//se ho i permessi sul parent
					String path=parent.getName();
					int indiceDelloSlash=path.lastIndexOf("/");
					path=path.substring(0,indiceDelloSlash)+dir.getName();
					List<Sharing> sh=sharingService.getSharingDao().getSharingByResourceAndTarget(parent, u);
					List<Resource> rlist=sharingService.getMyResource(path,sh.get(0).getOwnerUser());
					ItemListWrapper listWrapper=new ItemListWrapper(rlist);//ora devo trimmerare il path, deve rimanere solo il path visibile a me.
					for (ItemWrapper item : listWrapper.getItem()) {
							item.setName(item.getName().substring(indiceDelloSlash));//ho tolto il path che non mi è stato condiviso.
					}
					response.setResult(listWrapper.getItem().toArray());
				}
			} catch (FileNotFoundException e) {
				response.setStatus(Status.FAIL);
				response.appendError(Status.FAIL,e.getMessage());
			}
			
		}
		
		return response;
	}
	
	/***
	 * restituisce i file che hai condiviso con qualcuno
	 * @param dir
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/your/files/shared",method=RequestMethod.GET)
	public @ResponseBody Response getYourSharedFiles(@Valid Directory dir,
													 BindingResult bindingResult,
													 HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=accountService.getUserDao().findOne(userId);
		List<ResourceShared> list;
		int i;
		if(dir.getName().isEmpty()||dir.getName().equalsIgnoreCase("/")){//stiamo parlando del root
			list=sharingService.getMySharedResourceRoot(u);
		}else{//stiamo parlando di un figlio del root, in questo caso, siccome sono miei file allora vado sulla get path normale.
			List<Resource> rlist=sharingService.getMyResource(dir.getName(),u);
			list=new ArrayList<ResourceShared>();
			ResourceShared r;
			for(i=0;i<rlist.size();i++){
				r=new ResourceShared();
				r.setResource(rlist.get(i));
				list.add(r);
			}
		}
		response.setStatus(Status.OK);
		
		for(i=0;i<list.size();i++){
			response.getResult().add(new SharingItemWrapper(list.get(i).getResource(), list.get(i).getPending()));
		}
		return response;
	}
	
	/**
	 *  
	 * si occupa di creare il file nello spazio condiviso.
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/sharedwithyou/files",method=RequestMethod.POST)
	public @ResponseBody Response createFriendFile(@Valid FriendFileInput file,
													BindingResult bindingResult,
													HttpSession session) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		
//		System.out.println("il parent è:"+file.getId());
		
		try{
			User user=new User();
			user.setId(userId);
			Resource r=sharingService.getResource(file.getId());
			Sharing parent=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(r, user);
			
			if(parent!=null){
			
			FileUploadWrapper fuw=sharingService.createFile(file.getName(),
														file.getDigest(),
														file.getChunkNumber(),
														file.getMime(),
														file.getSize(), 
														user,
														parent.getResource(),
														parent.getOwnerUser());
		
			response.getResult().add(fuw);
			}
		}catch(HibernateException e){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		} catch (FileNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		}
		return response;
	}


	/**
	 * rinomina il file
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/sharedwithyou/file/{id}",method=RequestMethod.PUT,consumes={"application/json"})
	public @ResponseBody Response rinominaFile(@Valid @RequestBody ResourceInput fileResource,
												BindingResult bindingResult,
												@PathVariable("id") int resourceId,
												HttpSession session) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		
		try{
			User user=new User();
			user.setId(userId);
			Resource r=sharingService.getResource(resourceId);
			Sharing parent=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(r, user);
			
			if(parent!=null){/*
			il file che ho condiviso non lo posso rinominare. solo i suoi figli, 
			altrimenti fa casini nell'owner e potrebbe estendersi i diritti se è una directory
			*/
				if(sharingService.havePermissionWriteResource(r, user)){//guardo se ho i permessi su un genitore
					String name=sharingService.buildNameFromParent(parent.getResource(),fileResource.getName());
//					System.out.println("nuovo nome "+name);
					sharingService.rinominaFile(r, parent.getOwnerUser(), name);
					response.setStatus(Status.OK);
					response.appendMessage(Status.OK,"file rinominato");
				}
			}else{
				response.setStatus(Status.FAIL);
				response.appendError(Status.FAIL, "sharing empty");
			}
		}catch(HibernateException e){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		} catch (FileNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		}
		return response;
	}
	
	/**
	 * crea una nuova directory
	 * @param dir l'id rappresenta l'oggetto parent su cui ci sono i permessi, il name è il nome della directory da creare, path compreso
	 * @param session
	 * @param bindingResult
	 * @return 
	 */
	@RequestMapping(value="/sharedwithyou/files/newdirectory",method=RequestMethod.POST)
	public @ResponseBody Response newDirectory(@Valid Directory dir,
										BindingResult bindingResult,
										HttpSession session) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try {
			Resource resource,parent;
			User user=new User();
			user.setId(userId.intValue());
			user=accountService.getUserAccountDetails(user);
			parent=sharingService.getResource(dir.getId());
			if(sharingService.havePermissionWriteResource(parent, user)){
				String resourceName=sharingService.buildNameFromParent(parent, dir.getName());
				List<Sharing> s=sharingService.getSharing(parent, user);
				
				resource=sharingService.createDirectory(resourceName, s.get(0).getOwnerUser());
				
				DirectoryWrapper dw=new DirectoryWrapper();
				dw.setId((new Integer(resource.getId())).toString());
				dw.setName(dir.getName());
				
				response.setStatus(Status.OK);
				response.getResult().add(dw);
				
			}
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"user not found");
		} catch (HibernateException h){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,h.getMessage());
			
		} catch (FileNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		}
		return response;
	}
	
	/**
	 * solo se sei l'owner
	 * @param fileId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sharedwithyou/file/{id}",method=RequestMethod.POST)
	public @ResponseBody Response createSharingFile(@PathVariable("id") int fileId,
													@Valid SharingInputCreation sharingInputCreation,
													BindingResult bindingResult,HttpSession session){
		Response resp=new Response();
		if(bindingResult.hasErrors()){
			resp.setStatus(Status.BAD_PARAM);
			resp.appendAllErrors(bindingResult.getAllErrors());
            return resp;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=accountService.getUserDao().findOne(userId);
		Resource r;
		try {
			r = sharingService.getResource(fileId);
			User ut=null;
			if(u!=null && r!= null){
				ut = accountService.getUserDao().findByEmail(sharingInputCreation.getEmail());
			}
			if(u!=null && r!= null && ut==null){
				User newUser=new User();
				newUser.setEmail(sharingInputCreation.getEmail());
				String casualPass=""+Math.round((Math.random()*100000));
				newUser.setPassword(casualPass);
//				System.out.println(sharingInputCreation.getEmail());
				try {
					accountService.createAccount(newUser);
					ut=accountService.getUserDao().findByEmail(sharingInputCreation.getEmail());
//					System.out.println("trovato per email");
					accountService.recuperaAccount(ut);
				} catch (AccountCreationException e) {
					resp.setStatus(Status.FAIL);
					resp.appendError(Status.FAIL, "error");
					return resp;
				} catch (UserNotFoundException e) {
					resp.setStatus(Status.BAD_PARAM);
					resp.appendError(Status.BAD_PARAM, "error in params");
					return resp;
				}
			}
			if(u!=null && r!=null && ut!=null){
				if(sharingService.isOwnerResource(r, u)){
					SharingMode sm=sharingService.getSharingModeDao().findOne(sharingInputCreation.getMode());
					if(sm!=null){
						try {
							sharingService.shareResource(r,u, ut, sm);
							resp.setStatus(Status.OK);
							return resp;
						} catch (FileNotOwnedException e) {
							resp.setStatus(Status.BAD_PARAM);
							resp.appendError(Status.BAD_PARAM, "missing permission");
						}
					}
				}
			}
		} catch (FileNotFoundException e1) {
			resp.setStatus(Status.BAD_PARAM);
			resp.appendError(Status.BAD_PARAM, "error in params");
		}
		
		resp.setStatus(Status.BAD_PARAM);
		resp.appendError(Status.BAD_PARAM, "error in params");
		return resp;
	}
	
	/**
	 * sposta dentro il file 
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/sharedwithyou/file/{id}/moveinto",method=RequestMethod.PUT,consumes={"application/json"})
	public @ResponseBody Response spostaDentroFile(@Valid @RequestBody Integer[] resourceIdList,
												BindingResult bindingResult,
												@PathVariable("id") int resourceId,
												HttpSession session) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		
  		try{
  			User user=new User();
  			user.setId(userId);
  			Resource target=sharingService.getResource(resourceId);
  			Sharing parent=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(target, user);
  			
  			if(parent!=null){
  				List<Resource> moved=new ArrayList<Resource>();
  				if(sharingService.havePermissionWriteResource(target, user)){
  					if(target.isDirectory()){
  						List<Resource> resourceList=sharingService.getResourcesById(resourceIdList);
  						
  						for (Resource resource : resourceList) {
  							if(sharingService.havePermissionWriteResource(resource, user)){
  								resource=sharingService.moveResource(target, resource, parent.getOwnerUser());
  								if(resource!=null){
  									resource.setName(sharingService.trimUnknownPathFromResourceName(resource, user));
  									moved.add(resource);
  								}
  							}
  						}
  					}
  				}
  				response.setResult(new ItemListWrapper(moved).getItem().toArray());
  				response.setStatus(Status.OK);
  			}
			
			
		}catch(HibernateException e){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		} catch (FileNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		}
		

		return response;
	}
	
	
	/**
	 * sposta una serie di file nel cestino.
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/sharedwithyou/files/trash",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response setFilesInTrash(@Valid @RequestBody Integer[] resources,
													BindingResult bindingResult,
													HttpSession session) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		try{
			int i;
			User user=new User();
			user.setId(userId);
			user=accountService.getUserAccountDetails(user);
			Resource r;
			for(i=0;i<resources.length;i++){
				r=sharingService.getResource(resources[i].intValue());
				if(sharingService.havePermissionWriteResource(r, user)){
					Sharing sh=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(r,user);
					if(sh!=null && !r.isDeleted()){//se non è nel cestino
						sharingService.moveToTrashResource(r, sh.getOwnerUser());
					}//la cancellazione dal cestino non ha senso supportarla
				}
			}
			response.setStatus(Status.OK);
			return response;
		}catch(HibernateException i){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,i.getMessage());
			return response;
		}catch(FileNotFoundException e){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
			return response;
		}catch(FileNotOwnedException e){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
			return response;
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
			return response;
		} catch (Exception e){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
			return response;
		}
	}
}
