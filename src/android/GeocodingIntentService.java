package cordova.plugin.nativegeocoder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


class NativeGeocoderOptions {
    boolean useLocale = true;
    String defaultLocale = null;
    int maxResults = 1;
}

public class GeocodingIntentService extends IntentService {
    public static final String OPTIONS_DATA_EXTRA = "NATIVE_GEOCODER_OPTIONS_DATA_EXTRA";
    public static final String LOCATION_DATA_EXTRA = "NATIVE_GEOCODER_LOCATION_DATA_EXTRA";
    public static final String ADDRESS_STRING_DATA_EXTRA = "NATIVE_GEOCODER_ADDRESS_STRING_DATA_EXTRA";
    public static final String RECEIVER = "NATIVE_GEOCODER_RECEIVER";
    public static final int FAILURE_RESULT = 1;
    public static final int SUCCESS_RESULT = 0;
    public static final String RESULT_DATA_KEY = "NATIVE_GEOCODER_RECEIVER_RESULT_KEY";

    public GeocodingIntentService() {
        super("GeocodingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
        String options = intent.getStringExtra(OPTIONS_DATA_EXTRA);
        String addressString = intent.getStringExtra(ADDRESS_STRING_DATA_EXTRA);
        ResultReceiver mReceiver = intent.getParcelableExtra(RECEIVER);

        NativeGeocoderOptions geocoderOptions = getNativeGeocoderOptions(options);

        try {
            if (location != null) {
                reverseGeocode(mReceiver, location, geocoderOptions);
            } else {
                forwardGeocode(mReceiver, addressString, geocoderOptions);
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e.getMessage().equals("grpc failed") && !isNetworkAvailable()) {
                errorMsg = "No Internet Access";
            }
            deliverResultToReceiver(mReceiver, FAILURE_RESULT, "Geocoder:getFromLocationName Error: " + errorMsg);
        }
    }

    /**
     * Reverse geocode a given location to find location address
     * @param mReceiver ResultReceiver
     * @param location Location
     * @param geocoderOptions NativeGeocoderOptions
     */
    private void reverseGeocode(ResultReceiver mReceiver, Location location, NativeGeocoderOptions geocoderOptions) throws IOException {
        Geocoder geocoder = createGeocoderWithOptions(geocoderOptions);
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),
                geocoderOptions.maxResults);
        if (addresses.size() > 0) {
            JSONArray jsonAddresses = this.addressesToJSONArray(addresses, geocoderOptions.maxResults, false);
            deliverResultToReceiver(mReceiver, SUCCESS_RESULT, jsonAddresses.toString());
        } else {
            deliverResultToReceiver(mReceiver, FAILURE_RESULT, "Cannot get an address.");
        }
    }

    /**
     * Forward geocode a given address to find coordinates
     * @param mReceiver ResultReceiver
     * @param addressString String
     * @param geocoderOptions NativeGeocoderOptions
     */
    private void forwardGeocode(ResultReceiver mReceiver, String addressString, NativeGeocoderOptions geocoderOptions) throws IOException {
        Geocoder geocoder = createGeocoderWithOptions(geocoderOptions);
        List<Address> addresses = geocoder.getFromLocationName(addressString, geocoderOptions.maxResults);
        if (addresses.size() > 0) {
            JSONArray jsonAddresses = this.addressesToJSONArray(addresses, geocoderOptions.maxResults, true);
            if (jsonAddresses.length() > 0) {
                deliverResultToReceiver(mReceiver, SUCCESS_RESULT, jsonAddresses.toString());
            } else {
                deliverResultToReceiver(mReceiver, FAILURE_RESULT, "Cannot get latitude and/or longitude.");
            }
        } else {
            deliverResultToReceiver(mReceiver, FAILURE_RESULT, "Cannot find a location.");
        }
    }

    /**
     * Send the decoding result back to the receiver
     * @param mReceiver ResultReceiver
     * @param resultCode int
     * @param message String
     */
    private void deliverResultToReceiver(ResultReceiver mReceiver, int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    /**
     * Convert a list of addresses to a JSONArray
     * @param addresses List<Address>
     * @param maxResults int
     * @param skipEmptyLocationResults boolean
     * @return JSONArray
     */
    private JSONArray addressesToJSONArray(List<Address> addresses, int maxResults, boolean skipEmptyLocationResults) {
        JSONArray resultArray = new JSONArray();
        int maxResultObjects = Math.min(addresses.size(), maxResults);

        for (int i = 0; i < maxResultObjects; i++) {
            Address address = addresses.get(i);

            try {
                String latitude = String.valueOf(address.getLatitude());
                String longitude = String.valueOf(address.getLongitude());

                boolean hasLocation = !latitude.isEmpty() && !longitude.isEmpty();
                if (hasLocation || !skipEmptyLocationResults) {
                    // https://developer.android.com/reference/android/location/Address.html
                    JSONObject placemark = new JSONObject();
                    placemark.put("latitude", latitude.isEmpty() ? "" : latitude);
                    placemark.put("longitude", longitude.isEmpty() ? "" : longitude);
                    placemark.put("countryCode", address.getCountryCode() != null ? address.getCountryCode() : "");
                    placemark.put("countryName", address.getCountryName() != null ? address.getCountryName() : "");
                    placemark.put("postalCode", address.getPostalCode() != null ? address.getPostalCode() : "");
                    placemark.put("administrativeArea", address.getAdminArea() != null ? address.getAdminArea() : "");
                    placemark.put("subAdministrativeArea", address.getSubAdminArea() != null ? address.getSubAdminArea() : "");
                    placemark.put("locality", address.getLocality() != null ? address.getLocality() : "");
                    placemark.put("subLocality", address.getSubLocality() != null ? address.getSubLocality() : "");
                    placemark.put("thoroughfare", address.getThoroughfare() != null ? address.getThoroughfare() : "");
                    placemark.put("subThoroughfare", address.getSubThoroughfare() != null ? address.getSubThoroughfare() : "");
                    placemark.put("areasOfInterest", address.getFeatureName() != null ? new JSONArray(new String[]{address.getFeatureName()}) : new JSONArray());

                    resultArray.put(placemark);
                }
            } catch (JSONException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        return resultArray;
    }

    /**
     * Get a valid NativeGeocoderOptions object
     *
     * @param stringOptions String
     * @return NativeGeocoderOptions
     */
    private NativeGeocoderOptions getNativeGeocoderOptions(String stringOptions) {
        NativeGeocoderOptions geocoderOptions = new NativeGeocoderOptions();

        if (stringOptions != null) {
            try {
                JSONObject options = new JSONObject(stringOptions);
                geocoderOptions.useLocale = !options.has("useLocale") || options.getBoolean("useLocale");
                if (options.has("defaultLocale")) {
                    geocoderOptions.defaultLocale = options.getString("defaultLocale");
                } else {
                    geocoderOptions.defaultLocale = null;
                }
                if (options.has("maxResults")) {
                    geocoderOptions.maxResults = options.getInt("maxResults");

                    if (geocoderOptions.maxResults > 0) {
                        int MAX_RESULTS_COUNT = 5;
                        geocoderOptions.maxResults = Math.min(geocoderOptions.maxResults, MAX_RESULTS_COUNT);
                    } else {
                        geocoderOptions.maxResults = 1;
                    }

                } else {
                    geocoderOptions.maxResults = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return geocoderOptions;
    }

    /**
     * Create a Geocoder with NativeGeocoderOptions
     *
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
            return new Geocoder(this, locale);
        } else {
            if (geocoderOptions.useLocale) {
                return new Geocoder(this, Locale.getDefault());
            } else {
                return new Geocoder(this, Locale.ENGLISH);
            }
        }
    }

    /**
     * Get network connection
     *
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
