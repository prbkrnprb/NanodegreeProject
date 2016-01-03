package in.prabakaran.nanodegreeproject.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Prabakaran on 03-01-2016.
 */
public class MovieDetailsContract {

    public static final String CONTENT_AUTHORITY = "in.prabakaran.nanodegreeproject.moviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE_DETAILS = "movie_details";
    public static final String PATH_MOVIE_POPULAR = "movie_popular";
    public static final String PATH_MOVIE_TOP_RATED = "movie_top_rated";
    public static final String PATH_MOVIE_FAV = "movie_fav";


    public static final class MovieDetailsEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAILS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_MOVIE_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIE_DETAILS;


        public static final String TABLE_NAME = "movie_details";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW  = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_FAVOURITE = "favourite";

        public static Uri buildMovieDetailsUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI,_id);
        }
    }

    public static final class MoviePopularEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIE_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIE_POPULAR;

        public static final String TABLE_NAME = "movie_popular";

        public static final String COLUMN_RANK = "rank";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildMoviePopularUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI,_id);
        }
    }

    public static final class MovieTopRatedEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_TOP_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIE_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIE_TOP_RATED;

        public static final String TABLE_NAME = "movie_top_rated";

        public static final String COLUMN_RANK = "rank";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildMovieTopRatedUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI,_id);
        }
    }
}
