package in.prabakaran.nanodegreeproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesFragment extends Fragment {

    private ArrayAdapter<String> mMovieThumbnailAdapter;

    public PopularMoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mMovieThumbnailAdapter = new ArrayAdapter<String>(
                rootView.getContext(),
                R.layout.movie_thumbnail,
                R.id.movieThumbnail,
                new ArrayList<String>()
        );

        GridView movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
        movieGrid.setAdapter(mMovieThumbnailAdapter);

        populatePopularMovies();

        return rootView;
    }

    private void populatePopularMovies(){
        final String POPULAR_MOVIES = "popular";
        FetchMoviesTask populatePopularMovies = new FetchMoviesTask();
        populatePopularMovies.execute(POPULAR_MOVIES);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String[] getMovieIdsFromJSON(String jsonString){

            String resultString[];
            try {
                //final String TOTAL_RESULTS = "total_results";
                final String SINGLE_RESULTS = "results";
                final String MOVIE_ID = "id";

                JSONObject jsonObject = new JSONObject(jsonString);
                //int totalResults = Integer.parseInt(jsonObject.get(TOTAL_RESULTS).toString());
                JSONArray movieAllJSONArray = jsonObject.getJSONArray(SINGLE_RESULTS);
                resultString = new String[movieAllJSONArray.length()];

                for(int i=0; i<movieAllJSONArray.length();i++){
                    JSONObject movieJSONObject = movieAllJSONArray.getJSONObject(i);
                    resultString[i] = movieJSONObject.getString(MOVIE_ID);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG,"JSON Exception",e);
                return null;
            }
            return resultString;
        }

        @Override
        protected String[] doInBackground(String... params) {

            if(params.length == 0){
                Log.w(LOG_TAG, "No params");
                return null;
            }
            Log.v(LOG_TAG,"params : " + params[0]);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonServerValue = null;
            try{
                final String MOVIE_URI = "http://api.themoviedb.org/3/movie/" + params[0];
                final String API_KEY = "api_key";
                final String API_KEY_VALUE = "cd558162b81cd17de17f02254ff262ff";
                Uri uri = Uri.parse(MOVIE_URI).buildUpon()
                        .appendQueryParameter(API_KEY,API_KEY_VALUE)
                        .build();
                Log.v(LOG_TAG,"uri : " + uri.toString());

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
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
                    Log.w(LOG_TAG,"No response from server");
                    return null;
                }

                jsonServerValue = buffer.toString();
                Log.v(LOG_TAG, "JSON Value string: " + jsonServerValue);

            }catch(Exception e){
                Log.e(LOG_TAG,"Exception",e);
                return null;
            }finally{
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

            return getMovieIdsFromJSON(jsonServerValue);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mMovieThumbnailAdapter.clear();
                mMovieThumbnailAdapter.addAll(strings);
            }
        }
    }
}
