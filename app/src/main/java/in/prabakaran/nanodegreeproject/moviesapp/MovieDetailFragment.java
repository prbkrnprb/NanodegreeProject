package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import in.prabakaran.nanodegreeproject.R;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    View rootView;

    public static final String MOVIE_ID = "movie_id";
    public static final int DETAIL_LOADER = 0;
    private String mMovieId;

    TextView titleTxt;
    TextView synopsisTxt;
    TextView releaseDate;
    TextView ratingTxt;
    ImageView posterImage;
    Button mFavButton;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        titleTxt = (TextView) rootView.findViewById(R.id.movieTitleTxt);
        synopsisTxt = (TextView) rootView.findViewById(R.id.movieSynopsisTxt);
        releaseDate = (TextView) rootView.findViewById(R.id.movieReleaseDateTxt);
        ratingTxt = (TextView) rootView.findViewById(R.id.movieRatingTxt);
        posterImage = (ImageView) rootView.findViewById(R.id.movieThumbnailImage);
        mFavButton = (Button) rootView.findViewById(R.id.favButton);

        Bundle args = getArguments();

        if(args != null) {
            mMovieId = args.getString(MOVIE_ID);
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

        mFavButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieDetailsContract.MovieDetailsEntry.buildMovieDetailsUri(Long.parseLong(mMovieId)),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            titleTxt.setText(data.getString(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_TITLE)));
            synopsisTxt.setText(data.getString(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_OVERVIEW)));
            int year = Utility.formatDateToYear(data.getString(
                    data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_RELEASE_DATE)));
            releaseDate.setText(year + "");
            ratingTxt.setText(Utility.formatRating(
                    data.getDouble(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_VOTE_AVG))));
            Picasso.with(getActivity()).load(PopularMoviesFragment.TMDB_IMAGE_DOWNLOAD_URL + data.getString(
                    data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_POSTER_PATH)
            )).into(posterImage);

            mFavButton.setText((data.getInt(data.getColumnIndex(MovieDetailsContract.MovieDetailsEntry.COLUMN_FAVOURITE)) == 0)?
            getString(R.string.fav_button_on) : getString(R.string.fav_button_off));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.favButton:
                ContentValues values = new ContentValues();

                Boolean value = mFavButton.getText() == getString(R.string.fav_button_on)? Boolean.TRUE : Boolean.FALSE;

                values.put(MovieDetailsContract.MovieDetailsEntry.COLUMN_FAVOURITE, value);

                int updatedRows = getActivity().getContentResolver().update(
                        MovieDetailsContract.MovieDetailsEntry.CONTENT_URI,
                        values,
                        MovieDetailsContract.MovieDetailsEntry.COLUMN_MOVIE_ID + " = " + mMovieId,
                        null
                        );

                if(updatedRows <=0)
                    Toast.makeText(getActivity(),"Error in adding to favorites list",Toast.LENGTH_LONG);
                else
                    mFavButton.setText(mFavButton.getText() == getString(R.string.fav_button_on)?
                        getString(R.string.fav_button_off):getString(R.string.fav_button_on));
        }

    }
}
