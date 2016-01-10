package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.prabakaran.nanodegreeproject.R;

/**
 * Created by Prabakaran on 09-01-2016.
 */
public class TrailerAdapter extends CursorAdapter {

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_container, parent, false);

        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerViewHolder trailerViewHolder = (TrailerViewHolder) view.getTag();
        trailerViewHolder.key = cursor.getString(1);
        trailerViewHolder.trailerNameTxt.setText(cursor.getString(2));
    }

    public static class TrailerViewHolder {
        public final TextView trailerNameTxt;
        public String key;

        public TrailerViewHolder(View v){
            trailerNameTxt = (TextView) v.findViewById(R.id.trailerTxt);
        }
    }
}
