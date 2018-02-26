var exec = require('cordova/exec');

/**
 * Reverse geocode a given latitude and longitude to find location address.
 * @param {*} success {NativeGeocoderReverseResult[]} Success callback containing array of result objects
 * @param {*} error Error callback
 * @param {*} latitude {number} The latitude
 * @param {*} longitude {number} The longitude
 * @param {*} options {NativeGeocoderOptions} The options
 */
exports.reverseGeocode = function(success, error, latitude, longitude, options) {
    exec(success, error, "NativeGeocoder", "reverseGeocode", [latitude, longitude, options]);
};

/**
 * Forward geocode a given address to find coordinates.
 * @param {*} success {NativeGeocoderForwardResult[]} Success callback containing array of result objects
 * @param {*} error Error callback
 * @param {*} addressString {string} The address to be geocoded
 * @param {*} options {NativeGeocoderOptions} The options
 */
exports.forwardGeocode = function(success, error, addressString, options) {
    exec(success, error, "NativeGeocoder", "forwardGeocode", [addressString, options]);
};

/*
NativeGeocoderReverseResult:
- countryCode
- postalCode
- administrativeArea
- subAdministrativeArea
- locality
- subLocality
- thoroughfare
- subThoroughfare

NativeGeocoderForwardResult:
- latitude
- longitude

NativeGeocoderOptions:
- useLocale = true
- defaultLocale
- maxResults = 1
*/