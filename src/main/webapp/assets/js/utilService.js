app.factory('utilService', function($cookies, $window) {
	var utilService = {};

	console.log("Simple Utility Service/Factory");


	utilService.signOut = function(){
        GoogleAuth.signOut().then(function (value) {
            $cookies.remove("name");
            $cookies.remove("email");
            $cookies.remove("imageUrl");
            $window.location.href = '/#!/';
		});
    }

	return utilService;
});
