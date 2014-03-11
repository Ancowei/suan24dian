package com.ancowei.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class Background_music extends Service {
	private MediaPlayer music_player;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	public void onStart(Intent intent, int startId){  
		  
        if(music_player == null)  
        {  
        	//music_player = MediaPlayer.create(this, R.raw.song);  
        	music_player.setLooping(true);  
        	music_player.start();  
        }  
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		music_player.stop();
	}  

}
