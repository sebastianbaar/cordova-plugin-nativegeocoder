package cordova.plugin.nativegeocoder;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

class NativeGeocoderOptions {
    boolean useLocale = true;
    String defaultLocale = null;
    int maxResults = 1;
}

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
            JSONObject options = null;
            try {
                options = args.getJSONObject(2);
            } catch (JSONException ignored) { }

            final JSONObject finalOptions = options;
            cordova.getThreadPool().execute(() -> reverseGeocode(latitude, longitude, finalOptions, callbackContext));
            return true;
        }

        if (action.equals("forwardGeocode")) {
            String addressString = args.getString(0);
            JSONObject options = null;
            try {
                options = args.getJSONObject(1);
            } catch (JSONException ignored) { }

            final JSONObject finalOptions = options;
            cordova.getThreadPool().execute(() -> forwardGeocode(addressString, finalOptions, callbackContext));
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

        NativeGeocoderOptions geocoderOptions = getNativeGeocoderOptions(options);
        geocoder = createGeocoderWithOptions(geocoderOptions);

        try {
            List<Address> geoResults = geocoder.getFromLocation(latitude, longitude, geocoderOptions.maxResults);
            if (geoResults.size() > 0) {
                int maxResultObjects = geoResults.size() >= geocoderOptions.maxResults ? geoResults.size() : geoResults.size();
                JSONArray resultObj = new JSONArray();

                for (int i = 0; i < maxResultObjects; i++) {
                    Address address = geoResults.get(i);

                    // https://developer.android.com/reference/android/location/Address.html
                    JSONObject placemark = new JSONObject();
                    placemark.put("latitude", !String.valueOf(address.getLatitude()).isEmpty() ? address.getLatitude() : "");
                    placemark.put("longitude", !String.valueOf(address.getLongitude()).isEmpty() ? address.getLongitude() : "");
                    placemark.put("countryCode", address.getCountryCode() != null ? address.getCountryCode() : "");
                    placemark.put("countryName", address.getCountryName() != null ? address.getCountryName() : "");
                    placemark.put("postalCode", address.getPostalCode() != null ? address.getPostalCode() : "");
                    placemark.put("administrativeArea", address.getAdminArea() != null ? address.getAdminArea() : "");
                    placemark.put("subAdministrativeArea", address.getSubAdminArea() != null ? address.getSubAdminArea() : "");
                    placemark.put("locality", address.getLocality() != null ? address.getLocality() : "");
                    placemark.put("subLocality", address.getSubLocality() != null ? address.getSubLocality() : "");
                    placemark.put("thoroughfare", address.getThoroughfare() != null ? address.getThoroughfare() : "");
                    placemark.put("subThoroughfare", address.getSubThoroughfare() != null ? address.getSubThoroughfare() : "");
                    placemark.put("areasOfInterest", address.getFeatureName() != null ? new JSONArray(new String[]{ address.getFeatureName()} ) : new JSONArray());

                    resultObj.put(placemark);
                }

                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, resultObj));
            } else {
                PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get an address.");
                callbackContext.sendPluginResult(r);
            }
        }
        catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e.getMessage().equals("grpc failed") && !isNetworkAvailable()) {
                errorMsg = "No Internet Access";
            }
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder:getFromLocationName Error: " + errorMsg);
            callbackContext.sendPluginResult(r);
        }
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

        NativeGeocoderOptions geocoderOptions = getNativeGeocoderOptions(options);
        geocoder = createGeocoderWithOptions(geocoderOptions);

        try {
            List<Address> geoResults = geocoder.getFromLocationName(addressString, geocoderOptions.maxResults);

            if (geoResults.size() > 0) {
                int maxResultObjects = geoResults.size() >= geocoderOptions.maxResults ? geoResults.size() : geoResults.size();
                JSONArray resultObj = new JSONArray();

                for (int i = 0; i < maxResultObjects; i++) {
                    Address address = geoResults.get(i);

                    try {
                        String latitude = String.valueOf(address.getLatitude());
                        String longitude = String.valueOf(address.getLongitude());

                        if (!latitude.isEmpty() && !longitude.isEmpty()) {
                            // https://developer.android.com/reference/android/location/Address.html
                            JSONObject placemark = new JSONObject();
                            placemark.put("latitude", latitude);
                            placemark.put("longitude", longitude);
                            placemark.put("countryCode", address.getCountryCode() != null ? address.getCountryCode() : "");
                            placemark.put("countryName", address.getCountryName() != null ? address.getCountryName() : "");
                            placemark.put("postalCode", address.getPostalCode() != null ? address.getPostalCode() : "");
                            placemark.put("administrativeArea", address.getAdminArea() != null ? address.getAdminArea() : "");
                            placemark.put("subAdministrativeArea", address.getSubAdminArea() != null ? address.getSubAdminArea() : "");
                            placemark.put("locality", address.getLocality() != null ? address.getLocality() : "");
                            placemark.put("subLocality", address.getSubLocality() != null ? address.getSubLocality() : "");
                            placemark.put("thoroughfare", address.getThoroughfare() != null ? address.getThoroughfare() : "");
                            placemark.put("subThoroughfare", address.getSubThoroughfare() != null ? address.getSubThoroughfare() : "");
                            placemark.put("areasOfInterest", address.getFeatureName() != null ? new JSONArray(new String[]{ address.getFeatureName() }) : new JSONArray());

                            resultObj.put(placemark);
                        }
                    }
                    catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }

                if (resultObj.length() == 0) {
                    PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot get latitude and/or longitude.");
                    callbackContext.sendPluginResult(r);
                } else {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, resultObj));
                }

            } else {
                PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Cannot find a location.");
                callbackContext.sendPluginResult(r);
            }
        }
        catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e.getMessage().equals("grpc failed") && !isNetworkAvailable()) {
                errorMsg = "No Internet Access";
            }
            PluginResult r = new PluginResult(PluginResult.Status.ERROR, "Geocoder:getFromLocationName Error: " + errorMsg);
            callbackContext.sendPluginResult(r);
        }
    }

    /**
     * Get network connection
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Get a valid NativeGeocoderOptions object
     * @param options JSONObject
     * @return NativeGeocoderOptions
     */
    private NativeGeocoderOptions getNativeGeocoderOptions(JSONObject options) {
        NativeGeocoderOptions geocoderOptions = new NativeGeocoderOptions();

        if (options != null) {
            try {
                geocoderOptions.useLocale = !options.has("useLocale") || options.getBoolean("useLocale");
            } catch (JSONException e) {
                geocoderOptions.useLocale = true;
            }

            if (options.has("defaultLocale")) {
                try {
                    geocoderOptions.defaultLocale = options.getString("defaultLocale");
                } catch (JSONException e) {
                    geocoderOptions.defaultLocale = null;
                }
            } else {
                geocoderOptions.defaultLocale = null;
            }
            if (options.has("maxResults")) {
                try {
                    geocoderOptions.maxResults = options.getInt("maxResults");
                } catch (JSONException e) {
                    geocoderOptions.maxResults = 1;
                }

                if (geocoderOptions.maxResults > 0) {
                    int MAX_RESULTS_COUNT = 5;
                    geocoderOptions.maxResults = Math.min(geocoderOptions.maxResults, MAX_RESULTS_COUNT);
                } else {
                    geocoderOptions.maxResults = 1;
                }
            } else {
                geocoderOptions.maxResults = 1;
            }
        } else {
            geocoderOptions.useLocale = true;
            geocoderOptions.defaultLocale = null;
            geocoderOptions.maxResults = 1;
        }

        return geocoderOptions;
    }

    /**
     * Create a Geocoder with NativeGeocoderOptions
     * @param geocoderOptions NativeGeocoderOptions
     * @return Geocoder
     */
    private Geocoder createGeocoderWithOptions(NativeGeocoderOptions geocoderOptions) {
        if (geocoderOptions.defaultLocale != null && !geocoderOptions.defaultLocale.isEmpty()) {
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                locale = Locale.forLanguageTag(geocoderOptions.defaultLocale);
            } else {
                String[] parts = geocoderOptions.defaultLocale.split("[-_]", -1);
                if (parts.length == 1)
                    locale = new Locale(parts[0]);
                else if (parts.length == 2 || (parts.length == 3 && parts[2].startsWith("#")))
                    locale = new Locale(parts[0], parts[1]);
                else
                    locale = new Locale(parts[0], parts[1], parts[2]);
            }
            geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), locale);
        } else {
            if (geocoderOptions.useLocale) {
                geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), Locale.getDefault());
            } else {
                geocoder = new Geocoder(cordova.getActivity().getApplicationContext(), Locale.ENGLISH);
            }
        }
        return geocoder;
    }
}
