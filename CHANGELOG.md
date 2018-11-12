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
