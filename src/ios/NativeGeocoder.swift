import CoreLocation

struct NativeGeocoderReverseResult: Encodable {
    var countryCode: String?
    var countryName: String?
    var postalCode: String?
    var administrativeArea: String?
    var subAdministrativeArea: String?
    var locality: String?
    var subLocality: String?
    var thoroughfare: String?
    var subThoroughfare: String?
}

struct NativeGeocoderForwardResult: Encodable {
    var latitude: String?
    var longitude: String?
}

struct NativeGeocoderError {
    var message: String
}

struct NativeGeocoderOptions: Decodable {
    var useLocale: Bool = true
    var defaultLocale: String?
    var maxResults: Int = 1
}

@objc(NativeGeocoder) class NativeGeocoder : CDVPlugin {
    typealias ReverseGeocodeCompletionHandler = ([NativeGeocoderReverseResult]?, NativeGeocoderError?) -> Void
    typealias ForwardGeocodeCompletionHandler = ([NativeGeocoderForwardResult]?, NativeGeocoderError?) -> Void
    private static let MAX_RESULTS_COUNT = 5

    // MARK: - REVERSE GEOCODE
    @objc(reverseGeocode:) func reverseGeocode(_ command: CDVInvokedUrlCommand) {
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)

        if let latitude = command.arguments[0] as? Double,
            let longitude = command.arguments[1] as? Double {
            
            if (CLGeocoder().isGeocoding) {
                pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Geocoder is busy. Please try again later.")
                self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
                return
            }
            
            let location = CLLocation(latitude: latitude, longitude: longitude)
            var options = NativeGeocoderOptions(useLocale: true, defaultLocale: nil, maxResults: 1)
            if let optionsDict = command.arguments[2] as? NSDictionary {
                let useLocaleOption = optionsDict.value(forKey: "useLocale") as? Bool ?? true
                let defaultLocaleOption = optionsDict.value(forKey: "defaultLocale") as? String
                let maxResultsOption = optionsDict.value(forKey: "maxResults") as? Int ?? 1
                options.useLocale = useLocaleOption
                options.defaultLocale = defaultLocaleOption
                options.maxResults = maxResultsOption
            }
            
            reverseGeocodeLocationHandler(location, options: options, completionHandler: { [weak self] (resultObj, error) in
                if let error = error {
                    pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: error.message)
                } else {
                    if let encodedResult = try? JSONEncoder().encode(resultObj),
                        let result = try? JSONSerialization.jsonObject(with: encodedResult, options: .allowFragments) as? [Dictionary<String,Any>] {
                        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
                    } else {
                        pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Invalid JSON result")
                    }
                }
                
                self?.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
            })
        }
        else {
            pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Expected two non-empty double arguments.")
            self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
        }
    }
    
    private func reverseGeocodeLocationHandler(_ location: CLLocation, options: NativeGeocoderOptions, completionHandler: @escaping ReverseGeocodeCompletionHandler) {
        let geocoderOptions = getNativeGeocoderOptions(from: options)
        
        if #available(iOS 11, *) {
            var locale: Locale?
            if let defaultLocaleString = geocoderOptions.defaultLocale {
                locale = Locale.init(identifier: defaultLocaleString)
            } else if (geocoderOptions.useLocale == false) {
                locale = Locale.init(identifier: "en_US")
            }

            CLGeocoder().reverseGeocodeLocation(location, preferredLocale: locale, completionHandler: { [weak self] (placemarks, error) in
                self?.createReverseGeocodeResult(placemarks, error, maxResults: geocoderOptions.maxResults, completionHandler: { (resultObj, error) in
                    completionHandler(resultObj, error)
                })
            })
        } else {
            // fallback for < iOS 11
            CLGeocoder().reverseGeocodeLocation(location, completionHandler: { [weak self] (placemarks, error) in
                self?.createReverseGeocodeResult(placemarks, error, maxResults: geocoderOptions.maxResults, completionHandler: { (resultObj, error) in
                    completionHandler(resultObj, error)
                })
            })
        }
    }
    
    private func createReverseGeocodeResult(_ placemarks: [CLPlacemark]?, _ error: Error?, maxResults: Int, completionHandler: @escaping ReverseGeocodeCompletionHandler) {
        guard error == nil else {
            completionHandler(nil, NativeGeocoderError(message: "CLGeocoder:reverseGeocodeLocation Error"))
            return
        }
        
        if let placemarks = placemarks {
            let maxResultObjects = placemarks.count >= maxResults ? maxResults : placemarks.count
            var resultObj = [NativeGeocoderReverseResult]()
            
            for i in 0..<maxResultObjects {
                // https://developer.apple.com/documentation/corelocation/clplacemark
                let placemark = NativeGeocoderReverseResult(
                    countryCode: placemarks[i].isoCountryCode ?? "",
                    countryName: placemarks[i].country ?? "",
                    postalCode: placemarks[i].postalCode ?? "",
                    administrativeArea: placemarks[i].administrativeArea ?? "",
                    subAdministrativeArea: placemarks[i].subAdministrativeArea ?? "",
                    locality: placemarks[i].locality ?? "",
                    subLocality: placemarks[i].subLocality ?? "",
                    thoroughfare: placemarks[i].thoroughfare ?? "",
                    subThoroughfare: placemarks[i].subThoroughfare ?? ""
                )
                resultObj.append(placemark)
            }
            
            completionHandler(resultObj, nil)
        }
        else {
            completionHandler(nil, NativeGeocoderError(message: "Cannot get an address"))
        }
    }
    
    
    // MARK: - FORWARD GEOCODE
    @objc(forwardGeocode:)func forwardGeocode(_ command: CDVInvokedUrlCommand) {
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)
        
        if let address = command.arguments[0] as? String {
            
            if (CLGeocoder().isGeocoding) {
                pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Geocoder is busy. Please try again later.")
                self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
                return
            }
            
            var options = NativeGeocoderOptions(useLocale: true, defaultLocale: nil, maxResults: 1)
            if let optionsDict = command.arguments[1] as? NSDictionary {
                let useLocaleOption = optionsDict.value(forKey: "useLocale") as? Bool ?? true
                let defaultLocaleOption = optionsDict.value(forKey: "defaultLocale") as? String
                let maxResultsOption = optionsDict.value(forKey: "maxResults") as? Int ?? 1
                options.useLocale = useLocaleOption
                options.defaultLocale = defaultLocaleOption
                options.maxResults = maxResultsOption
            }
            
            forwardGeocodeHandler(address, options: options, completionHandler: { [weak self] (resultObj, error) in
                if let error = error {
                    pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: error.message)
                } else {
                    if let encodedResult = try? JSONEncoder().encode(resultObj),
                        let result = try? JSONSerialization.jsonObject(with: encodedResult, options: .allowFragments) as? [Dictionary<String,Any>] {
                        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
                    } else {
                        pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Invalid JSON result")
                    }
                }
                
                self?.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
            })
        }
        else {
            pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Expected a non-empty string argument.")
            self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
        }
    }
    
    func forwardGeocodeHandler(_ address: String, options: NativeGeocoderOptions, completionHandler: @escaping ForwardGeocodeCompletionHandler) {
        let geocoderOptions = getNativeGeocoderOptions(from: options)
        
        if #available(iOS 11, *) {
            var locale: Locale?
            if let defaultLocaleString = geocoderOptions.defaultLocale {
                locale = Locale.init(identifier: defaultLocaleString)
            } else if (geocoderOptions.useLocale == false) {
                locale = Locale.init(identifier: "en_US")
            }
            
            CLGeocoder().geocodeAddressString(address, in: nil, preferredLocale: locale, completionHandler: { [weak self] (placemarks, error) in
                self?.createForwardGeocodeResult(placemarks, error, maxResults: geocoderOptions.maxResults, completionHandler: { (resultObj, error) in
                    completionHandler(resultObj, error)
                })
            })
        } else {
            // fallback for < iOS 11
            CLGeocoder().geocodeAddressString(address, completionHandler: { [weak self] (placemarks, error) in
                self?.createForwardGeocodeResult(placemarks, error, maxResults: geocoderOptions.maxResults, completionHandler: { (resultObj, error) in
                    completionHandler(resultObj, error)
                })
            })
        }
    }

    private func createForwardGeocodeResult(_ placemarks: [CLPlacemark]?, _ error: Error?, maxResults: Int, completionHandler: @escaping ForwardGeocodeCompletionHandler) {
        guard error == nil else {
            completionHandler(nil, NativeGeocoderError(message: "CLGeocoder:geocodeAddressString Error"))
            return
        }
        
        if let placemarks = placemarks {
            let maxResultObjects = placemarks.count >= maxResults ? maxResults : placemarks.count
            var resultObj = [NativeGeocoderForwardResult]()
            
            for i in 0..<maxResultObjects {
                if let latitude = placemarks[i].location?.coordinate.latitude,
                    let longitude = placemarks[i].location?.coordinate.longitude {
                
                    let coordinates = NativeGeocoderForwardResult(latitude: "\(latitude)", longitude: "\(longitude)")
                    resultObj.append(coordinates)
                }
            }
            
            if (resultObj.count == 0) {
                completionHandler(nil, NativeGeocoderError(message: "Cannot get latitude and/or longitude"))
            } else {
                completionHandler(resultObj, nil)
            }
        }
        else {
            completionHandler(nil, NativeGeocoderError(message: "Cannot find a location"))
        }
    }
    
    // MARK: - Helper
    private func getNativeGeocoderOptions(from options: NativeGeocoderOptions) -> NativeGeocoderOptions {
        var geocoderOptions = NativeGeocoderOptions()
        geocoderOptions.useLocale = options.useLocale
        geocoderOptions.defaultLocale = options.defaultLocale
        if (options.maxResults > 0) {
            geocoderOptions.maxResults = options.maxResults > NativeGeocoder.MAX_RESULTS_COUNT ? NativeGeocoder.MAX_RESULTS_COUNT : options.maxResults
        } else {
            geocoderOptions.maxResults = 1
        }
        return geocoderOptions
    }

}
