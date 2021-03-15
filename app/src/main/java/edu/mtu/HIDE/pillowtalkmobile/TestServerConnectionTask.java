package edu.mtu.HIDE.pillowtalkmobile;

import android.os.AsyncTask;
import android.util.Log;
import javax.net.ssl.HttpsURLConnection;

import static edu.mtu.HIDE.pillowtalkmobile.HTTPSManager.getHttpsClient;

public class TestServerConnectionTask extends AsyncTask<String, Integer, String> {

    AsyncResponse delegate = null;

    protected String doInBackground(String... urls) {
        try
        {
            Log.d("TESTING",  "hi");
            HttpsURLConnection client = getHttpsClient(urls[0]);
            if  (client.getResponseMessage() != null) Log.d("TESTING",  "asdasd");
            int responseCode = client.getResponseCode();

            //Log.d("TESTING: ", "" + client.getResponseCode() == null);

            return "" + responseCode;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("TESTING EXCEPTION", e.getMessage());
        }

        return "TestServerConnectionTask failed";
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        Log.d("TESTING: GET code: ", result);
        delegate.testServerConnectionTaskResponse(result);
    }


}
