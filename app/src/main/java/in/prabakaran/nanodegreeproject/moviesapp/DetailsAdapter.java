package in.prabakaran.nanodegreeproject.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.prabakaran.nanodegreeproject.R;

/**
 * Created by Prabakaran on 09-01-2016.
 */
public class DetailsAdapter extends CursorAdapter {

    public final static int TYPE_TRAILER = 1;
    public final static int TYPE_REVIEW = 2;

    public DetailsAdapter(Context context, Cursor c, int flags) {
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
        Boolean type = false;
        try{
            cursor.getString(3);
        }catch (Exception e){
            type = true;
        }
        if(type) {
            trailerViewHolder.key = cursor.getString(1);
            trailerViewHolder.trailerNameTxt.setText(cursor.getString(2));
            trailerViewHolder.trailerImage.setImageDrawable(context.getResources().getDrawable(R.drawable.movie));
            trailerViewHolder.reviewAuthorTxt.setText("");
            trailerViewHolder.reviewContentTxt.setText("");
            trailerViewHolder.reviewUrlTxt.setText("");
            trailerViewHolder.reviewLinearLayout.removeViewInLayout(view);
            trailerViewHolder.type = TYPE_TRAILER;
        }else{
            trailerViewHolder.reviewAuthorTxt.setText(cursor.getString(1) + " : ");
            trailerViewHolder.reviewContentTxt.setText(cursor.getString(2));
            trailerViewHolder.reviewUrlTxt.setText(cursor.getString(3));
            trailerViewHolder.key = "";
            trailerViewHolder.trailerNameTxt.setText("");
            trailerViewHolder.trailerImage.setImageBitmap(null);
            trailerViewHolder.trailerLinearLayout.removeViewInLayout(view);
            trailerViewHolder.type = TYPE_REVIEW;
        }
    }

    public static class TrailerViewHolder {
        public final TextView trailerNameTxt;
        public final ImageView trailerImage;
        public String key;
        public int type;
        public final TextView reviewAuthorTxt;
        public final TextView reviewContentTxt;
        public final TextView reviewUrlTxt;
        public final LinearLayout trailerLinearLayout;
        public final LinearLayout reviewLinearLayout;

        public TrailerViewHolder(View v){
            trailerNameTxt = (TextView) v.findViewById(R.id.trailerTxt);
            trailerImage = (ImageView) v.findViewById(R.id.trailerImage);
            reviewAuthorTxt = (TextView) v.findViewById(R.id.reviewAuthorTxt);
            reviewContentTxt = (TextView) v.findViewById(R.id.reviewContentTxt);
            reviewUrlTxt = (TextView) v.findViewById(R.id.reviewUrlTxt);
            trailerLinearLayout = (LinearLayout) v.findViewById(R.id.trailerLinearLayout);
            reviewLinearLayout = (LinearLayout) v.findViewById(R.id.reviewLinearLayout);
        }
    }
}
