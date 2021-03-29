package edu.mtu.HIDE.pillowtalkmobile;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class POSTRequestTask extends AsyncTask<String, Integer, String> {

    POSTRequestAsyncResponse delegate = null;

    protected String doInBackground(String... postData) {
        try
        {
            URL url = new URL(postData[0]);
            String command = postData[1];

            //URL url = new URL("http://47.6.26.69:443/command");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setConnectTimeout(5000);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //String data = "command=inflate%20cushion_1%206";
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
