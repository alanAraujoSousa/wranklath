(function() {
  'use strict';

  angular
    .module('wranklath')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {

    $log.debug('wranklath has initialized.');
  }

})();
