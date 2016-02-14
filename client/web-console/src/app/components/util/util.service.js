(function() {
  angular.module('wranklath')
  .factory('util.service', [
    '$cookies',
    '$http',
    'BASE_DOMAIN',
    'BASE_PORT',
    'BASE_API',
    'building.dao',
    'unit.dao',
    'GRID_CELL_SIZE',
    function($cookies, $http, domain, port, api, buildingDAO, unitDAO, GRID_CELL_SIZE) {
      return {
        unitClicked : null,
        getBaseUrl : function() {
          return 'http://' + domain + ':' + port + api;
        },

        getUser : function() {
          return $cookies.get('user');
        },

        /**
        * Get initial position to initialize camera. 
        *
        */
        getInitialPosition : function() {
          var towns = buildingDAO.user.buildings;
          var initialPlace = {};
          
          if (towns.length > 0) {
            initialPlace = towns[0].place;
          } else {
            var armys = unitDAO.user.units;
            if (armys.length > 0) {
              var initialPlace = armys[0].place;
            } else {
              initialPlace.x = 150; // Random number.
              initialPlace.y = 150;
            }
          }

          return initialPlace;
        },

        // FIXME I am lazy, this method is very stupid.
        getVisiblePlaces : function(x, y, visibility) {

          var visibles = {};
          if (visibility == 0) {
            visibles['x' + x + 'y' + y] = [x, y];
            return visibles;
          }

          var delta = 0;

          if (visibility > 2) {
            if (visibility % 3 == 0) { // divisors
              delta = visibility / 3;
            } else if ((visibility + 1) % 3 == 0) { // adjacents
              delta = (visibility + 1) / 3;
              delta--;
            } else { // fails
              delta = (visibility - 1) / 3;
              delta--;
            }
          }

          // Miau for calc diagonal when visibility is 1.
          if (visibility == 1) {
            visibles['x' + (x + 1) + 'y' + (y + 1)] = [x + 1, y + 1];
            visibles['x' + (x - 1) + 'y' + (y - 1)] = [x - 1, y - 1];
            visibles['x' + (x + 1) + 'y' + (y - 1)] = [x + 1, y - 1];
            visibles['x' + (x - 1) + 'y' + (y + 1)] = [x - 1, y + 1];
          }

          var cont = visibility + delta + 1;
          var x2;
          var y2;
          x2 = x;
          y2 = y + visibility;
          for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y2 - y); j++) {
              var mY = (y + j);
              visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 < x + visibility) {
              x2++;
            }
            if (i > delta) {
              y2--;
            }
          }

          x2 = x;
          y2 = y + visibility;
          for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y2 - y); j++) {
              var mY = (y + j);
              visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 > x - visibility) {
              x2--;
            }
            if (i > delta) {
              y2--;
            }
          }

          x2 = x;
          y2 = y - visibility;
          for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y - y2); j++) {
              var mY = (y - j);
              visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 > x - visibility) {
              x2--;
            }
            if (i > delta) {
              y2++;
            }
          }

          x2 = x;
          y2 = y - visibility;
          for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y - y2); j++) {
              var mY = (y - j);
              visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 < x + visibility) {
              x2++;
            }
            if (i > delta) {
              y2++;
            }
          }

          return visibles;
        },

        getUnitSvgTexture : function(code) {
          switch (code) {
            case 422322:
            return "url(#cavalary)";
            case 392333:
            return "url(#infantry)";
            case 202356:
            return "url(#archer)";
          }
        },

        getBuildSvgTexture : function(code) {
          switch (code) {
            case 943213:
            return "url(#town)";
            case 194234:
            return "url(#barracks)";
          }
        },

        getTerrainSvgTexture : function(code) {
          switch (code) {
            case 1:
            return "url(#grassland)";
            case 2:
            return "url(#road)";
            case 3:
            return "url(#forest)";
            case 4:
            return "url(#water)";
          }
        },

        ofSVGToRealCoordinate : function(number) {
          return number / GRID_CELL_SIZE;
        },
        ofRealToSVGCoordinate : function(number) {
          return number * GRID_CELL_SIZE;
        },
        isTerrainWalkable : function(terrainType) {
          if (terrainType == 4) {
            return false;
          } else {
            return true;
          }
        }
      }
    }]);
})();
