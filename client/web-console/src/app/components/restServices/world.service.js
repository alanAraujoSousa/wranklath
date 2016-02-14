(function(){
	
	angular.module('rest')
	.factory('world.service', [
		'$http',
		WorldService
		]);

	function WorldService($http){
		return {
			world: {},
			queueDownloadMapChunck: [],

			downloadMapChunck : function(index) {
				if (this.queueDownloadMapChunck.indexOf(index) < 0) {
					var queue = this.queueDownloadMapChunck;

					queue.push(index);

					var url = './assets/map/data/map' + index + '.json';
					var prom = $http({
						url: url,
						method: 'GET'
					});

					prom.success(function(data) {
						//remove download of queue
						queue.splice(queue.indexOf(index), 1);

					}).error(function(data) {

					});

					return prom;
				}
			},

			getInfoMapChunckOn: function(initX, initY) {

				if (initX % 100 == 0)
					initX--;
				if (initY % 100 == 0)
					initY--;

				initX = Number.parseInt(initX / 100);
				initY = Number.parseInt(initY / 100);

				var index = 1;
				
				index += initX * 50;
				index += initY;

				initX *= 100;
				initY *= 100;

				var info = new Object();
				info.index = index;
				info.initX = initX;
				info.initY = initY;

				return info;
			},

			getTerrainType: function(x ,y) {
				var info = this.getInfoMapChunckOn(x, y);
				return info;
			}

		}
	}

})();