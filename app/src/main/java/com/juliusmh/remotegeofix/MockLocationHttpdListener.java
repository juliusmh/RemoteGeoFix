package com.juliusmh.remotegeofix;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Julius on 7/18/2016.
 */
public class MockLocationHttpdListener extends NanoHTTPD {

    public static String KEY_CMD = "cmd";

    public static String KEY_LAT = "lat";
    public static String KEY_LON = "lng";
    public static String KEY_ALT = "alt";

    public static String elevation_api = "http://maps.googleapis.com/maps/api/elevation/json?locations=";


    // Interfaces for UI interaction and etc...
    public interface HttpdListener{
        public void onExit();
    }

    // GPS settings
    private int accuracy = 1; // in meters
    private boolean fetchAlt = true;

    // Current states
    private double alt, lat, lng = -1;

    // All Modules
    private HttpdListener mCallback;
    private Context context;
    private OkHttpClient httpc = new OkHttpClient();

    // Constructor
    public MockLocationHttpdListener(int port, Context c, HttpdListener callback) {
        super(port);
        this.mCallback = callback;
        this.context = c;

        MockLocationProvider.init(this.context);
        MockLocationProvider.register();

    }

    // The Listener
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (uri.startsWith("/api")){

            Map<String, List<String>> params = session.getParameters();

            if (!params.containsKey(KEY_CMD)) {
                return errorResponse(R.string.no_cmd_provided);
            }

            String cmd = params.get(KEY_CMD).get(0);

            switch(cmd){
                case "fix" :
                    if (!params.containsKey(KEY_LAT)) return errorResponse(R.string.no_lat_provided);
                    if (!params.containsKey(KEY_LON)) return errorResponse(R.string.no_lon_provided);

                    lat = Double.parseDouble(params.get(KEY_LAT).get(0));
                    lng = Double.parseDouble(params.get(KEY_LON).get(0));

                    if (fetchAlt){
                        Request request = new Request.Builder()
                                .url(elevation_api + lat + "," + lng)
                                .build();
                        try {
                            okhttp3.Response response = httpc.newCall(request).execute();
                            JSONObject jsonRootObject = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonRootObject.optJSONArray("results");
                            JSONObject firstResult = jsonArray.getJSONObject(0);

                            alt = Double.parseDouble(firstResult.getString("elevation"));
                        } catch (Exception e) {}
                    }else{
                        alt = 0;
                    }

                    MockLocationProvider.simulate(lng, lat, alt);
                    Log.e("beet", "Fix");
                    return okResponse();

                case "heartbeet" :
                    if (lng != -1 && lat != -1 && alt != -1){
                        MockLocationProvider.simulate(lng, lat, alt);
                        Log.e("beet", "Heartbeet");
                    }
                    return okResponse();

                case "exit" :
                    MockLocationProvider.unregister();
                    mCallback.onExit();
                    return okResponse();

                case "help" : break;
            }

            return errorResponse(R.string.unknown_error);

        }

        return serveFile(uri);
    }

    private Response serveFile(String uri){
        InputStream mbuffer = null;

        Log.d("fio","opening file " + uri.substring(1));

        try {
            if(uri!=null){

                if(uri.contains(".js")){
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new NanoHTTPD.Response(Response.Status.OK, MIME_JAVASCRIPT, mbuffer, -1);

                }else if(uri.contains(".css")){
                    mbuffer = context.getAssets().open(uri.substring(1));
                    return new NanoHTTPD.Response(Response.Status.OK, MIME_CSS, mbuffer, -1);

                }else{
                    mbuffer = context.getAssets().open("index.html");
                    return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, mbuffer, -1);
                }
            }

        } catch (IOException e) {}

        return errorResponse(R.string.unknown_error);

    }

    private Response errorResponse(int id){
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML,context.getString(id) );
    }

    private Response okResponse(){
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML,context.getString(R.string.status_ok) );
    }

}
