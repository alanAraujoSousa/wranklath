(function() {
  'use strict';

  angular
    .module('wranklath')
    .directive('acmeMalarkey', acmeMalarkey);

  /** @ngInject */
  function acmeMalarkey(malarkey) {
    var directive = {
      restrict: 'E',
      scope: {
        extraValues: '='
      },
      template: '&nbsp;',
      link: linkFunc,
      controller: MalarkeyController,
      controllerAs: 'malarkeyCtrl'
    };

    return directive;

    function linkFunc(scope, el) {
      var typist = malarkey(el[0], {
        typeSpeed: 250,
        deleteSpeed: 40,
        pauseDelay: 2000,
        loop: true,
        postfix: '!'
      });

      el.addClass('acme-malarkey');

      angular.forEach(scope.extraValues, function(value) {
        typist.type(value).pause().delete();
      });
    }

    /** @ngInject */
    function MalarkeyController() {
    }
  }
})();
