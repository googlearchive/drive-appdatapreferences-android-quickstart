package com.google.drive.samples.appdatapreferencesdemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.drive.appdatapreferences.AppdataPreferencesSyncer;
import com.google.drive.appdatapreferences.AppdataPreferencesSyncer.OnChangeListener;
import com.google.drive.appdatapreferences.AppdataPreferencesSyncer.OnUserRecoverableAuthExceptionExceptionListener;

/**
 * Activity to illustrate simple syncing flow between a
 * {@code SharedPreferences} instance and Google Drive's Application Data folder
 * with AppdataPreferences for Android.
 * 
 * @author jbd@google.com (Burcu Dogan)
 */
public class SettingsActivity extends Activity implements OnChangeListener,
    OnUserRecoverableAuthExceptionExceptionListener {

  private RatingBar mRatingBar;
  private EditText mNameEditText;
  private Button mSaveButton;
  private Button mReloadButton;

  private AppdataPreferencesSyncer mSyncer;
  private SharedPreferences mPreferences;

  /**
   * Initializes the syncer by connecting a Google Account to a
   * {@code SharedPreferences} object. Starts to listen the changes
   * to the remote preferences file and auth exceptions.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);

    // get a shared preferences
    mPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
    GoogleAccountCredential credential =
        GoogleAccountCredential.usingOAuth2(this, "https://www.googleapis.com/auth/drive.appdata");
    credential.setSelectedAccountName(getGoogleAccountName());
    
    mSyncer = AppdataPreferencesSyncer.get(getApplicationContext());
    
    // binds an account to the preferences
    mSyncer.bind(credential, mPreferences);

    // listen changes
    mSyncer.setOnChangeListener(this);
    
    // listen auth exceptions
    mSyncer.setOnUserRecoverableAuthExceptionListener(this);
    
    mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
    mNameEditText = (EditText) findViewById(R.id.editTextName);
    mSaveButton = (Button) findViewById(R.id.buttonSave);
    mSaveButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
       save();
      }
    });

    mReloadButton = (Button) findViewById(R.id.buttonReload);
    mReloadButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        forceSync();
      }
    });

    // initially, force sync with the remote preferences file
    // to initially load the values.
    forceSync();
  }

  private void forceSync() {
    new ForceSyncAsyncTask().execute();
  }

  /**
   * Saves the values in the local preferences.
   */
  private void save() {
    // write to the preferences
    mPreferences.edit()
        .putFloat(KEY_RATING, mRatingBar.getRating())
        .putString(KEY_NAME, mNameEditText.getText() + "")
        .commit();
  }

  /**
   * Refreshes the screen with the preferences stored
   * in the local {@code SharedPreferences} preferences.
   */
  private void refresh() {
    Log.d(TAG, "Refreshing the screen");
    final float rating = mPreferences.getFloat(KEY_RATING, 0);
    final String name = mPreferences.getString(KEY_NAME, "");

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mRatingBar.setRating(rating);
        mNameEditText.setText(name);
      }
    });
  }

  /**
   * Handles changes pushed from the remote preferences file.
   */
  @Override
  public void onChange(SharedPreferences prefs) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        refresh();
      }
    });
  }

  /**
   * Handles {@code UserRecoverableAuthIOException}s thrown from
   * the background service. 
   */
  @Override
  public void onUserRecoverableAuthException(
      final UserRecoverableAuthIOException e) {
    Log.d(TAG, "Ask for permissions");
    startActivityForResult(e.getIntent(), 0);
  }

  /**
   * Retrieves the name of the first com.google account if a Google
   * account exists.
   * @return A Google account.
   */
  private String getGoogleAccountName() {
    Account[] accounts =
        AccountManager.get(getApplicationContext()).getAccounts();
    for (Account account : accounts) {
      if (account.type.equals("com.google")) {
        return account.name;
      }
    }
    return null;
  }
  
  /**
   * Async task to force sync the local preferences file
   * with the remote one and refresh the screen. 
   */
  class ForceSyncAsyncTask extends AsyncTask<Void, Boolean, Boolean> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mSaveButton.setEnabled(false);
    }
    @Override
    protected Boolean doInBackground(Void... params) {
      mSyncer.sync();
      return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
      mSaveButton.setEnabled(true);
      refresh();
    }
  }

  private static final String TAG = "preferences";
  private static final String KEY_RATING = "rating";
  private static final String KEY_NAME = "name";

}
