/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.device",["polibox.authentication",
	                                         "polibox.helpers",
	                                         "ui.bootstrap",
	                                         "polibox.notification",
	                                         "polibox.tools"]);

	app.run(["deviceService",
	         function(deviceService){
		deviceService.init();
	}]);

	app.controller("HomeDeviceController",["$scope","translate","messenger","$http","$modal","deviceService",
	                                       function($scope,translate,messenger,$http,$modal,deviceService){
		$scope.deviceList=deviceService.getAll;
		$scope.cnt={};
		translate("device_home.json",$scope.cnt);
		$scope.add=function(){
			var modalInstance = $modal.open({
			      animation: true,
			      templateUrl: 'template-polibox-add-device.html',
			      controller: 'AddDeviceController',
			      size: 'lg'
			    });
			modalInstance.result.then(function(result){
				deviceService.add(result);
			},function(){
				
			});
		};
		
		$scope.oneAtTime=true;
	}]);
	
	app.controller("AddDeviceController",["$scope","$modalInstance","translate",
	                                      function($scope,$modalInstance,translate){
		$scope.cnt={};
		translate("device_controller_modal_add.json",$scope.cnt);
		$scope.name="";
		$scope.ok=function(){
			var result=$scope.name+"";
			$modalInstance.close(result);
		};
		$scope.cancel=function(){
			$modalInstance.dismiss("cancel");
		};
	}]);
	
	app.directive("poliboxDevice",[function(){
		
		return {
			restrict:"E",
			transclude:true,
			templateUrl:"template-polibox-device.html",
			controller:"DeviceController",
			scope:{
				index:"=index",
				value:"=value"
			}
		};
	}]);
	
	app.controller("DeviceController",["$scope","$window","$http","deviceService","translate","notificationCodeService","messenger",
	                                   function($scope,$window,$http,deviceService,translate,notificationCodeService,messenger){
		$scope.cnt={};
		translate("device_controller.json",$scope.cnt);
		$scope.isRenaming=false;
		$scope.toggleRenaming=function(){
			$scope.isRenaming=!$scope.isRenaming;
			if($scope.isRenaming==false){
				deviceService.rename($scope.index,$scope.value.name);
			}
		};
		$scope.remove=function(){
			deviceService.remove($scope.index);
		};
		
		$scope.download=function(){
			deviceService.download($scope.index);
		};
		
		$scope.content=$scope.value.notification.list;
		notificationCodeService.bindAll(function(codes){
			angular.forEach($scope.content,function(value,index){
				angular.forEach(codes,function(v,i){
					if(v.code==value.code){
						value.description=v.description;
					}
				});
			});
		});
		
		$scope.save=function(index){
			 var req = {
			            method: 'PUT',
			            url: "device/"+$scope.value.id+"/notifications",
			            headers: {
			                'Content-Type': 'application/json'
			            },
			            data: JSON.stringify($scope.content[index])
			        };
				$http(req)
					.success(function(data,status,headers,config){
						messenger.pushUp($scope,data.messages);
						messenger.pushUp($scope,data.errors);
					})
					.error(function(data,status,headers,config){
						messenger.pushUp($scope,data.errors);
					});
		};
	}]);
	
	app.factory("deviceService",["$http","$window","$rootScope","messenger", 
	                             function($http,$window,$rootScope,messenger){
		var deviceList=[];
		
		function init(){
			$http.get("device")
			.success(function(data){
				deviceList.length=0;
				angular.forEach(data.result,function(value,key){
					deviceList.push(value);
				});
			})
			.error(function(data){
				messenger.pushDown($rootScope,data.errors);
			});
		};
		
		
		function add(newName){
			$http.post("device",{name:newName})
			.success(function(data){
				deviceList.push(data.result[0]);
			})
			.error(function(data){
				messenger.pushDown($rootScope,data.errors);
			});
			
		};
		
		function remove(index){
			$http.delete("device/"+deviceList[index].id)
			.success(function(data){
				deviceList.splice(index,1);
			})
			.error(function(data){
				messenger.pushDown($rootScope,data.errors);
			});
		};
		
		function rename(index,newName){
			$http.put("device/"+deviceList[index].id,{name:newName})
			.success(function(data){
				deviceList[index].name=newName;
			})
			.error(function(data){
				messenger.pushDown($rootScope,data.errors);
			});
		};
		
		function download(index){
			$window.open("device/"+deviceList[index].id);
		};
		
		return {
			"init": init,
			"add": add,
			"remove":remove,
			"rename":rename,
			"download":download,
			"getAll":function(){
				return deviceList;
			},
			"get":function(index){
				return deviceList[index];
			}
		};
	}]);
})(window.angular);