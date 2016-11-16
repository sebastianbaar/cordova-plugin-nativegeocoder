var exec = require('cordova/exec');

exports.reverseGeocode = function(success, error, latitude, longitude) {
    exec(success, error, "NativeGeocoder", "reverseGeocode", [latitude, longitude]);
};

exports.forwardGeocode = function(success, error, addressString) {
    exec(success, error, "NativeGeocoder", "forwardGeocode", [addressString]);
};