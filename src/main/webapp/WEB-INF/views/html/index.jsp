<!doctype html>
<html lang="it">
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<meta charset="utf-8">
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta name="_csrf" content="${_csrf.token}" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Polibox, another dropbox like instrument</title>

<link rel="stylesheet"
	href="rs/vendor/bootstrap-3.3.4-dist/css/bootstrap.min.css" />
<link rel="stylesheet" href="rs/css/app.css" />

</head>
<body ng-app="polibox">
	<message-box class="container text-center main-message-box"></message-box>
	<div class="container center-block"
		ng-controller="AuthenticationController">
		<!-- navigation toolbar -->
		<nav class="navbar navbar-inverse">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand" href="#/files">Polibox</a>
				</div>
				<div>
					<ul class="nav navbar-nav">
						<li><a href="#/files">File</a></li>
						<li><a href="#/devices">devices</a></li>
						<li><a href="#/notifications">notifications</a></li>
						<li><a href="#/account">account</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right"
						ng-controller="LanguageController">
						<li ng-repeat="(index,value) in list"
							ng-class="{active : isSelected(index)}"><a
							ng-click="setLanguage(index);">{{value}}</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li ng-if="!isAuthenticated()"><a href="#/registrati"><span
								class="glyphicon glyphicon-user"></span> registrati</a></li>
						<li ng-if="!isAuthenticated()"><a href="#/accedi"><span
								class="glyphicon glyphicon-log-in"></span> accedi</a></li>
						<li ng-if="isAuthenticated()"><notification-realtime-container
								class="nav navbar-nav"></notification-realtime-container></li>
						<li ng-if="isAuthenticated()" ng-controller="LogoutController"><a
							href="#/accedi" ng-click="ok()"><span
								class="glyphicon glyphicon-log-out"></span> logout</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<!-- main view -->
		<div class="container" ng-view>caricamento in corso...</div>

		<!-- footer -->
		<footer class="container">
			<a class="fl" href="#/terms">terms and conditions</a> <a class="fl"
				href="mailto:the.moloch@gmail.com">contattami</a>
		</footer>
	</div>

	<!-- jQuery -->
	<script src="rs/vendor/jquery-2.1.3.min.js"></script>

	<!-- jQueryUI -->
	<script src="rs/vendor/jquery-ui-1.11.4.custom/jquery-ui.min.js"></script>


	<!-- CryptoJS -->
	<script src="rs/vendor/cryptojs-v3.1.2/rollups/hmac-sha3.js"></script>

	<!-- angular js -->
	<script src="rs/vendor/angular-loader.min.js"></script>
	<script src="rs/vendor/angular.min.js"></script>
	<script src="rs/vendor/angular-route.min.js"></script>
	<script src="rs/vendor/angular-animate.min.js"></script>
	<script src="rs/vendor/http-auth-interceptor.js"></script>
	<script src="rs/vendor/ui-bootstrap-tpls-0.12.0.min.js"></script>


	<!-- libs -->
	<script src="rs/js/lib/cache.js"></script>
	<script src="rs/js/lib/template.js"></script>
	<script src="rs/js/lib/translation.js"></script>
	<script src="rs/js/lib/tools.js"></script>
	<!-- application -->
	<script src="rs/js/controller/helpers.js"></script>
	<script src="rs/js/controller/authentication.js"></script>
	<script src="rs/js/controller/home.js"></script>
	<script src="rs/js/controller/notification.js"></script>
	<script src="rs/js/controller/sharing.js"></script>
	<script src="rs/js/controller/file.js"></script>
	<script src="rs/js/controller/file.directive.js"></script>
	<script src="rs/js/controller/file.service.js"></script>
	<script src="rs/js/controller/file.controller.js"></script>
	<script src="rs/js/controller/device.js"></script>
	<script src="rs/js/controller/account.js"></script>
	<script src="rs/js/app.js"></script>

	<iframe id="manifest_hack" style="display: none" src="rs/manifest.html"></iframe>
</body>
</html>