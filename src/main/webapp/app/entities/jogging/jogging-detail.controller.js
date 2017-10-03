(function() {
    'use strict';

    angular
        .module('joggingApp')
        .controller('JoggingDetailController', JoggingDetailController);

    JoggingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Jogging'];

    function JoggingDetailController($scope, $rootScope, $stateParams, previousState, entity, Jogging) {
        var vm = this;

        vm.jogging = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('joggingApp:joggingUpdate', function(event, result) {
            vm.jogging = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
