package in.prabakaran.nanodegreeproject.dummy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import in.prabakaran.nanodegreeproject.R;

public class Dummy_Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.movie);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap bmp1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = (ImageView) findViewById(R.id.imageView);
        Log.v("byte array", byteArray.toString());

        image.setImageBitmap(bmp1);
    }

}
