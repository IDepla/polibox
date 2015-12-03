/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
/**
 * dependency on jQuery , cache
 */
(function(angular){
	var app=angular.module("polibox.translation",['polibox.cache']);
	var translator;
	app.constant("translationsDirectory","rs/translate");
	
	app.run(["cacheService","$http","translationsDirectory",
	         function(cacheService,$http,translationsDirectory){
		translator=new Translation(cacheService,$http);
		translator.setBaseDir(translationsDirectory);
	}]);
	
	app.provider("translation",[function TranslationProvider(){
		this.get=function(url,scope){
			translator.now(url,scope);
			translator.controller.put(scope,url);
		}
		this.$get=[function(){
			return translator;
		}];
	}]);

	app.factory('translate',[function(){
		return translator.now;
	}]);
	
	app.factory('translator',[function(){
		return translator;
	}]);
	
	
	app.controller("LanguageController",["$scope","translator",function($scope,translator){
		$scope.list=translator.language.getAll();
		$scope.isSelected=function(index){
			return translator.language.isSelected(index);
		}
		$scope.setLanguage=function(index){
			translator.language.select=index;
		};
	}]);
	
	function Translation(cache,$http){
		if(!cache){
			throw Error("cache not found"); 
		}
		var list=["it"];
		var selected=0;
		var directory;
		var self=this;
		var controllerList=[];
		
		self.language={};
		self.controller={};
		
		self.language.setList=function(newList){
			list=newList;
		};
		
		self.language.select=function(number){
			selected=number;
			self.controller.refresh();
		};
				
		self.language.isSelected=function(index){
			if(index==selected)
				return true;
			return false;
		};
		
		self.language.getAll=function(){
			return list;
		};
		self.language.my=function(){
			return list[selected];
		};
			
		self.setBaseDir=function(base){
			directory=base;
		};
		
		self.controller.put=function(scope,url){
			controllerList[controllerList.length]={"scope":scope,"url":url};
		};
		
		
		self.controller.refresh=function(){
			for(a in controllerList){
				self.now(a.url,a.scope);
			}
		};
		
		self.now=function(page,scope){
			var url=directory+"/"+self.language.my()+"/"+page;
			if(cache.lsIs(url)){
				jQuery.extend(true,scope,angular.fromJson(cache.lsGet(url)));
			}else{
				$http.get(url).
					success(function(data){
						cache.lsPut(url,angular.toJson(data));
						jQuery.extend(true,scope,data);
					});
			}
		};
			
		return self;
	};



})(window.angular);
