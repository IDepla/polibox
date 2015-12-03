/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.account",["ui.bootstrap",
	                                          "polibox.authentication",
	                                          "polibox.translation",
	                                          "polibox.notification",
	                                          "polibox.tools",
	                                          "polibox.helpers",
	                                          "polibox.template"]);
	
	app.run(["template",function(template){
		template("rs/view/account/tab/account.html");
		template("rs/view/account/tab/userdetails.html");
		template("rs/view/account/tab/notificationoption.html");
	}]);
	
	app.controller("RegistrationController",["$scope","translate","$http","$location","messenger",
	                                         function($scope,translate,$http,$location,messenger){
		$scope.cnt={};
		$scope.form={};
		$scope.form.email="";
		$scope.form.password="";
		$scope.form.name="";
		$scope.form.surname="";
		$scope.form.repassword="";
		$scope.form.term="";
		translate("registration.json",$scope.cnt);
		
		$scope.ok=function(){
			$http.post("account/registrazione",$scope.form)
			.success(function(data, status, headers, config) {
				switch(data.status){
					case "200":
						messenger.pushUp($scope,data.messages);
						$location.path("#/attivazione");
						break;
					default:
						messenger.pushDown($scope,{
								"code":data.status,
								"message":"Some errors occurred"
								})
						messenger.pushDown($scope,data.errors);
						$scope.form.password="";
						$scope.form.repassword="";
						break;
				}
				
			})
			.error(function(data, status, headers, config) {
				messenger.pushDown($scope,data.errors);
			});	
		};
	}]);
	
	app.controller("AccountActivatorController",["$scope","translate","$http","messenger","$location",
	                                              function($scope,translate,$http,messenger,$location){
		$scope.cnt={};
		$scope.form={};
		$scope.form.email="";
		$scope.form.code="";
		translate("activator.json",$scope.cnt);

		$scope.ok=function(){
			$http.post("account/attivazione",$scope.form)
			.success(function(data, status, headers, config) {
				messenger.pushUp($scope,data.messages);
			   $location.path("#/home");
			})
			.error(function(data, status, headers, config) {
				messenger.pushDown($scope,data.errors);
			});	
		};
	}]);
	
	app.controller("HomeAccountController",["$scope","translate","$http","messenger","template","$controller","$rootScope",
	                                        function($scope,translate,$http,messenger,template,$controller,$rootScope){
		$scope.cnt={};
		$scope.cnt.tabs=[{
							"template":"rs/view/account/tab/account.html",
							"controller":"AccountTabController",
							"active":true
						},{
							"template":"rs/view/account/tab/userdetails.html",
							"controller":"PersonalDetailsTabController",
							"active":false
						},{
							"template":"rs/view/account/tab/notificationoption.html",
							"controller":"EmailNotificationOptionsTabController",
							"active":false
						}];
		translate("account.json",$scope.cnt);
		angular.forEach($scope.cnt.tabs,function(value,index){
			value.controller=$controller(value.controller,{
															"$scope":$scope.$new(true,$scope),
															"$http":$http,
															"messenger":messenger,
															"translate":translate
															}).constructor;
		});
		$scope.save=function(){
			$scope.$broadcast("event:account-must-be-saved",{});
		};
	}]);
	
	app.controller("AccountTabController",["$scope","$http","messenger","translate","CryptoJS",
                                 function($scope,$http,messenger,translate,CryptoJS){
		$scope.content={};
		$scope.form={};
		translate("account_tab_account.json",$scope);
		$scope.init=function(){
			$http.get("account")
				.success(function(data,status,headers,config){
					angular.extend($scope.form,data.result[0]);
					$scope.signature=CryptoJS.SHA3($scope.form);
					messenger.pushUp($scope,data.errors);
				})
				.error(function(data,status,headers,config){
					messenger.pushUp($scope,data.errors);
				});
			var killSaveListener=$scope.$on("event:account-must-be-saved",function(event,args){
				var tmpSign=CryptoJS.SHA3($scope.form);
				if(!CryptoJS.isEqual($scope.signature,tmpSign)){
					var tmp={};
					angular.extend(tmp,$scope.form);
					delete tmp.lastLogin;
					delete tmp.lastActionTime;
					delete tmp.id;
					$http.post("account",tmp)
						.success(function(data,status,headers,config){
							messenger.pushUp($scope,data.messages);
							messenger.pushUp($scope,data.errors);
							$scope.form.password="";
							$scope.form.newPassword="";
							$scope.form.newRepassword="";
						})
						.error(function(data,status,headers,config){
							messenger.pushUp($scope,data.errors);
						});
				}
			});
			$scope.$on("$destroy",function(event,args){
				killSaveListener();
			});
		};
	}]);
	
	app.controller("PersonalDetailsTabController",["$scope","$http","messenger","translate","CryptoJS",
	                                     function($scope,$http,messenger,translate,CryptoJS){
		$scope.content={};
		$scope.form={};
		translate("account_tab_personal.json",$scope);
		$scope.init=function(){
			$http.get("account/details")
				.success(function(data,status,headers,config){
					angular.extend($scope.form,data.result[0]);
					$scope.signature=CryptoJS.SHA3($scope.form);
					messenger.pushUp($scope,data.errors);
				})
				.error(function(data,status,headers,config){
					messenger.pushUp($scope,data.errors);
				});
			var killSaveListener=$scope.$on("event:account-must-be-saved",function(event,args){
				var tmpSign=CryptoJS.SHA3($scope.form);
				if(!CryptoJS.isEqual($scope.signature,tmpSign)){
					$http.post("account/details",$scope.form)
						.success(function(data,status,headers,config){
							messenger.pushUp($scope,data.messages);
							messenger.pushUp($scope,data.errors);
						})
						.error(function(data,status,headers,config){
							messenger.pushUp($scope,data.errors);
						});
				}
			});
			$scope.$on("$destroy",function(event,args){
				killSaveListener();
			});
		};
		
	}]);
	
	app.controller("EmailNotificationOptionsTabController",["$scope","$http","messenger","translate","CryptoJS","notificationCodeService",
	                                                        function($scope,$http,messenger,translate,CryptoJS,notificationCodeService){
		$scope.init=function(){
			notificationOptionsTabController($scope,$http,messenger,translate,"email",CryptoJS,notificationCodeService);
		};
	}]);
	
	function notificationOptionsTabController($scope,$http,messenger,translate,type,CryptoJS,notificationCodeService){
		$scope.content=[];
		$http.get("account/notification/"+type)
			.success(function(data,status,headers,config){
				var form;
				var result;
				messenger.pushUp($scope,data.errors);
				form=data.result[0];
				result=notificationCodeService.getAll();
				angular.extend($scope.content,result);
				angular.forEach($scope.content,function(value,index){
					angular.forEach(form.list,function(v,i){
						if(v.code==value.code){
							value.option=v.option;
						}
					});
				});
				$scope.signature=CryptoJS.SHA3(form.list);
			})
			.error(function(data,status,headers,config){
				messenger.pushUp($scope,data.errors);
			});
		
		var killSaveListener=$scope.$on("event:account-must-be-saved",function(event,args){
			var tmpArray={'list':jQuery.extend(true,[],$scope.content)};
			angular.forEach(tmpArray.list,function(value,index){
				delete value.description;
				delete value.$$hashKey;
			});
			var tmpSign=CryptoJS.SHA3(tmpArray.list);
			if(!CryptoJS.isEqual($scope.signature,tmpSign)){
			    var req = {
			            method: 'POST',
			            url: "account/notification/"+type,
			            headers: {
			                'Content-Type': 'application/json'
			            },
			            data: JSON.stringify(tmpArray)
			        };
				$http(req)
					.success(function(data,status,headers,config){
						messenger.pushUp($scope,data.messages);
						messenger.pushUp($scope,data.errors);
					})
					.error(function(data,status,headers,config){
						messenger.pushUp($scope,data.errors);
					});
			}
		});
		$scope.$on("$destroy",function(event,args){
			killSaveListener();
		});
	};
})(window.angular);