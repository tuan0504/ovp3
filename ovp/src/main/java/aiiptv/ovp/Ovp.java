package aiiptv.ovp;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.view.Menu;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.text.format.Time;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;
public class Ovp<MotionEvent> extends Activity {
	private static final String TAG = "OVPplayer";

// GUI member
	private VideoView mVideoView;
	private ListView mVideo_menu_list;
	private ListView mEPG_list;
	private ListView mEPG_Day_list;
	boolean bEPG_List_Flag=false;
	private ImageView mRePlay;
	private Button mToLive;
	private SeekBar mSeek;
	private TextView mCurrent_Live_Time;
	private TextView mCurrent_Dvr_Time;
	private TextView mDvr_Starting_Time;
	private EditText mChannelNoInputField;
	private String current;
	private String g_token = null;
	private String g_url = null;
	private int g_cur_ch_no=1;
	private int g_total_ch_no=1;
	private ArrayList<String> video_menu_list_array = new ArrayList<String>();
	private ArrayList<String> epg_list_array = new ArrayList<String>();
	private ArrayList<String> epg_day_list_array = new ArrayList<String>();
	
	private String g_ezserver_ip="192.168.0.7";
	private int g_ezserver_port = 18000;
	private int g_http_port = 8000;
	private String g_user_name = null;
	private String g_password = null;
	private boolean g_ch_list_visible=false;
	private Channel_Info g_channel_info=new Channel_Info();
	private int g_StartTrackingPos=0;
	private boolean g_bPlaying=false;
	private boolean g_dvr_mode=false;
	private Time g_ch_pressed_time;
	private Time g_today;
	private Time g_SeekTime=new Time();
	private Time g_dvr_starting_time=new Time();
	private Time g_current_dvr_time=new Time();
	private float g_how_many=0;
	private long g_server_start_millisecond=0;
	private long g_dvr_start_millisecond=0;
	private long g_dvr_end_millisecond=0;
	private ScheduledExecutorService scheduleTaskExecutor;
	private ScheduledFuture<?> queueFuture;
	private long g_pressed_dvr_time;
	private long g_pressed_today_time;
	private int TOUCH_MODE=1;
	private int DPAD_FF_SEEK=2;
	private int DPAD_BK_SEEK=3;
	private int DPAD_PAUSE=4;
	private int DPAD_NORMAL=5;
	private int KEY_CHANNEL_UP=1;
	private int KEY_CHANNEL_DOWN=2;
	
	private int	bSeekDirection=TOUCH_MODE;
	private boolean g_dvr_menu=false;
	private int g_server_uptime_seconds=0;
	private int g_server_dvr_uptime_seconds=0;
	private int g_channel_dvr_buffer_peroid=0;
	private ScheduledExecutorService InputChannelNo_scheduleTaskExecutor;
	private ScheduledFuture<?> InputChannelNo_queueFuture;
	private int InputChannelNo_Delay=0;
	private boolean bFirstInputChannelNo=true;
	private String g_churl=null;
	private int g_keycode=KEY_CHANNEL_UP;

	
	private Intent intent;
	private float x1,x2=0;;
	private float y1,y2=0;
	private int SETTING_REQUEST=0;
	private int g_ntemp=0;
	private String g_dmsg;
	private int g_flashinstalled=0;
	private String weburl;
	private int g_nTry_Play_Times=0;
	private	int g_m3u8_channel=0;
	private int VOD_REQUEST=2;
  	private double g_duration=0;
	private double g_bitrate=0;
	
	private int g_nScreenWidth;
	private int g_nScreenHeight;



