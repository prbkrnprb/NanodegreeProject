package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.prabakaran.nanodegreeproject.R;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ListView.OnItemClickListener {

    View rootView,detailView;

    public static final String MOVIE_ID = "movie_id";
    public static final int DETAIL_LOADER = 0;
    public static final int TRAILER_LOADER = 1;
    public static final int REVIEW_LOADER = 2;
    private String mMovieId;

    TextView mTitleTextView;
    TextView mSynopsisTextView;
    TextView mReleaseDateTextView;
    TextView mRatingTextView;
    ImageView mPosterImageView;
    Button mFavButton;
    ListView mTrailerReviewListView;
    DetailsAdapter mDetailsAdapter;
    Cursor mTrailerCursor;

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        detailView = getActivity().getLayoutInflater().inflate(R.layout.detail_container, null);

        mTitleTextView = (TextView) detailView.findViewById(R.id.movieTitleTxt);
        mSynopsisTextView = (TextView) detailView.findViewById(R.id.movieSynopsisTxt);
        mReleaseDateTextView = (TextView) detailView.findViewById(R.id.movieReleaseDateTxt);
        mRatingTextView = (TextView) detailView.findViewById(R.id.movieRatingTxt);
        mPosterImageView = (ImageView) detailView.findViewById(R.id.movieThumbnailImage);
        mFavButton = (Button) detailView.findViewById(R.id.favButton);
        mTrailerReviewListView = (ListView) rootView.findViewById(R.id.trailerList);

        mDetailsAdapter = new DetailsAdapter(
                getActivity(),
                null,
                0
        );

        Bundle args = getArguments();

        if(args != null) {
            mMovieId = args.getString(MOVIE_ID);
            if(Utility.isOnline(getActivity()))
                new FetchAdvancedMoviesTask().execute(mMovieId);
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        }

        mFavButton.setOnClickListener(this);
        mTrailerReviewListView.addHeaderView(detailView);
        mTrailerReviewListView.setOnItemClickListener(this);
        mTrailerReviewListView.setAdapter(mDetailsAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case DETAIL_LOADER:
                return new CursorLoader(getActivity(),
                        MovieDetailsContract.MovieDetailsEntry.buildMovieDetailsUri(Long.parseLong(mMovieId)),
                        null,
                        null,
                        null,
                        null);
            case TRAILER_LOADER:
                return new CursorLoader(getActivity(),
                        MovieDetailsContract.MovieTrailersEntry.CONTENT_URI,
                        new String[]{
                                MovieDetailsContract.MovieTrailersEntry._ID,
                                MovieDetailsContract.MovieTrailersEntry.COLUMN_KEY,
                                MovieDetailsContract.MovieTrailersEntry.COLUMN_NAME
                        },
                        MovieDetailsContract.MovieTrailersEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{mMovieId},
                        null);
            case REVIEW_LOADER:
                return new CursorLoader(getActivity(),
                        MovieDetailsContract.MovieReviewsEntry.CONTENT_URI,
                        new String[]{
                                MovieDetailsContract.MovieReviewsEntry._ID,
                                MovieDetailsContract.MovieReviewsEntry.COLUMN_AUTHOR,
                                MovieDetailsContract.MovieReviewsEntry.COLUMN_REVIEW,
                                MovieDetailsContract.MovieReviewsEntry.COLUMN_URL
                        },
                        MovieDetailsContract.MovieReviewsEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{mMovieId},
                        null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()){
            case DETAIL_LOADER:
                if (data != null && data.moveToFirst()) {
                    mTitleTextView.setText(data.getString(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_TITLE)));
                    mSynopsisTextView.setText(data.getString(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_OVERVIEW)));
                    int year = Utility.formatDateToYear(data.getString(
                            data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_RELEASE_DATE)));
                    mReleaseDateTextView.setText(year + "");
                    mRatingTextView.setText(Utility.formatRating(
                            data.getDouble(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_VOTE_AVG))));
                    Picasso.with(getActivity()).load(Utility.TMDB_IMAGE_DOWNLOAD_URL + data.getString(
                            data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_POSTER_PATH)
                    )).into(mPosterImageView);

                    mFavButton.setText((data.getInt(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_FAVOURITE)) == 0)?
                            getString(R.string.fav_button_on) : getString(R.string.fav_button_off));
                }
                break;

            case TRAILER_LOADER:
                mTrailerCursor = data;
                getLoaderManager().initLoader(REVIEW_LOADER, null, this);
                break;

            case REVIEW_LOADER:
                MergeCursor mergeCursor = new MergeCursor(new Cursor[]{mTrailerCursor,data});
                mDetailsAdapter.swapCursor(mergeCursor);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case TRAILER_LOADER:
            case REVIEW_LOADER:
                mDetailsAdapter.swapCursor(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.favButton:
                ContentValues values = new ContentValues();

                Boolean value = mFavButton.getText() == getString(R.string.fav_button_on)? Boolean.TRUE : Boolean.FALSE;

                Log.v(LOG_TAG, "onClick movie id : " + mMovieId);
                Log.v(LOG_TAG, "onClick mFavButton.getText() : " + mFavButton.getText());

                values.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_FAVOURITE, value);

                int updatedRows = getActivity().getContentResolver().update(
                        MovieDetailsContract.MovieDetailsEntry.CONTENT_URI,
                        values,
                        MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = " + mMovieId,
                        null
                        );

                if(updatedRows <=0)
                    Toast.makeText(getActivity(),"Error in adding to favorites list",Toast.LENGTH_LONG).show();
                else
                    mFavButton.setText(mFavButton.getText() == getString(R.string.fav_button_on)?
                        getString(R.string.fav_button_off):getString(R.string.fav_button_on));

                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DetailsAdapter.TrailerViewHolder trailerViewHolder = (DetailsAdapter.TrailerViewHolder) view.getTag();
        String url="";
        switch (trailerViewHolder.type){
            case DetailsAdapter.TYPE_TRAILER:
                url = Utility.YOUTUBE_URI + trailerViewHolder.key;
                break;
            case DetailsAdapter.TYPE_REVIEW:
                url = trailerViewHolder.reviewUrlTxt.getText().toString();
                break;
        }

        Log.v(LOG_TAG, "onItemClick url : " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        ((Callback) getActivity()).onTrailerReviewClick(intent);
    }

    public interface Callback {
        void onTrailerReviewClick(Intent intent);
    }

    public class FetchAdvancedMoviesTask extends AsyncTask<String, Void, MovieAdvancedData> {

        private final String LOG_TAG = FetchAdvancedMoviesTask.class.getSimpleName();

        private MovieAdvancedData getMovieDataFromJSON(String jsonAdvanced, String jsonTrailer, String jsonReview) {

            MovieAdvancedData resultData = new MovieAdvancedData();

            try {
                final String MOVIE_RUN_TIME = "runtime";
                final String TRAILER_NAME = "name";
                final String TRAILER_KEY = "key";
                final String REVIEW_AUTHOR = "author";
                final String REVIEW_URL = "url";
                final String REVIEW_CONTENT = "content";
                final String _ID = "id";

                JSONObject jsonObject = new JSONObject(jsonAdvanced);
                resultData.runTime = jsonObject.getInt(MOVIE_RUN_TIME);

                JSONArray trailerJSONArray = Utility.getResultFromString(jsonTrailer);
                resultData.trailerId = new String[trailerJSONArray.length()];
                resultData.trailerKey = new String[trailerJSONArray.length()];
                resultData.trailerName = new String[trailerJSONArray.length()];
                for (int i = 0; i < trailerJSONArray.length(); i++) {
                    JSONObject movieJSONObject = trailerJSONArray.getJSONObject(i);
                    resultData.trailerId[i] = movieJSONObject.getString(_ID);
                    resultData.trailerKey[i] = movieJSONObject.getString(TRAILER_KEY);
                    resultData.trailerName[i] = movieJSONObject.getString(TRAILER_NAME);
                }

                JSONArray reviewJSONArray = Utility.getResultFromString(jsonReview);
                resultData.reviewId = new String[reviewJSONArray.length()];
                resultData.reviewAuthor = new String[reviewJSONArray.length()];
                resultData.reviewContent = new String[reviewJSONArray.length()];
                resultData.reviewUrl = new String[reviewJSONArray.length()];
                for (int i = 0; i < reviewJSONArray.length(); i++) {
                    JSONObject movieJSONObject = reviewJSONArray.getJSONObject(i);
                    resultData.reviewId[i] = movieJSONObject.getString(_ID);
                    resultData.reviewAuthor[i] = movieJSONObject.getString(REVIEW_AUTHOR);
                    resultData.reviewContent[i] = movieJSONObject.getString(REVIEW_CONTENT);
                    resultData.reviewUrl[i] = movieJSONObject.getString(REVIEW_URL);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Exception", e);
                return null;
            }
            return resultData;
        }

        @Override
        protected MovieAdvancedData doInBackground(String... params) {

            if (params.length == 0) {
                Log.w(LOG_TAG, "No params");
                return null;
            }
            Log.v(LOG_TAG, "params : " + params[0]);

            String jsonTrailerServerValue = null;
            String jsonReviewServerValue = null;
            String jsonAdvancedServerValue = null;
            Uri uri = Uri.parse(Utility.TMDB_API_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(Utility.TMDB_API_TRAILER_PATH)
                    .appendQueryParameter(Utility.API_KEY_PARM, Utility.API_KEY_VALUE)
                    .build();
            jsonTrailerServerValue = Utility.getDataFromUri(uri);

            Log.v(LOG_TAG, "Data from server Trailer data : " + jsonTrailerServerValue);

            uri = Uri.parse(Utility.TMDB_API_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(Utility.TMDB_API_REVIEW_PATH)
                    .appendQueryParameter(Utility.API_KEY_PARM, Utility.API_KEY_VALUE)
                    .build();

            jsonReviewServerValue = Utility.getDataFromUri(uri);

            Log.v(LOG_TAG, "Data from server Review data : " + jsonReviewServerValue);

            uri = Uri.parse(Utility.TMDB_API_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(Utility.API_KEY_PARM, Utility.API_KEY_VALUE)
                    .build();

            jsonAdvancedServerValue = Utility.getDataFromUri(uri);

            Log.v(LOG_TAG, "Data from server Advanced data : " + jsonAdvancedServerValue);

            MovieAdvancedData movieAdvancedData = getMovieDataFromJSON(jsonAdvancedServerValue,jsonTrailerServerValue,jsonReviewServerValue);
            movieAdvancedData.id = params[0];

            return movieAdvancedData;
        }

        @Override
        protected void onPostExecute(MovieAdvancedData movieAdvancedData) {
            addAdvancedMovieDetails(movieAdvancedData);
            addTrailerDetails(movieAdvancedData);
            addReviewDetails(movieAdvancedData);
        }
    }

    private boolean addAdvancedMovieDetails(MovieAdvancedData movieAdvancedData) {
        Cursor cursor = getActivity().getContentResolver().query(
                MovieDetailsContract.MovieDetailsEntry.buildMovieDetailsUri(Long.parseLong(movieAdvancedData.id)),
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_RUN_TIME, movieAdvancedData.runTime);
            int updatedRows = getActivity().getContentResolver().update(
                    MovieDetailsContract.MovieDetailsEntry.buildMovieDetailsUri(Long.parseLong(movieAdvancedData.id)),
                    values,
                    null,
                    null);

            Log.v(LOG_TAG, "addAdvancedMovieDetails updatedRows : " + updatedRows);

            if(updatedRows <= 0)
                return false;
            return true;
        } else {
            return false;
        }
    }

    private boolean addTrailerDetails(MovieAdvancedData movieAdvancedData) {
        for(int i = 0; i<movieAdvancedData.trailerKey.length; i++) {
            Cursor cursor = getActivity().getContentResolver().query(
                    MovieDetailsContract.MovieTrailersEntry.CONTENT_URI,
                    null,
                    MovieDetailsContract.MovieTrailersEntry._ID + "= ?",
                    new String[]{movieAdvancedData.trailerId[i]},
                    null);

            if (cursor.moveToFirst()) {
                continue;
            } else {
                ContentValues values = new ContentValues();
                values.put(MovieDetailsContract.MovieTrailersEntry._ID, movieAdvancedData.trailerId[i]);
                values.put(MovieDetailsContract.MovieTrailersEntry.COLUMN_KEY, movieAdvancedData.trailerKey[i]);
                values.put(MovieDetailsContract.MovieTrailersEntry.COLUMN_NAME, movieAdvancedData.trailerName[i]);
                values.put(MovieDetailsContract.MovieTrailersEntry.COLUMN_MOVIE_ID, movieAdvancedData.id);

                Uri uri = getActivity().getContentResolver().insert(MovieDetailsContract.MovieTrailersEntry.CONTENT_URI, values);

                Log.v(LOG_TAG, "addTrailerDetails uri : " + uri);

                continue;
            }
        }
        return true;
    }

    private boolean addReviewDetails(MovieAdvancedData movieAdvancedData) {
        for(int i = 0; i<movieAdvancedData.reviewId.length; i++) {
            Cursor cursor = getActivity().getContentResolver().query(
                    MovieDetailsContract.MovieReviewsEntry.CONTENT_URI,
                    null,
                    MovieDetailsContract.MovieReviewsEntry._ID + "= ?",
                    new String[]{movieAdvancedData.reviewId[i]},
                    null);

            if (cursor.moveToFirst()) {
                continue;
            } else {
                ContentValues values = new ContentValues();
                values.put(MovieDetailsContract.MovieReviewsEntry._ID, movieAdvancedData.reviewId[i]);
                values.put(MovieDetailsContract.MovieReviewsEntry.COLUMN_AUTHOR, movieAdvancedData.reviewAuthor[i]);
                values.put(MovieDetailsContract.MovieReviewsEntry.COLUMN_REVIEW, movieAdvancedData.reviewContent[i]);
                values.put(MovieDetailsContract.MovieReviewsEntry.COLUMN_URL, movieAdvancedData.reviewUrl[i]);
                values.put(MovieDetailsContract.MovieReviewsEntry.COLUMN_MOVIE_ID, movieAdvancedData.id);

                Uri uri = getActivity().getContentResolver().insert(MovieDetailsContract.MovieReviewsEntry.CONTENT_URI, values);

                Log.v(LOG_TAG, "addReviewDetails uri : " + uri);

                continue;
            }
        }
        return true;
    }
}
