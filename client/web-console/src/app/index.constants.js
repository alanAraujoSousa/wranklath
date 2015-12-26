/* global malarkey:false, moment:false */
(function() {
  'use strict';

  angular
    .module('wranklath')
    .constant('malarkey', malarkey)
    .constant('moment', moment)
    .constant('BASE_DOMAIN', '127.0.0.1')
    .constant('BASE_PORT', '8080')
    .constant('BASE_API', '/wranklath/rest');
})();
