/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.file.controller",["polibox.authentication",
                                           "polibox.file.service",
   	                                       "polibox.file.directive",
	                                       "polibox.helpers",
	                                       "polibox.sharing",
	                                       "ui.bootstrap",
	                                       "polibox.tools"]);
	
	app.controller("HomeFileController",["$scope",function($scope){

	}]);

	app.controller("FileContainerController",["$scope","$element","$modal","selection","fileManager","uploadService",
	                                          function($scope,$element,$modal,selection,fileManager,uploadService){
		$scope.fileList=fileManager.getFile();
		$scope.directoryList=fileManager.getDirectory();
				
		$scope.$on("event:refresh-item-list",function(event){
			$scope.fileList=fileManager.getFile();
			$scope.directoryList=fileManager.getDirectory();
		});

		$scope.$on("event:upload-file",function(event,scope){
		    event.stopPropagation();
		    uploadService.stop();
		    var modalInstance = $modal.open({
		      animation: true,
		      templateUrl: 'template-polibox-drop-zone.html',
		      controller: 'FileUploaderController',
		      size: 'lg'
		    });
		    modalInstance.result.then(function(res){
		    	uploadService.start();
		    },function(){
		    	uploadService.rollback();
		    });
		});
	}]);
	

	
	app.controller("FileUploaderController",["$scope","$modalInstance","translate",
	                                         function ($scope, $modalInstance,translate) {
		$scope.cnt={};
		translate("file_modal_controller.json",$scope.cnt);
		 

	  $scope.ok = function () {
	    $modalInstance.close('OK');
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };
	}]);
	
	app.controller("FilePropertiesController",["$scope","$http","$modalInstance","pathService","selection","translate","$window","messenger","sharingMode","$modal","fileManager",
	                                           function($scope,$http,$modalInstance,pathService,selection,translate,$window,messenger,sharingMode,$modal,fileManager){
		$scope.cnt={};
		$scope.id=selection.getFirst();
		translate("file_properties_controller.json",$scope.cnt);
		$http.get("your/files/"+$scope.id+"/properties")
			.success(function(data){
//				console.info("file properties",data);
				angular.extend($scope,data.result[0]);
				angular.forEach($scope.shared,function(v,i){
					sharingMode.bindMode(v);
				});
			})
			.error(function(data){
				messenger.pushUp($scope,data.errors);
			});
		$scope.download=function(version){
			$window.open("your/file/"+$scope.id+"/"+version);
		};
		
		$scope.promote=function(version){
			$http.put("your/file/"+$scope.id+"/"+version)
			.success(function(data){
				$scope.history.push(data.result[0]);
				messenger.pushUp($scope,data.messages);
			});
		};
		
		$scope.rinomina=function(){
			var modalInstance = $modal.open({
			      animation: true,
			      templateUrl: 'template-polibox-new-directory-modal.html',
			      controller: 'ResourceRenameController',
			      size: 'md',
			      resolve:{
			    	oldName:function(){
			    		return $scope.name;  
			    	}
			      }
			    });
			  
			    modalInstance.result.then(function (newName) {
			    	var extension="";
			    	if($scope.name.lastIndexOf(".")>0)
			    		extension=$scope.name.slice($scope.name.lastIndexOf("."));
			    	var path=$scope.name.substring(0,$scope.name.lastIndexOf("/"))+"/"+newName;
			    	var callback="";
			    	if(extension.length>0){//file
			    		path=path.replace(/[^\/A-Za-z0-9._-]/g,"_");
			    		callback="getFileById";
			    	}else{//directory
			    		path=path.replace(/[^\/A-Za-z0-9_-]/g,"_");
			    		callback="getDirectoryById";
			    	}
			    	
			    	$http({
			    		url:pathService.getMode()+"/file/"+$scope.id,
			    		method:"PUT",
			    		data: JSON.stringify({
							"name":path
						}),
			    		headers:{
			    			'Content-Type': 'application/json'
			    			}
			    		})
			    		.success(function(data){
			    			$scope.name=path;
			    			fileManager[callback]($scope.id)
				    			.then(function(data){
				    				data.path=path;
				    			});
			    			messenger.pushUp($scope,data.messages);
			    			$scope.$emit("event:refresh-item-list",{});
			    		})
			    		.error(function(data){
			    			messenger.pushUp($scope,data.errors);
			    		});
			    }, function () {
			    });
		};
		
		$scope.ok = function () {
			$modalInstance.dismiss('ok');
		  };
	}]);
	
	app.controller("ResourceRenameController",["$scope","translate","$modalInstance","oldName",
		                                         function($scope,translate,$modalInstance,oldName){
		$scope.cnt={};
		translate("resource_rename_controller.json",$scope.cnt);
		$scope.newname=oldName.slice(oldName.lastIndexOf("/")+1);
		$scope.position=oldName;
		
		$scope.ok = function () {
		    $modalInstance.close($scope.newname);
		  };

		  $scope.cancel = function () {
		    $modalInstance.dismiss('cancel');
		  };
	}]);
	
	app.controller("FileCommandListController",["$scope","translate","selection","$modal","$http","fileManager","pathService","messenger","$window","sharingUtilities","directoryService",
	                                            function($scope,translate,selection,$modal,$http,fileManager,pathService,messenger,$window,sharingUtilities,directoryService){
		$scope.cnt={};
		translate("file_command_list_controller.json",$scope.cnt);
		$scope.isSelect=selection.is;
		$scope.hasOne=selection.hasOne;
		$scope.command=pathService.getScope().command;
		
		$scope.properties=function(){
			  var modalInstance = $modal.open({
			      animation: true,
			      templateUrl: 'template-polibox-file-properties-modal.html',
			      controller: 'FilePropertiesController',
			      size: 'md'
			    });
		};
		$scope.newDirectory=function(){
			    var modalInstance = $modal.open({
			      animation: true,
			      templateUrl: 'template-polibox-new-directory-modal.html',
			      controller: 'NewDirectoryController',
			      size: 'md'
			    });
			  
			    modalInstance.result.then(function (result) {
			    	var basePath=pathService.get().slice(0);
			    	directoryService.create(basePath+"/"+result.name,pathService.getMode(),result.parent)
			    		.success(function(data){
			    			fileManager.addDirectory(data.result[0]);
			    			messenger.pushUp($scope,data.messages);
			    			$scope.$emit("event:refresh-item-list",{});
			    		})
			    		.error(function(data){
			    			messenger.pushUp($scope,data.errors);
			    		});
			    }, function () {
			    });
		};
		$scope.share=function(){
			sharingUtilities.openModalInterface(selection.getFirst(),$scope);
		};
		
		$scope.download=function(){
			$window.open(pathService.getMode()+"/file/"+selection.getFirst());
		};
		$scope.upload=function(){
			$scope.$emit('event:upload-file',{});
		};
		$scope.trash=function(){
			var req = {
			            method: 'POST',
			            url: pathService.getMode()+"/files/trash",
			            headers: {
			                'Content-Type': 'application/json'
			            },
			            data: JSON.stringify(selection.get())
			        };
    		$http(req)
	    		.success(function(data){
	    			fileManager.remove(selection.get());
	    			selection.clear();
	    			$scope.$emit("event:refresh-item-list",{});
	    			messenger.pushUp($scope,data.errors);
	    		})
	    		.error(function(data){
	    			messenger.pushUp($scope,data.errors);
	    		});
		};
		$scope.ripristina=function(){
			var req = {
		            method: 'POST',
		            url: "your/files/trash/ripristina",
		            headers: {
		                'Content-Type': 'application/json'
		            },
		            data: JSON.stringify(selection.get())
		        };
			$http(req)
	    		.success(function(data){
	    			fileManager.remove(selection.get());
	    			selection.clear();
	    			$scope.$emit("event:refresh-item-list",{});
	    			messenger.pushUp($scope,data.errors);
	    		})
	    		.error(function(data){
	    			messenger.pushUp($scope,data.errors);
	    		});
		};
		$scope.move=function(){
			var selezione=selection.getCopy();
			var path=pathService.get();
			var mode=pathService.getMode();
			var arraySeletti=[];
			angular.forEach(selezione,function(v,i){
//				console.info(v);
				fileManager.getResourceById(v)
					.then(function(obj){
						arraySeletti.push({id:obj.id,path:obj.path});
					});
			});
			
			var modalInstance = $modal.open({
			      animation: true,
			      templateUrl: 'template-polibox-resource-move-modal.html',
			      controller: 'ResourceMoveController',
			      size: 'lg',
			      resolve:{
			    	source:function(){
			    		return arraySeletti;  
			    	},
			    	mode:function(){
			    		return  mode;
			    	},
			    	sourcePath:function(){
			    		return path;
			    	}
			      }
			    });
			  
		    modalInstance.result.then(function (target) {
		    	//console.info("ritorno dal modal:"+target.id+" ras:"+target.path);
		    	$http({
		    		method:"PUT",
		    		url: mode+"/file/"+target.id+"/moveinto",
		    		data:JSON.stringify(selezione),
		    		headers:{
		    			'Content-Type': 'application/json'
		    			}
		    	}).success(function(data){
//		    		console.info(data.result);
		    		angular.forEach(data.result,function(v,i){
		    			var tar=[v.id];
		    			fileManager.remove(tar);
		    		});
		    		
		    	});
		    }, function () {
		    	//niente deve essere fatto.
		    });
		};
	}]);
	
		
	app.controller("ResourceMoveController",["$scope","translate","$modalInstance","source","mode","sourcePath","$http",
	                                         function($scope,translate,$modalInstance,source,mode,sourcePath,$http){
		$scope.cnt={};
		translate("file_resource_move_controller.json",$scope.cnt);
		$scope.directoryList=[];
		$scope.path=[];
		$scope.radio={selected:undefined};
		$scope.resourceList=source;
		$scope.path.push({id:0,path:"root:"});
		$scope.radiobuttons=[];
		
		$scope.getName=function(name){
			return name.slice(name.lastIndexOf("/")+1);
		};
		
		
		$scope.openPath=function(index,id,name){
			$scope.open(id,name,true);
			if($scope.path.length>index){
				$scope.path.splice((index+1),($scope.path.length-index-1));
			}
		};
		$scope.open=function(id,name,push){
			$scope.directoryList.length=0;
			$scope.radiobuttons.length=0;
			if(push == undefined){
				$scope.path.push({"id":id,"path":name});
			}
			var parentId=0;
			if($scope.path.length>1){
				parentId=$scope.path[1].id;
			}
			if(name=="root:"){
				id=null;
				name="";
			}
			$http.get(mode+"/files",{
									"params":{ 
										  "name":name,
										  "id":parentId 
										  }
			})
				.success(function(data){
					angular.forEach(data.result,function(v,i){
						if(v.directory==true){
							var i;
							for(i=0;i<$scope.resourceList.length;i++){
								if($scope.resourceList[i].id==v.id)
									return;
							}
							$scope.directoryList.push({"id":v.id,"path":v.name});
							$scope.radiobuttons.push(""+($scope.directoryList.length-1));
						}
					});
				});
		};
		
		$scope.ok=function(){
			if($scope.radio.selected!=undefined){
				$modalInstance.close($scope.directoryList[$scope.radio.selected]);
			}else if($scope.path.length==1){//sposta in root
				$modalInstance.close({id:0,path:"/"});
			}
		};
		$scope.cancel=function(){
			$modalInstance.dismiss("close");
		};
		
		$scope.open(0,"root:",true);
	}])

	app.controller("NewDirectoryController",["$scope","translate","$modalInstance","pathService",
	                                         function($scope,translate,$modalInstance,pathService){
		$scope.cnt={};
		translate("new_directory_controller.json",$scope.cnt);
		$scope.newname="";
		$scope.position=pathService.get()||"/";
		$scope.parent=pathService.getParent();
		
		$scope.ok = function () {
			
		    $modalInstance.close({
		    						"name":$scope.newname,
		    						"parent":$scope.parent
		    					});
		  };

		  $scope.cancel = function () {
		    $modalInstance.dismiss('cancel');
		  };
	}]);
	

	app.controller("FileMenuController",["$scope","translate","$http","messenger","fileManager","selection","pathService",
	                                     function($scope,translate,$http,messenger,fileManager,selection,pathService){
		$scope.cnt={};
		translate("file_menu_controller.json",$scope.cnt);
		$scope.sel=1;
		$scope.clearSelection=function(){
			selection.clear()
		};
		$scope.command={
				"directory":false,
				"trash":false,
				"share":false,
				"property":false,
				"upload":false,
				"download":false,
				"rename":false,
				"ripristina":false,
				"move":false,
				"your":false
		};
		$scope.myFiles=function(){
			$scope.sel=1;
			$scope.command.directory=true;
			$scope.command.trash=true;
			$scope.command.share=true;
			$scope.command.property=true;
			$scope.command.upload=true;
			$scope.command.download=true;
			$scope.command.rename=true;
			$scope.command.ripristina=false;
			$scope.command.move=true;
			$scope.command.your=true;
			pathService.init("your/files",$scope,"");
		};
		
		$scope.sharedWithMe=function(){
			$scope.sel=2;
			$scope.command.directory=true;
			$scope.command.trash=true;
			$scope.command.share=false;
			$scope.command.property=true;
			$scope.command.upload=true;
			$scope.command.download=true;
			$scope.command.rename=true;
			$scope.command.ripristina=false;
			$scope.command.move=true;
			$scope.command.your=false;
			pathService.init("sharedwithyou/files",$scope,"");
		};
		
		$scope.sharedByMe=function(){
			$scope.sel=3;
			$scope.command.directory=true;
			$scope.command.trash=true;
			$scope.command.share=true;
			$scope.command.property=true;
			$scope.command.upload=true;
			$scope.command.download=true;
			$scope.command.rename=true;
			$scope.command.ripristina=false;
			$scope.command.move=true;
			$scope.command.your=true;
			pathService.init("your/files/shared",$scope,"");
		};
		$scope.trash=function(){
			$scope.sel=4;
			$scope.command.directory=false;
			$scope.command.trash=true;
			$scope.command.share=false;
			$scope.command.property=true;
			$scope.command.upload=false;
			$scope.command.download=true;
			$scope.command.rename=true;
			$scope.command.ripristina=true;
			$scope.command.move=false;
			$scope.command.your=true;
			pathService.init("your/files/trash",$scope,"");
		};
		
		$scope.myFiles();
	}]);

})(window.angular);