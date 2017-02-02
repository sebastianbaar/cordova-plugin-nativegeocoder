import { Component } from '@angular/core';

import { NavController, Platform } from 'ionic-angular';
import { NativeGeocoder, NativeGeocoderForwardResult, NativeGeocoderReverseResult } from 'ionic-native';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  foo: string;
  bar: string;

  constructor(public navCtrl: NavController, public platform: Platform) {
    
    this.platform.ready().then(() => {

      NativeGeocoder.reverseGeocode(52.5072095, 13.1452818)
        .then((result: NativeGeocoderReverseResult) => {
          this.foo = "The address is " + result.street + " " + result.houseNumber + " in " + result.city + ", " + result.countryCode;
          console.log("The address is " + result.street + " " + result.houseNumber + " in " + result.city + ", " + result.countryCode);
        })
        .catch((error: any) => console.log(error));
  
      NativeGeocoder.forwardGeocode("Berlin")
        .then((coordinates: NativeGeocoderForwardResult) => {
          this.bar = "The coordinates are latitude=" + coordinates.latitude + " and longitude=" + coordinates.longitude;
          console.log("The coordinates are latitude=" + coordinates.latitude + " and longitude=" + coordinates.longitude);
        })
        .catch((error: any) => console.log(error));
    });
  }
}
