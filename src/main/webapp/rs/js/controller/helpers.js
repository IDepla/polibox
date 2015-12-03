/**
 * Copyright (c) 2015 Igor Deplano
 * License: MIT
 * https://github.com/IDepla/polibox
 **/
(function(angular){
	
	var app=angular.module("polibox.helpers",["polibox.authentication",
	                                          "polibox.tools",
	                                          "polibox.template"]);

	app.directive("messageBox",["$interval","$rootScope","template",
	                            function($interval,$rootScope,template){
		template("rs/view/common/messagebox.html");
		function Box(scope,element,attr){
			var timeout;
			attr=attr||"message";
			
			element.on("destroy",function(){
				$interval.cancel(timeout);
			})
			
			timeout=$interval(function(){
				scope.shift();
			},10000);
			
			scope.$on("throw.message",function(event,arg){
				event.targetScope.push(arg);
				event.preventDefault(true);
			});
		};
		
		return {
			restrict:'EA',
			link:Box,
			transclude: true,
			controller:"MessageBoxController",
			templateUrl:"rs/view/common/messagebox.html"
		};
	}]);
	

	
	
	app.controller("MessageBoxController",["$scope","code2",
	                                       function($scope,code2){
		$scope.messages=[];
		$scope.messageLimit=10;
		
		$scope.push=function(messageArray){
			angular.forEach(messageArray,function(value,key){
				if($scope.messageLimit==$scope.messages.length){
					$scope.shift();
				}
				$scope.messages.push({
					"class":code2().cssClass(value.code),
					"message":value.message
				});
			});			
		};
		
		$scope.limit=function(number){
			$scope.messageLimit=number;
		};
				
		$scope.shift=function(){
			return $scope.messages.shift();
		};
		
	}]);

	app.factory("code2",[function(){
		var codemap={
			"l400":"error",
			"l200":"message",
			"lerror":"error",
			"lmessage":"message"
		};
		return function(){
			var self=this;
			self["cssClass"]=function(code){
				try{
					return codemap["l"+code];
				}catch(e){
					return codemap["l400"];
				}
			}
			return self;
		};
	}])

	app.factory("messenger",["$rootScope",function($rootScope){
		return {
			"pushUp":function($scope,message){
				$rootScope.$broadcast("throw.message",message);
			},
			"pushDown":function($scope,message){
				$scope.$broadcast("throw.message",message);
			}
		};
	}]);

	
})(window.angular);