(function() {
    'use strict';

    angular
        .module('joggingApp')
        .controller('JoggingDialogController', JoggingDialogController);

    JoggingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Jogging'];

    function JoggingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Jogging) {
        var vm = this;

        vm.jogging = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.jogging.id !== null) {
                Jogging.update(vm.jogging, onSaveSuccess, onSaveError);
            } else {
                Jogging.save(vm.jogging, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('joggingApp:joggingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.start = false;
        vm.datePickerOpenStatus.finish = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
