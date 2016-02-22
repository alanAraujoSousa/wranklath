(function() {
  angular.module('rest')
  .factory("user.service", [
    'util.service',
    '$http',
    '$cookies',
    'unit.dao',
    'building.dao',
    function(utilService, $http, $cookies, unitDAO, buildingDAO) {

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
        },

        downloadNewEntities : function() {  
          var prom = this.listAllVisible();
          prom.success(function (data) {

            unitDAO.clear();
            buildingDAO.clear();

            if (data != null && data.length > 0) {
              for (var i = 0; i < data.length; i++) {
                var el = data[i];

                var entityOwner = el.userLogin;
                var userLogged = utilService.getUser();

                if (entityOwner == userLogged) {
                          // FIXME find another way to differ builds and units.
                          if (el.conclusionDate == null) { // This is for differ buildings and units
                            unitDAO.addUnitToUser(el);
                          } else {
                            buildingDAO.addBuildingToUser(el);
                          }
                      } else {
                          if (el.conclusionDate == null) { // This is for differ buildings and units
                            unitDAO.addUnitToEnemy(el);
                          } else {
                            buildingDAO.addBuildingToEnemy(el);
                          }
                      }
                  }
              }
          });
          return prom;
        }
      }

    }]);
  })();
