(function() {
    'use strict';

    angular
        .module('joggingApp')
        .controller('JoggingDeleteController',JoggingDeleteController);

    JoggingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Jogging'];

    function JoggingDeleteController($uibModalInstance, entity, Jogging) {
        var vm = this;

        vm.jogging = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Jogging.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
