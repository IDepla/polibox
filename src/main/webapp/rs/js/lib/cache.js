/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(window){ 
	var app=window.angular.module("polibox.cache",[]);
	var cache=new Cache(window);
	
	app.provider("cache",[function CacheProvider(){
		this.instance=function(){
			return cache;
		}
		this.$get=[function(){
			return cache;
		}];
	}]);
	
	app.factory('cacheService',[function(){
		return cache;	
	}]);
	
	function Cache(w){
	
		var self=this;
		self.ls=0||w.localStorage;
		self.ss=0||w.sessionStorage;
		
		self.lsPut=function(key,data,force){
			force=0||force;
			if(self.ls){
				if(force && localStorage.getItem(key)) throw 452;
				localStorage.setItem(key,data);
				return true;
			}
			return false;
		};
		
		self.lsGet=function(key){
			if(self.ls)
			  return localStorage.getItem(key);
			return {};
		};
		
		self.lsIs=function(key){
		   if(self.ls && localStorage.getItem(key))
			return true;
		   return false;
		};
		
		self.lsClear=function(){
			if(self.ls) localStorage.clear();
		};
		
		self.ssPut=function(key,data,force){
			force=0||force;
			if(self.ss){
				if(force && sessionStorage.getItem(key)) throw 452;
				sessionStorage.setItem(key,data);
				return true;
			}
			return false;
		};
		
		self.ssGet=function(key){
			if(self.ss)
				return sessionStorage.getItem(key);
			return {};
		};
		
		self.ssIs=function(key){
			 if(self.ss && sessionStorage.getItem(key))
				return true;
			return false;
		};
		
		self.ssClear=function(){
			if(self.ss) sessionStorage.clear();
		};
	
	 };

	

})(window);