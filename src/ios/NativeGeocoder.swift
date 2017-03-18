import CoreLocation

@objc(NativeGeocoder) class NativeGeocoder : CDVPlugin {

    @objc(reverseGeocode:) func reverseGeocode(command: CDVInvokedUrlCommand) {

        var pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR
        )

        let latitude = command.arguments[0] as? Double ?? 0
        let longitude = command.arguments[1] as? Double ?? 0

        if latitude == 0 || longitude == 0 {
            
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Expected two non-empty double arguments."
            )
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
            
        } else {

          let location = CLLocation(latitude: latitude, longitude: longitude)

          CLGeocoder().reverseGeocodeLocation(location, completionHandler: { placemarks, error in

            if error == nil {
                
              let address = placemarks![0] as CLPlacemark

              let resultObj = [
                    "street": address.thoroughfare ?? "",
                    "houseNumber": address.subThoroughfare ?? "",
                    "postalCode": address.postalCode ?? "",
                    "city": address.locality ?? "",
                    "district": address.subLocality ?? "",
                    "countryName": address.country ?? "",
                    "countryCode": address.isoCountryCode ?? ""
              ]
     
              pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: resultObj
              )
                
              
            } else {
                
              pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Cannot get an address"
              )
                
            }

            self.commandDelegate!.send(
              pluginResult,
              callbackId: command.callbackId
            )
            
          })
        }
  }

  @objc(forwardGeocode:)func forwardGeocode(command: CDVInvokedUrlCommand) {

    var pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )

    let address = command.arguments[0] as? String ?? ""
    
    if address.isEmpty {
        
        pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: "Expected a non-empty string argument."
        )
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
        
        
    } else {

      CLGeocoder().geocodeAddressString(address, completionHandler: { placemarks, error in

        if error == nil {
          if let firstPlacemark = placemarks?[0] {
            
            let coordinates = [
                "latitude": "\(firstPlacemark.location!.coordinate.latitude)",
                "longitude": "\(firstPlacemark.location!.coordinate.longitude)"
            ]
            
            pluginResult = CDVPluginResult(
              status: CDVCommandStatus_OK,
              messageAs: coordinates
            )
            
          } else {
            
            pluginResult = CDVPluginResult(
              status: CDVCommandStatus_ERROR,
              messageAs: "Cannot get a location"
            )
            
          }
        } else {
            
          pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: "Cannot get a location"
          )
            
        }
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
        
      })    
    }
  }

}
