'use strict';

myappApp.controller('DomainController', function ($scope, resolvedDomain, Domain) {

        $scope.domains = resolvedDomain;

        $scope.create = function () {
            Domain.save($scope.domain,
                function () {
                    $scope.domains = Domain.query();
                    $('#saveDomainModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.domain = Domain.get({id: id});
            $('#saveDomainModal').modal('show');
        };

        $scope.delete = function (id) {
            Domain.delete({id: id},
                function () {
                    $scope.domains = Domain.query();
                });
        };

        $scope.clear = function () {
            $scope.domain = {name: null, id: null};
        };
    });
