(function() {
  'use strict';

  angular.module('wranklath')
  .controller('MainController', [
    '$location',
    'user.service',
    function($location, userService) {
      var vm = this;

      vm.login = function(user) {
        var prom = userService.login(user);
        prom.success(function() {
          $location.url('/map');
        });
      }

      vm.logout = function() {

      }

      vm.forgotPass = function() {

      }
    }
  ]);
})();

/*

Use the directives to manipulate the dom.

var options = {
'btn-loading': '<i class="fa fa-spinner fa-pulse"></i>',
'btn-success': '<i class="fa fa-check"></i>',
'btn-error': '<i class="fa fa-remove"></i>',
};

function remove_loading($form)
{
$form.find('[type=submit]').removeClass('error success');
$form.find('.login-form-main-message').removeClass('show error success').html('');
}

function form_loading($form)
{
$form.find('[type=submit]').addClass('clicked').html(utils['btn-loading']);
}

function form_success($form)
{
$form.find('[type=submit]').addClass('success').html(options['btn-success']);
$form.find('.login-form-main-message').addClass('show success').html("All Good! Redirecting...");
}

function form_failed($form)
{
$form.find('[type=submit]').addClass('error').html(options['btn-error']);
$form.find('.login-form-main-message').addClass('show error').html("Wrong login credentials!");
}
*/
