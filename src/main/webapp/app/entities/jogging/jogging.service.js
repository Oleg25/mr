(function() {
    'use strict';
    angular
        .module('joggingApp')
        .factory('Jogging', Jogging);

    Jogging.$inject = ['$resource', 'DateUtils'];

    function Jogging ($resource, DateUtils) {
        var resourceUrl =  'api/joggings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.start = DateUtils.convertDateTimeFromServer(data.start);
                        data.finish = DateUtils.convertDateTimeFromServer(data.finish);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
