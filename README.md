# Cordova NativeGeocoder plugin

Call `nativegeocoder.reverseGeocode()` to transform a latitude and longitude into an address or `nativegeocoder.forwardGeocode()` to transform an address into a latitude and longitude using iOS [CoreLocation](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CoreLocation_Framework/) service and Android [Geocoder](https://developer.android.com/reference/android/location/Geocoder.html) class.

> No need for creating API keys or querying external APIs

## Installation

```
cordova plugin add cordova-plugin-nativegeocoder
```
The iOS part is written in Swift 3 and the [Swift support plugin](https://github.com/akofman/cordova-plugin-add-swift-support) is configured as a dependency.

## Ionic Native Demo

Have a look at the [demo](https://github.com/sebastianbaar/cordova-plugin-nativegeocoder/tree/master/demo) project for a working demo with the [Ionic Framework](http://ionicframework.com/) and [Ionic Native](http://ionicframework.com/docs/v2/native/).

## Supported Platforms

- iOS
- Android (works only on native devices)

## Methods

- nativegeocoder.reverseGeocode
- nativegeocoder.forwardGeocode

## nativegeocoder.reverseGeocode

Reverse geocode a given latitude and longitude to find location address.

    nativegeocoder.reverseGeocode(successCallback, errorCallback, latitude, longitude);

### Parameters

- __latitude__: The latitude. (Double)
- __longitude__: The longtitude. (Double)

### Example

```js
nativegeocoder.reverseGeocode(success, failure, 52.5072095, 13.1452818);
function success(result) {
  alert("The address is: \n\n" + result.street + " " + result.houseNumber + ", " + result.postalCode + " " + result.city + " " + result.district + " in " + result.countryName + " - " + result.countryCode);
}
function failure(err) {
  alert(JSON.stringify(err));
}
```

## nativegeocoder.forwardGeocode

Forward geocode a given address to find coordinates.

    nativegeocoder.forwardGeocode(successCallback, errorCallback, addressString);

### Parameters

- __addressString__: The address to be geocoded. (String)

### Example

```js
nativegeocoder.forwardGeocode(success, failure, "Berlin");
function success(coordinates) {
  alert("The coordinates are latitude = " + coordinates.latitude + " and longitude = " + coordinates.longitude);
}
function failure(err) {
  alert(JSON.stringify(err));
}
```
