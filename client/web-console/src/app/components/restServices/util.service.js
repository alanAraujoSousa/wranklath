(function() {
  angular.module('wranklath')
  .factory('util.service', [
    '$cookies',
    'BASE_DOMAIN',
    'BASE_PORT',
    'BASE_API',
    function($cookies, domain, port, api) {
      return {
        getBaseUrl : function() {
          return 'http://' + domain + ':' + port + api;
        }
      }

    }]);
  })();
