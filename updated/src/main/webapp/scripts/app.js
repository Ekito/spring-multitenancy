'use strict';

/* App Module */
var httpHeaders;

var myappApp = angular.module('myappApp', ['http-auth-interceptor', 'tmh.dynamicLocale',
    'ngResource', 'ngRoute', 'ngCookies', 'myappAppUtils', 'pascalprecht.translate', 'truncate', 'ngCacheBuster']);

myappApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider, USER_ROLES) {

            //Cache everything except rest api requests
            httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*rest.*/],true);

            $routeProvider
                .when('/register', {
                    templateUrl: 'views/register.html',
                    controller: 'RegisterController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/activate', {
                    templateUrl: 'views/activate.html',
                    controller: 'ActivationController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/error', {
                    templateUrl: 'views/error.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/settings', {
                    templateUrl: 'views/settings.html',
                    controller: 'SettingsController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/password', {
                    templateUrl: 'views/password.html',
                    controller: 'PasswordController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/sessions', {
                    templateUrl: 'views/sessions.html',
                    controller: 'SessionsController',
                    resolve:{
                        resolvedSessions:['Sessions', function (Sessions) {
                            return Sessions.get();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/metrics', {
                    templateUrl: 'views/metrics.html',
                    controller: 'MetricsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/health', {
                    templateUrl: 'views/health.html',
                    controller: 'HealthController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/configuration', {
                    templateUrl: 'views/configuration.html',
                    controller: 'ConfigurationController',
                    resolve:{
                        resolvedConfiguration:['ConfigurationService', function (ConfigurationService) {
                            return ConfigurationService.get();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logs', {
                    templateUrl: 'views/logs.html',
                    controller: 'LogsController',
                    resolve:{
                        resolvedLogs:['LogsService', function (LogsService) {
                            return LogsService.findAll();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/audits', {
                    templateUrl: 'views/audits.html',
                    controller: 'AuditsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logout', {
                    templateUrl: 'views/main.html',
                    controller: 'LogoutController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/docs', {
                    templateUrl: 'views/docs.html',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .otherwise({
                    templateUrl: 'views/main.html',
                    controller: 'MainController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                });

            // Initialize angular-translate
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });

            $translateProvider.preferredLanguage('en');

            $translateProvider.useCookieStorage();

            tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js')
            tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
            httpHeaders = $httpProvider.defaults.headers;
        })
        .run(function($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES) {
                $rootScope.authenticated = false;
                $rootScope.$on('$routeChangeStart', function (event, next) {
                    $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
                    $rootScope.userRoles = USER_ROLES;
                    AuthenticationSharedService.valid(next.access.authorizedRoles);
                });

                // Call when the the client is confirmed
                $rootScope.$on('event:auth-loginConfirmed', function(data) {
                    $rootScope.authenticated = true;
                    if ($location.path() === "/login") {
                        var search = $location.search();
                        if (search.redirect !== undefined) {
                            $location.path(search.redirect).search('redirect', null).replace();
                        } else {
                            $location.path('/').replace();
                        }
                    }
                });

                // Call when the 401 response is returned by the server
                $rootScope.$on('event:auth-loginRequired', function(rejection) {
                    Session.invalidate();
                    $rootScope.authenticated = false;
                    if ($location.path() !== "/" && $location.path() !== "" && $location.path() !== "/register" &&
                            $location.path() !== "/activate" && $location.path() !== "/login") {
                        var redirect = $location.path();
                        $location.path('/login').search('redirect', redirect).replace();
                    }
                });

                // Call when the 403 response is returned by the server
                $rootScope.$on('event:auth-notAuthorized', function(rejection) {
                    $rootScope.errorMessage = 'errors.403';
                    $location.path('/error').replace();
                });

                // Call when the user logs out
                $rootScope.$on('event:auth-loginCancelled', function() {
                    $location.path('');
                });
        });
