package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import in.prabakaran.nanodegreeproject.R;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MovieDetailsEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    MovieData movieData[];
    View rootView;
    public final static String TMDB_IMAGE_DOWNLOAD_URL = "http://image.tmdb.org/t/p/w500";
    FetchMoviesTask fetchMoviesTask;
    final String CURR_POSITION = "current position";
    int mPosition;
    Context mContext;
    private static final int MOVIE_DETAILS_LOADER = 0;
    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();

    public final static String SORT_ORDER_FAVORITE = "favorite";
    public final static String SORT_ORDER_POPULAR = "popular";
    public final static String SORT_ORDER_TOP_RATED = "top_rated";

    private MovieThumbnailAdapter mMovieThumbnailAdapter;

    public PopularMoviesFragment() {
        movieData = new MovieData[0];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mContext = rootView.getContext();

        mMovieThumbnailAdapter = new MovieThumbnailAdapter(
                mContext,
                null,
                0
        );

        GridView movieGrid = (GridView) rootView.findViewById(R.id.movieGrid);
        movieGrid.setAdapter(mMovieThumbnailAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURR_POSITION)) {
            movieGrid.setVerticalScrollbarPosition(savedInstanceState.getInt(CURR_POSITION));
        }

        movieGrid.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshData(getSortOrderFromPref());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private String getSortOrderFromPref() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_default));
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIE_DETAILS_LOADER, null, this);
    }

    private void refreshData(String contentType) {
        if(!contentType.equals(SORT_ORDER_FAVORITE)) {
            fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(contentType);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(CURR_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        switch (getSortOrderFromPref()){
            case SORT_ORDER_FAVORITE:
                uri = MovieDetailsEntry.CONTENT_FAV_URI;
                break;
            case SORT_ORDER_POPULAR:
                uri = MovieDetailsContract.MoviePopularEntry.CONTENT_URI;
                break;
            case SORT_ORDER_TOP_RATED:
                uri = MovieDetailsContract.MovieTopRatedEntry.CONTENT_URI;
                break;
            default:
                uri = MovieDetailsEntry.CONTENT_URI;
        }

        return new CursorLoader(
                mContext,
                uri,
                new String[]{
                        MovieDetailsEntry.COLUMN_MOVIE_ID,
                        MovieDetailsEntry.COLUMN_POSTER_PATH},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieThumbnailAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieThumbnailAdapter.swapCursor(null);
    }

    public interface Callback {
        void beginDetailFragment(String movieId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieThumbnailAdapter.MovieViewHolder holder = (MovieThumbnailAdapter.MovieViewHolder) view.getTag();
        mPosition = position;
        ((Callback) getActivity()).beginDetailFragment(holder.movieId);
    }

    private boolean addMovieDetails(MovieData movieData) {
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailsEntry.buildMovieDetailsUri(Long.parseLong(movieData.id)),
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(MovieDetailsEntry.COLUMN_MOVIE_ID, Integer.parseInt(movieData.id));
            values.put(MovieDetailsEntry.COLUMN_TITLE, movieData.title);
            values.put(MovieDetailsEntry.COLUMN_OVERVIEW, movieData.overview);
            values.put(MovieDetailsEntry.COLUMN_RELEASE_DATE, movieData.releaseDate.toString());
            values.put(MovieDetailsEntry.COLUMN_POPULARITY, movieData.popularity);
            values.put(MovieDetailsEntry.COLUMN_VOTE_AVG, movieData.voteAverage);
            values.put(MovieDetailsEntry.COLUMN_VOTE_COUNT, movieData.voteCount);
            values.put(MovieDetailsEntry.COLUMN_POSTER_PATH, movieData.posterPath);
            values.put(MovieDetailsEntry.COLUMN_BACKDROP_PATH, movieData.backdropPath);

            values.put(MovieDetailsEntry.COLUMN_FAVOURITE, Boolean.FALSE);

            Uri uri = mContext.getContentResolver().insert(MovieDetailsEntry.CONTENT_URI, values);
            Log.v(LOG_TAG, "Movie details inserted : " + ContentUris.parseId(uri));

            return true;
        }
    }

    private boolean addMovieSortDetails(String sortTable, String movieId, int rank) {
        Uri uri;
        ContentValues contentValues = new ContentValues();
        switch (sortTable){
            case SORT_ORDER_POPULAR:
                if(rank == 1)
                    mContext.getContentResolver().delete(MovieDetailsContract.MoviePopularEntry.CONTENT_URI,null,null);
                contentValues.put(MovieDetailsContract.MoviePopularEntry.COLUMN_MOVIE_ID,movieId);
                contentValues.put(MovieDetailsContract.MoviePopularEntry.COLUMN_RANK,rank);
                uri = mContext.getContentResolver().insert(MovieDetailsContract.MoviePopularEntry.CONTENT_URI,contentValues);
                break;
            case SORT_ORDER_TOP_RATED:
                if(rank == 1)
                    mContext.getContentResolver().delete(MovieDetailsContract.MovieTopRatedEntry.CONTENT_URI,null,null);
                contentValues.put(MovieDetailsContract.MovieTopRatedEntry.COLUMN_MOVIE_ID,movieId);
                contentValues.put(MovieDetailsContract.MovieTopRatedEntry.COLUMN_RANK,rank);
                uri = mContext.getContentResolver().insert(MovieDetailsContract.MovieTopRatedEntry.CONTENT_URI,contentValues);
                break;
            default:
                Log.e(LOG_TAG, "Movie sort details failed to inserted : " + movieId + " " + sortTable);
                return false;
        }
        Log.v(LOG_TAG, "Movie sort details inserted : " + ContentUris.parseId(uri));
        return true;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private MovieData[] getMovieDataFromJSON(String jsonString) {

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
                JSONArray movieAllJSONArray = jsonObject.getJSONArray(SINGLE_RESULTS);
                resultData = new MovieData[movieAllJSONArray.length()];
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                for (int i = 0; i < movieAllJSONArray.length(); i++) {
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
                Log.e(LOG_TAG, "JSON Exception", e);
                return null;
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parse Exception", e);
                return null;
            }
            return resultData;
        }

        @Override
        protected MovieData[] doInBackground(String... params) {

            if (params.length == 0) {
                Log.w(LOG_TAG, "No params");
                return null;
            }
            Log.v(LOG_TAG, "params : " + params[0]);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonServerValue = null;
            try {
                final String MOVIE_URI = "http://api.themoviedb.org/3/movie/" + params[0];
                final String API_KEY = "api_key";
                final String API_KEY_VALUE = "cd558162b81cd17de17f02254ff262ff";
                Uri uri = Uri.parse(MOVIE_URI).buildUpon()
                        .appendQueryParameter(API_KEY, API_KEY_VALUE)
                        .build();
                Log.v(LOG_TAG, "uri : " + uri.toString());

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
                    Log.w(LOG_TAG, "No response from server");
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

            return getMovieDataFromJSON(jsonServerValue);
        }

        @Override
        protected void onPostExecute(MovieData[] movieDatas) {
            String sortOrder = getSortOrderFromPref();
            for(int i=0; i<movieDatas.length; i++) {
                addMovieDetails(movieDatas[i]);
                addMovieSortDetails(sortOrder,movieDatas[i].id,i+1);
            }
        }

    }


}


