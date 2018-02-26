# Cordova NativeGeocoder plugin
Call `nativegeocoder.reverseGeocode()` to transform a latitude and longitude into an address or `nativegeocoder.forwardGeocode()` to transform an address into a latitude and longitude using iOS [CoreLocation](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CoreLocation_Framework/) service and Android [Geocoder](https://developer.android.com/reference/android/location/Geocoder.html) class.

> No need for creating API keys or querying external APIs

**This plugin is also available for [Ionic Native](https://ionicframework.com/docs/native/native-geocoder/)**

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
- Android (works only on native devices)

## Methods
- nativegeocoder.reverseGeocode
- nativegeocoder.forwardGeocode

## nativegeocoder.reverseGeocode
Reverse geocode a given latitude and longitude to find location address.

    nativegeocoder.reverseGeocode(successCallback, errorCallback, latitude, longitude, options);

### Parameters
- __latitude__: The latitude. (Double)
- __longitude__: The longtitude. (Double)
- __options__: The Options

```
{ 
  useLocale: boolean      (default: true)   (works only for Android and iOS 11.0+)
  defaultLocale: string                     (e.g.: 'fa-IR' or 'de_DE'; works only for Android and iOS 11.0+)
  maxResults: number      (default: 1)      (min-max: 1-5)
}
```

### Result Object (Array)
https://developer.apple.com/documentation/corelocation/clplacemark
https://developer.android.com/reference/android/location/Address.html

- countryCode
- postalCode
- administrativeArea
- subAdministrativeArea
- locality
- subLocality
- thoroughfare
- subThoroughfare

### Example
```js
nativegeocoder.reverseGeocode(success, failure, 52.5072095, 13.1452818, { useLocale: true, maxResults: 1 });
function success(result) {
  alert("The address is: \n\n" + JSON.stringify(result[0]));
}
function failure(err) {
  alert(JSON.stringify(err));
}
```

## nativegeocoder.forwardGeocode
Forward geocode a given address to find coordinates.

    nativegeocoder.forwardGeocode(successCallback, errorCallback, addressString, options);

### Parameters
- __addressString__: The address to be geocoded. (String)
- __options__: The Options.

```
{ 
  useLocale: boolean      (default: true)   (works only for Android and iOS 11.0+)
  defaultLocale: string                     (e.g.: 'fa-IR' or 'de_DE'; works only for Android and iOS 11.0+)
  maxResults: number      (default: 1)      (min-max: 1-5)
}
```

### Result Object (Array)
- latitude
- longitude

### Example
```js
nativegeocoder.forwardGeocode(success, failure, "Berlin", { useLocale: true, maxResults: 1 });
function success(coordinates) {
  alert("The coordinates are latitude = " + coordinates[0].latitude + " and longitude = " + coordinates[0].longitude);
}
function failure(err) {
  alert(JSON.stringify(err));
}
```
