package in.prabakaran.nanodegreeproject.moviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MovieDetailsEntry;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MoviePopularEntry;
import in.prabakaran.nanodegreeproject.moviesapp.data.MovieDetailsContract.MovieTopRatedEntry;

/**
 * Created by Prabakaran on 03-01-2016.
 */
public class MovieDetailsProvider extends ContentProvider {

    private MovieDetailsDbHelper dbHelper;

    public static final int MOVIE_DETAILS_ALL = 100;
    public static final int MOVIE_DETAILS = 101;
    public static final int MOVIE_DETAILS_FAV = 102;
    public static final int MOVIE_DETAILS_POPULAR = 200;
    public static final int MOVIE_DETAILS_TOP_RATED = 300;

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(MovieDetailsContract.CONTENT_AUTHORITY,
                MovieDetailsContract.PATH_MOVIE_DETAILS, MOVIE_DETAILS_ALL);
        matcher.addURI(MovieDetailsContract.CONTENT_AUTHORITY,
                MovieDetailsContract.PATH_MOVIE_DETAILS + "/#", MOVIE_DETAILS);
        matcher.addURI(MovieDetailsContract.CONTENT_AUTHORITY,
                MovieDetailsContract.PATH_MOVIE_POPULAR, MOVIE_DETAILS_POPULAR);
        matcher.addURI(MovieDetailsContract.CONTENT_AUTHORITY,
                MovieDetailsContract.PATH_MOVIE_TOP_RATED, MOVIE_DETAILS_TOP_RATED);
        matcher.addURI(MovieDetailsContract.CONTENT_AUTHORITY,
                MovieDetailsContract.PATH_MOVIE_DETAILS + "/" + MovieDetailsContract.PATH_MOVIE_FAV, MOVIE_DETAILS_FAV);
    }

    private static final SQLiteQueryBuilder sMovieDetailsByPopular = new SQLiteQueryBuilder();
    static {
        sMovieDetailsByPopular.setTables(
                MovieDetailsEntry.TABLE_NAME + " INNER JOIN "
                        + MoviePopularEntry.TABLE_NAME + " ON "
                        + MovieDetailsEntry.TABLE_NAME + "."
                        + MovieDetailsEntry.COLUMN_MOVIE_ID + " = "
                        + MoviePopularEntry.TABLE_NAME + "."
                        + MoviePopularEntry.COLUMN_MOVIE_ID
        );
    }

    private static final SQLiteQueryBuilder sMovieDetailsByTopRated = new SQLiteQueryBuilder();
    static {
        sMovieDetailsByTopRated.setTables(
                MovieDetailsEntry.TABLE_NAME + " INNER JOIN "
                        + MovieTopRatedEntry.TABLE_NAME + " ON "
                        + MovieDetailsEntry.TABLE_NAME + "."
                        + MovieDetailsEntry.COLUMN_MOVIE_ID + " = "
                        + MovieTopRatedEntry.TABLE_NAME + "."
                        + MovieTopRatedEntry.COLUMN_MOVIE_ID
        );
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDetailsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;

        switch (matcher.match(uri)){
            case MOVIE_DETAILS_ALL:
                returnCursor = dbHelper.getReadableDatabase().query(
                        MovieDetailsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_DETAILS:
                returnCursor = dbHelper.getReadableDatabase().query(
                        MovieDetailsEntry.TABLE_NAME,
                        projection,
                        MovieDetailsEntry.COLUMN_MOVIE_ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_DETAILS_FAV:
                returnCursor = dbHelper.getReadableDatabase().query(
                        MovieDetailsEntry.TABLE_NAME,
                        projection,
                        MovieDetailsEntry.COLUMN_FAVOURITE + "= 1",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_DETAILS_POPULAR:
                returnCursor = sMovieDetailsByPopular.query(
                        dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        MoviePopularEntry.COLUMN_RANK + " ASC"
                );
                break;
            case MOVIE_DETAILS_TOP_RATED:
                returnCursor = sMovieDetailsByTopRated.query(
                        dbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        MovieTopRatedEntry.COLUMN_RANK + " ASC"
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)){
            case MOVIE_DETAILS_ALL:
                return MovieDetailsEntry.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MovieDetailsEntry.CONTENT_ITEM_TYPE;
            case MOVIE_DETAILS_FAV:
                return MovieDetailsEntry.CONTENT_TYPE;
            case MOVIE_DETAILS_POPULAR:
                return MoviePopularEntry.CONTENT_TYPE;
            case MOVIE_DETAILS_TOP_RATED:
                return MovieTopRatedEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (matcher.match(uri)){
            case MOVIE_DETAILS_ALL: {
                long _id = db.insert(MovieDetailsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieDetailsEntry.buildMovieDetailsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert into Movie Details table " + uri);
                }
                break;
            }
            case MOVIE_DETAILS_POPULAR: {
                long _id = db.insert(MoviePopularEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MoviePopularEntry.buildMoviePopularUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert into Movie Popular table " + uri);
                }
                break;
            }
            case MOVIE_DETAILS_TOP_RATED: {
                long _id = db.insert(MovieTopRatedEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieTopRatedEntry.buildMovieTopRatedUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert into Movie Top rated table " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnRows;

        switch (matcher.match(uri)){
            case MOVIE_DETAILS_ALL: {
                returnRows = db.delete(MovieDetailsEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            case MOVIE_DETAILS_POPULAR: {
                returnRows = db.delete(MoviePopularEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            case MOVIE_DETAILS_TOP_RATED: {
                returnRows = db.delete(MovieTopRatedEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if(selection == null || returnRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnRows;

        switch (matcher.match(uri)){
            case MOVIE_DETAILS_ALL: {
                returnRows = db.update(MovieDetailsEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case MOVIE_DETAILS_POPULAR: {
                returnRows = db.update(MoviePopularEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case MOVIE_DETAILS_TOP_RATED: {
                returnRows = db.update(MovieTopRatedEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if(returnRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnRows;
    }
}
