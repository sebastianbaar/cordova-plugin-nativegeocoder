# 3.4.1 (2020-04-29)
- add cordova-plugin-add-swift-support to fix Swift version errors (closes #57)

# 3.4.0 (2020-03-30)

- remove `IntentService` and use `cordova.getThreadPool()`

# 3.3.1 (2020-03-27)

- closes #55


# 3.3.0 (2020-03-25)

- merge PR #54 "move Geocoding in an IntentService to not block the main thread" (closes #47, thank you @DavidWiesner)

## ** BREAKING CHANGES **
- remove dependency [Swift support plugin](https://github.com/akofman/cordova-plugin-add-swift-support); min Cordova iOS Version is now >5.0.0


# 3.2.2 (2019-04-14)

- update cordova-plugin-add-swift-support to 2.0.2 (closes #45, thank you @DavidWiesner)
- fix Android error logging (closes #44)

# 3.2.1 (2019-03-13)

- iOS: fix Optionals for latitude and longitude in reverse geocoding

# 3.2.0 (2019-03-10)

- result array returns the same information for forward and reverse geocoding (closes #35)
- result array return points of interest string array for iOS and Android (closes #38)
    - results[0].areasOfInterest = ["Brandenburger Tor"]
- update dependency 'cordova-plugin-add-swift-support' to 1.7.2

## ** BREAKING CHANGES **
- replace __NativeGeocoderForwardResult__ with __NativeGeocoderResult__
- replace __NativeGeocoderReverseResult__ with __NativeGeocoderResult__

# 3.1.3 (2018-11-12)

Android: return empty String if Address property is null (closes #34)

# 3.1.2 (2018-07-10)

- Better handle 'grpc failed' error on Android (#21)
    - add "No Internet Access" error if grpc failed because of no Internet connection
    - add "android.permission.ACCESS_NETWORK_STATE" permission
- update README
- refactoring...

# 3.1.1 (2018-02-27)

refactoring...

# 3.1.0 (2018-02-26)

Add 'defaultLocale' to options Object to set a default locale like 'fa-IR' or 'de_DE' (closes #26)

# 3.0.0 (2018-02-06)

For making the API more robust for future changes and new features I changed the return type of both methods to an array and added an 'options' param.

## ** BREAKING CHANGES **
- The result Object of __nativegeocoder.reverseGeocode__ is an Array.
- The result Object of __nativegeocoder.forwardGeocode__ is an Array.

## CHANGELOG
- Add options param for both __nativegeocoder.reverseGeocode__ and __nativegeocoder.forwardGeocode__ for setting locale & max result objects (closes #17, #25)

# 2.0.5 (2017-10-13)

update dependency 'cordova-plugin-add-swift-support' to 1.7.1 (closes #24)

# 2.0.4 (2017-10-13)

fix Swift compiler issue (closes #10)

# 2.0.3 (2017-09-26)

Add configuration variable to customize the iOS location plist entry (closes #13)

# 2.0.2 (2017-07-27)

fix github links

# 2.0.1 (2017-07-26)

Add CHANGELOG

# 2.0.0 (2017-07-26)

## ** BREAKING CHANGES **

The result Object of __nativegeocoder.reverseGeocode__ has changed:
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

## CHANGELOG
- Closes #6
- Closes #7 
