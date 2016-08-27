package com.portfolio.archit.weathermaster;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.portfolio.archit.weathermaster.database.WeatherContract;
import com.portfolio.archit.weathermaster.sync.SunshineSyncAdapter;
import com.portfolio.archit.weathermaster.utils.Utility;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final int PLACE_PICKER_REQUEST = 9632;
    private GeneralPreferenceFragment generalPreferenceFragment;


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference != null) {
            String key = preference.getKey();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (key.equals(getString(R.string.pref_location_key))) {
                @SunshineSyncAdapter.LocationStatus int status = Utility.getLocationStatus(this);
                switch (status) {
                    case SunshineSyncAdapter.LOCATION_STATUS_OK:
                        preference.setSummary(stringValue);
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN:
                        preference.setSummary(getString(R.string.pref_location_unknown_description, value.toString()));
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                        preference.setSummary(getString(R.string.pref_location_error_description, value.toString()));
                        break;
                    default:
                        // Note --- if the server is down we still assume the value
                        // is valid
                        preference.setSummary(stringValue);
                }
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        generalPreferenceFragment = new GeneralPreferenceFragment();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, generalPreferenceFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (generalPreferenceFragment != null) {
            generalPreferenceFragment.onActivityResultFragment(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Intent onBuildStartFragmentIntent(String fragmentName, Bundle args, int titleRes, int shortTitleRes) {

        return super.onBuildStartFragmentIntent(fragmentName, args, titleRes, shortTitleRes);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(false);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class GeneralPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

        private Activity mActivity;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            mActivity = getActivity();
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(false);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_art_pack_key)));

        }


        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            // Trigger the listener immediately with the preference's current value.
            // Set the preference summaries
            setPreferenceSummary(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            setPreferenceSummary(preference, newValue);
            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_location_key))) {
                // we've changed the location
                // Wipe out any potential PlacePicker latlng values so that we can use this text entry.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(getString(R.string.pref_location_latitude));
                editor.remove(getString(R.string.pref_location_longitude));
                editor.commit();

                Utility.resetLocationStatus(mActivity);
                SunshineSyncAdapter.syncImmediately(mActivity);
            } else if (key.equals(getString(R.string.pref_units_key))) {
                // units have changed. update lists of weather entries accordingly
                getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
            } else if (key.equals(getString(R.string.pref_location_status_key))) {
                // our location status has changed.  Update the summary accordingly
                Preference locationPreference = findPreference(getString(R.string.pref_location_key));
                bindPreferenceSummaryToValue(locationPreference);
            } else if (key.equals(getString(R.string.pref_art_pack_key))) {
                // art pack have changed. update lists of weather entries accordingly
                getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
            }
        }


        // Registers a shared preference change listener that gets notified when preferences change
        @Override
        public void onResume() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            sp.registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        // Unregisters a shared preference change listener
        @Override
        public void onPause() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            sp.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(SettingsActivity.this, data);
                    String address = place.getAddress().toString();
                    LatLng latLong = place.getLatLng();

                    // If the provided place doesn't have an address, we'll form a display-friendly
                    // string from the latlng values.
                    if (TextUtils.isEmpty(address)) {
                        address = String.format("(%.2f, %.2f)", latLong.latitude, latLong.longitude);
                    }

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.pref_location_key), address);

                    // Also store the latitude and longitude so that we can use these to get a precise
                    // result from our weather service. We cannot expect the weather service to
                    // understand addresses that Google formats.
                    editor.putFloat(getString(R.string.pref_location_latitude), (float) latLong.latitude);
                    editor.putFloat(getString(R.string.pref_location_longitude), (float) latLong.longitude);

                    editor.commit();

                    // Tell the SyncAdapter that we've changed the location, so that we can update
                    // our UI with new values. We need to do this manually because we are responding
                    // to the PlacePicker widget result here instead of allowing the
                    // LocationEditTextPreference to handle these changes and invoke our callbacks.
                    Preference locationPreference = findPreference(getString(R.string.pref_location_key));
                    setPreferenceSummary(locationPreference, address);
                    Utility.resetLocationStatus(SettingsActivity.this);
                    SunshineSyncAdapter.syncImmediately(SettingsActivity.this);
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }


//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    public Intent getParentActivityIntent() {
//        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    }

}
