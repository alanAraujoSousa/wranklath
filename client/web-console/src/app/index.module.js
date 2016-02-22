(function() {

  angular
  .module('rest', []);

  angular
  .module('models', []);

  angular
  .module('svg',[]);

  angular
    .module('wranklath', [
      'ngAnimate',
      'ngCookies',
      'ngSanitize',
      'ngMessages',
      'ngAria',
      'ngResource',
      'ngRoute',
      'ui.bootstrap',
      'rest',
      'models',
      'svg'
    ]);
})();
