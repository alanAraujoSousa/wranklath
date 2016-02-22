(function() {

	angular.module('wranklath')
	.directive('mainEntity', MainEntity);

	MainEntity.$inject = [
	'$compile',
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
	];

	function MainEntity($compile, userService, unitService, buildingService, utilService, 
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
			link: function($scope, element, iAttrs) {

				stalkEntities();
				function stalkEntities() {

					$interval(function () {
						var promise = userService.downloadNewEntities();

						promise.success(function () {
							drawEntitiesVisible();
							drawVisibilityRange();
						});
        			}, 3000); // draw all
				}

				function drawEntitiesVisible() {

			        // d3.select("#buildingGroup").selectAll("*").remove();
			        // d3.select("#unitGroup").selectAll("*").remove();

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


			        // If entity still in same position.
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
			    	rect.attr('user-unit-click', '');
			    	rect.on("click", handleMyUnitClick);
			    }
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
			        	}
			        	*/

			        	svgService.clearMapMarks();

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
			  	svgService.toogleUnitMark(unit);
			  	svgService.clearMapMarks();
			  	d3.event.stopPropagation();
			  }

			  function drawVisibilityRange() {
			  	var all = new Array();
			  	var units = unitDAO.user.units;
			  	var buildings = buildingDAO.user.buildings;

			  	$.merge($.merge(all, buildings), units);

			  	var g = d3.select('#visibleEffect');

		        // The diff have a side effect that cause lack.
		        // Delete all have a better visual experience.
		        // g.selectAll("*").remove(); // for simple refresh

		      	var visibles = {};
		      	for (var it = 0; it < all.length; it++) {
		      		el = all[it];
		      		var x = el.place.x;
		      		var y = el.place.y;

		        	// This is because Buildings have no visibility range.
		        	var visibility;
		          	if (el.conclusionDate == null) { // differ buildings of unit.
		          		visibility = el.visibility;
		          	} else {
		            	visibility = 3 // FIXME give visibility to buildings on backend.
		            }

		            var v = utilService.getVisiblePlaces(x, y, visibility);
		            for (var k in v) {
		            	var place = v[k];
		            	visibles["glade" + k] = place;
		            }
		        }
		        for (var key in visibles) {
		        	var place = visibles[key];

		        	var el = d3.select("#" + key)[0][0];
		        	if (el != null) {
		        		continue;
		        	}
			    	// console.log(el);
			    	// debugger;
			    	var x = utilService.ofRealToSVGCoordinate(place[0]);
			    	var y = utilService.ofRealToSVGCoordinate(place[1]);

			    	g.append("rect")
			    	.attr("id", key)
			    	.attr("filter", utilService.getVisibleTexture())
			    	.attr("x", x)
			    	.attr("y", y)
			    	.attr("width", GRID_CELL_SIZE)
			    	.attr("height", GRID_CELL_SIZE)
			    	.attr("fill", "yellow");
			    }

			    // Glades is the fog vision already drawn.
			    var glades = g[0][0].childNodes;
			    for (var i = 1; i < glades.length; i++) {
			    	var el = glades[i];
			    	var x = el.getAttribute("x");
			    	var y = el.getAttribute("y");

			    	x = utilService.ofSVGToRealCoordinate(x);
			    	y = utilService.ofSVGToRealCoordinate(y);
			    	if (!visibles['glade'+ 'x'+x+'y'+y]) {
			    		el.remove();
			    	}
			    }

			}
		}
	};
}

})();