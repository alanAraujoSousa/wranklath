(function() {
  angular.module('rest')
  .factory("user.service", [
    'util.service',
    '$http',
    '$cookies',
    function(utilService, $http, $cookies) {

      return {
        login : function(data) {
          var url = utilService.getBaseUrl() + '/user/login';
          var login = data.login;
          var prom = $http.post(url, data);
          prom.success(function(dataReceived) {
            $cookies.put('token', dataReceived);
            $cookies.put('user', login);
          });
          return prom;
        },

        // TODO
        logout :  function() {
          var prom = $http.post("url");
          prom.success(function() {
            $cookies.remove('token');
          });
          return prom;
        },

        listAllVisible : function() {
          var url = utilService.getBaseUrl() + "/user/allvisible";
          var token = $cookies.get('token');
          var prom = $http({
            url: url,
            method: 'GET',
            headers: {'token': token}
          });

          return prom;
        }
      }

    }]);
  })();
