package in.prabakaran.nanodegreeproject.moviesapp.data;

import android.provider.BaseColumns;

/**
 * Created by Prabakaran on 03-01-2016.
 */
public class MovieDetailsContract {

    public static final class MovieDetailsEntry implements BaseColumns{

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
    }

    public static final class MoviePopularEntry implements BaseColumns{

        public static final String TABLE_NAME = "movie_popular";

        public static final String COLUMN_RANK = "rank";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public static final class MovieTopRatedEntry implements BaseColumns{

        public static final String TABLE_NAME = "movie_top_rated";

        public static final String COLUMN_RANK = "rank";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }
}
