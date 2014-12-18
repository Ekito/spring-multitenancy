angular.module('myappAppUtils', [])
    .service('Base64Service', function () {
        var keyStr = "ABCDEFGHIJKLMNOP" +
            "QRSTUVWXYZabcdef" +
            "ghijklmnopqrstuv" +
            "wxyz0123456789+/" +
            "=";
        this.encode = function (input) {
            var output = "",
                chr1, chr2, chr3 = "",
                enc1, enc2, enc3, enc4 = "",
                i = 0;

            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            }

            return output;
        };

        this.decode = function (input) {
            var output = "",
                chr1, chr2, chr3 = "",
                enc1, enc2, enc3, enc4 = "",
                i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            while (i < input.length) {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            }
        };
    })
    .factory('StorageService', function ($rootScope) {
        return {

            get: function (key) {
                return JSON.parse(localStorage.getItem(key));
            },

            save: function (key, data) {
                localStorage.setItem(key, JSON.stringify(data));
            },

            remove: function (key) {
                localStorage.removeItem(key);
            },

            clearAll : function () {
                localStorage.clear();
            }
        };
    })
    .factory('AccessToken', function($location, $http, StorageService, $rootScope) {
            var TOKEN = 'token';
            var service = {};
            var token = null;

            service.get = function() {
                // read the token from the localStorage
                if (token == null) {
                    token = StorageService.get(TOKEN);
                }

                if (token != null) {
                    return token.access_token;
                }

                return null;
            };

            service.set = function(oauthResponse) {
                token = {};
                token.access_token = oauthResponse.access_token;
                setExpiresAt(oauthResponse);
                StorageService.save(TOKEN, token);
                return token
            };

            service.remove = function() {
                token = null;
                StorageService.remove(TOKEN);
                return token;
            };

            service.expired = function() {
                return (token && token.expires_at && token.expires_at < new Date().getTime())
            };

            var setExpiresAt = function(oauthResponse) {
                if (token) {
                    var now = new Date();
                    var minutes = parseInt(oauthResponse.expires_in) / 60;
                    token.expires_at = new Date(now.getTime() + minutes*60000).getTime()
                }
            };

            return service;
        });

