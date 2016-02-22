(function() {
  'use strict';

  angular
    .module('wranklath')
    .config(routeConfig);

  function routeConfig($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/main/main.html',
        controller: 'MainController',
        controllerAs: 'main'
      })
      .when('/map', {
        templateUrl: 'app/map/map.html',
        controller: 'MapController',
        controllerAs: 'map'
      })
      .otherwise({
        redirectTo: '/'
      });
  }

})();
