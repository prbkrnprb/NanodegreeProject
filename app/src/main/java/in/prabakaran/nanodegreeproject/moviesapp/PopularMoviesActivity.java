package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import in.prabakaran.nanodegreeproject.R;

public class PopularMoviesActivity extends ActionBarActivity implements PopularMoviesFragment.Callback,MovieDetailFragment.Callback{

    Boolean mTwoPane;
    private final String LOG_TAG = PopularMoviesActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);

        if(!Utility.isOnline(this)) {
            Toast.makeText(this, "No Internet Connection Available. App may crash", Toast.LENGTH_LONG).show();
            Log.v(LOG_TAG, "No internet connectivity");
        }

        if (findViewById(R.id.container_movie_detail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_movie_detail, new MovieDetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beginDetailFragment(String movieId) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString(MovieDetailFragment.MOVIE_ID, movieId);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_movie_detail, movieDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.MOVIE_ID,movieId);
            startActivity(intent);
        }
    }

    @Override
    public void onTrailerReviewClick(Intent intent) {
        startActivity(intent);
    }
}
