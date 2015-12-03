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
package it.polito.ai.polibox.web.controllers.rest;

import it.polito.ai.polibox.persistency.model.Resource;
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.ResourceVersionId;
import it.polito.ai.polibox.persistency.model.Sharing;
import it.polito.ai.polibox.persistency.model.SharingMode;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.exception.AccountCreationException;
import it.polito.ai.polibox.persistency.model.exception.FileNotOwnedException;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.persistency.model.service.FileService;
import it.polito.ai.polibox.persistency.model.service.SharingService;
import it.polito.ai.polibox.web.controllers.inputform.Directory;
import it.polito.ai.polibox.web.controllers.inputform.FileInput;
import it.polito.ai.polibox.web.controllers.inputform.FriendFileInput;
import it.polito.ai.polibox.web.controllers.inputform.ResourceInput;
import it.polito.ai.polibox.web.controllers.inputform.SharingInputCreation;
import it.polito.ai.polibox.web.controllers.inputform.UploadChunk;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;
import it.polito.ai.polibox.web.controllers.result.DirectoryWrapper;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.FullResource;
import it.polito.ai.polibox.web.controllers.result.ItemListWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(produces={MediaType.APPLICATION_JSON})
public class FileRestController {
	
	private static final Logger LOGGER = Logger.getLogger(FileRestController.class.getName());
	
	@Autowired
	private FileService fileService;

	@Autowired
	private SharingService sharingService;
	
	@Autowired
	private AccountService accountService;

	public FileRestController() {
		
	}

