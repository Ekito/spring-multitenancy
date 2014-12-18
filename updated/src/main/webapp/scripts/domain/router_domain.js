'use strict';

myappApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/domain', {
                    templateUrl: 'views/domains.html',
                    controller: 'DomainController',
                    resolve:{
                        resolvedDomain: ['Domain', function (Domain) {
                            return Domain.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
