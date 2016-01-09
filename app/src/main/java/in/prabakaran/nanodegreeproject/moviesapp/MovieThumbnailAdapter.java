package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import in.prabakaran.nanodegreeproject.R;

/**
 * Created by Prabakaran on 04-01-2016.
 */
public class MovieThumbnailAdapter extends CursorAdapter {

    public MovieThumbnailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_thumbnail, parent, false);

        MovieViewHolder viewHolder = new MovieViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MovieViewHolder movieViewHolder = (MovieViewHolder) view.getTag();
        movieViewHolder.movieId = cursor.getString(0);
        Picasso.with(mContext).load(PopularMoviesFragment.TMDB_IMAGE_DOWNLOAD_URL + cursor.getString(1)).into(movieViewHolder.imageView);
    }

    public static class MovieViewHolder{
        public final ImageView imageView;
        public String movieId;

        public MovieViewHolder(View v){
            imageView = (ImageView) v.findViewById(R.id.movieThumbnail);
        }
    }
}
