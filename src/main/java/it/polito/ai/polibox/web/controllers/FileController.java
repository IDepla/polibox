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
import it.polito.ai.polibox.persistency.model.ResourceChunk;
import it.polito.ai.polibox.persistency.model.ResourceOwners;
import it.polito.ai.polibox.persistency.model.ResourceVersion;
import it.polito.ai.polibox.persistency.model.ResourceVersionId;
import it.polito.ai.polibox.persistency.model.Sharing;
import it.polito.ai.polibox.persistency.model.User;
import it.polito.ai.polibox.persistency.model.exception.FileNotOwnedException;
import it.polito.ai.polibox.persistency.model.exception.UserNotFoundException;
import it.polito.ai.polibox.persistency.model.service.AccountService;
import it.polito.ai.polibox.persistency.model.service.FileService;
import it.polito.ai.polibox.persistency.model.service.SharingService;
import it.polito.ai.polibox.service.ZipBuilderService;
import it.polito.ai.polibox.web.controllers.inputform.Directory;
import it.polito.ai.polibox.web.controllers.inputform.FileInput;
import it.polito.ai.polibox.web.controllers.inputform.ResourceInput;
import it.polito.ai.polibox.web.controllers.inputform.UploadChunk;
import it.polito.ai.polibox.web.controllers.inputform.validation.DirectoryValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.FileInputValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.ResourceInputValidator;
import it.polito.ai.polibox.web.controllers.inputform.validation.UploadChunkValidator;
import it.polito.ai.polibox.web.controllers.response.Response;
import it.polito.ai.polibox.web.controllers.response.constants.Status;
import it.polito.ai.polibox.web.controllers.result.DirectoryWrapper;
import it.polito.ai.polibox.web.controllers.result.FileUploadWrapper;
import it.polito.ai.polibox.web.controllers.result.ItemListWrapper;
import it.polito.ai.polibox.web.controllers.result.PropertiesWrapper;
import it.polito.ai.polibox.web.controllers.session.constants.SessionAttribute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileController {

	private static final Logger LOGGER = Logger.getLogger(FileController.class.getName());
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SharingService sharingService;
	
	@Autowired
	private ZipBuilderService zipBuilderService;
	
	public ZipBuilderService getZipBuilderService() {
		return zipBuilderService;
	}

	public void setZipBuilderService(ZipBuilderService zipBuilderService) {
		this.zipBuilderService = zipBuilderService;
	}

	public SharingService getSharingService() {
		return sharingService;
	}

	public void setSharingService(SharingService sharingService) {
		this.sharingService = sharingService;
	}

	public FileController() {
		
	}

	@InitBinder("dir")
    protected void initDirBinder(WebDataBinder binder) {
		binder.setValidator(new DirectoryValidator());
    }
	
	@InitBinder("file")
    protected void initFileBinder(WebDataBinder binder) {
		binder.setValidator(new FileInputValidator());
    }
	
	@InitBinder("fileResource")
    protected void initResourceFileBinder(WebDataBinder binder) {
		binder.setValidator(new ResourceInputValidator());
    }
	
	
	@InitBinder("chunk")
    protected void initChunkBinder(WebDataBinder binder) {
		binder.setValidator(
				new UploadChunkValidator()
				);
    }
	
	/**
	 * crea file da zero. se il file esiste già crea nuova versione.
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/your/files",method=RequestMethod.POST)
	public @ResponseBody Response createFile(@Valid FileInput file,
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
			User user=new User();
			user.setId(userId);
			FileUploadWrapper fuw=fileService.createFile(file.getName(),
														file.getDigest(),
														file.getChunkNumber(),
														file.getMime(),
														file.getSize(), 
														user);
			response.getResult().add(fuw);
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
	@RequestMapping(value="/your/file/{id}",method=RequestMethod.PUT,consumes={"application/json"})
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
			Resource r=fileService.getResource(resourceId);
			if(fileService.isOwnerResource(r, user)){
//				System.out.println("filename:"+fileResource.getName());
				fileService.rinominaFile(r, user, fileResource.getName());
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
	 * sposta dentro il file
	 * @param file
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/your/file/{id}/moveinto",method=RequestMethod.PUT,consumes={"application/json"})
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
		User u=new User();
		u.setId(userId);
		Resource target;
		try {
			if(resourceId==0){
				target= new Resource();
				target.setName("");
				target.setDirectory(true);
			}else{
				target = fileService.getResource(resourceId);
			}
			List<Resource> moved=new ArrayList<Resource>();
			if(resourceId==0 || fileService.isOwnerResource(target, u)){
				if(target.isDirectory()){
					List<Resource> resourceList=fileService.getResourcesById(resourceIdList);
					
					for (Resource resource : resourceList) {
						if(fileService.isOwnerResource(resource, u)){
							resource=fileService.moveResource(target, resource, u);
							if(resource!=null)
								moved.add(resource);
						}
					}
				}
			}
			response.setResult(new ItemListWrapper(moved).getItem().toArray());
			response.setStatus(Status.OK);
		} catch (FileNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		}
		return response;
	}
	
	/**
	 * fa il promote di una versione di file 
	 * @return
	 */
	@RequestMapping(value="/your/file/{id}/{version}",method=RequestMethod.PUT)	
	public @ResponseBody Response promoteVersion(@PathVariable("id") int resource,
										@PathVariable("version") int version,
										HttpSession session) {
		Response response=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=new User();
		u.setId(userId);
		Resource r;
		try {
			r = fileService.getResource(resource);
			if(!r.isDirectory()){
				if(fileService.isOwnerResource(r, u) || sharingService.havePermissionResource(r, u)){
					ResourceVersion rv=fileService.promuoviVersione(r, version, u);
					if(rv!=null){
						response.setStatus(Status.OK);
						response.appendMessage(Status.OK, "file promoted");
						PropertiesWrapper v=new PropertiesWrapper();
						PropertiesWrapper.Version w=v.new Version();
						w.setFrom(rv.getCreationTime());
						w.setSize(rv.getSize());
						w.setUploader(rv.getCreator().getEmail());
						w.setVersion(rv.getPk().getVersion());
						response.getResult().add(w);
					}else{
						response.setStatus(Status.FAIL);
						response.appendError(Status.FAIL,"error");
					}
				}
			}
		} catch (FileNotFoundException e) {
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,e.getMessage());
		}
		return response;
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
	@RequestMapping(value={"/your/file/{id}/{version}",
						   "/sharedwithyou/file/{id}/{version}"}, method=RequestMethod.POST)
	public @ResponseBody Response upload(@Valid UploadChunk chunk,
										BindingResult bindingResult,
										@PathVariable("id") int id,
										@PathVariable("version") int version,
										HttpSession session) {
		Response response=new Response();
		
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
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
	
	/**
	 * fa il download di una versione di file
	 * @return
	 */
	@RequestMapping(value={"/your/file/{id}/{version}","/sharedwithyou/file/{id}/{version}"},method=RequestMethod.GET)	
	public @ResponseBody void download(@PathVariable("id") int resource,
										@PathVariable("version") int version,
										HttpSession session,
										HttpServletResponse response) {
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		User u=new User();
		u.setId(userId);
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
			
					response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+r.getName().substring(r.getName().lastIndexOf("/")+1));
					response.addHeader(HttpHeaders.CONTENT_TYPE, rv.getMime());
					response.addHeader(HttpHeaders.CONTENT_LENGTH, ""+ rv.getSize());
					ServletOutputStream out=response.getOutputStream();
					for(i=0;i<lb.size();i++){
						//out.write(Base64.decodeBase64(lb.get(i).getBytes(1, (int) lb.get(i).length())));
						out.write(lb.get(i).getBytes(1, (int) lb.get(i).length()));
					}
					out.close();
				}else{//se è una directory
					File zippedContent=zipBuilderService.prepareZip(r);
					response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+r.getName().substring(r.getName().lastIndexOf("/")+1).concat(".zip"));
					response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
					response.addHeader(HttpHeaders.CONTENT_LENGTH, ""+ zippedContent.length());
					ServletOutputStream out=response.getOutputStream();
					FileInputStream fis=new FileInputStream(zippedContent);
					byte[] bytes = new byte[1024*50];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						out.write(bytes, 0, length);
					}
					fis.close();
					out.close();
					zippedContent.delete();
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
	@RequestMapping(value={"/your/file/{id}","/sharedwithyou/file/{id}"},method=RequestMethod.GET)	
	public @ResponseBody void downloadResource(@PathVariable("id") int resource,
												HttpSession session,
												HttpServletRequest request,
												HttpServletResponse response){
		int version;
		Resource r;
		try {
			r = fileService.getResource(resource);
			if(r.isDirectory()){
				version=-1;
			}else{
				ResourceVersion lastVersion=fileService.getResourceVersionDao().getLastResourceVersion(r);
				version=lastVersion.getPk().getVersion();
			}
			request.getRequestDispatcher("/your/file/"+resource+"/"+version).forward(request, response);
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (ServletException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * crea una nuova directory
	 * 
	 * 
	 * @param dir l'id rappresenta l'oggetto parent su cui ci sono i permessi, il name è il nome della directory da creare, path compreso
	 * @param session
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/your/files/newdirectory",method=RequestMethod.POST)
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
			Resource resource;
			User user=new User();
			user.setId(userId.intValue());
			user=accountService.getUserAccountDetails(user);
			resource=fileService.createDirectory(dir.getName(),  user);
			
			DirectoryWrapper dw=new DirectoryWrapper();
			dw.setId((new Integer(resource.getId())).toString());
			dw.setName(resource.getName());
			
			response.setStatus(Status.OK);
			response.getResult().add(dw);
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
 * restituisce i tuoi file
 * @param dir
 * @param session
 * @param bindingResult
 * @return
 */
	@RequestMapping(value="/your/files",method=RequestMethod.GET)
	public @ResponseBody Response getYourFiles(@Valid Directory dir,
												BindingResult bindingResult,
												HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		try{
			Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
			User user=new User();
			user.setId(userId);
//			System.out.println(dir.getName());
			ItemListWrapper it=new ItemListWrapper(fileService.getMyResource(dir.getName(), user));
			response.setStatus(Status.OK);
			response.setResult(it.getItem().toArray());
			return response;
		}catch(HibernateException i){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,i.getMessage());
			return response;
		}
	}
	

	

	/**
	 * restituisce i file che sono nel tuo cestino
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/your/files/trash",method=RequestMethod.GET)
	public @ResponseBody Response getTrash(@Valid Directory dir,
											BindingResult bindingResult,
											HttpSession session){
		Response response=new Response();
		if(bindingResult.hasErrors()){
			response.setStatus(Status.BAD_PARAM);
			response.appendAllErrors(bindingResult.getAllErrors());
            return response;
        }
		try{
			Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
			User user=new User();
			user.setId(userId);
			ItemListWrapper it;
			if(dir.getName().isEmpty() || dir.getName().equalsIgnoreCase("/")){
				it=new ItemListWrapper(fileService.getTrashResource( user));
			}else{
				it=new ItemListWrapper(fileService.getMyResource(dir.getName(), user));
			}
			
			response.setStatus(Status.OK);
			response.setResult(it.getItem().toArray());
			return response;
		}catch(HibernateException i){
			response.setStatus(Status.FAIL);
			response.appendError(Status.FAIL,i.getMessage());
			return response;
		}
	}
	/**
	 * sposta una serie di file nel cestino.
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/your/files/trash",method=RequestMethod.POST, consumes={"application/json"})
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
				r=fileService.getResource(resources[i].intValue());
				if(fileService.isOwnerResource(r, user)){
					if(!r.isDeleted()){//se non è nel cestino
						fileService.moveToTrashResource(r, user);
					}else{//se è nel cestino cancella definitivamente e ricorsivamente
						fileService.deleteResource(r, user);
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
	@RequestMapping(value="/your/files/trash/ripristina",method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Response unsetFilesInTrash(@Valid @RequestBody Integer[] resources,
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
				r=fileService.getResource(resources[i].intValue());
				if(fileService.isOwnerResource(r, user)){
					if(r.isDeleted()){//se è nel cestino
						fileService.moveFromTrashResource(r, user);
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
	 * restituisce le proprietà di una risorsa
	 * @return
	 */
	@RequestMapping(value="/your/files/{id}/properties",method=RequestMethod.GET)
	public @ResponseBody Response getFileProperties(@PathVariable("id") int id,
													HttpSession session){
		Response resp=new Response();
		Integer userId=(Integer)session.getAttribute(SessionAttribute.LOGGED_USER);
		Resource r;
		try {
			r = fileService.getResource(id);
			User u = new User();
			u.setId(userId);
			int ind;
			PropertiesWrapper pw=new PropertiesWrapper();
			if(fileService.isOwnerResource(r, u) || sharingService.havePermissionResource(r, u)){
				if(fileService.isOwnerResource(r, u)){
					pw.setName(r.getName());
				}else{//devi trimmare il path 
					String displayedName=sharingService.trimUnknownPathFromResourceName(r, u);
					pw.setName(displayedName);
				}
				pw.setResource(id);
				if(!r.isDirectory()){
					ResourceVersion lastVersion=fileService.getResourceVersionDao().getLastResourceVersion(r);
					pw.setVersion(lastVersion.getPk().getVersion());
					pw.setSize(lastVersion.getSize());
				}else{
					pw.setSize(fileService.getDirectorySize(r,u));
					pw.setVersion(-1);
				}
				List<ResourceOwners> list=fileService.getOwnersByResource(r);
				for (ResourceOwners user : list) {
					pw.addOwner(user.getUser().getName()+" "+user.getUser().getSurname(), 
							user.getUser().getEmail(), 
							user.getFrom());
				}
				
				List<ResourceVersion> lrv=fileService.getResourceVersionDao().getHistory(r);
				ResourceVersion rv;
				for(ind=0;ind<lrv.size();ind++){
					rv=lrv.get(ind);
					pw.addVersion(	rv.getPk().getVersion(), 
									rv.getSize(), 
									rv.getCreationTime(), 
									rv.getCreator().getEmail());
				}
				List<Sharing> ss;
				if(fileService.isOwnerResource(r, u)){
					ss=sharingService.getSharing(r);
					
				}else{
					ss=sharingService.getAcceptedSharing(r);
				}
				for(ind=0;ind<ss.size();ind++){
					pw.addShare(ss.get(ind));
				}
			}
			resp.getResult().add(pw);
		} catch (FileNotFoundException e) {
			resp.setStatus(Status.FAIL);
			resp.appendError(Status.FAIL,e.getMessage());
		}
		return resp;
	}
	


	
	
	
	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}
	
}
