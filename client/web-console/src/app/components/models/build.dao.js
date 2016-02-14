(function() {
  angular.module('models')
  .factory("building.dao", [
    buildingDAO
    ]);

    function buildingDAO() {

    	return {
    	   user : {buildings: []},
    	   enemy : {buildings: []},

			addBuildingToUser: function(building) {
				this.user.buildings.push(building);
			},
			addBuildingToEnemy: function(building) {
				this.enemy.buildings.push(building);
			},
            clear: function() {
                while(this.user.buildings.length > 0) {
                    this.user.buildings.pop();
                }
                while(this.enemy.buildings.length > 0) {
                    this.enemy.buildings.pop();
                }
            }
    	}
    }

})();