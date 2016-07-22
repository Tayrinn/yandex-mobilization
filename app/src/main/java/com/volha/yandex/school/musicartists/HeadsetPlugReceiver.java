package com.volha.yandex.school.musicartists;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Volha on 21.07.2016.
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {

        if (intent.getAction().equals( Intent.ACTION_HEADSET_PLUG )) {
            int state = intent.getIntExtra("state", -1);
            switch ( state ) {
                case 1:
                    showYaShareDialog( context );
                    break;
            }
        }
    }


    private void showYaShareDialog( final Context context ) {

        View content = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate( R.layout.dialog_ya_share, null );

        ImageButton yaMusic = ( ImageButton ) content.findViewById( R.id.yamusic );
        yaMusic.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ru.yandex.music")));

            }
        } );

        ImageButton yaRadio = ( ImageButton ) content.findViewById( R.id.yaradio );
        yaRadio.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ru.yandex.radio")));
            }
        } );

        AlertDialog.Builder adb = new AlertDialog.Builder( context );
        adb.setView( content );
        adb.setCancelable( true );
        adb.show();
    }}
