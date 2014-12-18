'use strict';

myappApp.factory('Author', function ($resource) {
        return $resource('app/rest/authors/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
