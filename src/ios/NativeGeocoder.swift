import CoreLocation

@objc(NativeGeocoder) class NativeGeocoder : CDVPlugin {

    @objc(reverseGeocode:) func reverseGeocode(_ command: CDVInvokedUrlCommand) {

        var pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR
        )

        if let latitude = command.arguments[0] as? Double,
            let longitude = command.arguments[1] as? Double {
         
            let location = CLLocation(latitude: latitude, longitude: longitude)
            
            CLGeocoder().reverseGeocodeLocation(location, completionHandler: { placemarks, error in
                
                if error == nil {
                    
                    if let placemark = placemarks?[0] as CLPlacemark? {
                        
                        // https://developer.apple.com/documentation/corelocation/clplacemark
                        let resultObj: Dictionary = [
                            "countryCode": placemark.isoCountryCode ?? "",
                            "countryName": placemark.country ?? "",
                            "postalCode": placemark.postalCode ?? "",
                            "administrativeArea": placemark.administrativeArea ?? "",
                            "subAdministrativeArea": placemark.subAdministrativeArea ?? "",
                            "locality": placemark.locality ?? "",
                            "subLocality": placemark.subLocality ?? "",
                            "thoroughfare": placemark.thoroughfare ?? "",
                            "subThoroughfare": placemark.subThoroughfare ?? ""
                        ]
                        
                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_OK,
                            messageAs: resultObj
                        )
                        
                    }
                    else {
                        
                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_ERROR,
                            messageAs: "Cannot get an address"
                        )
                        
                    }
                }
                else {
                    
                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_ERROR,
                        messageAs: "CLGeocoder:reverseGeocodeLocation Error"
                    )
                    
                }
                
                self.commandDelegate!.send(
                    pluginResult,
                    callbackId: command.callbackId
                )
                
            })
        }
        else {
            
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Expected two non-empty double arguments."
            )
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
            
        }
  }

  @objc(forwardGeocode:)func forwardGeocode(_ command: CDVInvokedUrlCommand) {

    var pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )
    
    if let address = command.arguments[0] as? String {
        
        CLGeocoder().geocodeAddressString(address, completionHandler: { placemarks, error in
            
            if error == nil {
                
                if let firstPlacemark = placemarks?[0] {
                    
                    if let latitude = firstPlacemark.location?.coordinate.latitude,
                        let longitude = firstPlacemark.location?.coordinate.longitude {
                        
                        let coordinates = [
                            "latitude": "\(latitude)",
                            "longitude": "\(longitude)"
                        ]
                        
                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_OK,
                            messageAs: coordinates
                        )
                    }
                    else {
                        
                        pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_ERROR,
                            messageAs: "Cannot get latitude and/or longitude"
                        )
                        
                    }
                }
                else {
                    
                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_ERROR,
                        messageAs: "Cannot find a location"
                    )
                    
                }
            }
            else {
                
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: "CLGeocoder:geocodeAddressString Error"
                )
                
            }
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
            
        })
        
    }
    else {
        
        pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: "Expected a non-empty string argument."
        )
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
        
    }
  }
}