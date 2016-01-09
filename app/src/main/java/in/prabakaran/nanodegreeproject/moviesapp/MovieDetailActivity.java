package in.prabakaran.nanodegreeproject.moviesapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import in.prabakaran.nanodegreeproject.R;

public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            String movieId = getIntent().getStringExtra(MovieDetailFragment.MOVIE_ID);
            Bundle args = new Bundle();
            args.putString(MovieDetailFragment.MOVIE_ID,movieId);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_movie_detail, movieDetailFragment)
                    .commit();
        }
    }

}
