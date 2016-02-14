(function(){

	angular.module('wranklath')
		.factory('finder.service', [
			'util.service',
			'unit.dao',
			'building.dao',
			'world.service',
			FinderService
			]);

	function FinderService(utilService, unitDAO, buildingDAO, worldService) {
		return {
			pathTo : function(initX, initY, destinyX, destinyY) {
		        var GRID_CENTRAL_X = 29; 
		        var GRID_CENTRAL_Y = 29; 

		        var grid = this.getGridAround(initX, initY);

		        var finder = new PF.AStarFinder({
		          allowDiagonal: true,
		          dontCrossCorners: true
		        });

		        destinyX = (destinyX - initX) + GRID_CENTRAL_X;
		        destinyY = (destinyY - initY) + GRID_CENTRAL_Y;

		        // Function finder.findPath(x, y, destinyX, destinyY, grid), 
		        // have x and y match with grid coordinate system 
		        var result = finder.findPath(GRID_CENTRAL_X, GRID_CENTRAL_Y, destinyX, destinyY, grid);

		        var path = [];
		        for (var i = 0; i < result.length; i++) {
		          var el = result[i];

		          var stepX = (el[0] - GRID_CENTRAL_X) + initX;
		          var stepY = (el[1] - GRID_CENTRAL_Y) + initY;

		          path.push(stepX);
		          path.push(stepY);
		        }
		        return path;
			},

			getGridAround : function(centralX, centralY) {

		        // Define a grid 60x60 matrix-like,
		        var grid = new PF.Grid(60, 60);

		        // ** Snippet to define if Entities are a obstacle on grid.
		        var units = unitDAO.user.units;
		        var unitsE = unitDAO.enemy.units;
		        var buildings = buildingDAO.user.buildings;
		        var buildingsE = buildingDAO.enemy.buildings;

		        var allE = new Array();

		        // (: this is for unificate all entities in a single array.
		        var allU = $.merge($.merge([], units), unitsE);
		        var allB = $.merge($.merge([], buildings), buildingsE);
		        $.merge(allE, allB);
		        $.merge(allE, allU);

		        // Search where entity is.
		        if (allE != null && allE.length > 0) {
		          for (var i = 0; i < allE.length; i++) {
		            var el = allE[i];
		            var elX = el.place.x;
		            var elY = el.place.y;

		            elX -= centralX;
		            elY -= centralY;
		            elX += 29;
		            elY += 29;

		            // If entity is out of grid.
		            if (elX > 59 || elY > 59 || elX < 0 || elY < 0) {
		              continue;
		            } else {
		              grid.setWalkableAt(elX, elY, false);
		            }
		          }
		        }
		        // ** end of snippet

		        // ** Snippet to define if terrain is a obstacle on grid.

		        // centralX and Y "-29" means: 'top left corner of grid'.
		        var topLeftCornerOfGrid = {};
		        topLeftCornerOfGrid.x = (centralX - 29);
		        topLeftCornerOfGrid.y = (centralY - 29);

		        // variables 'i' is the X and 'j' is the Y on grid.
		        for (var i = 0; i < 60; i++) {
		          for (var j = 0; j < 60; j++) {

		            var isPassable = true // Default value of terrain is 'passable'.

		            if (grid.isWalkableAt(i, j)) { // If the current place is passable.
		              
		              var coorX = topLeftCornerOfGrid.x + i;
		              var coorY = topLeftCornerOfGrid.y + j;

		              var info = worldService.getInfoMapChunckOn(coorX, coorY);

		              coorX = coorX - info.initX;
		              coorY = coorY - info.initY;

		              var worldChunk =  worldService.world[info.index];
		              
		              if (worldChunk != null) { // woldChunck null means that this chunk is not downloaded.
		                var terrainType = worldChunk[coorX - 1][coorY - 1];
		                // Move this for a util service. Like a "utilService.IsTerrainWalkable()".
		               	utilService.isTerrainWalkable(terrainType);
		              }
		              // isPassable = isPassable >>> 0;
		              grid.setWalkableAt(i, j, isPassable);
		            }
		          } 
		        }
		        // ** end of snippet. 
		        return grid;
			}
		}
	}

})();