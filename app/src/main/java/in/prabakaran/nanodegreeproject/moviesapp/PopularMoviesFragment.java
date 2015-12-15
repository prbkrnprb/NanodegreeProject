package in.prabakaran.nanodegreeproject.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import in.prabakaran.nanodegreeproject.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesFragment extends Fragment implements AdapterView.OnItemClickListener {

    MovieData movieData[];
    View rootView;
    private final String POPULAR_MOVIES = "popular";
    private final String TOP_RATED_MOVIES = "top_rated";
    FetchMoviesTask fetchMoviesTask;

    private MovieThumbnailAdapter mMovieThumbnailAdapter;

    public PopularMoviesFragment() {
        movieData = new MovieData[0];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mMovieThumbnailAdapter = new MovieThumbnailAdapter(
                rootView.getContext(),
                R.id.movieThumbnail,
                movieData
        );

        GridView movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
        movieGrid.setAdapter(mMovieThumbnailAdapter);
        movieGrid.setOnItemClickListener(this);


        //refreshData(getSortOrderFromPref());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshData(getSortOrderFromPref());
    }

    private String getSortOrderFromPref(){
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));
    }

    private void refreshData(String contentType){
        fetchMoviesTask= new FetchMoviesTask();
        fetchMoviesTask.execute(contentType);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieThumbnailViewHolder holder= (MovieThumbnailViewHolder) view.getTag();
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        StaticClassForIntent.movieData = holder.movieData;
        StaticClassForIntent.bitmap = holder.bitmap;
       //intent.putExtra("MovieData", holder.movieData);
       //intent.putExtra("ImagePoster",holder.bitmap);
        startActivity(intent);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private MovieData[] getMovieDataFromJSON(String jsonString){

            MovieData resultData[];
            try {
                //final String TOTAL_RESULTS = "total_results";
                final String SINGLE_RESULTS = "results";
                final String MOVIE_ID = "id";
                final String MOVIE_TITLE = "title";
                final String MOVIE_OVERVIEW = "overview";
                final String MOVIE_POPULARITY = "popularity";
                final String MOVIE_RELEASE_DATE = "release_date";
                final String MOVIE_POSTER_PATH = "poster_path";
                final String MOVIE_VOTE_AVERAGE = "vote_average";
                final String MOVIE_VOTE_COUNT = "vote_count";
                final String MOVIE_BACKDROP_PATH = "backdrop_path";

                JSONObject jsonObject = new JSONObject(jsonString);
                //int totalResults = Integer.parseInt(jsonObject.get(TOTAL_RESULTS).toString());
                JSONArray movieAllJSONArray = jsonObject.getJSONArray(SINGLE_RESULTS);
                resultData = new MovieData[movieAllJSONArray.length()];
                //resultData = new MovieData[2];
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                for(int i=0; i<movieAllJSONArray.length();i++){
                //for(int i=0; i<2;i++){
                    JSONObject movieJSONObject = movieAllJSONArray.getJSONObject(i);
                    resultData[i] = new MovieData();
                    resultData[i].id = movieJSONObject.getString(MOVIE_ID);
                    resultData[i].title = movieJSONObject.getString(MOVIE_TITLE);
                    resultData[i].overview = movieJSONObject.getString(MOVIE_OVERVIEW);
                    resultData[i].popularity = movieJSONObject.getDouble(MOVIE_POPULARITY);
                    resultData[i].releaseDate = dateFormat.parse(movieJSONObject.getString(MOVIE_RELEASE_DATE));
                    resultData[i].posterPath = movieJSONObject.getString(MOVIE_POSTER_PATH);
                    resultData[i].voteAverage = movieJSONObject.getDouble(MOVIE_VOTE_AVERAGE);
                    resultData[i].voteCount = movieJSONObject.getInt(MOVIE_VOTE_COUNT);
                    resultData[i].backdropPath = movieJSONObject.getString(MOVIE_BACKDROP_PATH);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG,"JSON Exception",e);
                return null;
            }catch (ParseException e) {
                Log.e(LOG_TAG,"Parse Exception",e);
                return null;
            }
            return resultData;
        }

        @Override
        protected MovieData[] doInBackground(String... params) {

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

            return getMovieDataFromJSON(jsonServerValue);
        }

        @Override
        protected void onPostExecute(MovieData[] movieDatas) {
            mMovieThumbnailAdapter.replaceAll(movieDatas);
        }
    }

    public class MovieThumbnailAdapter extends ArrayAdapter{

        MovieData movieData[];
        LayoutInflater inflator;

        public MovieThumbnailAdapter(Context context, int resource, MovieData[] movieData) {
            super(context, resource, movieData);
            this.movieData = movieData;
            inflator = ((Activity)context).getLayoutInflater();
        }

        @Override
        public int getCount() {
            return movieData.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MovieThumbnailViewHolder viewHolder;
            if(convertView == null){
                convertView = inflator.inflate(R.layout.movie_thumbnail,null);
                viewHolder = new MovieThumbnailViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.movieThumbnail);
                convertView.setTag(viewHolder);
            }

            viewHolder = (MovieThumbnailViewHolder) convertView.getTag();
            viewHolder.url = movieData[position].posterPath;
            viewHolder.movieData = movieData[position];
            new MovieThumnailDownload().execute(viewHolder);
            return convertView;
        }

        public void addAll(MovieData[] movieData){
            MovieData[] tempData = new MovieData[this.movieData.length + movieData.length];

            int i;
            for(i=0 ; i<this.movieData.length; i++)
                tempData[i] = this.movieData[i];

            for(int j=0 ; j<movieData.length; j++,i++)
                tempData[i] = movieData[j];

            this.movieData = tempData;
            this.notifyDataSetChanged();
        }

        public void replaceAll(MovieData[] movieData){
            this.movieData = movieData;
            this.notifyDataSetChanged();
        }

        public class MovieThumnailDownload extends AsyncTask<MovieThumbnailViewHolder,Void,MovieThumbnailViewHolder>{

            private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

            @Override
            protected MovieThumbnailViewHolder doInBackground(MovieThumbnailViewHolder... params) {
                MovieThumbnailViewHolder resultHolder = params[0];
                try{
                    URL url = new URL("http://image.tmdb.org/t/p/w500" + resultHolder.url);
                    resultHolder.bitmap = BitmapFactory.decodeStream(url.openStream());
                }catch (IOException e){
                    Log.e(LOG_TAG,"Error downloading image", e);
                }
                return resultHolder;
            }

            @Override
            protected void onPostExecute(MovieThumbnailViewHolder movieThumbnailViewHolder) {
                movieThumbnailViewHolder.imageView.setImageBitmap(movieThumbnailViewHolder.bitmap);
            }
        }
    }
}


class MovieThumbnailViewHolder{
    ImageView imageView;
    String url;
    Bitmap bitmap;
    MovieData movieData;
}
