package cordova.plugin.nativegeocoder;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NativeGeocoder extends CordovaPlugin {

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
            JSONObject options = null;
            try {
                options = args.getJSONObject(2);
            } catch (JSONException ignored) { }

            this.reverseGeocode(latitude, longitude, options, callbackContext);

            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }

        if (action.equals("forwardGeocode")) {
            String addressString = args.getString(0);
            JSONObject options = null;
            try {
                options = args.getJSONObject(1);
            } catch (JSONException ignored) { }
            this.forwardGeocode(addressString, options, callbackContext);

            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
        }

        return false;
    }

    /**
     * Reverse geocode a given latitude and longitude to find location address
     * @param latitude double
     * @param longitude double
     * @param options JSONObject
     * @param callbackContext CallbackContext
     */
    private void reverseGeocode(double latitude, double longitude, JSONObject options, CallbackContext callbackContext) {

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
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Intent intent = new Intent(this.cordova.getActivity(), GeocodingIntentService.class);
        intent.putExtra(GeocodingIntentService.RECEIVER, new AddressResultReceiver(callbackContext));
        intent.putExtra(GeocodingIntentService.LOCATION_DATA_EXTRA, location);
        intent.putExtra(GeocodingIntentService.OPTIONS_DATA_EXTRA, options.toString());
        this.cordova.getActivity().startService(intent);
    }


    /**
     * Forward geocode a given address to find coordinates
     * @param addressString String
     * @param options JSONObject
     * @param callbackContext CallbackContext
     */
    private void forwardGeocode(String addressString, JSONObject options, CallbackContext callbackContext) {
        if (addressString == null || addressString.length() == 0) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Expected a non-empty string argument.");
            callbackContext.sendPluginResult(r);
            return;
        }

        if (!Geocoder.isPresent()) {
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder is not present on this device/emulator.");
            callbackContext.sendPluginResult(r);
            return;
        }

        Intent intent = new Intent(this.cordova.getActivity(), GeocodingIntentService.class);
        intent.putExtra(GeocodingIntentService.RECEIVER, new AddressResultReceiver(callbackContext));
        intent.putExtra(GeocodingIntentService.ADDRESS_STRING_DATA_EXTRA, addressString);
        intent.putExtra(GeocodingIntentService.OPTIONS_DATA_EXTRA, options != null ? options.toString() : null);
        this.cordova.getActivity().startService(intent);
    }

    static class AddressResultReceiver extends ResultReceiver {

        private final CallbackContext callbackContext;

        AddressResultReceiver(CallbackContext callbackContext) {
            super(new Handler());
            this.callbackContext = callbackContext;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String result = resultData.getString(GeocodingIntentService.RESULT_DATA_KEY);
            PluginResult pluginResult;
            if (resultCode == GeocodingIntentService.FAILURE_RESULT) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, result);
            } else {
                try {
                    JSONArray json = new JSONArray(result);
                    pluginResult = new PluginResult(PluginResult.Status.OK, json);
                } catch (JSONException e) {
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, "can not parse result");
                }
            }
            this.callbackContext.sendPluginResult(pluginResult);
        }
    }
}
