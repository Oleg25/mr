(function() {
    'use strict';

    angular
        .module('joggingApp')
        .factory('JoggingSearch', JoggingSearch);

    JoggingSearch.$inject = ['$resource'];

    function JoggingSearch($resource) {
        var resourceUrl =  'api/_search/joggings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
