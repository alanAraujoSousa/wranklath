(function() {
	angular.module('wranklath')
	.factory('pan.service', [
		'GRID_CELL_SIZE',
		PanService
		]);

	function PanService(GRID_CELL_SIZE){
		return {
			svgPan: {},

			init: function(svgElementId) {
				this.svgPan = svgPanZoom(svgElementId, {
					mouseWheelZoomEnabled: false,
					fit: false,
					center: false,
					dblClickZoomEnabled: false
				});
			},

			pan: function(initX, initY) {
				this.svgPan.pan({
					x: initX * GRID_CELL_SIZE * -1,
					y: initY * GRID_CELL_SIZE * -1
				});
			},

			getCurrentTopLeftCorner: function() {

				// top, left corner.
	          	var point = {};

	            var x = this.svgPan.getPan().x;
	            var y = this.svgPan.getPan().y;

	            x = Number.parseInt(x / 100);
	            y = Number.parseInt(y / 100);

	            // Only calc coordinates positives.
	            if (x < 0)
	              x *= -1;

	            if (y < 0)
	              y *= -1;

	          	point.x = x;
	          	point.y = y;

	          	return point;
      		}

		}
	}
})();