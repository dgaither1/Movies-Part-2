package com.dg.movies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Class to manage HTTP requests
public class HttpConnectionManager {

    private HttpConnectionDelegate delegate;

    // Set the delegate who will receive call backs
    public void setDelegate(HttpConnectionDelegate caller) {
        delegate = caller;
    }


    // Perform an HTTP Get request
    public void performGet(String urlString) {

        if(delegate != null) {
            AsyncTask task = new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] params) {
                    if (params[0] == null || params[0].equals("") || !(params[0] instanceof String)) {
                        Log.e(HttpConnectionManager.class.getName(), "Error - Invalid GET Input");  // No context guaranteed to use a string resource
                        return null;
                    }

                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    String responseJson = null;

                    try {
                        URL url = new URL((String) params[0]);

                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET"); // No context guaranteed to use a string resource
                        connection.connect();

                        InputStream inputStream = connection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }

                        if (buffer.length() == 0) {
                            return null;
                        }
                        responseJson = buffer.toString();

                    } catch (IOException e) {
                        Log.e(HttpConnectionManager.class.getName(), "Error", e); // No context guaranteed to use a string resource
                    } finally {
                        if (connection != null) {
                            connection.disconnect();

                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                                Log.e(HttpConnectionManager.class.getName(), "Error closing stream", e); // No context guaranteed to use a string resource
                            }
                        }
                    }

                    return responseJson;
                }

                @Override
                protected void onPostExecute(Object responseJson) {
                    if (responseJson != null && (responseJson instanceof String) && !responseJson.equals("")) {
                        // Notify listener of response Json
                        if(delegate != null) {
                            delegate.httpGetResponse((String) responseJson);
                        } else {
                            Log.e(HttpConnectionManager.class.getName(), "Error - Delegate no longer available"); // No context guaranteed to use a string resource
                        }
                    } else {
                        // Notify listener of no response
                        if(delegate != null) {
                            delegate.httpGetResponse(null);
                        } else {
                            Log.e(HttpConnectionManager.class.getName(), "Error - Delegate no longer available"); // No context guaranteed to use a string resource
                        }
                    }
                }
            };

            Object[] params = new Object[1];
            params[0] = urlString;

            task.execute(params);
        } else {
            // No delegate set
            Log.e(HttpConnectionManager.class.getName(), "Error - Delegate must be set first"); // No context guaranteed to use a string resource
        }

    }

    public interface HttpConnectionDelegate {
        public void httpGetResponse(String responseJson);
    }
}
