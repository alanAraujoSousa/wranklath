(function() {

  angular.module('wranklath')
  .controller('GameController', [
    'user.service',
    'unit.service',
    'building.service',
    'util.service',
    'GRID_CELL_SIZE',
    'unit.dao',
    'building.dao',
    'pan.service',
    'world.service',
    'finder.service',
    'svg.service',
    '$interval',
    function(userService, unitService, buildingService, utilService, 
      GRID_CELL_SIZE, unitDAO, buildingDAO, panService, worldService, 
      finderService, svgService, $interval) {

      var vm = new Object();

      vm.unitClicked = null;
 
      downloadNewEntities().success(function() {
        generateInitialMap();
      });

      function generateInitialMap() {

        var initialPlace = utilService.getInitialPosition();

        var initX = initialPlace.x;
        var initY = initialPlace.y;

        panService.svgPan = panService.svgPan;

        // Init pan on elemente with id #paper
        panService.init('#paper');

        // Draw initial map
        drawMapVisible(initX, initY);

        stalkEmptyWorld();

        // pan to initial position.
        // TODO put initial coordinates centralized on map.
        panService.pan(initX, initY);

        stalkEntities();
      }

      function clearMapMarks() {
        d3.select("#path").remove();
        d3.select("#selectMapEffect").attr("style", "display:none;");
        d3.select("#attackEffect").attr("style", "display:none;");
      }

      function handleEnemyClick() {
        var enemyEntity = d3.event.target;
        var eId = enemyEntity.getAttribute("id");
        var eX = Number(enemyEntity.getAttribute("x"));
        var eY = Number(enemyEntity.getAttribute("y"));

        var unitClicked = utilService.unitClicked;


        if (unitClicked != null && eId != null && eId.search("unit") >= 0) {

          /*          
          var units = unitDAO.user.units;
          var atkRange = 0;
          var unitClickedId = unitClicked.substr(4); // trim 'unit' prefix.
          for (var i = 0; i < units.length; i++) {
            var u = units[i];
            if (u.id == unitClickedId) {
              atkRange = u.atkRange;
              break;
            }
          }*/

          clearMapMarks();

          var effect = d3.select("#attackEffect");
          effect.attr("transform", "translate(" + eX + "," + eY + ")");
          effect.attr("style", "display:true;");

          var unit = d3.select("#" + unitClicked)[0][0];
          var unitX = unit.getAttribute("x") / GRID_CELL_SIZE;
          var unitY = unit.getAttribute("y") / GRID_CELL_SIZE;

          eX /= GRID_CELL_SIZE;
          eY /= GRID_CELL_SIZE;

          // var trustfulPlaces = utilService.getVisiblePlaces(eX, eY, atkRange - 1);
          // var pathToSendToBackEnd = drawPath(unitX, unitY, eX, eY, null);

          // var promise = restExecuteMovement(pathToSendToBackEnd, unitClicked);
        }

        d3.event.stopPropagation();
      }

      function handleMyUnitClick() {
        var unit = d3.event.target;
        var id = unit.getAttribute("id");
        var x = Number(unit.getAttribute("x"));
        var y = Number(unit.getAttribute("y"));

        var effect = d3.select("#selectUnitEffect");
        if (id != utilService.unitClicked) {
          utilService.unitClicked = id;
          var factor = GRID_CELL_SIZE / 2; // Factor is only to centralize.
          effect.attr("transform", "translate(" + (x + factor) + "," + (y + factor) + ")");
          effect.attr("style", "display:true;");
        } else {
          utilService.unitClicked = null;
          effect.attr("style", "display:none;");
        }

        clearMapMarks();

        d3.event.stopPropagation();
      }

      function handleMapClick() {

        // if we have a unit selected, search() is how we differentiate buildings of units.
        if (utilService.unitClicked != null && utilService.unitClicked.search("unit") >= 0) {
          var place = d3.event.target;
          var id = place.getAttribute("id");
          var placeX = Number(place.getAttribute("x"));
          var placeY = Number(place.getAttribute("y"));

          clearMapMarks(); // clear all previous marks on world.

          // Draw a mark on place clicked.
          var effect = d3.select("#selectMapEffect");
          effect.attr("transform", "translate(" + placeX + "," + placeY + ")");
          effect.attr("style", "display:true;");

          var unit = d3.select("#" + utilService.unitClicked)[0][0];

          var unitX = unit.getAttribute("x");
          var unitY = unit.getAttribute("y");

          // Convert to real coordinate for calc the path to be drawn on screen.
          unitX = utilService.ofSVGToRealCoordinate(unitX);
          unitY = utilService.ofSVGToRealCoordinate(unitY);

          placeX = utilService.ofSVGToRealCoordinate(placeX);
          placeY = utilService.ofSVGToRealCoordinate(placeY);
          
          var path = finderService.pathTo(unitX, unitY, placeX, placeY);

          svgService.drawPath(path);

          var promise = unitService.executeMovement(path, utilService.unitClicked);
        }
        d3.event.stopPropagation();
      }

      function drawVisibilityRange() {
        var all = new Array();
        var units = unitDAO.user.units;
        var buildings = buildingDAO.user.buildings;

        $.merge($.merge(all, buildings), units);

        var g = d3.select('#visibleEffect');
        g.selectAll("*").remove(); // for simple refresh

        var visibles = {};
        for (var it = 0; it < all.length; it++) {
          el = all[it];
          var x = el.place.x;
          var y = el.place.y;

          var visibility;
          if (el.conclusionDate == null) // differ buildings of unit.
            visibility = el.visibility;
          else
            visibility = 3 // FIXME give visibility to buildings on backend.

          var v = utilService.getVisiblePlaces(x, y, visibility);
          for (var k in v) {
            visibles[k] = v[k];
          }
        }

        for (var key in visibles) {
          var place = visibles[key];
          g.append("rect")
          .attr("id", key)
          .attr("filter", "url(#visible)")
          .attr("x", place[0] * GRID_CELL_SIZE)
          .attr("y", place[1] * GRID_CELL_SIZE)
          .attr("width", GRID_CELL_SIZE)
          .attr("height", GRID_CELL_SIZE)
          .attr("fill", "yellow");
        }
      }

      function stalkEntities() {
        $interval(function () {
          var promise = downloadNewEntities();

          promise.success(function () {
            drawEntitiesVisible();
            drawVisibilityRange();
          });
        }, 3000); // draw all
      }

      function stalkEmptyWorld() {
        $interval(function () {

          var point = panService.getCurrentTopLeftCorner();
          drawMapVisible(point.x, point.y);

        }, 3000);
      }

      function downloadNewEntities() {  
        var prom = userService.listAllVisible();
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

      function drawEntitiesVisible() {

        d3.select("#buildingGroup").selectAll("*").remove();
        d3.select("#unitGroup").selectAll("*").remove();

        var point = panService.getCurrentTopLeftCorner();
        var initX = point.x;
        var initY = point.y;

        var range = 30;

        var buildings = buildingDAO.user.buildings;
        var buildingsE = buildingDAO.enemy.buildings;
        var units = unitDAO.user.units;
        var unitsE = unitDAO.enemy.units;

        var all = new Array();
        $.merge($.merge(all, buildings), buildingsE);
        $.merge($.merge(all, units), unitsE);

        for (var i = 0; i < all.length; i++) {
          var entity = all[i];
          var targetX = entity.place.x;
          var targetY = entity.place.y;

          if (((initX - range) <= targetX && (initX + range) >= targetX) &&
            ((initY - range) <= targetY && (initY + range) >= targetY)) {
            drawEntity(entity);
          }
        }
      }

      function drawEntity(entity) {
        var x = entity.place.x;
        var y = entity.place.y;

        var type = entity.type;
        var id = entity.id;

        var login = entity.userLogin;

        var nameOfType;
        if (entity.conclusionDate == null) {
          nameOfType = 'unit'
          type = utilService.getUnitSvgTexture(type);
        } else {
          nameOfType = 'building'
          type = utilService.getBuildSvgTexture(type);
        }

        // Check if entity is already drawn on screen.
        var d3Element = d3.select("#" + nameOfType + id);
        var elementDrawed = d3Element[0][0];
        if (elementDrawed != null) {
          var actualX = elementDrawed.getAttribute("x") / GRID_CELL_SIZE;
          var actualY = elementDrawed.getAttribute("y") / GRID_CELL_SIZE;

          // If entity still 
          if (actualX == x && actualY == y) {
            return;
          } else {
            d3Element.remove();
          }
        }

        var rect = d3.select("#" + nameOfType + "Group").append("rect")
        rect.attr("id", nameOfType + id)
        .attr("x", x * GRID_CELL_SIZE)
        .attr("y", y * GRID_CELL_SIZE)
        .attr("width", GRID_CELL_SIZE)
        .attr("height", GRID_CELL_SIZE)
        .attr("fill", type);

        if (login != utilService.getUser()) {
          rect.attr("filter", "url(#enemy)");
          rect.on("click", handleEnemyClick);
        } else {
          rect.on("click", handleMyUnitClick);
        }
      }

      function drawMapVisible(x, y) {
        drawMap(x, y + 50);
        drawMap(x, y - 50);
        drawMap(x + 50, y);
        drawMap(x + 50, y + 50);
        drawMap(x + 50, y - 50);
        drawMap(x - 50, y);
        drawMap(x - 50, y - 50);
        drawMap(x - 50, y - 50);
      }

      function drawMap(initX, initY) {

        var info = worldService.getInfoMapChunckOn(initX, initY);

        var index = info.index;
        var deltaX = info.initX;
        var deltaY = info.initY;

        var world = worldService.world;

        if (world[index] != null) {
          paintMap(index, deltaX, deltaY);
        }

        if (world[index] == null) {
            // promise
            var prom = worldService.downloadMapChunck(index);
            if (prom != null) {
              prom.success(function (data) {
                console.log("Downloaded map chunck: " + index);
              // store data of world
              world[index] = data;
              // Draw world on screen.
              paintMap(index, deltaX, deltaY);
            });
            }
          }


          function paintMap(index, deltaX, deltaY) {
            var isPainted = d3.select("#map" + index)[0][0] != null;
            // if this chunck isn't already painted.
            if (!isPainted) {
              var group = d3.select('#mapGroup').append("g").attr("id", "map" + index);
              for (var x = 1; x <= 100; x++) {
                var factorX = x + deltaX;
                for (var y = 1; y <= 100; y++) {
                  var factorY = y + deltaY;

                  var terrainType = world[index][x - 1][y - 1]; // ohhay!
                  terrain = utilService.getTerrainSvgTexture(terrainType);

                  group.append("rect")
                  .attr("id", "x" + factorX + "y" + factorY)
                  .attr("x", factorX * GRID_CELL_SIZE)
                  .attr("y", factorY * GRID_CELL_SIZE)
                  .attr("width", GRID_CELL_SIZE)
                  .attr("height", GRID_CELL_SIZE)
                  .attr("fill", terrain)
                  .style("stroke-width", "1px")
                  .style("stroke", "black")
                  .on("click", handleMapClick);
                }
              }
            }
          }
      }

    }
  ]);
})();
