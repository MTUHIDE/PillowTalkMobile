package edu.mtu.HIDE.pillowtalkmobile;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class POSTRequestTask extends AsyncTask<String, Integer, String> {

    POSTRequestAsyncResponse delegate = null;

    /**
     *
     * @param postData Elements we need to pass in to do task. First parameter should be the url,
     *                 second should be the command we generate.
     * @return
     */
    protected String doInBackground(String... postData) {
        try
        {
            URL url = new URL(postData[0]);
            String command = postData[1];

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setConnectTimeout(5000);
            http.setRequestProperty("Content-Type", "application/json");

            byte[] out = command.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            return http.getResponseCode() + " " + http.getResponseMessage();
        }
        catch (Exception e)
        {
            Log.d("TESTING", "failed at doInBackground: " + e.getMessage());
        }

        return "POSTRequestTask failed";
    }

    protected void onProgressUpdate(Integer... progress) { }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        Log.d("TESTING: POST code: ", result);
        delegate.POSTRequestTaskResponse(result);
    }
}
