(function() {
  angular.module('models')
  .factory("unit.dao", [
    UnitDAO
    ]);

    function UnitDAO($cookies) {

    	return {
    		user : {units: []},
    		enemy : {units: []},

			addUnitToUser: function(unit) {
				this.user.units.push(unit);
			},
			addUnitToEnemy: function(unit) {
				this.enemy.units.push(unit);
			},
            clear: function() {
                while(this.user.units.length > 0) {
                    this.user.units.pop();
                }
                while(this.enemy.units.length > 0) {
                    this.enemy.units.pop();
                }
            }
    	}
    }

})();