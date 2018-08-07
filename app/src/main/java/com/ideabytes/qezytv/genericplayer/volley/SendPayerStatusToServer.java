package com.ideabytes.qezytv.genericplayer.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by viplov on 18/10/16.
 */
public class SendPayerStatusToServer implements FolderAndURLConstants,DeviceConstants {
    private static String TAG = SendPayerStatusToServer.class.getSimpleName();
    private static Context context;
    private static RequestQueue mRequestQueue;
    public SendPayerStatusToServer(Context context1) {
        context = context1;
    }
    public static void sendStatus(final VolleyCallback volleyCallback, final JSONObject inputObject) {
        try {
            mRequestQueue = Volley.newRequestQueue(context);
            ///Log.v(TAG,"input to service "+inputObject);
           StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL_TO_POST_STATUS,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           try {
                               volleyCallback.onSuccess(new JSONObject(response).put("status","200"));
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                   }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,"server error "+error.getMessage());
                   try {
                       volleyCallback.onSuccess(new JSONObject("{status:404}"));
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           }) {
               @Override
               protected Map<String,String> getParams(){
                   Map<String,String> params = new HashMap<String, String>();
                   try {
                       params.put(CHANNEL_ID,inputObject.getString(CHANNEL_ID));
                       params.put(DATE_TIME,inputObject.getString(DATE_TIME));
                       params.put(DEVICE_ID,inputObject.getString(DEVICE_ID));
                       params.put(ACTIVITY_STATUS,inputObject.getString(ACTIVITY_STATUS));
                       params.put(INTERNET_SPEED,inputObject.getString(INTERNET_SPEED));
                       params.put(STREAM_STATUS,inputObject.getString(STREAM_STATUS));
                       params.put(VIDEO_TYPE,inputObject.getString(VIDEO_TYPE));
                       params.put(IP_ADDRESS,inputObject.getString(IP_ADDRESS));
                       params.put(ACCESS_TOKEN,inputObject.getString(ACCESS_TOKEN));
                       params.put(LOCATION,inputObject.getString(LOCATION));
                       params.put(GEO_LANGITUDE,inputObject.getString(GEO_LANGITUDE));
                       params.put(GEO_LATITUDE,inputObject.getString(GEO_LATITUDE));
                       params.put(CONSUMED_BIT_RATE,inputObject.getString(CONSUMED_BIT_RATE));

                       Log.i("Bit Rate",inputObject.getString(CONSUMED_BIT_RATE));
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   return params;
               }
           };
            mRequestQueue.add(stringRequest);
            //added by rajesh
            new Utils().deleteCache(SendPayerStatusToServer.context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void postConfirmation(final VolleyCallback volleyCallback, final JSONObject inputObject) {
            try {
                mRequestQueue = Volley.newRequestQueue(context);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL_TO_CONFIRM,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    volleyCallback.onSuccess(new JSONObject(response).put("status","200"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            volleyCallback.onFailure(new JSONObject("{status:404}"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }) {
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        try {
                            params.put(CHANNEL_ID,inputObject.getString(CHANNEL_ID));
                            params.put(DEVICE_ID,inputObject.getString(DEVICE_ID));
                            params.put(ACCESS_TOKEN,inputObject.getString(ACCESS_TOKEN));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return params;
                    }
                };
                mRequestQueue.add(stringRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void getBandWidthURL(final VolleyCallback volleyCallback)
    {
        try {
            mRequestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, SERVER_URL_TO_GET_BANDWIDTHCHECK_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG,"Get States Response = "+response);
                            try {

                                volleyCallback.onSuccess(new JSONObject(response).put("responseCode","200"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        volleyCallback.onFailure(new JSONObject("{responseCode:404}"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            mRequestQueue.add(stringRequest);
           // MySingleton.getInstance(context).addToRequestQueue(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public interface VolleyCallback {
        void onSuccess(JSONObject response);
        void onFailure(JSONObject response);
    }
}
