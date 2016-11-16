import CoreLocation

@objc(NativeGeocoder) class NativeGeocoder : CDVPlugin {

  func reverseGeocode(command: CDVInvokedUrlCommand) {

    var pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )

    let latitude = command.arguments[0] as? Double ?? 0
    let longitude = command.arguments[1] as? Double ?? 0

    if latitude == 0 || longitude == 0 {
      pluginResult = CDVPluginResult(
        status: CDVCommandStatus_ERROR,
        messageAsString: "Expected two non-empty double arguments."
      )      
    } else {

      let location = CLLocation(latitude: latitude, longitude: longitude)

      CLGeocoder().reverseGeocode(location, completionHandler: { placemarks, error in

        if error == nil {
          if let firstPlacemark = placemarks?[0] as? CLPlacemark {
            pluginResult = CDVPluginResult(
              status: CDVCommandStatus_OK,
              messageAsDictionary: firstPlacemark.addressDictionary
            )
          } else {
            pluginResult = CDVPluginResult(
              status: CDVCommandStatus_ERROR,
              messageAsString: "Cannot get a address"
            )
          }
        } else {
          pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAsString: "Cannot get a address"
          )
        }

        self.commandDelegate!.sendPluginResult(
          pluginResult,
          callbackId: command.callbackId
        )
      })
    }
  }

  func forwardGeocode(command: CDVInvokedUrlCommand) {

    var pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )

    let address = command.arguments[0] as? string ?? ""
    
    if address.isEmpty {
      pluginResult = CDVPluginResult(
        status: CDVCommandStatus_ERROR,
        messageAsString: "Expected a non-empty string argument."
      )      
    } else {

      CLGeocoder().geocodeAddressString(address, completionHandler: { placemarks, error in

        if error == nil {
          if let firstPlacemark = placemarks?[0] as? CLPlacemark {
            pluginResult = CDVPluginResult(
              status: CDVCommandStatus_OK,
              messageAsDictionary: firstPlacemark.location.coordinate
            )
          } else {
            pluginResult = CDVPluginResult(
              status: CDVCommandStatus_ERROR,
              messageAsString: "Cannot get a location"
            )
          }
        } else {
          pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAsString: "Cannot get a location"
          )
        }
      })    
    }

    self.commandDelegate!.sendPluginResult(
      pluginResult,
      callbackId: command.callbackId
    )
  }

}