# Cordova NativeGeocoder plugin
Call `nativegeocoder.reverseGeocode()` to transform a latitude and longitude into an address or `nativegeocoder.forwardGeocode()` to transform an address into a latitude and longitude using iOS [CoreLocation](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CoreLocation_Framework/) service and Android [Geocoder](https://developer.android.com/reference/android/location/Geocoder.html) class.

> No need for creating API keys or querying external APIs


**Please read [Known Issues](#known-issues) before posting an issue! Thank you!** :heart_eyes:


This plugin is also available for **[Ionic Native](https://ionicframework.com/docs/native/native-geocoder/)**.

## Installation
```
cordova plugin add cordova-plugin-nativegeocoder
```
The iOS part is written in Swift and the [Swift support plugin](https://github.com/akofman/cordova-plugin-add-swift-support) is configured as a dependency.

## Configuration
You can also configure the following variable to customize the iOS location plist entry

- `LOCATION_WHEN_IN_USE_DESCRIPTION` for `NSLocationWhenInUseUsageDescription` (defaults to "Use geocoder service")

## Supported Platforms
- iOS
- Android

## API
- [.reverseGeocode(success, error, latitude, longitude, options)]()
- [.forwardGeocode(success, error, addressString, options)]()

## .reverseGeocode(successCallback, errorCallback, latitude, longitude, options);
Reverse geocode a given latitude and longitude to find location address.

### Parameters

| Parameter        | Type       | Default | Description                                                   |
| ---------------- | ---------- | ------- | ------------------------------------------------------------- |
| `success` | `Function` |         | Success callback (with Array<Result>)              |
| `error`   | `Function` |         | Error callback. |
| `latitude` | `Number` |         | The latitude.               |
| `longitude`   | `Number` |         | The longtitude. |
| `options`   | `Object` |         | Optional. Is called when the api encounters an error while initializing the context. |

All available `options` attributes:

| Attribute                      | Type     | Default                                                      | Comment                                        |
| ------------------------------ | -------- | ------------------------------------------------------------ | -------------------------------------------------- |
| `useLocale`  | `Boolean` | true | Optional. Only works for Android and iOS 11.0+. |
| `defaultLocale` | `String` |  | Optional. E.g.: 'fa-IR' or 'de_DE'; works only for Android and iOS 11.0+. |
| `maxResults` | `Number` | 1 | Optional. Min value: 1, max value: 5. |

### Array<Result>
Conforms to [Apple's](https://developer.apple.com/documentation/corelocation/clplacemark) and [Android's](https://developer.android.com/reference/android/location/Address.html) reverse geocoder's result arrays.

| Value | Type     |
|-------------|-----------
| `countryCode`  | `String` | 
| `postalCode`  | `String` | 
| `administrativeArea`  | `String` | 
| `subAdministrativeArea`  | `String` | 
| `locality`  | `String` | 
| `subLocality`  | `String` | 
| `thoroughfare`  | `String` | 
| `subThoroughfare`  | `String` | 

### Example
```js
nativegeocoder.reverseGeocode(success, failure, 52.5072095, 13.1452818, { useLocale: true, maxResults: 1 });

function success(result) {
  var firstResult = result[0];
  console.log("First Result: " + JSON.stringify(firstResult));
}

function failure(err) {
  console.log(err);
}
```

## .forwardGeocode(success, error, addressString, options)
Forward geocode a given address to find coordinates.

### Parameters

| Parameter        | Type       | Default | Description                                                   |
| ---------------- | ---------- | ------- | ------------------------------------------------------------- |
| `success` | `Function` |         | Success callback (with Array<Result>)              |
| `error`   | `Function` |         | Error callback. |
| `addressString` | `String` |         | The address to be geocoded.               |
| `options`   | `Object` |         | Optional. Is called when the api encounters an error while initializing the context. |

All available `options` attributes:

| Attribute                      | Type     | Default                                                      | Comment                                        |
| ------------------------------ | -------- | ------------------------------------------------------------ | -------------------------------------------------- |
| `useLocale`  | `Boolean` | true | Optional. Only works for Android and iOS 11.0+. |
| `defaultLocale` | `String` |  | Optional. E.g.: 'fa-IR' or 'de_DE'; works only for Android and iOS 11.0+. |
| `maxResults` | `Number` | 1 | Optional. Min value: 1, max value: 5. |

### Array<Result>
| Value | Type     |
|-------------|-----------
| `latitude`  | `String` | 
| `longitude`  | `String` | 


### Example
```js
nativegeocoder.forwardGeocode(success, failure, "Berlin", { useLocale: true, maxResults: 1 });

function success(coordinates) {
  var firstResult = coordinates[0];
  console.log("The coordinates are latitude = " + firstResult.latitude + " and longitude = " + firstResult.longitude);
}

function failure(err) {
  console.log(err);
}
```

## Known Issues

### Android 

#### Geocoder not working
Errors like `Geocoder is not present on this device/emulator.`, `Geocoder [...] Error` or `Geocoder Service not available`.

**Why?**: 
The plugin checks for `Geocoder.isPresent()` ([Android docs](https://developer.android.com/reference/android/location/Geocoder.html#isPresent())). One reason for Geocoder not working on a device could be that you are running your app on an emulator or on a device without Google Play Services. But Geocoder API "[...] requires a backend service that is not included in the core android framework".

**Any workaround?**: 
Yes. Query [Google Maps Geocoding API](https://developers.google.com/maps/documentation/geocoding/start) as a backup if Geocoder is not present.

**Can the plugin handle this?**: 
No. I decided not to implement Google Maps Geocoding API as a backup because you have to add an [API key and more](https://developers.google.com/maps/documentation/geocoding/usage-and-billing). I want this plugin to remain as small and lightweight as possible.

### iOS
...

## Thank you :heart:
Yes you! Thank you very much for using cordova-plugin-nativegeocoder. If you have any feedback or run into issues using the plugin, please file an [issue](https://github.com/sebastianbaar/cordova-plugin-nativegeocoder/issues/new) on this repository.
