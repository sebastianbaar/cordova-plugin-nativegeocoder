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
    
    Geocoder geocoder;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        System.out.print("NativeGeocoder initialize");
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

        geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), Locale.getDefault());

        if (!geocoder.isPresent()) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder is not present on this device/emulator.");
            callbackContext.sendPluginResult(r);
            return;
        }

        try {
            List<Address> geoResults = geocoder.getFromLocation(latitude, longitude, 1);
            if (geoResults.size() > 0) {
                Address address = geoResults.get(0);
                
                JSONObject resultObj = new JSONObject();
                resultObj.put("street", address.getThoroughfare());
                resultObj.put("houseNumber", address.getSubThoroughfare());
                resultObj.put("postalCode", address.getPostalCode());
                resultObj.put("city", address.getLocality());
                resultObj.put("district", address.getSubLocality());
                resultObj.put("countryName", address.getCountryName());
                resultObj.put("countryCode", address.getCountryCode());

                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, resultObj));
            } else {
                PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get an address.");
                callbackContext.sendPluginResult(r);
            }
        } catch (Exception e) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
            callbackContext.sendPluginResult(r);
        }
    }

    private void forwardGeocode(String addressString, CallbackContext callbackContext) {

        geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), Locale.getDefault());

        if (!geocoder.isPresent()) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder is not present on this device/emulator.");
            callbackContext.sendPluginResult(r);
            return;
        }

        if (addressString != null && addressString.length() > 0) {

            try {
                List<Address> geoResults = geocoder.getFromLocationName(addressString, 1);
                if (geoResults.size()>0) {
                    Address address = geoResults.get(0);

                    JSONObject coordinates = new JSONObject();
                    coordinates.put("latitude", address.getLatitude());
                    coordinates.put("longitude", address.getLongitude());

                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, coordinates));
                } else {
                    PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get a location.");
                    callbackContext.sendPluginResult(r);
                }
            
            } catch (Exception e) {
                PluginResult r = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                callbackContext.sendPluginResult(r);
            }
            
        } else {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Expected a non-empty string argument.");
            callbackContext.sendPluginResult(r);
        }
    }
    
}
