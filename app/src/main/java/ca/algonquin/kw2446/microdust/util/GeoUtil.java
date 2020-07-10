package ca.algonquin.kw2446.microdust.util;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GeoUtil {

    public interface GeoUtilListener {
        void onSuccess(double lat, double lng, String name);

        void onError(String message);
    }

    public static void getLocationFromName(Context context, final String city, final GeoUtilListener listener) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(city, 1);
            if (addresses.size() > 0) {
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();
                listener.onSuccess(lat, lng, city);
            } else {
                listener.onError("There is no the matched result of the address");
            }
        } catch (IOException e) {
            listener.onError(e.getMessage());
        }
    }

    public static void getFromLocation(Context context, final double lat, final double lng, final GeoUtilListener listener) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(lat,lng , 1);
            if (addresses.size() > 0) {
                String name = addresses.get(0).getLocality();

                listener.onSuccess(lat, lng, name);
            } else {
                listener.onError("There is no the matched result of the address");
            }
        } catch (IOException e) {
            listener.onError(e.getMessage());
        }
    }

}
