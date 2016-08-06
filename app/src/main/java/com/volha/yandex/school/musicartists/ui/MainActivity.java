package com.volha.yandex.school.musicartists.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.volha.yandex.school.musicartists.HeadsetPlugReceiver;
import com.volha.yandex.school.musicartists.R;

import de.psdev.licensesdialog.LicensesDialog;

public class MainActivity extends AppCompatActivity {

    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );
        MainFragment fragment = ( MainFragment ) getSupportFragmentManager().findFragmentByTag( MainFragment.TAG );
        if ( fragment == null ) {
            fragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack( null )
                    .replace( R.id.contentPanel, fragment, MainFragment.TAG )
                    .commit();
        }
        headsetPlugReceiver = new HeadsetPlugReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver( headsetPlugReceiver, filter );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver( headsetPlugReceiver );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();
        switch ( id ) {
            case R.id.action_notices:
                createLicenceDialog();
                break;
            case R.id.action_feedback:
                createAndSendEmailIntent();
                break;
            case android.R.id.home:
                getSupportFragmentManager()
                        .popBackStack( DetailFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE );
                break;
        }

        return super.onOptionsItemSelected( item );
    }

    private void createAndSendEmailIntent() {
        Intent intent = new Intent( Intent.ACTION_SEND );
        intent.putExtra( Intent.EXTRA_SUBJECT, getString( R.string.extra_subject) );
        intent.putExtra( Intent.EXTRA_EMAIL, getString( R.string.extra_email) );
        startActivity( intent );
    }

    private void createLicenceDialog() {
        new LicensesDialog.Builder(this)
                .setNotices( R.raw.notices)
                .build()
                .show();
    }

}
