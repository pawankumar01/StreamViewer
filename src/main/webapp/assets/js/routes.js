app.config(function($routeProvider) {
			// route for the home page
			console.log('In route');
			$routeProvider.when('/', {
                templateUrl : 'templates/login.html',
                controller  : 'loginCtrl',
            }).when('/home', {
				templateUrl : 'templates/home.html',
				controller  : 'svCtrl',
			}).when('/stats', {
                templateUrl : 'templates/stats.html',
                controller  : 'statsCtrl',
            }).when('/callback', {
                templateUrl : 'templates/callback.html',
                controller  : 'callbackCtrl',
            })
			.otherwise({
				redirectTo : '/',
			});
	});

