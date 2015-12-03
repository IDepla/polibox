/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.file.service",["polibox.authentication",
	                                       "polibox.helpers",
	                                       "polibox.file.controller",
	                                       "polibox.file.directive",
	                                       "polibox.sharing",
	                                       "ui.bootstrap",
	                                       "polibox.tools"]);
	
	app.factory("uploadService",["$http","$rootScope","pathService","CryptoJS","messenger","directoryService","fileManager",
	                             function($http,$rootScope,pathService,CryptoJS,messenger,directoryService,fileManager){
		var uploadStack=[];
		var lastElemNum=0;
		var uploadTime=0;
		var size_max=1024*100; //100kb
		
		function uploadFile(fileOrigin){
	//		console.info(fileOrigin,parent);
			var reader= new FileReader();
			reader.onload=(function(file){
				return function(event){
//console.info(event);					
					var payload=event.target.result;
					var end=size_max;
					var	start=payload.slice(0,end).indexOf(",")+1;//indice del payload quando carico in base64
					fileOrigin.max=Math.ceil(payload.length/size_max);
//					var start=0;
					end=end+start;//slitto la fine per allineare il file, altrimenti dovrei avere padding etc
//					console.info(payload);
//				console.info(file.resourceName + " size:"+ file.size+" payload size:"+payload.length);
//				console.info(fileOrigin.resourceName + " size:"+ file.size+" payload size:"+payload.length);
					$http.post(fileOrigin.mode+"/files",{//creo il file.
						"name":fileOrigin.resourceName,
						"chunkNumber":Math.ceil(payload.length/size_max),
//						"chunkNumber":Math.ceil(payload.byteLength/size_max),
						"digest":CryptoJS.SHA3(payload).toString(),
						"mime":file.type ,
						"size":file.size,
						"id":fileOrigin.parent
					})
					.success(function(data){
						var fileResponse=data.result[0];
						if(pathService.isChild(pathService.get(),fileResponse.name) && 
								!fileManager.fileExists(fileResponse.id)){
							fileManager.addFile(fileResponse);
						}
						(function uploadChunk(start,number,end){//TODO aggiungere controllo del digest
							 
							$http.post(fileOrigin.mode+"/file/"+fileResponse.id+"/"+fileResponse.version,{//upload del pezzo
								"version": fileResponse.version,
								"resource":fileResponse.id,
								"chunkNumber":number,
								"digest":CryptoJS.SHA3(payload.slice(start, end)).toString(),
								"data":payload.slice(start, end)
//								"data":new Uint8Array(payload.slice(start, end))
								
							})
							.success(function(data){
								fileOrigin.progress++;
								start = 0+end;//numero
							    end = start + size_max;
							    number++;

							    if(start<payload.length){
							    	uploadChunk(start,number,end);
							    }else{
							    	uploadStack.splice(0,1);
							    	uploadList(uploadStack);
							    }
							}).error(function(data){
//								messenger.pushDown($rootScope,"error while uploading "+file.name+" chunk "+number);
							});
						})(start,0,end);
					});
				};
			})(fileOrigin);
			
			//reader.readAsArrayBuffer(fileOrigin);
			reader.readAsDataURL(fileOrigin);
		};
	
		function uploadList(list){
			if(list.length>0){
//				console.info(list);
				if(uploadTime==1){
					list[0].deletable=false;
					var file=list[0];
					if(file.isDirectory==false){
						uploadFile(file);
					}else{
						directoryService.create(file.resourceName,file.mode,parent)
				    		.success(function(data){
				    			if(pathService.isChild(pathService.get(),data.result[0].name)&&
				    				!fileManager.directoryExists(data))
				    				fileManager.addDirectory(data.result[0]);
				    			file.progress=100;
				    			//$scope.$emit("event:refresh-item-list",{});
				    			list.splice(0,1);
				    			uploadList(list);
				    		})
				    		.error(function(data){
				    			messenger.pushDown($rootScope,data.errors);
				    		});
					}
				}
			}
		};
		
		
		function addStack(file,parent){
			file.deletable=true;
			file.progress=0;
			file.max=100;
			file.parent=parent;
			return uploadStack.push(file);
		};
		
		function pushItemFile(entry,basePath,mode,parent){
			var tmpFile={
					resourceName:(basePath+"/"+entry.fullPath).replace(/[^\/A-Za-z0-9._-]/g,"_").replace("//","/"),
		    		size:"?",
		    		isDirectory:false,
		    		mode:mode
		    	};
	    	var index = addStack(tmpFile,parent);
	    	entry.file(function(file){
	    		uploadStack.splice((index-1),1,file);
	    		file.isDirectory=false;
	    		file.resourceName=(basePath+"/"+entry.fullPath).replace(/[^\/A-Za-z0-9._-]/g,"_").replace("//","/");
	    		file.deletable=true;
	    		file.progress=0;
	    		file.mode=mode;
	    		file.parent=parent;
	    		file.max=Math.ceil(file.size/size_max);
	    	},function(){
	    		uploadStack.splice((index-1),1);//elimino il valore temporale
	    		throw entry.fullPath;
	    	});
		}
		
		return {
			"addItemList":function(arrayItem,parent){
//				console.info("addItemList",arrayItem);
				lastElemNum=lastElemNum+arrayItem.length;
				var basePath=pathService.get().slice(0);
				var mode=pathService.getMode();
                angular.forEach(arrayItem,function(v,i){
                	var entry = v.webkitGetAsEntry();
//                	console.info(entry);
    			    if (entry.isFile) {
    			    	pushItemFile(entry,basePath,mode,parent);
    			    } else if (entry.isDirectory) {
    			    	(function exploreDirectory(directory){
    			    		var tmpFile={
        			    		resourceName:(basePath+directory.fullPath),
        			    		size:"?",
        			    		isDirectory:true,
        			    		mode:mode
        			    	};
    			    		addStack(tmpFile,parent);
    						 var dirReader = directory.createReader();
    				    	  (function readEntries() {
    				    	     dirReader.readEntries (function(results) {
    				    	    	 angular.forEach(results,function(v,i){
    				    	    		if(v.isFile){
    				    	    			pushItemFile(v,basePath,mode,parent);
    				    	    		}else if(v.isDirectory){
    				    	    			exploreDirectory(v);
    				    	    		}
    				    	    		readEntries();
    				    	    	 });
    				    	    }, function(){
    				    	    	throw directory.fullPath;
    				    	    });
    				    	  })();
    					})(entry);
    			    }
                });
			},
			"addFileList":function(arrayFile,parent){
//				console.info("addFileList",arrayFile);
				var basePath=pathService.get().slice(0);
				lastElemNum=lastElemNum+arrayFile.length;
				var mode=pathService.getMode();
				angular.forEach(arrayFile,function(v,i){
					v.isDirectory=false;
					v.resourceName=(basePath+"/"+v.name).replace(/[^\/A-Za-z0-9._-]/g,"_").replace("//","/");
					v.mode=mode;
					v.max=Math.ceil(v.size/size_max);
					addStack(v,parent);
				});
			},
			"get":function(){
				return uploadStack;
			},
			"clear":function(){
				uploadStack.length=0;
				uploadTime=0;
			},
			"rollback":function(){
				uploadTime=1;//TODO non son sicuro ricontrolla a che serve
				if(uploadStack.length>0){
					var i;
					for(i=0;i<lastElemNum;i++)
						uploadStack.pop();
					uploadList(uploadStack);
				}
			},
			"start":function(){
				uploadTime=1;
				lastElemNum=0;
				uploadList(uploadStack);
			},
			"stop":function(){
				lastElemNum=0;
				uploadTime=0;
			},
			"remove":function(index){
				uploadStack.splice(index,1);
			}
		};
	}]);
	
	app.factory("directoryService",["$http","pathService",
	                                function($http,pathService){
		
		return {
			create:function(newName,mode,parent){
				var path=newName.replace("//","/");
//		    	console.info("directoryService.create",path);
		    	return $http.post(mode+
		    				"/files/newdirectory",{
		    						"name":path.replace(/[^\/A-Za-z0-9_-]/g,"_"),
		    						"id":parent
		    						});
			}
		}
	}]);	
	

	app.factory("pathService",["fileManager","$http","messenger","selection",
	                           function(fileManager,$http,messenger,selection){
		var path="";
		var url="";
		var mode;
		var lastScope=null;
		var parentId=null;//questo mi serve per lo sharewithyou, negli altri ivene ignorato
		var carica=function($scope){
			$scope=$scope||lastScope;
			selection.clear();
			$http.get(url,{"params":{ "name":path,
									  "id":parentId }
				}).success(function(data){//TODO
					fileManager.setListItem(data.result);
					$scope.$emit("event:refresh-item-list",{});
				}).error(function(data){
					messenger.pushUp($scope,data.messages);
				});
		};
		return {
			init:function(targetUrl,$scope,newPath){
				url=targetUrl;
				mode=targetUrl.split("/")[0];
				lastScope=$scope;
				path=newPath;
				parentId=null;
				carica($scope);
			},
			get:function(){
				return path.slice(0);
			},
			getParent:function(){
				return parentId;
			},
			isYourMode:function(){
				if(mode=="your")
					return true;
				return false;
			},
			isChild:function(base,resource){
				var t1=base.slice(0);
				var t2=resource.slice(0);
				var t3=t2.replace(t1,"");
				if(t3.split("\/").length>2)
					return false;
				return true;
			},
			getMode:function(){
				return mode;
			},
			getScope:function(){
				return lastScope;
			},
			set:function(newPath,id){
				path=newPath;
				parentId=parentId||id;
				carica(lastScope);
//				console.info("pathService.set ->",path);
			},
			clear:function(){
				path="";
				carica(lastScope);
			},
			add:function(newPiece){
				path=path.concat(newPiece);
				carica(lastScope);
//				console.info("pathService.add ->",path);
			},
			purgeBase:function(name){
				return name.slice(path.length+1);
			}
		};
	}]);
	
	app.factory("selection",[function(){
		var selected=[];
		
		return {
			get:function(){
				return selected;
			},
			getCopy:function(){
				return selected.slice(0);
			},
			getFirst:function(){
				return selected[0];
			},
			is:function(){
				if(selected.length == 0)
					return false;
				return true;
			},
			has:function(id){
				var el=selected.indexOf(id);
				if(el==-1){
					return -1;
				}else{
					return id;
				}
			},
			set:function(id){
				if(selected.indexOf(id)==-1){
					selected.push(id);
					return true;
				}else{
					selected.splice(selected.indexOf(id),1);
					return false;
				}
			},
			hasOne:function(){
				if(selected.length == 1)
					return true;
				return false;
			},
			clear:function(){
				selected.length=0;//=[];
			}
		};
	}]);
	app.factory("fileManager",["$http",function($http){
		var fileList=[];
		var directoryList=[];
		
		function search(array,index){
			var i=0;
			for(i=0;i<array.length;i++){
				if(array[i].id==index)
					return i;
			}
			return -1;
		}

		function promiseSearch(array,id){
			var store=[];
			var report=null;
			angular.forEach(array,function(v,i){
				if(v.id==id){
					report=v;
					angular.forEach(store,function(v,i){
						v(report);
					});
				}
			});
			return {
				"then":function(callback){
					if(report==null)
						store.push(callback);
					else
						callback(report);
				}
			};
		}
		
		
		return {
			"getFile":function(){
				return fileList;
			},
			"getDirectory":function(){
				return directoryList;
			},
			"fileExists":function(id){
				if(search(fileList,id)>-1){
					return true;
				}
				return false;
			},
			"directoryExists":function(id){
				if(search(directoryList,id)>-1){
					return true;
				}
				return false;
			},
			"resourceExists":function(id){
				if(search(fileList,id)>-1){
					return true;
				}else if(search(directoryList,id)>-1){
					return true;
				}
				return false;
			},
			"remove":function(array){
				angular.forEach(array,function(v,i){
					var index=search(fileList,v);
					if(index==-1){
						index=search(directoryList,v);
						if(index==-1)
							return false;
						directoryList.splice(index,1);
					}else{
						fileList.splice(index,1);
					}
				});
			},
			"addDirectory":function(data){
				directoryList.push({"id":data.id,"path":data.name});
			},
			"addFile":function(data){
				fileList.push({"id":data.id,"path":data.name});
			},
			"addListItem":function(arrayData){
				angular.forEach(arrayData,function(v,i){
					if(v.directory==true){
						directoryList.push({"id":v.id,"path":v.name});
					}else{
						fileList.push({"id":v.id,"path":v.name});
					}
				});
			},
			"getFileById":function(id){
				return promiseSearch(fileList,id);
			},
			"getDirectoryById":function(id){
				return promiseSearch(directoryList,id);
			},
			"getResourceById":function(id){
				if(search(fileList,id)>-1){
					return promiseSearch(fileList,id);
				}else{
					return promiseSearch(directoryList,id);
				}
			},
			"setListItem":function(arrayData){
				directoryList.length=0;
				fileList.length=0;
				angular.forEach(arrayData,function(v,i){
					if(v.directory==true){
						directoryList.push({"id":v.id,"path":v.name});
					}else{
						fileList.push({"id":v.id,"path":v.name});
					}
				});
			}
		};
	}]);
	
})(window.angular);