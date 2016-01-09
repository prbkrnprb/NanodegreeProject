package in.prabakaran.nanodegreeproject.moviesapp;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Prabakaran on 11-10-2015.
 */
public class MovieData implements Serializable{
    String id;
    String title;
    String overview;
    String backdropPath;
    String posterPath;
    Double voteAverage;
    int voteCount;
    Double popularity;
    Date releaseDate;
    Bitmap bitmap;
    public MovieData(){
        id = "";
        title = "";
        overview = "";
        backdropPath = "";
        posterPath = "";
        voteAverage = 0.0;
        voteCount = 0;
        popularity = 0.0;
        releaseDate = new Date();
    }
}
