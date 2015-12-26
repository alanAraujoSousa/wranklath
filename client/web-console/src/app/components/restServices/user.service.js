(function() {
  angular.module('wranklath')
  .factory("user.service", [
    'util.service',
    '$http',
    '$cookies',
    function(utilService, $http, $cookies) {
      
      return{
        login : function(data) {
          var url = utilService.getBaseUrl() + '/user/login';

          var prom = $http.post(url, data);
          prom.success(function(data) {
            $cookies.put('token', data);
          });
          return prom;
        },

        logout :  function() {
          var prom = $http.post("url");
          prom.success(function() {
            $cookies.remove('token');
          });
          return prom;
        }
      }
    }]);
  })();
