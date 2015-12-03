/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.notification",["polibox.authentication",
	                                               "polibox.tools",
	                                               "polibox.sharing",
	                                               "polibox.translation",
	 	                                           "polibox.template",
	                                               "ui.bootstrap"]);
	app.run(["template",
	         function(template){
		template("rs/view/notification/notification_realtime_container.html");
	}]);
	
	
	app.controller("HomeNotificationController",["$scope",function($scope){
		
	}]);
	
	app.controller("LastNotificationController",["$scope","notificationService","translate",
	                                             function($scope,notificationService,translate){
		$scope.cnt={};
		translate("last_notification_controller.json",$scope.cnt);
		$scope.today=new Date();
		$scope.format='dd.MM.yyyy';
		$scope.maxDate=$scope.today;
		
		$scope.from={};
		$scope.from.value=new Date();
		$scope.from.opened=false;
		
		$scope.notificationList=notificationService.getDay($scope.today);
		
		$scope.open=function(event,target){
			event.preventDefault();
			event.stopPropagation();
			
			$scope.from.opened=false;
			target.opened=true;
		};
		
		$scope.change=function(){
			$scope.notificationList=notificationService.getDay($scope.from.value);
		}
	}]);
	
	app.controller("SharingNotificationController",["$scope","translate","sharingService","sharingMode",
	                                                function($scope,translate,sharingService,sharingMode){
			$scope.cnt={};
			translate("sharing_notification_controller.json",$scope.cnt);
			$scope.shareList=[];
			sharingService.getAllPending().then(function(data){
//				console.info("notification.SharingNotificationController",data.data.result);
					$scope.shareList=data.data.result;
					sharingMode.bindMode($scope.shareList);
			});
			
			$scope.accept=function(index){
				var accepted=$scope.shareList.splice(index,1)[0];
				sharingService.accept(accepted.resource,accepted.owner);
			};
	}]);
	
	
	app.factory("notificationService",["$http",function($http){
		var result=[];
		
		
		return {
			"getDay":function(day){
				result.length=0;
				$http.get("notifications/"+day.getFullYear()+"/"+day.getMonth()+"/"+day.getDate())
				.success(function(data){
					console.info(data);
					angular.forEach(data.result,function(value,key){
						result.push(value);
					});
				}).error(function(data){
					
				});
				return result;
			}
		};
	}]);
	
	app.directive("notificationRealtimeContainer",["notificationRealtime","translate","$rootScope",
	                                                        function(notificationRealtime,translate,$rootScope){
		var notificationList=[];
		notificationRealtime.register("message",function(e){
			$rootScope.$apply(function($rootScope){
				notificationList.push(JSON.parse(e.data));
			});
			if(notificationList.length>20){
				notificationList.splice(0,1);
			}
		});
		return {
			restrict:"E",
			link: function(scope, element, attr) {
				scope.cnt={};
				translate("notification_realtime_container.json",scope.cnt);
				scope.status={
						isOpen:false
				}
				scope.list=notificationList;
				scope.isRTActive=notificationRealtime.isRunning;
				scope.isRTConnecting=notificationRealtime.isConnecting;
				scope.isRTClosed=notificationRealtime.isClosed;
				scope.length=function(){
					return 0+scope.list.length;
				};
			},
			templateUrl:"rs/view/notification/notification_realtime_container.html"
		};
	}]);
	
	app.factory("notificationRealtime",["$window",function($window){
		var source;
		var handlers=[];
		var pastMessages=[];
		
		function register(eventName,callback){
			handlers.push({
				"name":eventName,
				"callback":callback
			});
			if(!!source){
				source.addEventListener(eventName,callback,false);
			}
		};
		
		register("message", function(e) {
			pastMessages.push(JSON.parse(e.data));
			if(pastMessages.length>10){
				pastMessages.splice(0,1);
			}
		});
		register("open", function(e) {
		  console.log("open ",e);
		});
		register("error", function(e) {
			if (e.readyState == EventSource.CLOSED) {
			  console.log("error ",e);
		  }
		});
		
		return {
			"init":function(){
				if(!!$window.EventSource){
					source = new EventSource('notifications/realtime');
					
					angular.forEach(handlers,function(value,key){
						source.addEventListener(value.name,value.callback,false);
					});
				}
			},
			"register":register,
			"isRunning":function(){
				if(!!source && source.readyState==EventSource.OPEN)
					return true;
				return false;
			},
			"isConnecting":function(){
				if(!!source && source.readyState==EventSource.CONNECTING)
					return true;
				return false;
			},
			"isClosed":function(){
				if(!!source && source.readyState==EventSource.CLOSED)
					return true;
				return false;
			},
			"stop":function(){
				if(!!source){
					angular.forEach(handlers,function(value,key){
						source.removeEventListener(value.name,value.callback,false);
					});
				}
				return !!source && source.close();
			},
			"getAll":function(){
				return pastMessages;
			}
		};
	}]);
	
	app.factory("notificationCodeService",["$http","$rootScope",
	                                       function($http,$rootScope){
		var notificationCodes=[];
		var promise=$http.get("notification/codes");
		promise.success(function(data,status,headers,config){
			angular.extend(notificationCodes,data.result);
		})
		promise.error(function(data,status,headers,config){
			messenger.pushUp($rootScope,data.errors);
		});
		
		return {
			"getAll":function(){
				return notificationCodes.slice(0);
			},
			"bindAll":function(callback){
				promise.success(function(data){
					callback(notificationCodes);
				});
			}
		};
	}]);
	
	
	
})(window.angular);