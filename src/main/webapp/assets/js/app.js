'use strict';

var app = angular.module('app', [
        'ngSanitize',
        'ngRoute',
        'ngAnimate',
        'ngCookies',
        'ngResource',
        'ngStorage',
        'ngMaterial',
        'md.data.table'
    ])
    .config(['$mdThemingProvider', function ($mdThemingProvider) {
        'use strict';
        $mdThemingProvider.theme('default').primaryPalette('blue');
    }]);

app.run(function($rootScope) {
    $rootScope.$on("$locationChangeSuccess", function(event, next, current) {

        if(!GoogleAuth) return;
        // handle route changes
        if(isSignedIn()){
            if(window.location.href.indexOf("home") !== -1 || window.location.href.indexOf("stats") !== -1){
              //  console.log("sign in true but not doing anything");
            }else{
                window.location.href = '/#!/home';
            }
        }else{
            window.location.href= "/#!/";
        }

    });
});


