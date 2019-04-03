package aiiptv.ovp;

import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.view.Menu;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;
public class Ovp_VOD<MotionEvent> extends Activity {
	private static final String TAG = "OVP_VOD_player";

	private VideoView mVideoView;
	private ListView mVideo_menu_list;
	private MediaFormat mMediaFormat;
	private TextView mDuration;
	private TextView mCurrent_Time;
	private SeekBar mSeek;
	private String current;
	private String g_token = null;
	private String g_url = null;
	
	private String g_ezserver_ip="192.168.0.7";
	private int g_ezserver_port = 18000;
	private int g_http_port = 8000;
	private double g_duration;
	private double g_bitrate;
	private String g_user_name = null;
	private String g_password = null;
	private String g_movieurl=null;
	private int g_stopPosition=0;
	
	private Intent intent;
	private float x1,x2=0;;
	private float y1,y2=0;
	private int SETTING_REQUEST=0;
	private int g_ntemp=0;
	private String g_dmsg;
	private boolean g_bPlaying=false;
	private int g_SeekTime=0;
	private int g_StartTrackingPos=0;
	private int g_StopTrackingPos=0;
	private int hour;
	private int minute;
	private int second;
	private int nduration;
	private int nTemp;
	private ScheduledExecutorService scheduleTaskExecutor;
	private ScheduledFuture<?> queueFuture;
	private boolean bPlayback_Button_ON=false;
	private boolean g_bSeeking=false;
	private int TOUCH_MODE=1;
	private int DPAD_FF_SEEK=2;
	private int DPAD_BK_SEEK=3;
	private int DPAD_PAUSE=4;

	private int DPAD_NORMAL=5;
	private int	bSeekDirection=TOUCH_MODE;
	private	int nSeek_many=0;
	private	int nSeek_Buffer_Per_Time=0;
	private	int nSeek_Minor_Buffer_Per_Time=0;
	private String weburl;
	private ArrayList<String> video_menu_list_array = new ArrayList<String>();
	ViewGroup.MarginLayoutParams mlp_v;
	RelativeLayout.LayoutParams parms_v;
	private int g_nScreenWidth;
	private int g_nScreenHeight;

   	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		intent = getIntent();
		super.onCreate(savedInstanceState);

	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	      Display display=getWindowManager().getDefaultDisplay();
	        g_nScreenWidth=display.getWidth();
	        g_nScreenHeight=display.getHeight();
	       
		setContentView(R.layout.vod);
		mVideoView = (VideoView) findViewById(R.id.surface_view);

		mDuration= (TextView)findViewById(R.id.duration);
		mCurrent_Time= (TextView)findViewById(R.id.current_time);
	
		g_token=intent.getStringExtra("token");
		g_user_name=intent.getStringExtra("user_name");
		g_password=intent.getStringExtra("passowrd");
		//g_cur_ch_no=Integer.valueOf(intent.getStringExtra("cur_ch_no"));
		g_ezserver_ip=intent.getStringExtra("ezserver_ip");
		g_movieurl=intent.getStringExtra("movieurl");
		g_ezserver_port=Integer.valueOf(intent.getStringExtra("http_base_port"));
		g_duration=Double.valueOf(intent.getStringExtra("duration"));
		if (g_duration==0)
		{
			g_duration=60*60*2;
		}
		
		nSeek_Buffer_Per_Time=(int) (g_duration*1000)/10;
		nSeek_Minor_Buffer_Per_Time=(nSeek_Buffer_Per_Time/10);
		g_bitrate=Double.valueOf(intent.getStringExtra("bitrate"));
		
		nduration=(int) g_duration;
		    
		hour=nduration/3600;
		nTemp=nduration%3600;
		minute=nTemp/60;
		second=nTemp%60;
			    
		
		g_http_port=Integer.valueOf(intent.getStringExtra("http_port"));
		weburl=g_movieurl.replaceAll(" ", "%20");
		g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+weburl+"?token="+g_token;
	
