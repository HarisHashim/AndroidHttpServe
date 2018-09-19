package com.example.haris.httpserve;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

class WebServer extends NanoHTTPD {

    private String TAG = getClass().getSimpleName();

    private WebServerListener listener;

    ArrayList<Integer> ids;
    ArrayList<Item> items = new ArrayList<>();

    public WebServer(int port, WebServerListener listener) {
        super(port);
        this.listener = listener;
        ids = new Gson().fromJson(App.get().prefsRead(App.ITEM_IDS, App.DEFAULT_IDS),
                new TypeToken<ArrayList<Integer>>(){}.getType());// new ArrayList<Integer>().getClass());

        Log.d(TAG, "NAMES: " + ids.toString());

        for (int index =0; index< ids.size(); index++) {
            String item_name = App.get().prefsRead(Integer.toString(ids.get(index)), "No Name");
            items.add(new Item(ids.get(index), item_name));
            Log.d(TAG, "ID: " + ids.get(index) + " NAMES: " + item_name);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> mapBody = new HashMap<String, String>();
        Method method = session.getMethod();
        try {
            session.parseBody(mapBody);
        } catch (IOException ioe) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
        }

        String postBody = "";
        if (mapBody != null && mapBody.size() > 0) {
            // get the POST body
            postBody = mapBody.get("postData");
        }

        String queryParameter = "";
        if (session != null && session.getQueryParameterString() != null) {
            // get query parameters
            queryParameter = session.getQueryParameterString().toString();
        }

        String uri = session.getUri();
        String responseString = "";
        int itemId = -1;
        HashMap<String, String> parameters = new HashMap<>();

        if (!queryParameter.isEmpty()) {
            String params[] = queryParameter.split("&");
            for (String param : params) {
                String paramPair[] = param.split("=");
                parameters.put(paramPair[0], paramPair[1]);
            }
        }

        try {
            if (listener != null)
                listener.onResponse("Responded to " + method + " on " + uri + ". \nBODY: " + postBody + "\nPARAMS: " + queryParameter);
        } catch (Exception ex) {
            Log.w(TAG, "ERROR! " + ex.getMessage());
        }

        String[] urls = uri.split("/");
        if (urls.length == 3) {
            itemId = parseIntWithDefault(urls[2], -1);
        }

        if (urls[1].equalsIgnoreCase("items")) {

            if (method.name().equalsIgnoreCase("GET")) {

                if (items.size() <= 0) {
                    responseString = "{}";
                    return newFixedLengthResponse(Response.Status.NO_CONTENT, "application/json", responseString);
                }

                if (urls.length == 2) {
                    if (parameters.size() <= 0) {
                        responseString = new Gson().toJson(items);
                    } else {
                        responseString = new Gson().toJson(items);
                        Log.d(TAG, "PARAMS" + parameters.toString());
                    }
                } else if (urls.length == 3) {
                    if (parameters.size() <= 0) {
                        if (itemId >= 0) {
                            try {
                                responseString = new Gson().toJson(items.get(itemId));
                            } catch (Exception ex) {
                                return newFixedLengthResponse(Response.Status.NOT_FOUND,
                                        NanoHTTPD.MIME_PLAINTEXT,
                                        "GET error. " + ex.getMessage());
                            }

                        } else {
                            return newFixedLengthResponse(Response.Status.NOT_FOUND,
                                    NanoHTTPD.MIME_PLAINTEXT,
                                    "Invalid item id. Less than 0!");
                        }
                    } else {
                        Log.d(TAG, "PARAMS is " + parameters.toString());
                        responseString = new Gson().toJson(items.get(itemId));
                    }
                }
            } else if (method.name().equalsIgnoreCase("POST")) {

                if (postBody.isEmpty()) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                            NanoHTTPD.MIME_PLAINTEXT,
                            "Body is empty for POST operation!");
                } else {
                    try {
                        Item postItem = new Gson().fromJson(postBody, Item.class);

                        int existingId = -1;

                        for (Item item : items) {
                            if (item.id == postItem.id) {
                                existingId = item.id;
                                break;
                            }
                        }

                        if (existingId > 0) {
                            items.get(existingId).name = postItem.name;
                        } else {
                            ids.add(postItem.id);
                            App.get().prefsWrite(App.ITEM_IDS, new Gson().toJson(ids,
                                    new TypeToken<ArrayList<Integer>>(){}.getType())); // new ArrayList<Integer>().getClass()));
                            items.add(postItem);
                        }

                        App.get().prefsWrite(Integer.toString(postItem.id), postItem.name);
                        Log.d(TAG, "SAVED: " + App.get().prefsRead(App.ITEM_IDS, App.DEFAULT_IDS));

                    } catch (Exception ex) {
                        Log.e(TAG, "Error in POST operation! " + ex.getMessage());
                        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                                NanoHTTPD.MIME_PLAINTEXT, "Error in POST operation! " + ex.getMessage());
                    }
                }


            }
        }

        Log.d(TAG, "Response String: " + responseString);
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", responseString);
    }

    private int parseIntWithDefault(String numberString, int defaultNumber) {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException nex) {
            return defaultNumber;
        }
    }

    public interface WebServerListener {
        public void onResponse(String message);
    }

}
