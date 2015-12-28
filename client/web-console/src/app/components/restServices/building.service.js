(function() {
  angular.module('wranklath')
  .factory("building.service", [
    'util.service',
    '$http',
    '$cookies',
    function(utilService, $http, $cookies) {

      return {

        listBuildings : function() {
          var url = utilService.getBaseUrl() + '/user/building';
          var token = $cookies.get("token");
          var prom = $http({
            url: url,
            method: 'GET',
            headers: {
              'token': token
            }
          });

          return prom;
        }

      }
    }]);
  })();
