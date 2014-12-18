'use strict';

myappApp.factory('Domain', function ($resource) {
        return $resource('app/rest/domains/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
