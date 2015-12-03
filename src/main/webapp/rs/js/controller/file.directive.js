/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.file.directive",["polibox.authentication",
	                                                 "polibox.file.controller",
	      	                                       "polibox.file.service",
	                                       "polibox.helpers",
	                                       "polibox.sharing",
	                                       "ui.bootstrap",
	                                       "polibox.tools"]);
	
	
	app.directive("poliboxFileInput", ["uploadService","pathService",
	                                   function (uploadService,pathService) {
	    return {
	    	restrict:"A",
	        link: function (scope, element, attributes) {
	            element.bind("change", function (changeEvent) {
	            	var parent=pathService.getParent();
	            	scope.$apply(function(){
	            		uploadService.addFileList(changeEvent.target.files,parent);
	            	});
	            	element.val(null);
	            });
	        }
	    }
	}]);

	app.directive("poliboxDropZone",["uploadService","pathService",
	                                 function(uploadService,pathService){
		  return {
		        restrict : "EA",
		        template:'{{cnt.content}}',
		        link: function (scope, elem, attrs) {
		        	elem.attr("dropzone",true);
		        	elem.attr("webkitdropzone","copy file:*/*");
		        	
		        	elem.bind("dragover",function(e){
		        		 e.stopPropagation();
		        		 e.preventDefault();
		        		 elem.addClass("dropzone-hover");
		        	});
		        	elem.bind("dragleave",function(e){
		        		elem.removeClass("dropzone-hover");
		        	});
		            elem.bind('drop', function(evt) {
		            	evt.preventDefault();
		            	elem.removeClass("dropzone-hover");
		            	var parent=pathService.getParent();
		                if(evt.originalEvent.dataTransfer.items.length>0){
		                	try{
			                	scope.$apply(function(){
			                		uploadService.addItemList(evt.originalEvent.dataTransfer.items,parent);
			                	});
		                	}catch(e){
		                		messenger.pushUp(scope,"error while reading "+e);
		                	}
		                }
		                evt.stopPropagation();
		            });
		        }
		    };
	}]);
	
	app.directive("poliboxUploaderList",["uploadService",
	                                 function(uploadService){
		  return {
		        restrict : "E",
		        template:'<div class="list-group">'+
					'<div ng-repeat="(key,obj) in uploadList" class="list-group-item">'+
						'<span class="glyphicon glyphicon-remove-sign" ng-click="remove(key);" ng-if="obj.deletable"></span>'+
						'<span>{{obj.resourceName}}</span> <span class="col-xs-4"><progressbar animate="true" value="obj.progress" max="obj.max" type="info"><b>{{obj.progress}}%</b></progressbar></span>'+
						'<span class="badge" ng-if="obj.isDirectory==false">{{obj.size}}bytes</span>'+
					'</div>'+
				"</div>",
		        link:function($scope,elem,attr){
		        	$scope.getUploadList=uploadService.get;
		    		$scope.uploadList=uploadService.get();
		    		$scope.remove=uploadService.remove;
		        }
		    };
	}]);
	

	app.directive("poliboxFile",["selection","pathService","$modal",
	                             function(selection,pathService,$modal){
		
		return {
			restrict:"E",
			transclude:true,
			scope:{
				path:"@path",
				id:"@id"
				
			},
//			controller:"FileController",
			template:'<div ng-click="select()" ng-dblclick="property()"><img src="rs/img/text.png" /></div><div>{{getName()}}</div>',
			link:function($scope,element,attr){
				$scope.select=function(){
					selection.set($scope.id);
				};
				$scope.getName=function(){
					return pathService.purgeBase($scope.path);
				};
				$scope.property=function(){
				  if(selection.getFirst()!=$scope.id){
					  selection.clear();
					  selection.set($scope.id);
				  }
				  var modalInstance = $modal.open({
				      animation: true,
				      templateUrl: 'template-polibox-file-properties-modal.html',
				      controller: 'FilePropertiesController',
				      size: 'md'
				    });
				  modalInstance.result.then(function (res) {
					  selection.clear();
				  }, function () {
				      selection.clear();
				  });
				};
			}
		};
	}]);

	app.directive("poliboxDirectory",["selection","pathService",
	                                  function(selection,pathService){
		
		return {
			restrict:"E",
			transclude:true,
			scope:{
				path:"@path",
				id:"@id"	
			},
//			controller:"DirectoryController",
			template:'<div ng-click="select()" ng-dblclick="open()"><img src="rs/img/folder.png" /></div><div>{{getName()}}</div>',
			link:function($scope,element,attr){
				$scope.select=function(){
					selection.set($scope.id);
				};
				$scope.getName=function(){
					return pathService.purgeBase($scope.path);
				};
				$scope.open=function(){
					pathService.set($scope.path,$scope.id);
				};
			}
		}
	}]);
	

	app.directive("poliboxPath",["pathService","selection",
	                             function(pathService,selection){
		return {
			restrict:"E",
			transclude:true,
//			controller:"FilePathController",
			template:'/<span ng-repeat="obj in pathScomposto()"><a ng-click="open(obj.path)">{{obj.name}}</a>/</span>',
			link:function($scope,element,attr){
				$scope.path=pathService.get;
				var oldPath="";
				var result=[];
				
				$scope.pathScomposto=function(){
					var t=$scope.path();
					if(t==oldPath) return result;
					oldPath=t;
					result.length=0;
					var scomposto=t.split("/");
					angular.forEach(scomposto,function(v,i){
						if(i==0) return;
						if(i==1){
							result[i]={"name":v,"path":("/"+v)};
						}else{
							result[i]={"name":v,"path":(result[i-1].path+"/"+v)};
						}
					});
					return result;
				};
				$scope.open=function(path){
					pathService.set(path);
				};
			}
		}
	}]);
	
})(window.angular);