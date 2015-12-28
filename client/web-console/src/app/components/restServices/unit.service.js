(function() {
  angular.module('wranklath')
  .factory("unit.service", [
    'util.service',
    '$http',
    '$cookies',
    function(utilService, $http, $cookies) {

      return{
        executeAttack : function(data, unitId, enemyId) {
          var url = utilService.getBaseUrl() + '/unit/{id}/attack/{enemy}';
          unitId = unitId.replace('unit', '');
          enemyId = enemyId.replace('unit', '');
          url = url.replace('{id}', unitId);
          url = url.replace('{enemy}', enemyId);
          var token = $cookies.get('token');
          var prom = $http({
            method: 'POST',
            url: url,
            data: data,
            headers: {
              'token': token
            }
          });
          return prom;
        },

        executeMovement : function(steps, unitClicked) {
          unitClicked = unitClicked.replace('unit', '');
          var url = utilService.getBaseUrl() + '/unit/{id}/move';
          url = url.replace('{id}', unitClicked);
          var token = $cookies.get("token");
          var prom = $http({
            method: 'POST',
            url: url,
            data: steps,
            headers: {
              'token': token
            }
          });
          return prom;
        },

        listUnits : function() {
          var url = utilService.getBaseUrl() + '/user/unit';
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
