package cordova.plugin.nativegeocoder;

import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NativeGeocoder extends CordovaPlugin {
    
    private Geocoder geocoder;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        System.out.print("NativeGeocoder initialized");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("reverseGeocode")) {
            double latitude = args.getDouble(0);
            double longitude = args.getDouble(1);
            this.reverseGeocode(latitude, longitude, callbackContext);
            return true;
        }

        if (action.equals("forwardGeocode")) {
            String addressString = args.getString(0);
            this.forwardGeocode(addressString, callbackContext);
            return true;
        }

        return false;
        
    }

    private void reverseGeocode(double latitude, double longitude, CallbackContext callbackContext) {

        if (latitude == 0 || longitude == 0) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Expected two non-empty double arguments.");
            callbackContext.sendPluginResult(r);
            return;
        }

        if (!Geocoder.isPresent()) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder is not present on this device/emulator.");
            callbackContext.sendPluginResult(r);
            return;
        }

        geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), Locale.getDefault());

        try {
            List<Address> geoResults = geocoder.getFromLocation(latitude, longitude, 1);
            if (geoResults.size() > 0) {
                Address address = geoResults.get(0);

                // https://developer.android.com/reference/android/location/Address.html
                JSONObject resultObj = new JSONObject();
                resultObj.put("countryCode", address.getCountryCode());
                resultObj.put("countryName", address.getCountryName());
                resultObj.put("postalCode", address.getPostalCode());
                resultObj.put("administrativeArea", address.getAdminArea());
                resultObj.put("subAdministrativeArea", address.getSubAdminArea());
                resultObj.put("locality", address.getLocality());
                resultObj.put("subLocality", address.getSubLocality());
                resultObj.put("thoroughfare", address.getThoroughfare());
                resultObj.put("subThoroughfare", address.getSubThoroughfare());

                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, resultObj));
            } else {
                PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get an address.");
                callbackContext.sendPluginResult(r);
            }
        }
        catch (Exception e) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder:getFromLocation Error: " + e.getMessage());
            callbackContext.sendPluginResult(r);
        }
    }

    private void forwardGeocode(String addressString, CallbackContext callbackContext) {

        if (!Geocoder.isPresent()) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder is not present on this device/emulator.");
            callbackContext.sendPluginResult(r);
            return;
        }

        geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), Locale.getDefault());

        if (addressString != null && addressString.length() > 0) {

            try {
                List<Address> geoResults = geocoder.getFromLocationName(addressString, 1);
                if (geoResults.size() > 0) {
                    Address address = geoResults.get(0);

                    try {
                        String latitude = String.valueOf(address.getLatitude());
                        String longitude = String.valueOf(address.getLongitude());

                        if (latitude.isEmpty() || longitude.isEmpty()) {
                            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get latitude and/or longitude.");
                            callbackContext.sendPluginResult(r);
                            return;
                        }

                        JSONObject coordinates = new JSONObject();
                        coordinates.put("latitude", latitude);
                        coordinates.put("longitude", longitude);

                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, coordinates));

                    }
                    catch (RuntimeException e) {
                        PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get latitude and/or longitude.");
                        callbackContext.sendPluginResult(r);
                    }
                }
                else {
                    PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot find a location.");
                    callbackContext.sendPluginResult(r);
                }
            
            }
            catch (Exception e) {
                PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder:getFromLocationName Error: " +e.getMessage());
                callbackContext.sendPluginResult(r);
            }
            
        }
        else {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Expected a non-empty string argument.");
            callbackContext.sendPluginResult(r);
        }
    }
}