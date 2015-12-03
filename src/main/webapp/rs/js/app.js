/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var polibox=angular.module("polibox",["ngRoute",
	                                      "polibox.authentication",
	                                      "polibox.home",
	                                      "polibox.file",
	                                      "polibox.notification",
	                                      "polibox.device",
	                                      "polibox.account",
	                                      "polibox.cache",
	                                      "polibox.template",
	                                      "polibox.translation",
	                                      'http-auth-interceptor',
	                                      "polibox.tools"]);
	
	polibox.config(['$routeProvider', '$locationProvider',"$httpProvider",
	 	    function($routeProvider,$locationProvider,$httpProvider){
				$routeProvider
	 				.when('/accedi',{
	 					controller: "LoginController",
	 					templateUrl: "rs/view/authentication/login.html"
	 				})
	 				.when('/recuperapassword',{
	 					controller: "ForgetController",
	 					templateUrl: "rs/view/authentication/recuperapassword.html"
	 				})
	 				.when('/registrati',{
	 					controller:"RegistrationController",
	 					templateUrl:"rs/view/account/registration.html"
	 				})
	 				.when('/terms',{
	 					controller:"TermsController",
	 					templateUrl:"rs/view/common/terms.html"
	 				})
	 				.when('/account',{
	 					controller:"HomeAccountController",
	 					templateUrl:"rs/view/account/home.html"
	 				})
	  				.when('/devices',{
	 					controller:"HomeDeviceController",
	 					templateUrl:"rs/view/device/home.html"
	 				})
	 				.when('/notifications',{
	 					controller:"HomeNotificationController",
	 					templateUrl:"rs/view/notification/home.html"
	 				})
	 				.when('/files',{
	 					controller:"HomeFileController",
	 					templateUrl:"rs/view/file/home.html"
	 				})
	 				.when('/attivazione',{
	 					controller:"AccountActivatorController",
	 					templateUrl:"rs/view/account/activator.html"
	 				})
	 				.otherwise({
	 					redirectTo:'/accedi'
	 				});
	 			/*
	 			 * 
	 			 * $locationProvider.html5Mode(true);
	 			 * 
	 			 */
	 		}]);
	
	polibox.run(["$rootScope", "$location","template","$http","authentication",
	             function($rootScope, $location,template,$http,authentication) {
		template("rs/view/authentication/login.html");
		template("rs/view/account/home.html");
		template("rs/view/file/home.html");
		template("rs/view/notification/home.html");
		template("rs/view/device/home.html");
		template("rs/view/authentication/recuperapassword.html");
		template("rs/view/account/registration.html");
		template("rs/view/common/terms.html");
		template("rs/view/account/activator.html");
		
		authentication.init("authenticate/ping",60000);
		
		$rootScope.$on("event:auth-loginRequired",function(rejection){
			authentication.clear();
			$location.path("/accedi");
		});
		
		$rootScope.$on('event:auth-loginConfirmed', function(event, data){
		    authentication.set(data);
		    $location.path("/files");
		});
		
		$rootScope.$on( "$routeChangeStart", function(event, next, current) {
	    	
	      if (authentication.is() == false) {
	        // no logged user, redirect to /accedi
	    	switch(next.templateUrl){
		    	case "rs/view/authentication/login.html":
		    	case "rs/view/authentication/recuperapassword.html":
		    	case "rs/view/account/registration.html":
		    	case "rs/view/common/terms.html":
		    	case "rs/view/common/home.html":
		    		break;
	    		default:
	    			$location.path("/accedi");
	    	}
	      }
	    });
	  }]);	
})(window.angular);