		mVideo_menu_list= (ListView) findViewById(R.id.video_menu_list);
		video_menu_list_array.add("Pause");
		video_menu_list_array.add("Cancel");
		mVideo_menu_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,video_menu_list_array));
		// Video Menu
		parms_v = new RelativeLayout.LayoutParams(g_nScreenWidth/2,g_nScreenHeight/2);
		mVideo_menu_list.setLayoutParams(parms_v);
		mlp_v = (ViewGroup.MarginLayoutParams) mVideo_menu_list.getLayoutParams();
		mlp_v.setMargins(g_nScreenWidth/4, g_nScreenHeight/4, 0, 0);
		
		mSeek = (SeekBar) findViewById(R.id.seekbar);


	mVideoView.setOnCompletionListener(new OnCompletionListener() {
	
			@Override
			public void onCompletion(MediaPlayer mp) {
			setResult(RESULT_OK, intent);
			finish();
				
			}
		});	

	mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
		//Toast.makeText(Ovp_VOD.this, "Can not play the movie",Toast.LENGTH_LONG).show();
		finish();
                return true;
            }
        });	
	mVideoView.setOnTouchListener(new View.OnTouchListener() {
	

			@Override
			public boolean onTouch(View v, android.view.MotionEvent event) {
			switch (event.getAction())
			    {
			        case android.view.MotionEvent.ACTION_UP: 
			        {
			            break;
			        }
			        case android.view.MotionEvent.ACTION_DOWN: // gets called
			        {
			            break;
			        }
			    }
			    bSeekDirection=TOUCH_MODE;

			  
			    
				scheduleTaskExecutor= Executors.newScheduledThreadPool(5);
				
				    // This schedule a task to run every 10 minutes:
				    queueFuture=scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
				      public void run() {
				        runOnUiThread(new Runnable() {
				          public void run() {
				            int how_many=0;
				            // update your UI component here.
					    nduration=(int) g_duration;
					    g_StartTrackingPos=mVideoView.getCurrentPosition();
					    if (nduration>0)
					    {
					    	how_many=g_StartTrackingPos/(nduration*10);
					    }
					    mSeek.setProgress(how_many);
					
					    hour=(g_StartTrackingPos/1000)/3600;
					    nTemp=(g_StartTrackingPos/1000)%3600;
					    minute=nTemp/60;
					    second=nTemp%60;
					    String current_time_text=hour+":"+minute+":"+second;			
					    mCurrent_Time.setText(current_time_text);
					    
					    int seek_label_pos = (int)((float)(mSeek.getMeasuredWidth()) * ((float)how_many / 100f));
					    mCurrent_Time.setX(seek_label_pos);	
					   }
				        });
				      }
				    }, 0, 1, TimeUnit.SECONDS);

			    switch (event.getAction())
			    {
			        case android.view.MotionEvent.ACTION_UP: 
			        {
			        	
				    if (mSeek.getVisibility() == View.VISIBLE)
				    {
				    	Hide_Playback_Menu();
				    }else
				    {
				    	Show_Playback_Menu();
				    }
				 }
			    }				  
						
			return true;

					
			}
		});			
	
		mVideo_menu_list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> a, View view, int index,long id) {
				
				if (index==0) // Pause
				{
					if (mVideoView != null) {
						TextView mTextView=(TextView) view;
						if (g_bPlaying==false)
						{
							mTextView.setText("Pause");
							if (g_bPlaying==false)
							{
								mVideoView.seekTo(g_stopPosition);
				
								mVideoView.start();
							}
							Hide_Playback_Menu();
							g_bPlaying=true;				
						}else
						{
							mVideoView.pause();
							g_stopPosition = mVideoView.getCurrentPosition();
							g_bPlaying=false;
							mTextView.setText("Play");
							
						}
						//Hide_Playback_Menu();
					
					}
				}else if (index==1) // Cancel
				{
					
					Hide_Playback_Menu();
					
					
				}
			}
		});

	mSeek.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener() {
		public void onStartTrackingTouch (SeekBar seekBar) {
			if (mVideoView != null) {
				current = null;
				g_StartTrackingPos=mVideoView.getCurrentPosition(); 
				g_SeekTime=30*1000;			
			}
		}
		public void onStopTrackingTouch (SeekBar seekBar)  {
			if (mVideoView != null) {
				current = null;
				nduration=(int) g_duration;
				int nTimepercent=mSeek.getProgress();
				g_SeekTime=nTimepercent*nduration*10; // (x/100)*d*1000
				mVideoView.seekTo(g_SeekTime);
				mVideoView.start();
				
				Hide_Playback_Menu();
				
				queueFuture.cancel(true); 
				
			}
		}
		private void Show_Current_Time_on_Seekbar(int how_many)
		{

		    nduration=(int) g_duration;
		    g_SeekTime=how_many*nduration*10;
		    hour=(g_SeekTime/1000)/3600;
		    nTemp=(g_SeekTime/1000)%3600;
		    minute=nTemp/60;
		    second=nTemp%60;
		    String current_time_text=hour+":"+minute+":"+second;			
		    mCurrent_Time.setText(current_time_text);
		
		    int seek_label_pos = (int)((float)(mSeek.getMeasuredWidth()) * ((float)how_many / 100f));
		    mCurrent_Time.setX(seek_label_pos);
		}
		public void onProgressChanged(SeekBar seekBar, int  progress, boolean  fromUser) { 
			Show_Current_Time_on_Seekbar(progress);
		}
		
	});	

	
	
		runOnUiThread(new Runnable(){
			public void run() {
				playVideo();
			}
			
		});
		  
		
	}
      
	private boolean Show_Playback_Menu()
	{
	    mSeek.setVisibility(View.VISIBLE);
		if (bSeekDirection==TOUCH_MODE)
		{
			mVideo_menu_list.setVisibility(View.VISIBLE);
			//mPause.setVisibility(View.VISIBLE);
			//mPlay.setVisibility(View.VISIBLE);
			//mStop.setVisibility(View.VISIBLE);
		}

	
	    nduration=(int) g_duration;
	    
	    hour=nduration/3600;
	    nTemp=nduration%3600;
	    minute=nTemp/60;
	    second=nTemp%60;
	    
	    String duration_text="Duration: "+hour+":"+minute+":"+second;
	    mDuration.setText(duration_text);
	    mDuration.setVisibility(View.VISIBLE);
	    
	    g_StartTrackingPos=mVideoView.getCurrentPosition();
	    Show_Time_on_Seekbar(g_StartTrackingPos);
	    return true;
	}
	private boolean Hide_Playback_Menu()
	{
		mVideo_menu_list.setVisibility(View.INVISIBLE);
		mSeek.setVisibility(View.INVISIBLE);
		mDuration.setVisibility(View.INVISIBLE);
		mCurrent_Time.setVisibility(View.INVISIBLE);
		return true;
	}
	private InputStream getSubtitleSource(String url_path) {
	    InputStream inputstream = null;
		try{
			URL url = new URL(url_path);
			URLConnection urlConnection = url.openConnection();
			inputstream = new BufferedInputStream(urlConnection.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputstream;
	}      

	private void playVideo() {
		try {
			final String path = g_url;
			weburl=g_movieurl.replaceAll(" ", "%20");	
			 final String srt_path="http://"+g_ezserver_ip+":"+g_http_port+"/"+weburl+".srt"+"?token="+g_token;
			InputStream ins;

			Log.v(TAG, "path: " + path);
			if (path == null || path.length() == 0) {
				Toast.makeText(Ovp_VOD.this, "File URL/path is empty",
						Toast.LENGTH_LONG).show();

			} else {
				// If the path has not changed, just start the media player
				if (path.equals(current) && mVideoView != null) {
					mVideoView.start();
					mVideoView.requestFocus();
					return;
				}
	

	


				current = path;
				
				Hide_Playback_Menu();

				try
				{
					mVideoView.setVideoURI(Uri.parse(path));
					mVideoView.start();
					mVideoView.requestFocus();
					g_bPlaying=true;
				} catch (Exception e) {
					Log.e(TAG, "error: " + e.getMessage(), e);
					if (mVideoView != null) {
						mVideoView.stopPlayback();
					}
				}

			}
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
			if (mVideoView != null) {
				mVideoView.stopPlayback();
			}
		}
	}

	private void Show_Time_on_Seekbar(int pos)
	{
        	nduration=(int) g_duration;
		int how_many=0;
		if (nduration>0)
		{
			how_many=pos/(nduration*10);
		}
		mSeek.setProgress(how_many);
		
		hour=(pos/1000)/3600;
		nTemp=(pos/1000)%3600;
		minute=nTemp/60;
		second=nTemp%60;
		String current_time_text=hour+":"+minute+":"+second;			
		mCurrent_Time.setVisibility(View.VISIBLE);
		mCurrent_Time.setText(current_time_text);				
		int seek_label_pos = (int)((float)(mSeek.getMeasuredWidth()) * ((float)how_many / 110f));
		mCurrent_Time.setX(seek_label_pos);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	protected void onStop()
	{
		super.onStop();
		if (mVideoView != null) {
			mVideoView.stopPlayback();
			setResult(RESULT_OK, intent);
			 finish();
		
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)||(keyCode == KeyEvent.KEYCODE_MEDIA_STOP))
		{
			if ((mVideo_menu_list.getVisibility() == View.VISIBLE) ||
				(mSeek.getVisibility() == View.VISIBLE))
	 		{
	 			Hide_Playback_Menu();
	 			bPlayback_Button_ON=false;
				bSeekDirection=DPAD_NORMAL;
	 			return true;
	 		}else
	 		{
				if (mVideoView != null) {
					mVideoView.stopPlayback();
					setResult(RESULT_OK, intent);
					 finish();
				
				}
			}
		}
		
	    	if (bPlayback_Button_ON==false)
	    	{
			bSeekDirection=DPAD_NORMAL;
			if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)||(keyCode == KeyEvent.KEYCODE_ENTER))		
			{
					bSeekDirection=DPAD_PAUSE;
					if (mVideoView != null) 
					{
						mVideo_menu_list.setVisibility(View.VISIBLE);
						mVideo_menu_list.requestFocus();
						
					}
			}else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)||(keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) ||
				(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)||(keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD))
			{
			
				Show_Playback_Menu();
				nSeek_many=mVideoView.getCurrentPosition();
				scheduleTaskExecutor= Executors.newScheduledThreadPool(5);
					
				    // This schedule a task to run every 10 minutes:
				    queueFuture=scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
				      public void run() {
				        runOnUiThread(new Runnable() {
				          public void run() {
				            // update your UI component here.
				            if (!((bSeekDirection==DPAD_FF_SEEK) || (bSeekDirection==DPAD_BK_SEEK)))
					    {
						    nduration=(int) g_duration;
						    g_StartTrackingPos=mVideoView.getCurrentPosition();
						    int how_many=0;
						    if (nduration>0)
						    {
						    	how_many=g_StartTrackingPos/(nduration*10);
						    }
						    mSeek.setProgress(how_many);
						
						    hour=(g_StartTrackingPos/1000)/3600;
						    nTemp=(g_StartTrackingPos/1000)%3600;
						    minute=nTemp/60;
						    second=nTemp%60;
						    String current_time_text=hour+":"+minute+":"+second;			
						    mCurrent_Time.setText(current_time_text);
						    
						    int seek_label_pos = (int)((float)(mSeek.getMeasuredWidth()) * ((float)how_many / 100f));
						    mCurrent_Time.setX(seek_label_pos);	
					    }
					   }
				        });
				      }
				    }, 0, 1, TimeUnit.SECONDS);
				    bPlayback_Button_ON=true;
			  } 
		} else if (bPlayback_Button_ON==true)
		{
		        if (((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)||(keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)) && (mVideoView.isPlaying()==true)) {
		        	bSeekDirection=DPAD_BK_SEEK;
		        	
		        	g_StartTrackingPos=mVideoView.getCurrentPosition();
		        	
	        		if (nSeek_many>=nSeek_Buffer_Per_Time)
	        		{
	        			nSeek_many-=nSeek_Buffer_Per_Time; // minus 1 min
			        	g_StartTrackingPos=nSeek_many;
	        		}else
	        		{
	        			g_StartTrackingPos=0;
	        		}
	        		
	        		
		        	
		        	Show_Time_on_Seekbar(g_StartTrackingPos);
			} else if (((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)||(keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)) && (mVideoView.isPlaying()==true)) {
				bSeekDirection=DPAD_FF_SEEK;
		        	if (nSeek_many<(g_duration*1000))
		        	{
		        		nSeek_many+=nSeek_Buffer_Per_Time; // minus 1 min
			        	g_StartTrackingPos=nSeek_many; // 
		        	}else
		        	{
		        		g_StartTrackingPos=(int) (g_duration*1000);
		        	}
		        	
		        	
		        	Show_Time_on_Seekbar(g_StartTrackingPos);
		        	
			}else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP) && (mVideoView.isPlaying()==true)) {
		        	bSeekDirection=DPAD_BK_SEEK;
		        	
		        	g_StartTrackingPos=mVideoView.getCurrentPosition();
		        	
	        		if (nSeek_many>=nSeek_Minor_Buffer_Per_Time)
	        		{
	        			nSeek_many-=nSeek_Minor_Buffer_Per_Time; // minus 1 min
			        	g_StartTrackingPos=nSeek_many;
	        		}else
	        		{
	        			g_StartTrackingPos=0;
	        		}
	        		
	        		
		        	
		        	Show_Time_on_Seekbar(g_StartTrackingPos);
			} else if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN) && (mVideoView.isPlaying()==true)) {
				bSeekDirection=DPAD_FF_SEEK;
		        	if (nSeek_many<(g_duration*1000))
		        	{
		        		nSeek_many+=nSeek_Minor_Buffer_Per_Time; // minus 1 min
			        	g_StartTrackingPos=nSeek_many; // 
		        	}else
		        	{
		        		g_StartTrackingPos=(int) (g_duration*1000);
		        	}
		        	
		        	Show_Time_on_Seekbar(g_StartTrackingPos);
		        	
			} else if (((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)||(keyCode == KeyEvent.KEYCODE_ENTER)||(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY))) {
				if ((bSeekDirection==DPAD_FF_SEEK) || (bSeekDirection==DPAD_BK_SEEK))
				{
					queueFuture.cancel(true); 
					mVideoView.seekTo(g_StartTrackingPos);
	
					mVideoView.start();
					g_bSeeking=false;
					Hide_Playback_Menu();
					g_bPlaying=true;
					bPlayback_Button_ON=false;
					bSeekDirection=DPAD_NORMAL;
					nSeek_many=g_StartTrackingPos;
				}else if (g_bPlaying==false)
				{
					queueFuture.cancel(true); 
					Hide_Playback_Menu();
					mVideoView.seekTo(g_stopPosition);
					mVideoView.start();
					g_bPlaying=true;
					bPlayback_Button_ON=false;
					bSeekDirection=DPAD_NORMAL;
				}else if (g_bPlaying==true)
				{
					bSeekDirection=DPAD_PAUSE;
					if (mVideoView != null) 
					{
						mVideoView.pause();
						g_stopPosition = mVideoView.getCurrentPosition(); 
						g_bPlaying=false;
					}				
				}
				
			}
			
			
		}
	       return super.onKeyDown(keyCode, event);
	    }

}

