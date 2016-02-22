(function() {

	angular.module('wranklath')
	.directive('mainMap', MainMap);

	MainMap.$inject = [ 'user.service',
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
	'$interval'];

	function MainMap(userService, unitService, buildingService, utilService, 
		GRID_CELL_SIZE, unitDAO, buildingDAO, panService, worldService, 
		finderService, svgService, $interval) {
		// Runs during compile
		return {
			// name: '',
			// priority: 1,
			// terminal: true,
			// scope: {}, // {} = isolate, true = child, false/undefined = no change
			// controller: function($scope, $element, $attrs, $transclude) {},
			// require: 'ngModel', // Array = multiple requires, ? = optional, ^ = check parent elements
			// restrict: 'A', // E = Element, A = Attribute, C = Class, M = Comment
			// template: '',
			// templateUrl: '',
			// replace: true,
			// transclude: true,
			// compile: function(tElement, tAttrs, function transclude(function(scope, cloneLinkingFn){ return function linking(scope, elm, attrs){}})),
			link: function($scope, iElm, iAttrs) {
				userService.downloadNewEntities().success(function() {
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

			        // stalkEntities();
			    }

			    function handleMapClick() {

			        // if we have a unit selected, search() is how we differentiate buildings of units.
			        if (utilService.unitClicked != null && utilService.unitClicked.search("unit") >= 0) {
			        	var place = d3.event.target;
			        	var id = place.getAttribute("id");
			        	var placeX = Number(place.getAttribute("x"));
			        	var placeY = Number(place.getAttribute("y"));

			          svgService.clearMapMarks(); // clear all previous marks on world.

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

			          unitService.executeMovement(path, utilService.unitClicked);
				    }
				    d3.event.stopPropagation();
				}

				function stalkEmptyWorld() {
				  	$interval(function () {

				  		var point = panService.getCurrentTopLeftCorner();
				  		drawMapVisible(point.x, point.y);

				  	}, 3000);
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
		};
	}
})();