(function() {

  angular.module('wranklath')
  .controller('MapController', [
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
      
    }
  ]);
})();
