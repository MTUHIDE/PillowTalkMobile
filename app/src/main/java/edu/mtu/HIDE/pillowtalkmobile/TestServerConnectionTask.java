package edu.mtu.HIDE.pillowtalkmobile;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class TestServerConnectionTask extends AsyncTask<String, Integer, String> {

    TestServerConnectionAsyncResponse delegate = null;

    protected String doInBackground(String... urls) {
        try
        {
            //URL url = new URL("http://47.6.26.69:443/server_connection_test");
            URL url = new URL(urls[0]);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setConnectTimeout(5000);

            return http.getResponseCode() + " " + http.getResponseMessage();
        }
        catch (Exception e)
        {
            Log.d("TESTING", "failed at doInBackground: " + e.getMessage());
        }

        return "TestServerConnectionTask failed";
    }

    protected void onProgressUpdate(Integer... progress) { }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        Log.d("TESTING: GET code: ", result);
        delegate.testServerConnectionTaskResponse(result);
    }


}
