/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.sharing",["polibox.authentication",
	                                          "polibox.translation",
	                                          "polibox.template",
	                                          "polibox.helpers",
	                                          "ui.bootstrap",
	                                          "polibox.tools"]);
	
	app.run(["template",
	         function(template){
		template("rs/view/sharing/template-polibox-new-share-modal.html")
	}]);
	
	app.factory("sharingService",["$http",function($http){
		
		return {
			"getAllPending":function(){
				var promise=$http.get("sharing/file/pending");
				promise.success(function(data){
					return data.result;
				});
				
               	return promise;
			},
			"accept":function(resource,from){
				return $http.put("sharing/file/"+resource+"/"+from+"/accept");
			},
			"remove":function(resource,to){
				return $http.delete("sharing/file/"+resource+"/"+to);
			},
			"create":function(resource,email,permission){
				return $http.post("sharing/file/"+resource,{
					"email":email,
					"mode":permission
				});
			},
			"update":function(resource,toId,permission){
				return $http({
							method:"put",
							url:"sharing/file/"+resource+"/"+toId,
				            headers: {
				                'Content-Type': 'application/json'
				            },
				            data: JSON.stringify({"mode":permission})
				        });
			}
			
		};
	}]);
	
	app.factory("sharingMode",["$http",function($http){
		var promise=$http.get("sharing/modes");
		return {
			get:function(target){
				promise.success(function(data){
					angular.extend(target,data.result);
				});
			},
			getDefault:function(target){
				promise.success(function(data){
					var i;
					angular.forEach(data.result,function(v,i){
						if(v.defaultMode==true){
							target(v.id);
						}
					});
				});
			},
			bindMode:function(sharing){
				promise.success(function(data){
					var i;
					angular.forEach(data.result,function(v,i){
						if(v.id==sharing.permission){
							sharing.description=v.description;
						}
					});
				});
			}
		};
	}]);
	
	app.factory("sharingUtilities",["$http","$modal","messenger","sharingService",
	                                function($http,$modal,messenger,sharingService){
		
		return {
			"openModalInterface":function(fileId,$targetScope){
				var promise=$http.get("sharing/file/"+fileId);
				promise.success(function(data){
					console.info("openmodalinterface",data);
					var modalInstance = $modal.open({
					      animation: true,
					      templateUrl: 'rs/view/sharing/template-polibox-new-share-modal.html',
					      controller: 'SharingController',
					      size: 'lg',
					      resolve: {
					          fileResolved: function () {
					            return data.result[0];
					          }
					        }
					    });
					  
					    modalInstance.result.then(function (result) {
					    	angular.forEach(result.toSave,function(v,i){
					    		var promise=sharingService.create(fileId,v.email,v.mode);
					    		promise.success(function(data){
					    			messenger.pushUp($targetScope,data.errors);
					    			messenger.pushUp($targetScope,data.messages);
					    		});
					    		promise.error(function(data,status){
					    			if(status==400){
					    				messenger.pushUp($targetScope,"mancano parametri");
					    			}
					    		})
					    	});
					    	angular.forEach(result.toUpdate,function(v,i){
					    		console.info(v);
					    		var promise=sharingService.update(fileId,v.target,v.permission);
					    		promise.success(function(data){
					    			messenger.pushUp($targetScope,data.errors);
					    			messenger.pushUp($targetScope,data.messages);
					    		});
					    		promise.error(function(data,status){
					    			if(status==400){
					    				messenger.pushUp($targetScope,"mancano parametri");
					    			}
					    		})
					    	});
					    	angular.forEach(result.toDelete,function(v,i){
					    		console.info(v);
					    		var promise=sharingService.remove(fileId,v.target);
					    		promise.success(function(data){
					    			messenger.pushUp($targetScope,data.errors);
					    			messenger.pushUp($targetScope,data.messages);
					    		});

					    		promise.error(function(data,status){
					    			if(status==400){
					    				messenger.pushUp($targetScope,"mancano parametri");
					    			}
					    		})
					    	});
					    }, function () {});
				});
				promise.error(function(data){
					messenger.pushUp($targetScope,data.errors);
				});
			}
		};
	}]);
	
	app.controller("SharingController",["$scope","translate","fileResolved","sharingMode","$modalInstance",
	                                    function($scope,translate,fileResolved,sharingMode,$modalInstance){
		$scope.cnt={};
		translate("sharing_controller_modal.json",$scope.cnt);
		$scope.modes=[];
		$scope.defaultMode=0;
		sharingMode.get($scope.modes);
		sharingMode.getDefault(function(id){
			$scope.defaultMode=id;
		});
		$scope.file=fileResolved.file;
		$scope.sharing=fileResolved.sharing;
		angular.forEach($scope.sharing,function(v,i){
			sharingMode.bindMode(v)
			v.dirty=false;
		});
		$scope.unsaved=[];
		$scope.toDelete=[];
		
		$scope.nuova=function(){
			$scope.unsaved.push({
				email:"",
				id:$scope.file.id,
				mode:$scope.defaultMode
			});
		};
		$scope.indietro=function(){
			$modalInstance.dismiss("indietro");
		};
		$scope.remove=function(index){
			$scope.toDelete.push($scope.sharing.splice(index,1)[0])
		};
		$scope.removeUnsaved=function(index){
			$scope.unsaved.splice(index,1);
		};
		$scope.salva=function(){
			var update=[];
			angular.forEach($scope.sharing,function(v,i){
				if(v.dirty==true)
					update.push(v);
			});
			$modalInstance.close({
				"toSave":	$scope.unsaved,
				"toUpdate": update,
				"toDelete": $scope.toDelete
			});
		};
	}]);
	
})(window.angular);