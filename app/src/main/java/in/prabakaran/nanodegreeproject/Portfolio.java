package in.prabakaran.nanodegreeproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.stetho.Stetho;

public class Portfolio extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
//        OkHttpClient client = new OkHttpClient();
//        client.networkInterceptors().add(new StethoInterceptor());
        setContentView(R.layout.activity_portfolio);
    }
}