	//private int g_click_ch=0;
private  String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	}
  	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		ViewGroup.MarginLayoutParams mlp_v,mlp_e,mlp_d;
		RelativeLayout.LayoutParams parms_v,parms_e,parms_d;
		intent = getIntent();
		super.onCreate(savedInstanceState);

	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	      
	        Display display=getWindowManager().getDefaultDisplay();
	        g_nScreenWidth=display.getWidth();
	        g_nScreenHeight=display.getHeight();
		
	        
	        
		setContentView(R.layout.channel);
		mVideoView = (VideoView) findViewById(R.id.surface_view);

	
		g_token=intent.getStringExtra("token");
		g_user_name=intent.getStringExtra("user_name");
		g_password=intent.getStringExtra("passowrd");
		g_cur_ch_no=Integer.valueOf(intent.getStringExtra("cur_ch_no"));
		g_total_ch_no=Integer.valueOf(intent.getStringExtra("total_ch_no"));
		g_ezserver_ip=intent.getStringExtra("ezserver_ip");
		g_ezserver_port=Integer.valueOf(intent.getStringExtra("http_base_port"));
		g_http_port=Integer.valueOf(intent.getStringExtra("http_port"));
		g_churl=intent.getStringExtra("churl");
		
		mVideo_menu_list= (ListView) findViewById(R.id.video_menu_list);
		mEPG_list= (ListView) findViewById(R.id.epg_list);
		mEPG_Day_list= (ListView) findViewById(R.id.epg_day_list);
		mSeek = (SeekBar) findViewById(R.id.seekbar);
		mCurrent_Live_Time= (TextView)findViewById(R.id.current_live_time);
		mCurrent_Dvr_Time= (TextView)findViewById(R.id.current_dvr_time);
		mDvr_Starting_Time= (TextView)findViewById(R.id.dvr_starting_time);
		mChannelNoInputField=(EditText)findViewById(R.id.channel_no_input_field);
		mRePlay = (ImageView) findViewById(R.id.replay);
		mToLive = (Button) findViewById(R.id.tolive);
		
		
		
		// Detect Flash Player
		Intent flashintent = new Intent();

		flashintent.setComponent(new ComponentName("com.adobe.flashplayer", "com.adobe.flashplayer.FlashExpandableFileChooser"));
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(flashintent, 0);
		if (activities != null && activities.size() > 0) {
			g_flashinstalled=1;
		}
		else {
			g_flashinstalled=0;
		}
		Get_CH_information(g_token,g_ezserver_ip,g_ezserver_port);
 		epg_list_array=Get_EPG_Date();
		mEPG_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,epg_list_array));
		video_menu_list_array.add("EPG");
		String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
		video_menu_list_array.add("Pause");
		video_menu_list_array.add("Cancel");
		mVideo_menu_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,video_menu_list_array));
		
		// Video Menu
		parms_v = new RelativeLayout.LayoutParams(g_nScreenWidth/2,g_nScreenHeight/2);
		mVideo_menu_list.setLayoutParams(parms_v);
		mlp_v = (ViewGroup.MarginLayoutParams) mVideo_menu_list.getLayoutParams();
		mlp_v.setMargins(g_nScreenWidth/4, g_nScreenHeight/4, 0, 0);

		// EPG List
		parms_e = new RelativeLayout.LayoutParams(350,g_nScreenHeight-60);
		mEPG_list.setLayoutParams(parms_e);
		mlp_e = (ViewGroup.MarginLayoutParams) mEPG_list.getLayoutParams();
		mlp_e.setMargins(30, 30, 0, 30);

		// EPG Day List
		parms_d = new RelativeLayout.LayoutParams(g_nScreenWidth-350-60,g_nScreenHeight-60);
		mEPG_Day_list.setLayoutParams(parms_d);
		mlp_d = (ViewGroup.MarginLayoutParams) mEPG_Day_list.getLayoutParams();
		mlp_d.setMargins(0, 30, 0, 30);
	
 		g_cur_ch_no=g_channel_info.GetCHNoByFileName(g_churl); 
		ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
		if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
		{
			g_m3u8_channel=1;
		}else
		{
			g_m3u8_channel=0;
		}
		if (g_cur_ch_no==0) 
		{
			g_cur_ch_no=g_total_ch_no;
		}
		if (g_m3u8_channel==1)
		{
			g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
		}else
		{
			g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
		}
		
		
		mVideo_menu_list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> a, View view, int index,long id) {
				String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
				
				if (index==0) // EPG
				{
					if (mVideoView != null) {
						if (bEPG_List_Flag==true)
						{
					
							mEPG_list.setVisibility(View.INVISIBLE);
							bEPG_List_Flag=false;
						}else
						{
					
							Hide_Playback_Menu();
							mEPG_list.setVisibility(View.VISIBLE);
							bEPG_List_Flag=true;
							mEPG_list.requestFocus();
						}
						
					}				
				}else if (index==1) // Pause
				{
					if (mVideoView != null) {
						TextView mTextView=(TextView) view;
						if (g_bPlaying==false)
						{
							mTextView.setText("Pause");
						
							
							ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr"))
							{
								mRePlay.setAlpha(128);
								mRePlay.setVisibility(View.VISIBLE);
								//mRePlay.setBackgroundColor(0x00000000);
								
								
							}else
							{
								mRePlay.setVisibility(View.INVISIBLE);
							}
		
							try
							{
								g_nTry_Play_Times=0;
								mVideoView.start();
							}catch (Exception e) {
								Log.e(TAG, "error: " + e.getMessage(), e);
								//Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
								if (mVideoView != null) {
								mVideoView.stopPlayback();
								}
							}
			
							
							Hide_Playback_Menu();
							g_bPlaying=true;				
						}else
						{
							mVideoView.pause();
							g_bPlaying=false;
							mTextView.setText("Play");
						}
						//Hide_Playback_Menu();
					
					}
				}else if (index==2) // Favorite
				{
					
					Hide_Playback_Menu();
				}
			}
		});
		mEPG_list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> a, View view, int index,long id) {

	    		  	epg_day_list_array=Get_EPG_Day_Info(g_token,g_ezserver_ip,g_ezserver_port,g_cur_ch_no,index-7);
	    		  	
	    		  	if (epg_day_list_array.size()>0)
	    		  	{
			  		mEPG_Day_list.setAdapter(new ArrayAdapter<String>(Ovp.this, android.R.layout.simple_list_item_1,epg_day_list_array));
			  		mEPG_Day_list.setVisibility(View.VISIBLE);
			  		mEPG_Day_list.requestFocus();
		  		}
		  		
			}
		});

		mEPG_Day_list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> a, View view, int index,long id) {
				
				String szMovieName;
				String str;
				String starmark;
				Object clcikItemObj=a.getAdapter().getItem(index);
				
				str=clcikItemObj.toString();
				starmark=str.substring(1,4);
				if (starmark.equals("[*]"))
				{
				
					szMovieName=str.substring(11);
		    			if (mVideoView != null) 
					{
						mVideoView.stopPlayback();
						
					}
					Hide_Playback_Menu();
					mRePlay.setVisibility(View.INVISIBLE);
					
					Intent intent = new Intent(Ovp.this, Ovp_VOD.class);
					int i=0;
					String movie_url;
					
					intent.putExtra("token", g_token);
					intent.putExtra("ezserver_ip", g_ezserver_ip);
					intent.putExtra("http_base_port", Integer.toString(g_ezserver_port));
					intent.putExtra("http_port", Integer.toString(g_http_port));
					
					movie_url=szMovieName;
					
					Get_Movie_Duration_Bitrate(g_token,g_ezserver_ip,g_ezserver_port,movie_url);
			  		intent.putExtra("movieurl", movie_url);
					intent.putExtra("user_name", g_user_name);
					intent.putExtra("passowrd", g_password);
					intent.putExtra("duration", Double.toString(g_duration));
					intent.putExtra("bitrate", Double.toString(g_bitrate));
					
					Ovp.this.startActivityForResult(intent, 1);
				}
				//Toast.makeText(Ovp.this,szMovieName ,Toast.LENGTH_LONG).show();
				
			
			}
		});
	mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
	        
		String UnicodeString;
		String CHUicodeName;

 		if (g_nTry_Play_Times>=100)
 		{
			String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
			if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
			{
				g_m3u8_channel=1;
			}else
			{
				g_m3u8_channel=0;
			}
 			if (g_m3u8_channel==1)
 			{
				g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
			}else
			{
				g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
			}
			g_dmsg=g_churl;
	 		if (g_keycode==KEY_CHANNEL_UP)
	 		{
		 		//g_cur_ch_no=(g_cur_ch_no+1)%g_total_ch_no;
		 		g_cur_ch_no=g_cur_ch_no+1;
		 		if (g_cur_ch_no>g_total_ch_no) 
				{
					g_cur_ch_no=1;
				}
	 		}else if (g_keycode==KEY_CHANNEL_DOWN)
			{
				//g_cur_ch_no=(g_cur_ch_no-1)%g_total_ch_no;
				g_cur_ch_no=g_cur_ch_no-1;
				if (g_cur_ch_no<1) 
				{ 
					g_cur_ch_no=g_total_ch_no;
				}		
			}
		}
 	
		String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
		if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
		{
			g_m3u8_channel=1;
		}else
		{
			g_m3u8_channel=0;
		}
		if (g_m3u8_channel==1)
		{
			g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
		}else
		{
			g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
		}
		if (ch_type.equals("dvr"))
		{
			mRePlay.setAlpha(128);
			mRePlay.setVisibility(View.VISIBLE);
			
		}else
		{
			mRePlay.setVisibility(View.INVISIBLE);
		}
		mVideoView.setVideoPath(g_url);
		mVideoView.start();
		mVideoView.requestFocus();
		g_nTry_Play_Times++;
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
			           x2 = event.getX();
			           y2 = event.getY();
				   if ((x2-x1)<-10)
			           {
						g_cur_ch_no=g_cur_ch_no+1;
						g_keycode=KEY_CHANNEL_UP;
						if (g_cur_ch_no>g_total_ch_no) 
						{
							g_cur_ch_no=1;
						}
					        	if (mVideoView != null) 
					        	{
					        		mVideoView.stopPlayback();
					        	}
				        		if (g_dvr_mode==true)
							{
								queueFuture.cancel(true);
								g_dvr_menu=false;
								g_dvr_mode=false;
							}
//							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+":muxer=flv";
							String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
							{
								g_m3u8_channel=1;
							}else
							{
								g_m3u8_channel=0;
							}

							if (g_m3u8_channel==1)
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
							}else
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
							}
							g_churl=g_channel_info.GetCHFileName(g_cur_ch_no-1);
							
							playVideo();
						
						
			           }else if ((x2-x1)>10)
			           {
						g_cur_ch_no=g_cur_ch_no-1;
						g_keycode=KEY_CHANNEL_DOWN;
						if (g_cur_ch_no<1) 
						{ 
							//g_cur_ch_no=1;
							g_cur_ch_no=g_total_ch_no;
						}
					        	if (mVideoView != null) 
					        	{
					        		mVideoView.stopPlayback();
					        	}
				        		if (g_dvr_mode==true)
							{
								queueFuture.cancel(true);
								g_dvr_menu=false;
								g_dvr_mode=false;
							}
//							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+":muxer=flv";
							String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
							{
								g_m3u8_channel=1;
							}else
							{
								g_m3u8_channel=0;
							}

							if (g_m3u8_channel==1)
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
							}else
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
							}
							g_churl=g_channel_info.GetCHFileName(g_cur_ch_no-1);
							playVideo();
						
						

			           }else
			           {
			           	
            				 if ((mVideo_menu_list.getVisibility() == View.VISIBLE) ||
	 				    	(mEPG_list.getVisibility() == View.VISIBLE))
					    {
					    	mVideo_menu_list.setVisibility(View.INVISIBLE);
					    	Hide_Playback_Menu();
					    	g_dvr_menu=false;
					    }else
					    {
					    	Show_Playback_Menu();
					    	g_dvr_menu=true;
					    
					    }
			           }
			            break;
			        }
			        case android.view.MotionEvent.ACTION_DOWN: // gets called
			        {
			            x1 = event.getX();
			            y1 = event.getY();
			            break;
			        }
			    }
			    return true;	
			}
		});			
	
	mSeek.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener() {
		public void onStartTrackingTouch (SeekBar seekBar) {
			if (mVideoView != null) {
				//g_StartTrackingPos=mVideoView.getCurrentPosition(); 
				if (g_dvr_mode==false)
				{
					g_ch_pressed_time = new Time(Time.getCurrentTimezone());
					g_ch_pressed_time.setToNow();
				}else if (g_dvr_mode==true)
				{
					g_dvr_mode=false;
				}
					
			}
		}
		public void onStopTrackingTouch (SeekBar seekBar)  {
		    	
			if (mVideoView != null) {
				int nTimepercent=mSeek.getProgress();
				g_today.setToNow();
			        long dvr_buffer_millisecond=g_today.toMillis(true)-g_dvr_starting_time.toMillis(true);
	    		  	long ltimestamp=(g_today.toMillis(true)-(long)(((100-nTimepercent)*dvr_buffer_millisecond)/100));


	    		  	g_pressed_dvr_time=ltimestamp;
	    		  	g_pressed_today_time=g_today.toMillis(true);
	    		  	g_dvr_mode=true;
	    		  	ltimestamp=ltimestamp-g_dvr_start_millisecond;
				if (g_m3u8_channel==1)
				{
					g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token+"&timestamp="+ltimestamp;
				}else
				{
					g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+"&timestamp="+ltimestamp;
				}
				String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
				if (ch_type.equals("dvr"))
				{
					mRePlay.setAlpha(128);
					mRePlay.setVisibility(View.VISIBLE);
					
				}else
				{
					mRePlay.setVisibility(View.INVISIBLE);
				}
				try {
					g_nTry_Play_Times=0;
					mVideoView.setVideoPath(g_url);
					mVideoView.start();
					mVideoView.requestFocus();
				}catch (Exception e) {
					Log.e(TAG, "error: " + e.getMessage(), e);
					if (mVideoView != null) {
						mVideoView.stopPlayback();
					}
				}
				
				
				Hide_Playback_Menu();
				
				queueFuture.cancel(true); 
			}
		}
		private void Show_Current_Dvr_Time_on_Seekbar(int how_many)
		{

		    long tempmilsec;
		    long dvr_buffer_millisecond;
		  
		    dvr_buffer_millisecond=g_today.toMillis(true)-g_dvr_starting_time.toMillis(true);
		    tempmilsec=g_today.toMillis(true)-(long)(((100-how_many)*dvr_buffer_millisecond)/100);
		    g_SeekTime.set(tempmilsec);
		    mCurrent_Dvr_Time.setText(g_SeekTime.format("%m/%d %k:%M:%S"));
		    int seek_label_pos = (int)((float)(mSeek.getMeasuredWidth()) * ((float)how_many / 110f));
		    mCurrent_Dvr_Time.setX(seek_label_pos);
		}
		
		public void onProgressChanged(SeekBar seekBar, int  progress, boolean  fromUser) { 
			Show_Current_Dvr_Time_on_Seekbar(progress);
		}
		
	});
	mToLive.setOnClickListener(new OnClickListener() {
		public void onClick(View view) {
			if (mVideoView != null) {
				g_dvr_mode=false;
				if (g_m3u8_channel==1)
				{
					g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
				}else
				{
					g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
				}
				String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
				if (ch_type.equals("dvr"))
				{
					mRePlay.setAlpha(128);
					mRePlay.setVisibility(View.VISIBLE);
					//mRePlay.setBackgroundColor(0x00000000);
					
					
				}else
				{
					mRePlay.setVisibility(View.INVISIBLE);
				}

				try
				{
					g_nTry_Play_Times=0;
					mVideoView.setVideoPath(g_url);
					mVideoView.start();
					mVideoView.requestFocus();
				}catch (Exception e) {
					Log.e(TAG, "error: " + e.getMessage(), e);
					//Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
					if (mVideoView != null) {
						mVideoView.stopPlayback();
					}
				}

				Hide_Playback_Menu();
			}
		}
	});			
	runOnUiThread(new Runnable(){
			public void run() {
				playVideo();
			}
			
		});
		  
		
	}
	private class AsyncTask_playVideo extends AsyncTask<String, String, String> {
	        protected String doInBackground(String... async_input) {
			mVideoView.start();
			g_bPlaying=true;
			
	           return null;
	        }
	        
        }

	private void playVideo() {
		try {
			String path;
			String brandname=android.os.Build.BRAND;
			String ch_type;
			
			path = g_url;
			
			if (path == null || path.length() == 0) {
				Toast.makeText(Ovp.this, "File URL/path is empty",
						Toast.LENGTH_LONG).show();

			} else {
				// If the path has not changed, just start the media player
				if (path.equals(current) && mVideoView != null) {
					Hide_Playback_Menu();
					try
					{
						g_nTry_Play_Times=0;
						mVideoView.setVideoPath(path);
						mVideoView.start();
						mVideoView.requestFocus();
						
					}catch (Exception e) {
					Log.e(TAG, "error: " + e.getMessage(), e);
					if (mVideoView != null) {
						mVideoView.stopPlayback();
					}
				}

					return;
				}
	

	

				
				current = path;
				Hide_Playback_Menu();
				g_ch_list_visible=false;
		
				g_dmsg=g_churl;
			        ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
				if (ch_type.equals("dvr"))
				{
					mRePlay.setAlpha(128);
					mRePlay.setVisibility(View.VISIBLE);
					//mRePlay.setBackgroundColor(0x00000000);
					
					
				}else
				{
					mRePlay.setVisibility(View.INVISIBLE);
				}
			String UnicodeString;
			String CHUicodeName;
			
			
			UnicodeString=g_dmsg.replaceAll("%u", "\\\\u");
			CHUicodeName=decodeUnicode(UnicodeString);
				g_nTry_Play_Times=0;
				mVideoView.setVideoPath(path);
				new AsyncTask_playVideo().execute();
				mVideoView.requestFocus();
			}
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
			if (mVideoView != null) {
				mVideoView.stopPlayback();
			}
		}
	}
	private int  Get_Movie_Duration_Bitrate(String token, String ezserver_ip, int ezserver_port, String moviename)
	{

		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		String movielist = null;
		String file_size;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		int total_movie_no=0;
		String sduration;
		String sbitrate;
		double duration;
		double bitrate;
		try 
		{
			
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			
			
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/get_movie_duration_bitrate?token="+token+"&movie_name="+moviename+"\r\nUser-Agent=EZhometech\r\n\r\n";
			outbuffer=sendbuffer.getBytes();
			outstream.write(outbuffer);
		
			// Receive EZserver HTTP Response
			int toread=1024;
			int len=0;
			for(int index=0;index<toread;)
			{
				len=instream.read(inbuffer,index,toread-index);
				if (len<0) break;
				index=index+len;
			}
			revbuffer= new String(inbuffer);
			// Close Socket
			outstream.close();
			instream.close();
			
	
			// Get token
			if (revbuffer.contains("200 OK")== true)
			{
				movielist=revbuffer.substring(revbuffer.indexOf("duration="));
					keyword_pos=movielist.indexOf("duration=");
					if (keyword_pos!=-1)
					{
	
						crln_pos=movielist.indexOf("\r\n",keyword_pos);
						sduration= movielist.substring (keyword_pos+9,crln_pos);						
						movielist=movielist.substring(crln_pos);
						keyword_pos=movielist.indexOf("bitrate=");
						if (keyword_pos!=-1)
						{
							
							crln_pos=movielist.indexOf("\r\n",keyword_pos);
							sbitrate= movielist.substring (keyword_pos+8,crln_pos);
							g_bitrate=Double.parseDouble(sbitrate);
							g_duration=Double.parseDouble(sduration);
							movielist=movielist.substring(crln_pos);
						
							
						}else
						{
							return 0;
						}
						
						
						total_movie_no++;
					} else
					{
						return 0;
					}
				
				return 1;
			}else
			{
				return 0;
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return 0;
		}
		
	}
	private boolean Show_Playback_Menu()
	{
		String ch_type;

		g_dvr_menu=true;
		ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
		if (ch_type.equals("dvr"))
		{
			mRePlay.setVisibility(View.INVISIBLE);
			mSeek.setVisibility(View.VISIBLE);
			if (bSeekDirection==TOUCH_MODE)
			{
				mVideo_menu_list.setVisibility(View.VISIBLE);
			}else
			{
				
			}
			mCurrent_Live_Time.setVisibility(View.VISIBLE);
			mCurrent_Dvr_Time.setVisibility(View.VISIBLE);
			mDvr_Starting_Time.setVisibility(View.VISIBLE);
			g_today = new Time(Time.getCurrentTimezone());
			g_today.setToNow();
			mCurrent_Live_Time.setText(g_today.format("%k:%M:%S")); 
			if (g_dvr_mode==false)
			{			
				g_how_many=100;
				mSeek.setProgress((int) g_how_many);
			}else
			{
				if (bSeekDirection==TOUCH_MODE)
				{
					mToLive.setVisibility(View.VISIBLE);
				}
			}
			
			Get_Channel_DVR_Uptime_Seconds(g_token,g_ezserver_ip,g_ezserver_port);	
			g_server_start_millisecond=g_today.toMillis(true)-g_server_uptime_seconds*1000;
			// Check if DVR buffer is full.
			if (g_server_dvr_uptime_seconds > g_channel_dvr_buffer_peroid)
			{
				g_dvr_start_millisecond=g_today.toMillis(true)-g_channel_dvr_buffer_peroid*1000;
			}else
			{
				g_dvr_start_millisecond=g_today.toMillis(true)-g_server_dvr_uptime_seconds*1000;
			}
			

			g_dvr_starting_time.set(g_dvr_start_millisecond);
			mDvr_Starting_Time.setText(g_dvr_starting_time.format("%m/%d %k:%M:%S")); 
			scheduleTaskExecutor= Executors.newScheduledThreadPool(5);
				
			    // This schedule a task to run every 10 minutes:
			    queueFuture=scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			      public void run() {
			        runOnUiThread(new Runnable() {
			          public void run() {
			            // update your UI component here.
					g_today.setToNow();
					mCurrent_Live_Time.setText(g_today.format("%m/%d %k:%M:%S"));
					// Check if dvr buffer is full, if full, need to move the dvr starting time
					if ((g_today.toMillis(true)-g_server_start_millisecond) > g_channel_dvr_buffer_peroid*1000)
					{
						g_dvr_start_millisecond=g_today.toMillis(true)-g_channel_dvr_buffer_peroid*1000;
						
					}else
					{
						g_dvr_start_millisecond=g_server_start_millisecond;
					}
					
					
					g_dvr_starting_time.set(g_dvr_start_millisecond);
					mDvr_Starting_Time.setText(g_dvr_starting_time.format("%m/%d %k:%M:%S")); 
	
					if (g_dvr_mode)
					{
						long ltimestamp=(g_pressed_today_time-g_pressed_dvr_time);
						long dvr_buffer_time=g_today.toMillis(true)-g_dvr_start_millisecond;
						long current_dvr_time;
						int dvr_how_many=100-(int) ((ltimestamp*100.0)/dvr_buffer_time);
						mSeek.setProgress(dvr_how_many);
						current_dvr_time=g_today.toMillis(true)-(long)(((100-dvr_how_many)*dvr_buffer_time)/100);
						g_current_dvr_time.set(current_dvr_time);
						mCurrent_Dvr_Time.setText(g_current_dvr_time.format("%m/%d %k:%M:%S")); 						
						
					}
					
				   }
			        });
			      }
			    }, 0, 1, TimeUnit.SECONDS);
			
		
		}else
		{
			mVideo_menu_list.setVisibility(View.VISIBLE);
			mVideo_menu_list.requestFocus();
		}
		
		
		if (bSeekDirection==TOUCH_MODE)
		{
		}
		
	    return true;
	}
	private boolean Hide_Playback_Menu()
	{
		String ch_type;
		g_dvr_menu=false;
		ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
		if (ch_type.equals("dvr"))
		{
			mRePlay.setVisibility(View.VISIBLE);
		}
		mSeek.setVisibility(View.INVISIBLE);
		mCurrent_Live_Time.setVisibility(View.INVISIBLE);
		mCurrent_Dvr_Time.setVisibility(View.INVISIBLE);
		mDvr_Starting_Time.setVisibility(View.INVISIBLE);
		
		mVideo_menu_list.setVisibility(View.INVISIBLE);
		mEPG_list.setVisibility(View.INVISIBLE);
		bEPG_List_Flag=false;
		mEPG_Day_list.setVisibility(View.INVISIBLE);
		mToLive.setVisibility(View.INVISIBLE);
		mChannelNoInputField.setVisibility(View.INVISIBLE);
		return true;
	}  
	public class Channel_Info {
		private int MAX_CHANNEL_NO=10000;
		private int TotalChannelNo=0;
	    	public String ChannelName[]=new String[MAX_CHANNEL_NO];
	    	public String ChannelType[]=new String[MAX_CHANNEL_NO];
	    	
	    	// Add functions	
	    	public boolean AddCHFileName(int index, String channelname)
	    	{
	     		ChannelName[index]=channelname;
	    		TotalChannelNo++;
	    		return true;
	    	}  
	    	public String GetCHFileName(int index)
	    	{
	    		
	    		return ChannelName[index];
	    	}
	    	public int GetCHNoByFileName(String channelname)
	    	{
	    		int i=1;
	    		int bFlag=0;
	    		for (;i<=TotalChannelNo;i++)
	    		{
	    			if (ChannelName[i-1].equals(channelname))
	    			{
	    				bFlag=1;
	    				break;
	    			}	    			
	    		}
	    		if (bFlag==0)
	    		{
	    			return 0;
	    		}else
	    		{
	    		   	return i;
	    		}
	    	}
	 	
	    	public boolean AddChannelType(int index, String channeltype)
	    	{
	     		ChannelType[index]=channeltype;
	    		return true;
	    	}   	
	    	// Get Channel Functions	
	   	public int GetChannelTotalNo()
	    	{
	    		return TotalChannelNo;
	    	}
	     	public String GetChannelType(int index)
	    	{
	    		
	    		return ChannelType[index];
	    	}
 	
}
	private boolean Get_Channel_Uptime_Seconds(String token, String ezserver_ip, int ezserver_port){

		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		Socket socket;
		String chlist = null;
		int keyword_pos=0;
		int crln_pos=0;
		String Year, Month, Day, Hour, Minute, Second;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/inquery_channel_uptime_seconds?token="+token+"&ch_no="+g_cur_ch_no+"\r\n\r\n";
			outbuffer=sendbuffer.getBytes();
			outstream.write(outbuffer);
		
			// Receive EZserver HTTP Response
			int toread=1024;
			int len=0;
			for(int index=0;index<toread;)
			{
				len=instream.read(inbuffer,index,toread-index);
				if (len<0) break;
				index=index+len;
			}
			revbuffer= new String(inbuffer);
			// Close Socket
			outstream.close();
			instream.close();
			
			
			if (revbuffer.contains("200 OK")== true)
			{
					chlist=revbuffer.substring(revbuffer.indexOf("Uptime="));
					keyword_pos=chlist.indexOf("Uptime=");
					if (keyword_pos!=-1)
					{
						crln_pos=chlist.indexOf("\r\n",keyword_pos);
						String server_uptime_seconds= chlist.substring (keyword_pos+7,crln_pos);
						g_server_uptime_seconds=Integer.valueOf(server_uptime_seconds);
						
					} else
					{
						return false;
					}
					
				return true;
			}else
			{
				return false;
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return false;
		}
		
	}
	private boolean Get_Channel_DVR_Uptime_Seconds(String token, String ezserver_ip, int ezserver_port){

		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		Socket socket;
		String chlist = null;
		int keyword_pos=0;
		int crln_pos=0;
		String Year, Month, Day, Hour, Minute, Second;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/get_channel_dvr_information?token="+token+"&ch_no="+g_cur_ch_no+"\r\n\r\n";
			outbuffer=sendbuffer.getBytes();
			outstream.write(outbuffer);
		
			// Receive EZserver HTTP Response
			int toread=1024;
			int len=0;
			for(int index=0;index<toread;)
			{
				len=instream.read(inbuffer,index,toread-index);
				if (len<0) break;
				index=index+len;
			}
			revbuffer= new String(inbuffer);
			// Close Socket
			outstream.close();
			instream.close();
			
			
			if (revbuffer.contains("200 OK")== true)
			{
					chlist=revbuffer.substring(revbuffer.indexOf("DvrUptime="));
					keyword_pos=chlist.indexOf("DvrUptime=");
					if (keyword_pos!=-1)
					{
						crln_pos=chlist.indexOf("\r\n",keyword_pos);
						String server_dvr_uptime_seconds= chlist.substring (keyword_pos+10,crln_pos);
						g_server_dvr_uptime_seconds=Integer.valueOf(server_dvr_uptime_seconds);
						
						chlist=chlist.substring(crln_pos);
						keyword_pos=chlist.indexOf("DvrBufferPeriod=");
						
						if (keyword_pos!=-1)
						{
							
							crln_pos=chlist.indexOf("\r\n",keyword_pos);
							String server_dvr_buffer_peroid= chlist.substring (keyword_pos+16,crln_pos);
							g_channel_dvr_buffer_peroid=Integer.valueOf(server_dvr_buffer_peroid);
							
							chlist=chlist.substring(crln_pos);
							keyword_pos=chlist.indexOf("Uptime=");
							if (keyword_pos!=-1)
							{
								crln_pos=chlist.indexOf("\r\n",keyword_pos);
								String server_uptime_seconds= chlist.substring (keyword_pos+7,crln_pos);
								g_server_uptime_seconds=Integer.valueOf(server_uptime_seconds);
							}else
							{
								return false;
							}
						}else
						{
							return false;
						}
						
						
							
						
					} else
					{
						return false;
					}
					
				return true;
			}else
			{
				return false;
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return false;
		}
		
	}
	
	private ArrayList<String> Get_EPG_Date(){

		ArrayList<String> epg_date_list_in_array = new ArrayList<String>();
		String TempBuffer = null;
		int i;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy (EEE)"); 
		Calendar c = Calendar.getInstance();
		String str;
		
		c.add(Calendar.DAY_OF_MONTH, -7); 
		try 
		{
			
			for (i=-7;i<7;i++)
			{
				str = df.format(c.getTime());
				TempBuffer=str;
				epg_date_list_in_array.add(TempBuffer);		
				c.add(Calendar.DAY_OF_MONTH, 1); 
			}
			
			return epg_date_list_in_array;
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return null;
		}
		
	}
	
	private ArrayList<String>  Get_EPG_Day_Info(String token, String ezserver_ip, int ezserver_port, int nCHNo, int ndelta){

		String TempBuffer = null;
		int FirstContentLen=0;
		byte netbuffer[] = new byte[1024];
		String netrevbuffer = null;
		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[];
		byte inbuffer1[];
		byte outbuffer[] = new byte[1024];
		String chlist = null;
		ArrayList<String> day_epg_list_in_array = new ArrayList<String>();
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		
		String starthour;
		String title;
		String rec_type;
		String program_info;
		
		
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	

			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/get_day_epg?token="+token+"&ch_no="+nCHNo+"&delta="+ndelta+"\r\nUser-Agent=EZhometech\r\n\r\n";

			outbuffer=sendbuffer.getBytes();
			outstream.write(outbuffer);
		
			// Receive EZserver HTTP Response
			int toread=0;
			int len=0;
			int HTTPContentLength=0;
			int index=0;
			
			// read first data
			len=instream.read(netbuffer,0,1024);
			netrevbuffer= new String(netbuffer);

			if (netrevbuffer.contains("Content-Length: ")== false)
			{
				return day_epg_list_in_array;
				
			}
					
			TempBuffer=netrevbuffer.substring(netrevbuffer.indexOf("Content-Length: "));
			keyword_pos=TempBuffer.indexOf("Content-Length: ");

			crln_pos=TempBuffer.indexOf("\r\n",keyword_pos);
			HTTPContentLength= Integer.valueOf(TempBuffer.substring(keyword_pos+16,crln_pos));
			inbuffer= new byte[HTTPContentLength];
			
			// get data after \r\n\r\n
			crln_pos=netrevbuffer.indexOf("\r\n\r\n",keyword_pos);
			crln_pos=crln_pos+4;
			FirstContentLen=len-crln_pos;

			if (FirstContentLen==0)
			{
				toread=HTTPContentLength;
			}else if (FirstContentLen>0)
			{
				System.arraycopy(netbuffer,crln_pos,inbuffer,0,FirstContentLen);
				// get more
				// allocate buffer for incoming data
				toread=HTTPContentLength-FirstContentLen;
			}

			inbuffer1= new byte[toread];

			for(index=0;index<toread;)
			{
				len=instream.read(inbuffer1,index,toread-index);
				if (len<=0) break;
				index=index+len;
			}
			System.arraycopy(inbuffer1,0,inbuffer,FirstContentLen,toread);
			revbuffer= new String(inbuffer);
			// Close Socket
			outstream.close();
			instream.close();
			
				chlist=revbuffer;
				do {
					keyword_pos=chlist.indexOf("starttime=");
					if (keyword_pos!=-1)
					{
						crln_pos=chlist.indexOf("\r\n",keyword_pos);
						starthour= chlist.substring (keyword_pos+21,crln_pos-3);
						chlist=chlist.substring(crln_pos);
						keyword_pos=chlist.indexOf("title=");
						if (keyword_pos!=-1)
						{
							crln_pos=chlist.indexOf("\r\n",keyword_pos);
							title= chlist.substring (keyword_pos+6,crln_pos);
							String UnicodeString;
							String UnicodeName;
		        				UnicodeString=title.replaceAll("%u", "\\\\u");
		        				UnicodeName=decodeUnicode(UnicodeString);
											
						}else
						{
							break;
						}						
						chlist=chlist.substring(crln_pos);
						keyword_pos=chlist.indexOf("rec=");
						if (keyword_pos!=-1)
						{
							crln_pos=chlist.indexOf("\r\n",keyword_pos);
							rec_type= chlist.substring (keyword_pos+4,crln_pos);
							chlist=chlist.substring(crln_pos);
						}else
						{
							break;
						}
					} else
					{
						break;
					}
					if (rec_type.equals("1"))
					{
						program_info=" [*] "+starthour+" "+title;
					}else
					{
						program_info="     "+starthour+" "+title;
					}
					day_epg_list_in_array.add(program_info);
				} while(true);	
				
				return day_epg_list_in_array;
				
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return null;
				
		}
		
	}	
	private void Get_CH_information(String token, String ezserver_ip, int ezserver_port){

		String TempBuffer = null;
		int FirstContentLen=0;
		byte netbuffer[] = new byte[1024];
		String netrevbuffer = null;
		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[];
		byte inbuffer1[];
		byte outbuffer[] = new byte[1024];
		String chlist = null;
		//ArrayList<String> ch_list_in_array = new ArrayList<String>();
		String ch_name;
		String ch_no_name;
		int ch_no;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		String ch_type;
		int ch_no_temp;
		
		
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	

			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/query_channel_list?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";

			outbuffer=sendbuffer.getBytes();
			outstream.write(outbuffer);
		
			// Receive EZserver HTTP Response
			int toread=0;
			int len=0;
			int HTTPContentLength=0;
			int index=0;
			
			// read first data
			len=instream.read(netbuffer,0,1024);
			netrevbuffer= new String(netbuffer);

	
			if (netrevbuffer.contains("Content-Length: ")== false)
			{
//				return null;
				return;
			}
						
			TempBuffer=netrevbuffer.substring(netrevbuffer.indexOf("Content-Length: "));
			keyword_pos=TempBuffer.indexOf("Content-Length: ");

			crln_pos=TempBuffer.indexOf("\r\n",keyword_pos);
			HTTPContentLength= Integer.valueOf(TempBuffer.substring(keyword_pos+16,crln_pos));
			inbuffer= new byte[HTTPContentLength];
			
			// get data after \r\n\r\n
			crln_pos=netrevbuffer.indexOf("\r\n\r\n",keyword_pos);
			crln_pos=crln_pos+4;
			FirstContentLen=len-crln_pos;

			if (FirstContentLen==0)
			{
				toread=HTTPContentLength;
			}else if (FirstContentLen>0)
			{
				System.arraycopy(netbuffer,crln_pos,inbuffer,0,FirstContentLen);
				// get more
				// allocate buffer for incoming data
				toread=HTTPContentLength-FirstContentLen;
			}

			inbuffer1= new byte[toread];

			for(index=0;index<toread;)
			{
				len=instream.read(inbuffer1,index,toread-index);
				if (len<=0) break;
				index=index+len;
			}
			System.arraycopy(inbuffer1,0,inbuffer,FirstContentLen,toread);
			revbuffer= new String(inbuffer);
			// Close Socket
			outstream.close();
			instream.close();
			
				ch_no_temp=0;
				chlist=revbuffer.substring(revbuffer.indexOf("CH="));
				do {
					keyword_pos=chlist.indexOf("name=");
					if (keyword_pos!=-1)
					{
						crln_pos=chlist.indexOf("\r\n",keyword_pos);
						ch_name= chlist.substring (keyword_pos+5,crln_pos);
						String UnicodeString;
						String UnicodeCHName;
						String UnicodeCHNameWithNo;
	        				UnicodeString=ch_name.replaceAll("%u", "\\\\u");
	        				UnicodeCHName=decodeUnicode(UnicodeString);
						
						ch_no=ch_no_temp+1;
						ch_no_name=ch_no+": " +ch_name;
						UnicodeCHNameWithNo=ch_no+": " +UnicodeCHName;
						
						g_channel_info.AddCHFileName(ch_no_temp,ch_name);
						chlist=chlist.substring(crln_pos);
						keyword_pos=chlist.indexOf("type=");
						if (keyword_pos!=-1)
						{
							crln_pos=chlist.indexOf("\r\n",keyword_pos);
							ch_type= chlist.substring (keyword_pos+5,crln_pos);
							keyword_pos=ch_type.indexOf("delay");
							if (keyword_pos==0)
							{
								ch_type="delay";
							}
							
							g_channel_info.AddChannelType(ch_no_temp,ch_type);
							chlist=chlist.substring(crln_pos);
						}else
						{
							break;
						}
						
					
						ch_no_temp++;
					} else
					{
						break;
					}
				} while(true);	
				g_total_ch_no=ch_no_temp;
				return;
//				return ch_list_in_array;
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				//return null;
				return;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private boolean Show_Channel_No()
	{
		mChannelNoInputField.setCursorVisible(false);
		mChannelNoInputField.setVisibility(View.VISIBLE);
		mChannelNoInputField.requestFocus();

		g_today = new Time(Time.getCurrentTimezone());
		g_today.setToNow();
		mCurrent_Live_Time.setText(g_today.format("%k:%M:%S")); 
	
				    

	    return true;
	}
	@Override
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
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		String ch_type;
		
	
		//Toast.makeText(Ovp.this, "KEYCODE: ["+keyCode+"]",Toast.LENGTH_LONG).show();
		if ((keyCode == KeyEvent.KEYCODE_BACK)||(keyCode == KeyEvent.KEYCODE_MEDIA_STOP))
		{
			if ((mVideo_menu_list.getVisibility() == View.VISIBLE) ||
				(mSeek.getVisibility() == View.VISIBLE) ||
	 			    (mEPG_list.getVisibility() == View.VISIBLE))
	 		{
	 			Hide_Playback_Menu();
	 			return true;
	 		}else
	 		{
				if (mVideoView != null) {
					mVideoView.stopPlayback();
					setResult(RESULT_OK, intent);
					 finish();
				
				}
			}
		}else if ((keyCode == KeyEvent.KEYCODE_0)||(keyCode == KeyEvent.KEYCODE_1)||(keyCode == KeyEvent.KEYCODE_2)||(keyCode == KeyEvent.KEYCODE_3)||
			(keyCode == KeyEvent.KEYCODE_4)||(keyCode == KeyEvent.KEYCODE_5)||(keyCode == KeyEvent.KEYCODE_6)||
			(keyCode == KeyEvent.KEYCODE_7)||(keyCode == KeyEvent.KEYCODE_8)||(keyCode == KeyEvent.KEYCODE_9))

		{
			if (bFirstInputChannelNo==true)
			{
				    Show_Channel_No();
				    int ntemp=keyCode-7;
				    String ch_no=Integer.toString(ntemp); 

				    mChannelNoInputField.setText(ch_no);
				    mChannelNoInputField.setSelection(1);
				    
				    bFirstInputChannelNo=false;
				    InputChannelNo_scheduleTaskExecutor= Executors.newScheduledThreadPool(1);
					
				    // This schedule a task to run every 10 minutes:
				    InputChannelNo_queueFuture=InputChannelNo_scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
				      public void run() {
				        runOnUiThread(new Runnable() {
				          public void run() {
				            // update your UI component here.
				            InputChannelNo_Delay++;
				            if (InputChannelNo_Delay==3)
				            {
				            	String ch_no=mChannelNoInputField.getText().toString();
				            	int nch_no;
				            	nch_no=Integer.valueOf(ch_no);
				            	InputChannelNo_Delay=0;
				            	if ((nch_no>0)&&(nch_no<=g_total_ch_no))
				            	{
					        	if (mVideoView != null) 
					        	{
					        		mVideoView.stopPlayback();
					        	}
					        	g_cur_ch_no=nch_no;
//							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+":muxer=flv";
							String ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
							{
								g_m3u8_channel=1;
							}else
							{
								g_m3u8_channel=0;
							}

							if (g_m3u8_channel==1)
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
							}else
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
							}
							ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr"))
							{
								mRePlay.setAlpha(128);
								mRePlay.setVisibility(View.VISIBLE);
								
								
							}else
							{
								mRePlay.setVisibility(View.INVISIBLE);
							}
										
							try
							{
								g_nTry_Play_Times=0;
								mVideoView.setVideoPath(g_url);
								mVideoView.start();
								mVideoView.requestFocus();
							}catch (Exception e) {
								Log.e(TAG, "error: " + e.getMessage(), e);
								Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
								if (mVideoView != null) {
									mVideoView.stopPlayback();
								}
							}

							//InputChannelSelectionReady=true;
						}
						bFirstInputChannelNo=true;
						mChannelNoInputField.setVisibility(View.INVISIBLE);
						mChannelNoInputField.clearFocus();
						InputChannelNo_queueFuture.cancel(true);
						

				            }
				        
					   }
				        });
				      }
				    }, 0, 1, TimeUnit.SECONDS);
				    
			}else if (bFirstInputChannelNo==false)
			{
				InputChannelNo_Delay=0;
			}
		}
			
		ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
		if (ch_type.equals("dvr"))
		{
			if ((g_dvr_mode==true)&&(g_dvr_menu==false))
			{
				if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) || (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))
				{
					Show_Playback_Menu();
					g_dvr_menu=true;
					return super.onKeyDown(keyCode, event);
				}
			}
			if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN) && (mVideoView.isPlaying()==true)) {
			
				if ((mVideo_menu_list.getVisibility() == View.VISIBLE) ||
	 			    (mEPG_list.getVisibility() == View.VISIBLE))
	 			{
	 				
	 			}else
	 			{
					if (g_dvr_mode==true)
					{
						queueFuture.cancel(true);
						g_dvr_menu=false;
						g_dvr_mode=false;
					}
					Hide_Playback_Menu();
					g_keycode=KEY_CHANNEL_DOWN;
					
					g_cur_ch_no=g_cur_ch_no-1;
					if (g_cur_ch_no<1) 
					{
						g_cur_ch_no=g_total_ch_no;
					}
				        	if (mVideoView != null) 
				        	{
				        		mVideoView.stopPlayback();
				        	}
	//					g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+":muxer=flv";
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
						{
							g_m3u8_channel=1;
						}else
						{
							g_m3u8_channel=0;
						}
	
						if (g_m3u8_channel==1)
						{
							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
						}else
						{
							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
						}
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr"))
						{
							mRePlay.setAlpha(128);
							mRePlay.setVisibility(View.VISIBLE);
							//mRePlay.setBackgroundColor(0x00000000);
							
							
						}else
						{
							mRePlay.setVisibility(View.INVISIBLE);
						}
											
						try
						{
							g_nTry_Play_Times=0;
							mVideoView.setVideoPath(g_url);
							mVideoView.start();
							mVideoView.requestFocus();
						}catch (Exception e) {
							Log.e(TAG, "error: " + e.getMessage(), e);
							Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
							if (mVideoView != null) {
								mVideoView.stopPlayback();
							}
						}
	
			
					g_dmsg="Channel: "+g_cur_ch_no;
					Toast.makeText(Ovp.this, g_dmsg,200).show();
					bSeekDirection=DPAD_NORMAL;
				}
			} else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP) && (mVideoView.isPlaying()==true)) {
				if ((mVideo_menu_list.getVisibility() == View.VISIBLE) ||
	 			    (mEPG_list.getVisibility() == View.VISIBLE))
	 			{
	 				
	 			}else
	 			{
					if (g_dvr_mode==true)
					{
						queueFuture.cancel(true);
						g_dvr_menu=false;
						g_dvr_mode=false;
					}
					Hide_Playback_Menu();
					g_keycode=KEY_CHANNEL_UP;
					g_cur_ch_no=g_cur_ch_no+1;
					if (g_cur_ch_no>g_total_ch_no) 
					{
						g_cur_ch_no=1;
					}
				        	if (mVideoView != null) 
				        	{
				        		mVideoView.stopPlayback();
				        	}
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
						{
							g_m3u8_channel=1;
						}else
						{
							g_m3u8_channel=0;
						}
	
						if (g_m3u8_channel==1)
						{
							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
						}else
						{
							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
						}
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr"))
						{
							mRePlay.setAlpha(128);
							mRePlay.setVisibility(View.VISIBLE);
							//mRePlay.setBackgroundColor(0x00000000);
							
							
						}else
						{
							mRePlay.setVisibility(View.INVISIBLE);
						}
											
						try
						{
							g_nTry_Play_Times=0;
							mVideoView.setVideoPath(g_url);
							mVideoView.start();
							mVideoView.requestFocus();
						}catch (Exception e) {
							Log.e(TAG, "error: " + e.getMessage(), e);
							Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
							if (mVideoView != null) {
								mVideoView.stopPlayback();
							}
						}
	
					g_dmsg="Channel: "+g_cur_ch_no;
					Toast.makeText(Ovp.this, g_dmsg,200).show();
					bSeekDirection=DPAD_NORMAL;
				}
			}else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && (mVideoView.isPlaying()==true)) {
				if (mEPG_list.getVisibility() == View.VISIBLE)
				{
				}else
					{
					bSeekDirection=DPAD_FF_SEEK;
					g_dvr_mode=false;
					if (g_dvr_menu==false)
					{
						Show_Playback_Menu();
						g_ch_pressed_time = new Time(Time.getCurrentTimezone());
						g_ch_pressed_time.setToNow();
						g_dvr_menu=true;
					}
					
					
					g_how_many+=10;
					if (g_how_many>=100)
					{
						g_how_many=100;
					}
					mSeek.setProgress((int) g_how_many);
				}
	
			} else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && (mVideoView.isPlaying()==true)) {
				if (mEPG_list.getVisibility() == View.VISIBLE)
				{
				}else
					{
					bSeekDirection=DPAD_BK_SEEK;
					g_dvr_mode=false;
					if (g_dvr_menu==false)
					{
						g_ch_pressed_time = new Time(Time.getCurrentTimezone());
						g_ch_pressed_time.setToNow();
						Show_Playback_Menu();
						g_dvr_menu=true;
						
		
					}
					g_how_many-=10;
					if (g_how_many<=0)
					{
						g_how_many=0;
					}
					mSeek.setProgress((int) g_how_many);
				}
			} else if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)||(keyCode == KeyEvent.KEYCODE_ENTER)) {
				if ((bSeekDirection==DPAD_FF_SEEK) || (bSeekDirection==DPAD_BK_SEEK))
				{
					if (g_how_many==100)
					{
						if (mVideoView != null) {
							g_dvr_mode=false;
//							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+":muxer=flv";
							ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
							{
								g_m3u8_channel=1;
							}else
							{
								g_m3u8_channel=0;
							}

							if (g_m3u8_channel==1)
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
							}else
							{
								g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
							}
							ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
							if (ch_type.equals("dvr"))
							{
								mRePlay.setAlpha(128);
								mRePlay.setVisibility(View.VISIBLE);
								//mRePlay.setBackgroundColor(0x00000000);
								
								
							}else
							{
								mRePlay.setVisibility(View.INVISIBLE);
							}
										

							try
							{
								g_nTry_Play_Times=0;
								mVideoView.setVideoPath(g_url);
								mVideoView.start();
								mVideoView.requestFocus();
							}catch (Exception e) {
								Log.e(TAG, "error: " + e.getMessage(), e);
								Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
								if (mVideoView != null) {
									mVideoView.stopPlayback();
								}
							}

							g_dvr_mode=false;
						}
					}else
					{
						g_today.setToNow();
					        long dvr_buffer_millisecond=g_today.toMillis(true)-g_dvr_starting_time.toMillis(true);

			    		  	long ltimestamp=(g_today.toMillis(true)-(long)(((100-g_how_many)*dvr_buffer_millisecond)/100));
			    		  	g_pressed_dvr_time=ltimestamp;
			    		  	g_pressed_today_time=g_today.toMillis(true);
			    		  	g_dvr_mode=true;
			    		  	ltimestamp=ltimestamp-g_dvr_start_millisecond;
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
						{
							g_m3u8_channel=1;
						}else
						{
							g_m3u8_channel=0;
						}

						if (g_m3u8_channel==1)
						{
							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token+"&timestamp="+ltimestamp;
						}else
						{
							g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+"&timestamp="+ltimestamp;
						}
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr"))
						{
							mRePlay.setAlpha(128);
							mRePlay.setVisibility(View.VISIBLE);
							//mRePlay.setBackgroundColor(0x00000000);
							
							
						}else
						{
							mRePlay.setVisibility(View.INVISIBLE);
						}
										

						try
						{
							g_nTry_Play_Times=0;
							mVideoView.setVideoPath(g_url);
							mVideoView.start();
							mVideoView.requestFocus();
						}catch (Exception e) {
							Log.e(TAG, "error: " + e.getMessage(), e);
							Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
							if (mVideoView != null) {
								mVideoView.stopPlayback();
							}
						}
				
						queueFuture.cancel(true); 
					}
					Hide_Playback_Menu();
					g_dvr_menu=false;
					bSeekDirection=DPAD_NORMAL;
					
				}else
				{
					if (g_dvr_menu==false)
					{
						mVideo_menu_list.setVisibility(View.VISIBLE);
						mVideo_menu_list.requestFocus();
	
					}else if (g_dvr_menu==true)
					{
						queueFuture.cancel(true); 
						Hide_Playback_Menu();
						g_dvr_menu=false;
						ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
						if (ch_type.equals("dvr"))
						{
							mRePlay.setAlpha(128);
							mRePlay.setVisibility(View.VISIBLE);
							//mRePlay.setBackgroundColor(0x00000000);
							
							
						}else
						{
							mRePlay.setVisibility(View.INVISIBLE);
						}
										

						if (g_bPlaying==false)
						{
							try
							{
								g_nTry_Play_Times=0;
								mVideoView.start();
							}catch (Exception e) {
								Log.e(TAG, "error: " + e.getMessage(), e);
								Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
								if (mVideoView != null) {
									mVideoView.stopPlayback();
								}
							}

						}
						g_bPlaying=true;
						bSeekDirection=DPAD_NORMAL;
					}
					
	
				}
				
							
			}
		}else // if (ch_type.equals("dvr"))
		{
			if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN) && (mVideoView.isPlaying()==true)) {
				if (g_dvr_menu==true) g_dvr_menu=false;
//				g_keycode=KEY_CHANNEL_UP;
				g_keycode=KEY_CHANNEL_DOWN;
				g_cur_ch_no=g_cur_ch_no-1;
				if (g_cur_ch_no<1) 
				{
					g_cur_ch_no=g_total_ch_no;
				}
			        	if (mVideoView != null) 
			        	{
			        		mVideoView.stopPlayback();
			        	}
//					g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token+":muxer=flv";
					ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
					if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
					{
						g_m3u8_channel=1;
					}else
					{
						g_m3u8_channel=0;
					}

					if (g_m3u8_channel==1)
					{
						g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
					}else
					{
						g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
					}
					ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
					if (ch_type.equals("dvr"))
					{
						mRePlay.setAlpha(128);
						mRePlay.setVisibility(View.VISIBLE);
						//mRePlay.setBackgroundColor(0x00000000);
						
						
					}else
					{
						mRePlay.setVisibility(View.INVISIBLE);
					}
								

					try
					{
						g_nTry_Play_Times=0;
						mVideoView.setVideoPath(g_url);
						mVideoView.start();
						mVideoView.requestFocus();
					}catch (Exception e) {
						Log.e(TAG, "error: " + e.getMessage(), e);
						Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
						if (mVideoView != null) {
							mVideoView.stopPlayback();
						}
					}

		
				g_dmsg="Channel: "+g_cur_ch_no;
				Toast.makeText(Ovp.this, g_dmsg,200).show();
			} else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP) && (mVideoView.isPlaying()==true)) {
				if (g_dvr_menu==true) g_dvr_menu=false;
				g_keycode=KEY_CHANNEL_UP;
				g_cur_ch_no=g_cur_ch_no+1;
				if (g_cur_ch_no>g_total_ch_no) 
				{
					g_cur_ch_no=1;
				}
			        	if (mVideoView != null) 
			        	{
			        		mVideoView.stopPlayback();
			        	}
					ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
					if (ch_type.equals("dvr")|| ch_type.equals("delay")||ch_type.equals("hls"))
					{
						g_m3u8_channel=1;
					}else
					{
						g_m3u8_channel=0;
					}

					if (g_m3u8_channel==1)
					{
						g_url="http://"+g_ezserver_ip+":"+g_http_port+"/ch"+g_cur_ch_no+".m3u8?token="+g_token;
					}else
					{
						g_url="http://"+g_ezserver_ip+":"+g_http_port+"/"+g_cur_ch_no+".ch?token="+g_token;
					}
					ch_type=g_channel_info.GetChannelType(g_cur_ch_no-1);
					if (ch_type.equals("dvr"))
					{
						mRePlay.setAlpha(128);
						mRePlay.setVisibility(View.VISIBLE);
						//mRePlay.setBackgroundColor(0x00000000);
						
						
					}else
					{
						mRePlay.setVisibility(View.INVISIBLE);
					}
										

					try
					{
						g_nTry_Play_Times=0;
						mVideoView.setVideoPath(g_url);
						mVideoView.start();
						mVideoView.requestFocus();
					}catch (Exception e) {
						Log.e(TAG, "error: " + e.getMessage(), e);
						Toast.makeText(Ovp.this, "Can not play CH "+g_cur_ch_no,Toast.LENGTH_LONG).show();
						if (mVideoView != null) {
							mVideoView.stopPlayback();
						}
					}

				g_dmsg="Channel: "+g_cur_ch_no;
				Toast.makeText(Ovp.this, g_dmsg,200).show();
			}else if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)||(keyCode == KeyEvent.KEYCODE_ENTER)) {
			
				Show_Playback_Menu();
				g_dvr_menu=true;
				mEPG_list.requestFocus();
			}
		}
		
	
      	        return super.onKeyDown(keyCode, event);
	}
}

