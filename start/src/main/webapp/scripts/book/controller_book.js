'use strict';

myappApp.controller('BookController', function ($scope, resolvedBook, Book) {

        $scope.books = resolvedBook;

        $scope.create = function () {
            Book.save($scope.book,
                function () {
                    $scope.books = Book.query();
                    $('#saveBookModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.book = Book.get({id: id});
            $('#saveBookModal').modal('show');
        };

        $scope.delete = function (id) {
            Book.delete({id: id},
                function () {
                    $scope.books = Book.query();
                });
        };

        $scope.clear = function () {
            $scope.book = {title: null, description: null, publicationDate: null, price: null, id: null};
        };
    });
