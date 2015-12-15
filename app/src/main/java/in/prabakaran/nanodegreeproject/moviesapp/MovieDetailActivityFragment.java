package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.prabakaran.nanodegreeproject.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    View rootView;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView titleTxt = (TextView) rootView.findViewById(R.id.movieTitleTxt);
        TextView synopsisTxt = (TextView) rootView.findViewById(R.id.movieSynopsisTxt);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.moveReleasedateTxt);
        TextView ratingTxt = (TextView) rootView.findViewById(R.id.movieRatingTxt);
        ImageView posterImage = (ImageView) rootView.findViewById(R.id.movieThumbnailImage);

        Intent intent = getActivity().getIntent();

        //MovieData movieData =  intent.getParcelableExtra("MovieData");
        //Bitmap bitmap = intent.getParcelableExtra("ImagePoster");
        MovieData movieData = StaticClassForIntent.movieData;
        Bitmap bitmap = StaticClassForIntent.bitmap;

        titleTxt.setText(movieData.title);
        synopsisTxt.setText(movieData.overview);
        releaseDate.setText(movieData.releaseDate.toString());
        ratingTxt.setText(movieData.popularity.toString());
        posterImage.setImageBitmap(bitmap);

        return rootView;
    }
}
