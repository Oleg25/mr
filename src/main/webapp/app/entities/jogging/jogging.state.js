(function() {
    'use strict';

    angular
        .module('joggingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('jogging', {
            parent: 'entity',
            url: '/jogging?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Joggings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/jogging/joggings.html',
                    controller: 'JoggingController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('jogging-detail', {
            parent: 'jogging',
            url: '/jogging/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Jogging'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/jogging/jogging-detail.html',
                    controller: 'JoggingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Jogging', function($stateParams, Jogging) {
                    return Jogging.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'jogging',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('jogging-detail.edit', {
            parent: 'jogging-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jogging/jogging-dialog.html',
                    controller: 'JoggingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Jogging', function(Jogging) {
                            return Jogging.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('jogging.new', {
            parent: 'jogging',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jogging/jogging-dialog.html',
                    controller: 'JoggingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                start: null,
                                finish: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('jogging', null, { reload: 'jogging' });
                }, function() {
                    $state.go('jogging');
                });
            }]
        })
        .state('jogging.edit', {
            parent: 'jogging',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jogging/jogging-dialog.html',
                    controller: 'JoggingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Jogging', function(Jogging) {
                            return Jogging.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('jogging', null, { reload: 'jogging' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('jogging.delete', {
            parent: 'jogging',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jogging/jogging-delete-dialog.html',
                    controller: 'JoggingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Jogging', function(Jogging) {
                            return Jogging.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('jogging', null, { reload: 'jogging' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
