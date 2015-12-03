/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	var app=angular.module("polibox.home",['ui.bootstrap',
	                                       "polibox.translation",
	                                       "polibox.authentication",
	                                       "polibox.tools"]);
	

	
	app.controller("HomeController",["translate",function(translate){
		//translate("home.json");
		
	}]);
	
	app.controller("TermsController",["$scope","translate","$window",function($scope,translate,$window){
		$scope.cnt={};
		translate("terms.json",$scope.cnt);
		
		
		$scope.back=function(){
			if($window.history.length>0)
				$window.history.back();
				
		};
	}]);
})(window.angular);