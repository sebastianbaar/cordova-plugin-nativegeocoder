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
