package in.prabakaran.nanodegreeproject;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class PortfolioFragment extends Fragment implements Button.OnClickListener{

    public PortfolioFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_portfolio, container, false);
        Button spotifyStreamerBtn = (Button) rootView.findViewById(R.id.spotifyStreamerBtn);
        Button scoresBtn = (Button) rootView.findViewById(R.id.scoresBtn);
        Button libraryBtn = (Button) rootView.findViewById(R.id.libraryBtn);
        Button buildItBiggerBtn = (Button) rootView.findViewById(R.id.buildItBiggerBtn);
        Button xyzReaderBtn = (Button) rootView.findViewById(R.id.xyzReaderBtn);
        Button capstoneBtn = (Button) rootView.findViewById(R.id.capstoneBtn);

        spotifyStreamerBtn.setOnClickListener(this);
        scoresBtn.setOnClickListener(this);
        libraryBtn.setOnClickListener(this);
        buildItBiggerBtn.setOnClickListener(this);
        xyzReaderBtn.setOnClickListener(this);
        capstoneBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        String toastText = "";
        switch (v.getId()){
            case R.id.spotifyStreamerBtn:
                startActivity(new Intent(v.getContext(),PopularMovies.class));
                break;
            case R.id.scoresBtn:
                toastText = getString(R.string.scores);
                break;
            case R.id.libraryBtn:
                toastText = getString(R.string.library);
                break;
            case R.id.buildItBiggerBtn:
                toastText = getString(R.string.build_it_bigger);
                break;
            case R.id.xyzReaderBtn:
                toastText = getString(R.string.xyz_reader);
                break;
            case R.id.capstoneBtn:
                toastText = getString(R.string.capstone);
        }
        toastText = getString(R.string.toast_prefix_text) + toastText;
        Toast.makeText(v.getContext(), toastText, Toast.LENGTH_LONG).show();
    }
}
