package in.prabakaran.nanodegreeproject.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MovieDetailsEntry;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MoviePopularEntry;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MovieTopRatedEntry;

/**
 * Created by Prabakaran on 03-01-2016.
 */
public class MovieDetailsDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "movie_details.db";

    public MovieDetailsDbHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_DETAILS_TABLE = "CREATE TABLE " + MovieDetailsEntry.TABLE_NAME + " ("
                + MovieDetailsEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, "
                + MovieDetailsEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieDetailsEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + MovieDetailsEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, "
                + MovieDetailsEntry.COLUMN_POPULARITY + " DOUBLE NOT NULL, "
                + MovieDetailsEntry.COLUMN_VOTE_AVG + " DOUBLE NOT NULL, "
                + MovieDetailsEntry.COLUMN_VOTE_COUNT + " DOUBLE NOT NULL, "
                + MovieDetailsEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + MovieDetailsEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
                + MovieDetailsEntry.COLUMN_FAVOURITE + " BOOLEAN, "
                + ");";

        final String SQL_CREATE_MOVIE_POPULAR_TABLE = "CREATE TABLE " + MoviePopularEntry.TABLE_NAME + " ("
                + MoviePopularEntry.COLUMN_RANK + " INTEGER PRIMARY KEY, "
                + MoviePopularEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE, "

                + "FOREIGN KEY (" + MoviePopularEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieDetailsEntry.TABLE_NAME + " ("
                + MovieDetailsEntry.COLUMN_MOVIE_ID + ");";

        final String SQL_CREATE_MOVIE_TOP_RATED_TABLE = "CREATE TABLE " + MovieTopRatedEntry.TABLE_NAME + " ("
                + MovieTopRatedEntry.COLUMN_RANK + " INTEGER PRIMARY KEY, "
                + MovieTopRatedEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE, "

                + "FOREIGN KEY (" + MovieTopRatedEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieDetailsEntry.TABLE_NAME + " ("
                + MovieDetailsEntry.COLUMN_MOVIE_ID + ");";

        db.execSQL(SQL_CREATE_MOVIE_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_POPULAR_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TOP_RATED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDetailsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviePopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieTopRatedEntry.TABLE_NAME);

        onCreate(db);
    }
}
