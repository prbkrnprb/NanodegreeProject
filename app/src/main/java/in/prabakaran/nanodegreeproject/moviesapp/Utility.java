package in.prabakaran.nanodegreeproject.moviesapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Prabakaran on 09-01-2016.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String API_KEY_VALUE = "cd558162b81cd17de17f02254ff262ff";
    public static final String API_KEY_PARM = "api_key";
    public static final String TMDB_API_BASE_URL = "http://api.themoviedb.org/3/movie";
    public static final String TMDB_API_TRAILER_PATH = "videos";
    public static final String TMDB_API_REVIEW_PATH = "reviews";
    public static final String TMDB_IMAGE_DOWNLOAD_URL = "http://image.tmdb.org/t/p/w500";

    public static int formatDateToYear(String inpDate){
        String patern = "EEE MMM d hh:mm:ss zzz yyyy";
        SimpleDateFormat format = new SimpleDateFormat(patern);
        try {
            Date date = format.parse(inpDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatRating(Double rate){
        return rate + "/10";
    }

    public static JSONArray getResultFromString(String string) throws JSONException{
        final String SINGLE_RESULTS = "results";
        JSONObject jsonObject = new JSONObject(string);
        return jsonObject.getJSONArray(SINGLE_RESULTS);
    }

    public static String getDataFromUri(Uri uri){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonServerValue = null;
        try {
            URL urlTrailer = new URL(uri.toString());
            urlConnection = (HttpURLConnection) urlTrailer.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
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
                Log.e(LOG_TAG, "No response from server");
                return null;
            }

            jsonServerValue = buffer.toString();
            Log.v(LOG_TAG, "JSON Value string: " + jsonServerValue);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final Exception e) {
                    Log.e(LOG_TAG, "Error closing streams", e);
                }
            }
        }
        return jsonServerValue;
    }
}