	/**
	 * restituisce tutti i tuoi file
	 * 
	 * @param dir
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/rest/files/all", method = RequestMethod.GET)
	public @ResponseBody
	Response getAllYourFiles(HttpServletRequest request) {
		Response response = new Response();
		try {
			User user = new User();
			user.setId(Integer.parseInt(request.getHeader("user")));
			response.setStatus(Status.OK);
			response.setResult(fileService.getAllMyFullResource(user).toArray());
		} catch (HibernateException i) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL, i.getMessage());
			return response;
		}
		return response;
	}

	/**
	 * restituisce tutti i file condivisi con te
	 * 
	 * @param dir
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/rest/sharedwithyou/files/all", method = RequestMethod.GET)
	public @ResponseBody
	Response getAllFilesSharedWithYou(HttpServletRequest request) {
		Response response = new Response();
		try {
			User user = new User();
			user.setId(Integer.parseInt(request.getHeader("user")));
			response.setStatus(Status.OK);
			response.setResult(sharingService.getSharedWithMeFullResource(user)
					.toArray());
		} catch (HibernateException i) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL, i.getMessage());
			return response;
		}
		return response;
	}


	/**
	 * crea una nuova directory
	 * @param dir
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/rest/your/files/newdirectory",method=RequestMethod.POST,consumes={MediaType.APPLICATION_JSON})
	public @ResponseBody Response newDirectory(@RequestBody @Valid Directory dir,
										BindingResult bindingResult,
										HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		try {
			Resource resource;
//			System.out.println("directory ricevuta:"+dir.getName() + " "+dir.getId());
//			System.err.println(dir.getName());;
			User user=new User();
			user.setId(Integer.parseInt(request.getHeader("user")));
			user=accountService.getUserAccountDetails(user);
			resource=fileService.createDirectory(dir.getName(),  user);
			response.setStatus(Status.OK);
			response.getResult().add(new FullResource(resource));
		} catch (UserNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,"user not found");
		} catch (HibernateException h){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,h.getMessage());
		}
		return response;
	}
	
	/**
	 * crea file da zero. se il file esiste già crea nuova versione.
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/rest/your/files",method=RequestMethod.POST,consumes={MediaType.APPLICATION_JSON})
	public @ResponseBody Response createFile(@RequestBody @Valid FileInput file,
										BindingResult bindingResult,
										HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try{
//			System.out.println("file ricevuta:|"+file.getName() + "|"+file.getSize()+ "|"+ file.getChunkNumber()+"|"+file.getDigest()+"|"+file.getMime()+"|"+ user.getId()+"|");
			FileUploadWrapper fuw=fileService.createFile(file.getName(),
														file.getDigest(),
														file.getChunkNumber(),
														file.getMime(),
														file.getSize(), 
														user);
			FullResource f=new FullResource(fuw,user);
			response.getResult().add(f);
		}catch(HibernateException e){
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
	@RequestMapping(value="/rest/your/file/{id}",method=RequestMethod.PUT,consumes={"application/json"})
	public @ResponseBody Response rinominaFile(@Valid @RequestBody ResourceInput fileResource,
												BindingResult bindingResult,
												@PathVariable("id") int resourceId,
												HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try{
			Resource r=fileService.getResource(resourceId);
			if(fileService.isOwnerResource(r, user)){
//				System.out.println("filename:"+fileResource.getName());
				FullResource f=new FullResource(fileService.rinominaFile(r, user, fileResource.getName()));
				response.getResult().add(f);
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
	@RequestMapping(value="/rest/your/files/trash",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response setFilesInTrash(@Valid @RequestBody Integer[] resources,
													BindingResult bindingResult,
													HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try{
			int i;
			user=accountService.getUserAccountDetails(user);
			Resource r;
			for(i=0;i<resources.length;i++){
				r=fileService.getResource(resources[i].intValue());
				if(fileService.isOwnerResource(r, user)){
					if(!r.isDeleted()){//se non è nel cestino
						fileService.moveToTrashResource(r, user);
						FullResource f=new FullResource(r);
						f.setDeleted(true);
						response.getResult().add(f);
					}//qua non è prevista la cancellazione definitiva.
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
	
	@RequestMapping(value="/rest/your/files/trash/ripristina",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response unsetFilesInTrash(@Valid @RequestBody Integer[] resources,
													BindingResult bindingResult,
													HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try{
			int i;
			user=accountService.getUserAccountDetails(user);
			Resource r;
			for(i=0;i<resources.length;i++){
				r=fileService.getResource(resources[i].intValue());
				if(fileService.isOwnerResource(r, user)){
					if(r.isDeleted()){//se è nel cestino
						fileService.moveFromTrashResource(r, user);
						FullResource f=new FullResource(r);
						f.setDeleted(false);
						response.getResult().add(f);
					}
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
		}
	}
	
	/**
	 * recupera l'ultima versione restituisce il fullresource
	 * @param resource
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/rest/your/file/{id}/last",method=RequestMethod.GET)	
	public @ResponseBody Response downloadResource(@PathVariable("id") int resource,
												HttpServletRequest request){
		Response response=new Response();
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try {
			Resource r = fileService.getResource(resource);
			FullResource f;
			if(fileService.isOwnerResource(r, user) || sharingService.havePermissionResource(r, user)){
				if(r.isDirectory()){
					f=new FullResource(r,user);
					response.getResult().add(f);
				}else{
					ResourceVersion lastVersion=fileService.getResourceVersionDao().getLastResourceVersion(r);
					f=new FullResource(lastVersion,user);
					response.getResult().add(f);
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		}
		return response;
	}
	
	
	/**
	 * fa il download effettivo di una versione di file
	 * @return
	 */
	@RequestMapping(value="/rest/file/{id}/{version}",method=RequestMethod.GET)	
	public @ResponseBody void download(@PathVariable("id") int resource,
										@PathVariable("version") int version,
										HttpServletRequest request,
										HttpServletResponse response) {
		User u=new User();
		u.setId(Integer.parseInt(request.getHeader("user")));
		Resource r;
		int i;
		try {
			r = fileService.getResource(resource);
			if(fileService.isOwnerResource(r, u) || sharingService.havePermissionResource(r, u)){
				if(!r.isDirectory()){//se è un file
					ResourceVersionId rvid=new ResourceVersionId();
					rvid.setResource(r);
					rvid.setVersion(version);
					ResourceVersion rv=fileService.getResourceVersion(rvid);
					List<ResourceChunk> set=fileService.downloadResource(rv);
					List<Blob> lb=new ArrayList<Blob>();
					for(i=0;i<set.size();i++){
						lb.add(set.get(i).getData());
					}
			
					response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+r.getName().replaceFirst("/", "").replace("/", "-"));
					response.addHeader(HttpHeaders.CONTENT_TYPE, rv.getMime());
					response.addHeader(HttpHeaders.CONTENT_LENGTH, ""+ rv.getSize());
					ServletOutputStream out=response.getOutputStream();
					for(i=0;i<lb.size();i++){
						out.write((lb.get(i).getBytes(1, (int) lb.get(i).length())));
					}
					out.close();
				}
			}
		}catch (FileNotFoundException e) {
			LOGGER.error(e);
		}catch (SQLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	
	}
	
	
	/**
	 *  
	 * si occupa di creare il file nello spazio condiviso.
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/rest/sharedwithyou/files",method=RequestMethod.POST,consumes={MediaType.APPLICATION_JSON})
	public @ResponseBody Response createFriendFile(@RequestBody @Valid FriendFileInput file,
													BindingResult bindingResult,
													HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		
		System.out.println("il parent è:"+file.getId());
		
		try{
			User user=new User();
			user.setId(Integer.parseInt(request.getHeader("user")));
			Resource r=sharingService.getResource(file.getId());
			Sharing parent=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(r, user);
			
			if(parent!=null){
			System.out.println("parent:"+file.getId()+"|mime:"+file.getMime()+"|chunkNum:"+file.getChunkNumber());
			FileUploadWrapper fuw=sharingService.createFile(file.getName(),
														file.getDigest(),
														file.getChunkNumber(),
														file.getMime(),
														file.getSize(), 
														user,
														parent.getResource(),
														parent.getOwnerUser());
			System.out.println("parent:"+fuw.getId()+"|version:"+fuw.getVersion()+"|mime:"+fuw.getMime()+"|chunkNum:"+fuw.getChunkNumber());
			FullResource f=new FullResource(fuw,parent.getOwnerUser());
			response.getResult().add(f);
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
	@RequestMapping(value="/rest/sharedwithyou/file/{id}",method=RequestMethod.PUT,consumes={"application/json"})
	public @ResponseBody Response rinominaFriendFile(@Valid @RequestBody ResourceInput fileResource,
												BindingResult bindingResult,
												@PathVariable("id") int resourceId,
												HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		
		try{
			User user=new User();
			user.setId(Integer.parseInt(request.getHeader("user")));
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
	@RequestMapping(value="/rest/sharedwithyou/files/newdirectory",method=RequestMethod.POST,consumes={MediaType.APPLICATION_JSON})
	public @ResponseBody Response newFriendDirectory(@RequestBody @Valid Directory dir,
										BindingResult bindingResult,
										HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		try {
			Resource resource,parent;
			User user=new User();
			user.setId(Integer.parseInt(request.getHeader("user")));
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
	@RequestMapping(value="/rest/sharedwithyou/file/{id}",method=RequestMethod.POST)
	public @ResponseBody Response createSharingFile(@PathVariable("id") int fileId,
													@Valid SharingInputCreation sharingInputCreation,
													BindingResult bindingResult,
													HttpServletRequest request){
		Response resp=new Response();
		if(bindingResult.hasErrors()){
			resp.setStatus(Status.BAD_PARAM);
			resp.appendAllErrors(bindingResult.getAllErrors());
            return resp;
        }
		User u=accountService.getUserDao().findOne(Integer.parseInt(request.getHeader("user")));
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
	@RequestMapping(value="/rest/sharedwithyou/file/{id}/moveinto",method=RequestMethod.PUT,consumes={"application/json"})
	public @ResponseBody Response spostaDentroFile(@Valid @RequestBody Integer[] resourceIdList,
												BindingResult bindingResult,
												@PathVariable("id") int resourceId,
												HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		
		
  		try{
  			User user=new User();
  			user.setId(Integer.parseInt(request.getHeader("user")));
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
	@RequestMapping(value="/rest/sharedwithyou/files/trash",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response setSharedFilesInTrash(@Valid @RequestBody Integer[] resources,
													BindingResult bindingResult,
													HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try{
			int i;
			user=accountService.getUserAccountDetails(user);
			Resource r;
			Sharing parent;
			for(i=0;i<resources.length;i++){
				r=fileService.getResource(resources[i].intValue());
				parent=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(r, user);
	  			if(parent!=null){
	  				if(sharingService.havePermissionWriteResource(r, user)){
						if(!r.isDeleted()){//se non è nel cestino
							fileService.moveToTrashResource(r, parent.getOwnerUser());
							FullResource f=new FullResource(r);
							f.setDeleted(true);
							response.getResult().add(f);
						}//qua non è prevista la cancellazione definitiva.
	  				}
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
	
	@RequestMapping(value="/rest/sharedwithyou/files/trash/ripristina",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response unsetSharedFilesInTrash(@Valid @RequestBody Integer[] resources,
													BindingResult bindingResult,
													HttpServletRequest request) {
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		User user=new User();
		user.setId(Integer.parseInt(request.getHeader("user")));
		try{
			int i;
			user=accountService.getUserAccountDetails(user);
			Resource r;
			Sharing parent;
			for(i=0;i<resources.length;i++){
				r=fileService.getResource(resources[i].intValue());
				parent=sharingService.getFirstParentAcceptedSharedResourceByResourceAndTarget(r, user);
	  			if(parent!=null){
	  				if(sharingService.havePermissionWriteResource(r, user)){
						if(r.isDeleted()){//se è nel cestino
							fileService.moveFromTrashResource(r, parent.getOwnerUser());
							FullResource f=new FullResource(r);
							f.setDeleted(false);
							response.getResult().add(f);
						}
	  				}
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
		}
	}
	
	
	/**
	 * fa upload dei chunk, 
	 * il path accetta solo numeri
	 * @param chunk
	 * @param resourceId
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value={"/rest/file/{id}/{version}"}, method=RequestMethod.POST)
	public @ResponseBody Response upload(@Valid UploadChunk chunk,
										BindingResult bindingResult,
										@PathVariable("id") int id,
										@PathVariable("version") int version,
										HttpServletRequest request) {
		Response response=new Response();
		
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
//		System.out.println("digest:"+chunk.getDigest());
//		System.out.println("digest length:"+chunk.getDigest().length());
		Integer userId=Integer.parseInt(request.getHeader("user"));
		if(fileService.isCreatorResourceVersion(id, version, userId)){
			try{
				ResourceChunk rc=new ResourceChunk();
				rc.getPk().getResourceVersion().getPk().getResource().setId(id);
				rc.getPk().getResourceVersion().getPk().setVersion(version);
				rc.getPk().setNumber(chunk.getChunkNumber());
				rc.setDigest(chunk.getDigest());
				byte[] b=chunk.getByteData();
				rc.setSize(b.length);
				rc.setData(new SerialBlob(b));

				fileService.uploadResourceChunk(rc);
				response.setStatus(Status.OK);
			}catch(HibernateException e){
				response.appendError(Status.FAIL, e.getMessage());
			} catch (SerialException e) {
				response.appendError(Status.FAIL, e.getMessage());
			} catch (SQLException e) {
				response.appendError(Status.FAIL, e.getMessage());
			}
		}else{
			response.appendError(Status.FAIL, "wrong permission");
		}
		return response;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public SharingService getSharingService() {
		return sharingService;
	}

	public void setSharingService(SharingService sharingService) {
		this.sharingService = sharingService;
	}

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
}
