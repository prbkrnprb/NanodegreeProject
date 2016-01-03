package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import in.prabakaran.nanodegreeproject.R;

public class PopularMoviesActivity extends ActionBarActivity implements PopularMoviesFragment.Callback{


    Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beginDetailFragment() {
        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_movie_detail, new MovieDetailFragment())
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            startActivity(intent);
        }
    }
}
