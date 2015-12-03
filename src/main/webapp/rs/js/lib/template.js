/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(window){ 
	var app=window.angular.module("polibox.template",["polibox.tools",
	                                                  "polibox.cache"]);
	
	app.factory("template",["$http","cacheService",
	                        function($http,cache){
		return function(url){
		   var node=document.createElement("script");
		   node.setAttribute("type","text/ng-template");
		   node.setAttribute("id",url);
		   if(cache.lsIs(url)){
			   node.innerHTML=cache.lsGet(url);
			   window.document.body.appendChild(node);
		   }else{
		    	$http.get(url).
		    		success(function(data){
						cache.lsPut(url,data);
						node.innerHTML=data;
						window.document.body.appendChild(node);
					});
		   }
		   return window.angular.element(node);
		};
	}]);
	
	
	
})(window);