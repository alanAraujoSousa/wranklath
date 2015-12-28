(function() {
  angular.module('wranklath')
  .factory('util.service', [
    '$cookies',
    '$http',
    'BASE_DOMAIN',
    'BASE_PORT',
    'BASE_API',
    function($cookies, $http, domain, port, api) {
      return {

        getBaseUrl : function() {
          return 'http://' + domain + ':' + port + api;
        },

        getUser : function() {
          return $cookies.get('user');
        },

        downloadMapChunck : function(index) {
          var url = './assets/map/data/map' + index + '.json';
          var prom = $http({
            url: url,
            method: 'GET'
          });
          return prom;
        }
      }

    }]);
  })();
