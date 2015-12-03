/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.authentication",['ui.bootstrap',
	                                                 "polibox.helpers",
	                                                 "polibox.translation",
	                                                 "polibox.tools",
	                                                 'http-auth-interceptor',
	                                                 'polibox.notification',
	                                                 "polibox.cache"]);
	
	app.config(['$httpProvider', function($httpProvider) {
		var header_content=$("meta[name=_csrf]").attr("content");
		var header_name=$("meta[name=_csrf_header]").attr("content");
		$httpProvider.defaults.headers.common[header_name]=header_content;
	 }]);
	
	app.config(['$httpProvider', function($httpProvider) {//TODO potrei fare il cambio di CSRF
		$httpProvider.interceptors.push(["$q","$window",function($q,$window){
			return {
			 'responseError': function(rejection) {
			     if(rejection.status=="405"){
			    	 $window.location.reload();
			     }
			     return $q.reject(rejection);
			  }
			};
		}]);
	 }]);
	
	app.factory("ping",["$http",function($http){
		return function(url){
			$http.get(url);
		};
	}]);
	
	app.factory("authentication",["cacheService","$interval","ping","notificationRealtime",
	                              function(cacheService,$interval,ping,notificationRealtime){
		var authenticated=false;
		var data=null;
		var testUrl;
		var promisePing;
		var timing;
		return {
			init:function(url,time){
				authenticated=false;
				var data=null;
				timing=time||60000;
				testUrl=url;
				if(cacheService.ssIs("logged")){
					authenticated=true;
					data=cacheService.ssGet("logged");
				}
				promisePing=$interval(function(){ping(testUrl);},timing);
			    notificationRealtime.init();
			},
			set:function(d){
				authenticated=true;
				data=d;
			    cacheService.ssPut("logged",d,true);
			    if(promisePing == null){
			    	promisePing=$interval(function(){ping(testUrl);},timing);
			    }
			    notificationRealtime.init();
			},
			is:function(){
				return authenticated;
			},
			get:function(){
				return data;
			},
			clear:function(){
				authenticated=false;
				data=null;
				$interval.cancel(promisePing);
				promisePing=null;
				if(cacheService.ssIs("logged")){
					cacheService.ssClear();
				}
				notificationRealtime.stop();
			}
		};
	}]);
	
	app.controller("AuthenticationController",["$scope","authentication",
	                                           function($scope,authentication){
		$scope.isAuthenticated=function(){return authentication.is();}
	}]);
	
	app.controller("LoginController",["$scope","translate","$http","messenger","cacheService","authService",
	                                  function($scope,translate,$http,messenger,cacheService,authService){
		$scope.cnt={};
		translate("login.json",$scope.cnt);
		$scope.form={};
		$scope.form.email="";
		$scope.form.password="";
		$scope.form.rememberme="";

		$scope.ok=function(){
			$http.post("authenticate/process",$scope.form)
					.success(function(data, status, headers, config) {
						switch(data.status){
							case "200":
								messenger.pushUp($scope,data.messages);
								authService.loginConfirmed([1]);
								break;
							default:
								messenger.pushDown($scope,data.errors);
								$scope.form.password="";
								break;
						}
						
					})
					.error(function(data, status, headers, config) {
						messenger.pushDown($scope,data.errors);
					});
		};
	}]);
	
	app.controller("LogoutController",["$scope","$http","$location","messenger","authentication",
	                           function($scope,$http,$location,messenger,authentication){
		$scope.ok=function(){
			$http.post("authenticate/logout")
			.success(function(data, status, headers, config) {
				messenger.pushUp($scope,data.messages);
				authentication.clear();
				$location.path("/home");
				
			})
			.error(function(data, status, headers, config) {
				messenger.pushDown($scope,data.errors);
			});
		};
	}]);
	
	app.controller("ForgetController",["$scope","translate","$http","$location","messenger",
	                                   function($scope,translate,$http,$location,messenger){
		$scope.cnt={};
		translate("recuperapassword.json",$scope.cnt);
		$scope.form={};
		$scope.form.email="";
		
		$scope.ok=function(){
			$http.post("authenticate/forgotten",$scope.form)
			.success(function(data, status, headers, config) {
				messenger.pushUp($scope,data.messages);
			})
			.error(function(data, status, headers, config) {
				messenger.pushDown($scope,data.errors);
			});	
		};
	}]);
	
})(window.angular);