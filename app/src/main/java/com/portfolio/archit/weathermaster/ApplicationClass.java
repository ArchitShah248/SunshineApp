package com.portfolio.archit.weathermaster;

import android.app.Application;
import android.widget.Toast;

import com.portfolio.archit.weathermaster.data.AppConstants;

/**
 * Created by Archit Shah on 8/27/2016.
 */
public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (AppConstants.OPEN_WEATHER_MAP_API_KEY.equalsIgnoreCase("YOUR OPEN WEATHER API KEY")) {
            Toast.makeText(ApplicationClass.this, "Please enter your ' Open Weather ' API key in " + AppConstants.class.getSimpleName() + ".java file.", Toast.LENGTH_LONG).show();
        }
    }
}
