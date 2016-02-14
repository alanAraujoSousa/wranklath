(function() {

	angular.module('wranklath')
	.factory('svg.service', [
		'world.service',
		'util.service',
		'GRID_CELL_SIZE',
		SvgService
	]);

	function SvgService(worldService, utilService, GRID_CELL_SIZE) {
		return {

			drawPath : function(path) {

		        // This is a little conversion to a data format understandable by 'lineFunction'.
		        var pathToBeDrawed = [];
		        for (var i = 0; i < path.length; i+=2) {
		          var obj = {};
		          obj.x = utilService.ofRealToSVGCoordinate(path[i]);
		          obj.y = utilService.ofRealToSVGCoordinate(path[i+1]);
		          pathToBeDrawed.push(obj);
		        }

		        if (pathToBeDrawed.length > 0) {

		          // Helper function to draw a line.
		          var lineFunction = d3.svg.line()
		          .x(function (d) {
		            return d.x + (GRID_CELL_SIZE / 2); // this calc is to centralize line on cell.
		          })
		          .y(function (d) {
		            return d.y + (GRID_CELL_SIZE / 2);
		          })
		          .interpolate("linear");

		          // Draw line in SVG.
		          var lineGraph = d3.select('#movementEffects').append("path")
		          .attr("id", "path")
		          .attr("d", lineFunction(pathToBeDrawed))
		          .attr("stroke", "green")
		          .attr("stroke-width", 12)
		          .attr("stroke-dasharray", "50,25")
		          .attr("fill", "none");
		        }
	    	}
      	}
	}

})();