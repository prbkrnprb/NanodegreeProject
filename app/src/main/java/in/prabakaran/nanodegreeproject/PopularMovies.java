package in.prabakaran.nanodegreeproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class PopularMovies extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}