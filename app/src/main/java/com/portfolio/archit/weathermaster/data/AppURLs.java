package com.portfolio.archit.weathermaster.data;

/**
 * Created by archi on 28-03-2016.
 */
public final class AppURLs {

    // Construct the URL for the OpenWeatherMap query
    // Possible parameters are avaiable at OWM's forecast API page, at
    // http://openweathermap.org/API#forecast
    public static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
//    public static final String FORECAST_BASE_URL = "http://google.com/?";
//    public static final String FORECAST_BASE_URL = "http://google.com/ping?";

    public static final String QUERY_PARAM = "q";
    public static final String FORMAT_PARAM = "mode";
    public static final String UNITS_PARAM = "units";
    public static final String DAYS_PARAM = "cnt";
    public static final String APPID_PARAM = "APPID";


}
