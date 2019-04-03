
package aiiptv.ovp;

import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import org.xmlpull.v1.XmlPullParser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;

import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.GridView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.Toast;

public class Login extends Activity {
	private static final String TAG = "Login";

	private GridView mGridview;
	private GridView mCategoryview;
	private LinearLayout mFrameview;
	
	private ImageButton mIptv;
	private EditText mChannelNoInputField;
	private String g_token = null;
	
	private String g_ezserver_ip="192.168.0.7";
	private String g_management_ip="192.168.0.7";
	private int g_ezserver_port = 8000;
	private int g_cur_ch_no=1;
	private String g_user_name = null;
	private String g_password = null;
	private String g_httpport=null;
	
	private int MAX_CH_NO=10000;
	private int MAX_MOVIE_NO=10000;
	private int SETTING_REQUEST=0;
	private int IPTV_REQUEST=1;
	private int VOD_REQUEST=2;
	private int EDIT_FAVORITE=3;

	private int CHANNEL_MENU_ID=1000;
	private int MOVIE_MENU_ID=1001;
	private int FAVORITE_MENU_ID=1002;
	private int EDIT_FAVORITE_MENU_ID=1003;
	private int SETTING_MENU_ID=1004;
	private int ABOUT_MENU_ID=1005;
	private int AI_CHANNEL_MENU_ID=1006;
	private int AI_MOVIE_MENU_ID=1007;

	private int EPG_REQUEST=4;
	private boolean g_login_status=false;
	private String g_message;
	private int color_type;
	private int ICOND_WIDTH=200;
	private int ICON_HEIGHT=160;
	private int MAX_GRID_ICON_NO_ON_SCREEN=8;
	private int g_start_position=0;
	private int g_c=0;

	String[] g_folder_list_in_array = new String[128];
	int g_total_folder_no=0;
	MovieInfo g_movie_list_in_array=new MovieInfo();
	MovieInfo g_m_list_in_array=new MovieInfo();
	int g_total_movie_no=0;
	int g_current_movie_folder_list_no=0;
	CHInfo g_ch_list_in_array=new CHInfo();
	CHInfo g_list_in_array=new CHInfo();
	int g_total_ch_no=0;
	int g_current_ch_folder_list_no=0;
	int bGridView_IPTV_VOD_Use=1;
	int g_cur_player_movie_index=0;
	int g_cur_player_ch_index=0;
        String g_dmsg;
	Menu g_menu;
	private View g_pre_view=null;
	private ScheduledExecutorService InputChannelNo_scheduleTaskExecutor;
	private ScheduledFuture<?> InputChannelNo_queueFuture;
	private int InputChannelNo_Delay=0;
	private boolean bFirstInputChannelNo=true;
	String g_DownloadAPKPath;
	String g_CurVersion;
	private boolean bTVScreen=true;
		
	private String g_config_filename = "config.xml";
	private EditText mRating_Password;
	
	private String g_favorite_filename= "favorite.xml";
	CHInfo g_ch_favorite_array=new CHInfo();
	int g_total_ch_favorite_no=0;
	int g_daysleft=0;

	int grid_width = ICOND_WIDTH;
	int grid_height = grid_width;// ((display.getHeight()*30)/100)
	int image_width = grid_width*3/5;
	int image_height = image_width;// ((display.getHeight()*30)/100)
	boolean bVersion5plus=true;
	boolean bStoreFlag=false;
	ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
		PackageManager pm = Login.this.getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_TELEVISION))
		{
			bTVScreen=true;
		}else
		{
			bTVScreen=false;
		}
		setContentView(R.layout.login);
		String szAndroidVersion=android.os.Build.VERSION.RELEASE.substring(0,1);
		int nAndroidVersion=Integer.valueOf(szAndroidVersion);
		if (nAndroidVersion>=5)
		{
			bVersion5plus=true;
		}else
		{
			bVersion5plus=false;
		}
		if (Get_Config() == false) return;
		g_token=null;
  		mGridview = (GridView) findViewById(R.id.gridview); 
  		mCategoryview = (GridView) findViewById(R.id.categoryview); 
		new Login_Register_EZserver().execute(g_ezserver_ip,Integer.toString(g_ezserver_port),g_user_name,g_password);
		mChannelNoInputField=(EditText)findViewById(R.id.channel_no_input_field);
		mChannelNoInputField.setVisibility(View.GONE);
		g_CurVersion=getSoftwareVersion();
	        g_DownloadAPKPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/ovp3_update.apk";
       		String url = "http://"+g_ezserver_ip+":"+g_ezserver_port+"/apk/ovp3.apk";
	      	new ApkUpdateAsyncTask().execute(url);
	      	
       	        	
			
	         mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	         	@Override
	         	public void onItemClick(AdapterView<?> parent, View view,
	         	int position, long id) {
				if (bGridView_IPTV_VOD_Use==1)
				{
					g_cur_ch_no=position+1;
					g_cur_player_ch_index=position;
					String retCategory=g_list_in_array.GetCHCategory(g_cur_player_ch_index);
					
					Button mPassword_ok;
					Button mPassword_cancel;	

					
					if (retCategory.indexOf("18+")>=0)
					{
						final Dialog dialog = new Dialog(Login.this);
						dialog.setContentView(R.layout.password_check);
						//dialog.setTitle(password);
						dialog.setTitle("Please input rating password.");
						mRating_Password = (EditText) dialog.findViewById(R.id.rating_password);
					
						mPassword_ok = (Button) dialog.findViewById(R.id.password_ok);
						mPassword_cancel = (Button) dialog.findViewById(R.id.password_cancel);
						mPassword_ok.requestFocus();
					
						mPassword_ok.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								String input_password=mRating_Password.getText().toString();
								//dialog.setTitle(input_password);
								boolean bCheck=Check_User_Rating_Password(g_token,g_ezserver_ip,g_ezserver_port,g_user_name,input_password);
								if (bCheck)
								{
									dialog.cancel();
									StartIPTV();
									
								}else
								{
									dialog.setTitle("Invalid Rating Password.");
								}				
							}
							
						});
					
						mPassword_cancel.setOnClickListener(new OnClickListener() {
							public void onClick(View view) {
								//dialog.dismiss();
								dialog.cancel();
							}
						});
						dialog.show();	
					}else
					{
						StartIPTV();
					}
					//StartIPTV();
				}else if (bGridView_IPTV_VOD_Use==2)
				{
					g_cur_player_movie_index=position;
					String retCategory=g_m_list_in_array.GetMovieCategory(g_cur_player_movie_index);
					Button mPassword_ok;
					Button mPassword_cancel;	

					
					if (retCategory.indexOf("18+")>=0)
					{
						final Dialog dialog = new Dialog(Login.this);
						dialog.setContentView(R.layout.password_check);
						//dialog.setTitle(password);
						dialog.setTitle("Please input rating password.");
						mRating_Password = (EditText) dialog.findViewById(R.id.rating_password);
					
						mPassword_ok = (Button) dialog.findViewById(R.id.password_ok);
						mPassword_cancel = (Button) dialog.findViewById(R.id.password_cancel);
						mPassword_ok.requestFocus();
					
						mPassword_ok.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								String input_password=mRating_Password.getText().toString();
								//dialog.setTitle(input_password);
								boolean bCheck=Check_User_Rating_Password(g_token,g_ezserver_ip,g_ezserver_port,g_user_name,input_password);
								if (bCheck)
								{
									dialog.cancel();
									StartVOD();
									
								}else
								{
									dialog.setTitle("Invalid Rating Password.");
								}				
							}
							
						});
					
						mPassword_cancel.setOnClickListener(new OnClickListener() {
							public void onClick(View view) {
								//dialog.dismiss();
								dialog.cancel();
							}
						});
						dialog.show();	
					}else
					{
						StartVOD();
					}
					//StartVOD();
				}
	         	}
	         });  
	         mCategoryview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	         	@Override
	         	public void onItemClick(AdapterView<?> parent, View view,
	         	int position, long id) {
				if (bGridView_IPTV_VOD_Use==1)
				{
					g_current_ch_folder_list_no=position;
	         		
					mGridview.setAdapter(new CH_ImageAdapter(Login.this));
				}else
				{
					g_current_movie_folder_list_no=position;
	         		
					mGridview.setAdapter(new VOD_ImageAdapter(Login.this));
				}
	         	}
	         });  
	}
	
    protected void onDestroy() {
	if (g_token!=null)
	{
		g_token=User_Logout(g_token,g_ezserver_ip,g_ezserver_port,g_user_name,g_password);
		g_token=null;
	}
	super.onDestroy();
     }

public class ApkUpdateAsyncTask extends AsyncTask<String , Void, String>{
     
    public ApkUpdateAsyncTask(){}
 
    protected String doInBackground(String... urls){
       try{
 		String url = urls[0];
		File ApkFile=new File(g_DownloadAPKPath);
		if ((ApkFile.exists())) {
			ApkFile.delete();
		}
			
		// download the file
		InputStream input = new java.net.URL(url).openStream();
		OutputStream output = new FileOutputStream(g_DownloadAPKPath);
		byte data[] = new byte[1024];
		int count;
		while ((count = input.read(data)) != -1)
		{
			output.write(data, 0, count);
		}
		
		output.flush();
		output.close();
		input.close();
		final PackageManager pm = getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(g_DownloadAPKPath, 0);
		int isNewer = compareVersions(info.versionName, g_CurVersion); 
		if (isNewer == 1)
		{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(ApkFile), "application/vnd.android.package-archive");
			startActivity(intent);
		}         
	
       }catch(Exception e){}
 
       return g_DownloadAPKPath;
    }
}
private String getSoftwareVersion() { 
	try { 
	        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0); 
	       // g_message="Versio: "+packageInfo.versionName;
		//Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();
	
	        return packageInfo.versionName; 
	} catch (PackageManager.NameNotFoundException e) { 
	        //Log.e(TAG, "Package name not found", e); 
	 }; 
	return null;
}

public static final int compareVersions(String ver1, String ver2) {

    String[] vals1 = ver1.split("\\.");
    String[] vals2 = ver2.split("\\.");
    int i=0;
    while(i<vals1.length&&i<vals2.length&&vals1[i].equals(vals2[i])) {
      i++;
    }

    if (i<vals1.length&&i<vals2.length) {
        int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
        return diff<0?-1:diff==0?0:1;
    }

    return vals1.length<vals2.length?-1:vals1.length==vals2.length?0:1;
}
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
    private boolean Get_Ezserver_Store_Token(String ezserver_ip, int ezserver_port, String username, String password){

		String sendbuffer = null;
		String revbuffer = null;
		
		String Username_Password= null;
		int keyword_pos=0;
		int crln_pos=0;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		
		String Received_macid= null;
		String Received_Str;
		
		Socket socket;
		try 
		{
			socket= new Socket(ezserver_ip, ezserver_port);
			socket.setSoTimeout(30*1000); // 30 seconds
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
			
			// Send EZserver HTTP Command
			Username_Password=username+':'+password;


			sendbuffer="GET HTTP/1.1 /token/get_ezserver_store_token?encrpty="+Base64.encodeToString(Username_Password.getBytes(), Base64.DEFAULT)+"\r\nUser-Agent=Ezclient\r\n\r\n";
			
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
			
			if (revbuffer.contains("200 OK")== false)
			{
				return false;
			}else
			{
						//Toast.makeText(Login.this, revbuffer, Toast.LENGTH_LONG).show();
				keyword_pos=revbuffer.indexOf("encrpty=");
				if (keyword_pos!=-1)
				{
					try 
					{
						crln_pos=revbuffer.indexOf("\r\n",keyword_pos);
						Received_Str= revbuffer.substring (keyword_pos+8,crln_pos);
					        byte[] data = Base64.decode(Received_Str, Base64.DEFAULT);
						String Decrypted_Str = new String(data, "UTF-8");
						crln_pos=Decrypted_Str.indexOf(":",0);
						String Received_Password=Decrypted_Str.substring (0,crln_pos);
						if (Received_Password.equals(password))
						{
							return true;
						}else
						{
							return false;
						}
						
					}catch (Exception ex) {
						Log.e(TAG, "Use Wired Cable...", ex);
					}
					return true;
				} else
				{
					return false;
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return false;
		}
		
	}
public class MovieInfo {
	private int TotalMovieNo=0;
    	public String MovieFileName[]=new String[MAX_MOVIE_NO];
    	public String MoviePictureFileName[]=new String[MAX_MOVIE_NO];
    	public String MovieCategory[]=new String[MAX_MOVIE_NO];
    	public double Duration[]=new double[MAX_MOVIE_NO];
    	public double Bitrate[]=new double[MAX_MOVIE_NO];
    	public int PreviewDuration[]=new int[MAX_MOVIE_NO];
    	public double MovieFileSize[]=new double[MAX_MOVIE_NO];
    	public int QualityType[]=new int [MAX_MOVIE_NO];
    	public int MovieFee[]=new int [MAX_MOVIE_NO];
    	
    	// Add functions	
    	public boolean AddMovieFileName(int index, String moviename)
    	{
     		MovieFileName[index]=moviename;
    		TotalMovieNo++;
    		return true;
    	}   	
   	public boolean AddMoviePictureFileName(int index, String icon_path)
    	{
     		MoviePictureFileName[index]=icon_path;
    		TotalMovieNo++;
    		return true;
    	}  
    	public boolean AddMovieCategory(int index, String movie_category)
    	{
     		MovieCategory[index]=movie_category;
    		TotalMovieNo++;
    		return true;
    	}    
    	public boolean AddMovieFileSize(int index, double filesize)
    	{
    		MovieFileSize[index]=filesize;
     		return true;
    	}
    	public boolean AddMovieDRM_Info(int index, double duration, double bitrate)
    	{
    		Duration[index]=duration;
    		Bitrate[index]=bitrate;
    		return true;
    	}    
    	// Get Movie Functions	
   	public int GetMovieTotalNo()
    	{
    		return TotalMovieNo;
    	}
    	// Get Movie DRM Functions
     	public String GetMovieFileName(int index)
    	{
    		
    		return MovieFileName[index];
    	}
     	public String GetMoviePictureFileName(int index)
    	{
    		
    		return MoviePictureFileName[index];
    	}
    	public String GetMovieCategory(int index)
    	{
    		
    		return MovieCategory[index];
    	}
    	public double GetMovieFileSize(int index)
    	{
    		
    		return MovieFileSize[index];
    	}
    	public double GetMovieDRM_Duration(int index)
    	{  		
     		return Duration[index];
    	}
    	public double GetMovieDRM_Bitrate(int index)
    	{  		
     		return Bitrate[index];
    	}    	
    	public String GetMovieDRM_MovieTitle(int index)
    	{  		
     		return MovieFileName[index];
    	}    	
    	public double GetMovieDRM_PreviewDuration(int index)
    	{  		
     		return PreviewDuration[index];
    	}    	
    	public int GetMovieDRM_QualityType(int index)
    	{  		
     		return QualityType[index];
    	}    	
    	public double GetMovieDRM_MovieFee(int index)
    	{  		
     		return MovieFee[index];
    	}    	
    }
    public class CHInfo {
	private int TotalCHNo=0;
    	public String CHFileName[]=new String[MAX_CH_NO];
    	public String CHPictureFileName[]=new String[MAX_CH_NO];
	public String ChannelCategory[]=new String[MAX_CH_NO];
	public String ChannelType[]=new String[MAX_CH_NO];
  	
    	// Add functions	
    	public boolean AddCHFileName(int index, String chname)
    	{
     		CHFileName[index]=chname;
    		TotalCHNo++;
    		return true;
    	}   	
   	public boolean AddCHPictureFileName(int index, String icon_path)
    	{
     		CHPictureFileName[index]=icon_path;
    		TotalCHNo++;
    		return true;
    	}   
    	public boolean AddChannelCategory(int index, String channelcategory)
	{
     		ChannelCategory[index]=channelcategory;
    		return true;
	}   	
  	public boolean AddChannelType(int index, String channeltype)
	{
     		ChannelType[index]=channeltype;
    		return true;
	}   	
    	// Get Movie Functions	
   	public int GetCHTotalNo()
    	{
    		return TotalCHNo;
    	}
      	public String GetCHFileName(int index)
    	{
    		
    		return CHFileName[index];
    	}
     	public String GetCHPictureFileName(int index)
    	{
    		
    		return CHPictureFileName[index];
    	}  
     	public String GetCHCategory(int index)
    	{
    		
    		return ChannelCategory[index];
    	}  
 	public boolean ClearFavoriteFlag()
    	{
  		int index;
     		for (index=0;index<TotalCHNo;index++)
    		{
    			CHFileName[index]=null;
    		}
    		TotalCHNo=0;
    		return true;
    	}   	

    	public boolean CheckFavoriteFlag(String channelname)
    	{
    		int index;
    		boolean bFound=false;
    		for (index=0;index<TotalCHNo;index++)
    		{
	    		if (CHFileName[index].equals(channelname))
	    		{
		    		bFound=true;
		    		break;
    			}
		}
    		return bFound;
    	}

    }
  
     public class CategoryViewAdapter extends BaseAdapter { 
    private Context mContext; 
    String category_icon_url[]= new String[100];
	View grid_array[]=new View[100];
 	int i;
    public Integer[] mThumbIds = { 
    }; 
  
    // Constructor 
    public CategoryViewAdapter(Context c){ 
        mContext = c; 
	g_total_folder_no=Get_Channel_Category(g_token,g_ezserver_ip,g_ezserver_port);
	    			
    } 
  
    @Override
    public int getCount() { 
  	return g_total_folder_no;
     } 
  
      @Override
    public Object getItem(int position) { 
         return null;
    } 
  
    @Override
    public long getItemId(int position) { 
        return 0; 
    } 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
 	View grid=null;
	String UnicodeString;
	String folder_name;
	ImageView imageView=null;
	        	 		

	
       		if (convertView==null)
        		{
      
		    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				grid = inflater.inflate(R.layout.grid_single, null);
					
			}else
			{
				grid=convertView;
			}

			
			
			grid_array[position]=grid;
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			
			i=position;
			
			
				
				
			        imageView = (ImageView)grid.findViewById(R.id.grid_image);       
			       
			 	
			 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(0,0);
				parms_w.gravity=Gravity.CENTER;
				imageView.setLayoutParams(parms_w);

				UnicodeString=g_folder_list_in_array[i].replaceAll("%u", "\\\\u");
				folder_name=decodeUnicode(UnicodeString);
				textView.setText(folder_name);
			
			
       return grid; 
    } 
  }

     public class MovieCategoryViewAdapter extends BaseAdapter { 
    private Context mContext; 
    String category_icon_url[]= new String[100];
	View grid_array[]=new View[100];
 	int i;
    public Integer[] mThumbIds = { 
    }; 
  
    // Constructor 
    public MovieCategoryViewAdapter(Context c){ 
        mContext = c; 
	g_total_folder_no=Get_Movie_Category(g_token,g_ezserver_ip,g_ezserver_port);
	    			
    } 
  
    @Override
    public int getCount() { 
  	return g_total_folder_no;
     } 
  
      @Override
    public Object getItem(int position) { 
         return null;
    } 
  
    @Override
    public long getItemId(int position) { 
        return 0; 
    } 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
 	View grid=null;
	String UnicodeString;
	String folder_name;
	ImageView imageView=null;
	        	 		

	
       		if (convertView==null)
        		{
      
		    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				grid = inflater.inflate(R.layout.grid_single, null);
					
			}else
			{
				grid=convertView;
			}

			
			
			grid_array[position]=grid;
			/*
			LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
			grid.setLayoutParams(parms);
			*/
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			
			i=position;
			
			
			//if (i<g_total_folder_no)
			//{
				
				
			        imageView = (ImageView)grid.findViewById(R.id.grid_image);       
			       
//			 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
			 	
			 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(0,0);
				parms_w.gravity=Gravity.CENTER;
				imageView.setLayoutParams(parms_w);

				UnicodeString=g_folder_list_in_array[i].replaceAll("%u", "\\\\u");
				folder_name=decodeUnicode(UnicodeString);
				//g_message="["+ position+" ]"+":"+folder_name;
				//Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();	
				textView.setText(folder_name);
			//}
			
			
       return grid; 
    } 
  }

    public class ImageAdapter extends BaseAdapter { 
    private Context mContext; 
    private int ch_no=1;
    String channel_icon_url[]= new String[MAX_CH_NO];
	View grid_array[]=new View[MAX_CH_NO];
	int i;
	ImageView image_array[]=new ImageView[MAX_CH_NO];
    // Keep all Images in array 
    public Integer[] mThumbIds = { 
    }; 
  
    // Constructor 
    public ImageAdapter(Context c){ 
        mContext = c; 
	//new AsyncTask_Get_All_Channel_List().execute(g_token,g_ezserver_ip,Integer.toString(g_ezserver_port));
		g_total_ch_no=Get_All_Channel_List(g_token,g_ezserver_ip,g_ezserver_port,1);

	
    } 
  
    @Override
    public int getCount() { 
    	bGridView_IPTV_VOD_Use=1;
  	
    	return g_total_ch_no;
    } 
  
      @Override
  public Object getItem(int position) { 
        return null;
    } 
  
    @Override
    public long getItemId(int position) { 
        return 0; 
    } 
    
    

    


    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
	View grid=null;
	ImageView imageView=null;
	String ch_name_wo_extent_name;
 		
	
       		if (convertView==null)
        		{
			    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					grid = inflater.inflate(R.layout.grid_single, null);
					
			}else
			{
				grid=convertView;
				/*
				imageView = (ImageView)grid.findViewById(R.id.grid_image);
				Bitmap oldImage=((BitmapDrawable)imageView.getDrawable()).getBitmap();
				if (oldImage!=null) oldImage.recycle();
				*/
			}
			grid_array[position]=grid;
			
			if (bVersion5plus)
	             	{
				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
				grid.setLayoutParams(parms);
				
			}
			
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			
				ch_no=position;
				
				String UnicodeString;
				String CHFileName;
				String CHUicodeName;
				
				CHFileName=g_ch_list_in_array.GetCHFileName(ch_no);
				UnicodeString=CHFileName.replaceAll("%u", "\\\\u");
				CHUicodeName=decodeUnicode(UnicodeString);
				textView.setText(CHUicodeName);
				String CHPictureFileName=g_ch_list_in_array.GetCHPictureFileName(ch_no);
				ch_name_wo_extent_name=CHPictureFileName.substring (7);
			        channel_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+ch_name_wo_extent_name;
			        
			        imageView = (ImageView)grid.findViewById(R.id.grid_image);       
			       
			 	
			 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
				parms_w.gravity=Gravity.CENTER;
				imageView.setLayoutParams(parms_w);
				

		        	new DownloadImageTask(imageView).execute(channel_icon_url[position]);
		        	image_array[position]=imageView;
       return grid; 
    } 
  }

    public class AI_Channel_ImageAdapter extends BaseAdapter { 
    private Context mContext; 
    private int ch_no=1;
    	String channel_icon_url[]= new String[MAX_CH_NO];
	View grid_array[]=new View[MAX_CH_NO];
	int i;
	ImageView image_array[]=new ImageView[MAX_CH_NO];
	public Integer[] mThumbIds = { 
    }; 
  
    // Constructor 
    public AI_Channel_ImageAdapter(Context c){ 
        mContext = c; 
        g_total_ch_no=0;
	g_total_ch_no=Get_All_Channel_List(g_token,g_ezserver_ip,g_ezserver_port,2);
	
    } 
  
    @Override
    public int getCount() { 
    	bGridView_IPTV_VOD_Use=1;
  	
  	return g_total_ch_no;
    } 
  
      @Override
    public Object getItem(int position) { 
        return null;
    } 
  
    @Override
    public long getItemId(int position) { 
        return 0; 
    } 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
	View grid=null;
	ImageView imageView=null;
	String ch_name_wo_extent_name;
 			
       		if (convertView==null)
        		{
			    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					grid = inflater.inflate(R.layout.grid_single, null);
					
			}else
			{
				grid=convertView;
				/*
				imageView = (ImageView)grid.findViewById(R.id.grid_image);
				Bitmap oldImage=((BitmapDrawable)imageView.getDrawable()).getBitmap();
				if (oldImage!=null) oldImage.recycle();
				*/
			}
			//g_message=g_total_ch_no+"ch "+"1. pos:"+position;
			//Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();
			
			grid_array[position]=grid;
			if (bVersion5plus)
			{
				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
				grid.setLayoutParams(parms);
			}
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			
				ch_no=position;
				
				String UnicodeString;
				String CHFileName;
				String CHUicodeName;
				
				CHFileName=g_ch_list_in_array.GetCHFileName(ch_no);
				UnicodeString=CHFileName.replaceAll("%u", "\\\\u");
				CHUicodeName=decodeUnicode(UnicodeString);
				textView.setText(CHUicodeName);
				String CHPictureFileName=g_ch_list_in_array.GetCHPictureFileName(ch_no);
				ch_name_wo_extent_name=CHPictureFileName.substring (7);
			        channel_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+ch_name_wo_extent_name;
			        
			        imageView = (ImageView)grid.findViewById(R.id.grid_image);       
			       
			 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
				parms_w.gravity=Gravity.CENTER;
				imageView.setLayoutParams(parms_w);

		        	new DownloadImageTask(imageView).execute(channel_icon_url[position]);
		        	image_array[position]=imageView;
			         
			     
       return grid; 
    } 
  }


public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private Bitmap image;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = (ImageView) imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            image = null;
        }
        return image;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
        }
    }
}

 
  private class BitmapWorkerTask extends AsyncTask<String,Void,Drawable> {
	
        private WeakReference<ImageView> imageViewReference= null;
 
        public BitmapWorkerTask(ImageView imageView) throws IOException {
            imageViewReference = new WeakReference<ImageView>(imageView);         	
        }
	
	@Override
	protected Drawable doInBackground(String... params) {
		 
		        try{ 
			        
		        	java.net.URL src_url = new java.net.URL(params[0]);
		        	try{ 
		        		
		        		Drawable drawable=android.graphics.drawable.Drawable.createFromStream(src_url.openStream(),"test"); 
		        		return drawable;
		        	}catch(IOException ioe )
		        	{
		        		return null;
		        	}
		        }catch(IOException ioe ){ 
		        	return null;
		        }
		        
		
	}
	
	@Override
        protected void onPostExecute(Drawable drawable) {
            if (imageViewReference != null && drawable != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageDrawable(drawable);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                 }
            }
        }
   }
    public class Favorite_ImageAdapter extends BaseAdapter { 
    private Context mContext; 
    private int ch_no=1;
    private int total_ch_no=0;
    String channel_icon_url[];
	View grid_array[];
	int i;
	ImageView image_array[]=new ImageView[MAX_CH_NO];
    public Integer[] mThumbIds = { 
    }; 
  
    // Constructor 
    public Favorite_ImageAdapter(Context c){ 
        mContext = c; 
	total_ch_no=Get_Favorite_Channel_List();
	grid_array= new View[total_ch_no];
	channel_icon_url=  new String[total_ch_no];
    } 
  
    @Override
    public int getCount() { 
    	bGridView_IPTV_VOD_Use=1;
  	
    	return total_ch_no;
     } 
  
      @Override
    public Object getItem(int position) { 
        return null;
    } 
  
    @Override
    public long getItemId(int position) { 
        return 0; 
    } 
        @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
       //  ImageView imageView = new ImageView(mContext); 
	View grid=null;
	ImageView imageView=null;
	String ch_name_wo_extent_name;
 		
       		if (convertView==null)
        		{
			    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					grid = inflater.inflate(R.layout.grid_single, null);
					
			}else
			{
				grid=convertView;
			}
			if (bVersion5plus)
	             	{

				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
				grid.setLayoutParams(parms);
			}

			
			grid_array[position]=grid;
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			
				ch_no=position;
				String UnicodeString;
				String CHFileName;
				String CHUicodeName;
				
				CHFileName=g_list_in_array.GetCHFileName(ch_no);
				UnicodeString=CHFileName.replaceAll("%u", "\\\\u");
				CHUicodeName=decodeUnicode(UnicodeString);
				textView.setText(CHUicodeName);
				String CHPictureFileName=g_list_in_array.GetCHPictureFileName(ch_no);
				ch_name_wo_extent_name=CHPictureFileName.substring (7);
			        channel_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+ch_name_wo_extent_name;
			        imageView = (ImageView)grid.findViewById(R.id.grid_image); 
			        
       			 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
				parms_w.gravity=Gravity.CENTER;
				imageView.setLayoutParams(parms_w);
       	
		        	new DownloadImageTask(imageView).execute(channel_icon_url[position]);
		        	image_array[position]=imageView;
       return grid; 
    } 
  }

   public class CH_ImageAdapter extends BaseAdapter { 
    private Context mContext; 
    private int ch_no=1;
    private int total_ch_no=0;
   String channel_icon_url[];
    View grid_array[];
    public Integer[] mThumbIds = { 
    }; 
  
    // Constructor 
    public CH_ImageAdapter(Context c){ 
        mContext = c; 
        
 	total_ch_no=Get_Channel_List(g_folder_list_in_array[g_current_ch_folder_list_no]);
	grid_array=new View[total_ch_no];
	channel_icon_url= new String[total_ch_no];
    } 
  
    @Override
    public int getCount() { 
    	bGridView_IPTV_VOD_Use=1;
  	
    	return total_ch_no;
    } 
  
      @Override
    public Object getItem(int position) { 
        return null;
    } 
  
    @Override
    public long getItemId(int position) { 
        return 0; 
    } 
  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
	View grid=null;
	String ch_name_wo_extent_name;
   	
   	if (convertView==null)		
	{
    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		grid = new View(mContext);
		grid = inflater.inflate(R.layout.grid_single, null);
	}else
	{
		grid=convertView;
	}
	grid_array[position]=grid;
	if (bVersion5plus)
	{
		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
		grid.setLayoutParams(parms);
	}
	
   	
	TextView textView = (TextView) grid.findViewById(R.id.grid_text);
	ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);

	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
	parms_w.gravity=Gravity.CENTER;
	imageView.setLayoutParams(parms_w);

	
	ch_no=position;
	
	String UnicodeString;
	String CHFileName;
	String CHUicodeName;
	
	CHFileName=g_list_in_array.GetCHFileName(ch_no);
	UnicodeString=CHFileName.replaceAll("%u", "\\\\u");
	CHUicodeName=decodeUnicode(UnicodeString);
	textView.setText(CHUicodeName);
	
	String CHPictureFileName=g_list_in_array.GetCHPictureFileName(ch_no);
	ch_name_wo_extent_name=CHPictureFileName.substring (7);
        channel_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+ch_name_wo_extent_name;
        
	new DownloadImageTask(imageView).execute(channel_icon_url[position]);
      return grid; 
    } 
  }

    private class Login_Register_EZserver extends AsyncTask<String, String, String> {
    	private String token=null;
   	private boolean bhttpport;
   	int nheight=0;
	int nwidth = 0;
			
        protected String doInBackground(String... urls) {

             
           		token=User_Login(urls[0],Integer.valueOf(urls[1]),urls[2],urls[3]);
			if (token==null){
				return null;
			}
			bhttpport=Get_HTTP_Port(token,urls[0],Integer.valueOf(urls[1]));
			if (bhttpport==false)
			token=null;
			g_daysleft=Query_User_Days_Left(token,urls[0],Integer.valueOf(urls[1]));
	             	g_total_folder_no=Get_Channel_Category(token,urls[0],Integer.valueOf(urls[1]));
	             	
			
			g_total_ch_no=Get_All_Channel_List(token,urls[0],Integer.valueOf(urls[1]),1);
	          	g_total_movie_no=Get_All_Movie_List(token,urls[0],Integer.valueOf(urls[1]),g_folder_list_in_array[0],1);			
           return token;
        }
        
       protected void onPostExecute(String result) {
		String UnicodeString;
		String folder_name;	   	
        	g_token=result;
   	   	MenuItem mnuName;
   	   	int i=0;

    		if (g_token==null){
    			g_login_status=false;
    			g_message="Faild to Connect Ezserver("+g_ezserver_ip+":"+g_ezserver_port+")";
			StartSetting(null);
   		}else
    		{
	             	mCategoryview.setNumColumns(g_total_folder_no);
	             	if (bVersion5plus)
	             	{
				nheight=mCategoryview.getHeight();
				nwidth = mCategoryview.getColumnWidth();
				//mCategoryview.setColumnWidth(190);
			}else
			{
				nheight=90;
				nwidth =120;
				mGridview.setColumnWidth(140);
			}
			LinearLayout.LayoutParams parms_c= new LinearLayout.LayoutParams(g_total_folder_no*nwidth,nheight);
			
			mCategoryview.setLayoutParams(parms_c);

			if (g_daysleft<=7)
			{
				g_message="Your subscription will be expired after "+g_daysleft+" days.";
				Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();				
			}
    			g_login_status=true;
    		        mCategoryview.setAdapter(new CategoryViewAdapter(Login.this)); 
 		        mGridview.setAdapter(new ImageAdapter(Login.this)); 
  		       
    		}
        }
 

    }

 	private boolean Get_Access_Config(){
		int keyword_pos=0;
		int crln_pos=0;
		try {
		
		AssetManager am = this.getAssets();

				InputStream is = am.open("config.xml");
		        InputStreamReader inputStreamReader = new InputStreamReader(is);
		        BufferedReader f = new BufferedReader(inputStreamReader);
				char config_info[] = new char[512];
				String config_buffer;
	            f.read(config_info);
	            f.close();
	            
	            config_buffer = new String(config_info);
				keyword_pos=config_buffer.indexOf("<user>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</user>",keyword_pos);
					g_user_name= config_buffer.substring (keyword_pos+6,crln_pos);				
				} 
			
					keyword_pos=config_buffer.indexOf("<password>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</password>",keyword_pos);
					g_password= config_buffer.substring (keyword_pos+10,crln_pos);				
				} 
					keyword_pos=config_buffer.indexOf("<httpport>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</httpport>",keyword_pos);
					g_ezserver_port= Integer.valueOf(config_buffer.substring (keyword_pos+10,crln_pos));				
				} 
				keyword_pos=config_buffer.indexOf("<cur_chno>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</cur_chno>",keyword_pos);
					g_cur_ch_no= Integer.valueOf(config_buffer.substring (keyword_pos+10,crln_pos));				
				} 
				keyword_pos=config_buffer.indexOf("<ezserver_ip>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</ezserver_ip>",keyword_pos);
					g_ezserver_ip= config_buffer.substring (keyword_pos+13,crln_pos);				
				} 
				keyword_pos=config_buffer.indexOf("<management_ip>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</management_ip>",keyword_pos);
					g_management_ip= config_buffer.substring (keyword_pos+15,crln_pos);				
				} 
				
	            return true;

	        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
 	private boolean Get_Config(){
		int keyword_pos=0;
		int crln_pos=0;
				byte[] config_info = new byte[512];
				String config_buffer;
		
		try {
		
		File file =getFileStreamPath(g_config_filename);
			if(!file.exists()) 
			{

				Get_Access_Config();
				return true;
			}
				
				FileInputStream fos = openFileInput(g_config_filename);
				fos.read(config_info);
				fos.close();

        
	            config_buffer = new String(config_info);
				keyword_pos=config_buffer.indexOf("<user>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</user>",keyword_pos);
					g_user_name= config_buffer.substring (keyword_pos+6,crln_pos);				
				} 
			
					keyword_pos=config_buffer.indexOf("<password>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</password>",keyword_pos);
					g_password= config_buffer.substring (keyword_pos+10,crln_pos);				
				} 
					keyword_pos=config_buffer.indexOf("<httpport>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</httpport>",keyword_pos);
					g_ezserver_port= Integer.valueOf(config_buffer.substring (keyword_pos+10,crln_pos));				
				} 
				keyword_pos=config_buffer.indexOf("<cur_chno>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</cur_chno>",keyword_pos);
					g_cur_ch_no= Integer.valueOf(config_buffer.substring (keyword_pos+10,crln_pos));				
				} 
				
				keyword_pos=config_buffer.indexOf("<ezserver_ip>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</ezserver_ip>",keyword_pos);
					g_ezserver_ip= config_buffer.substring (keyword_pos+13,crln_pos);				
				} 
				keyword_pos=config_buffer.indexOf("<management_ip>");
				if (keyword_pos!=-1)
				{
					crln_pos=config_buffer.indexOf("</management_ip>",keyword_pos);
					g_management_ip= config_buffer.substring (keyword_pos+15,crln_pos);				
				} 
					
	            return true;

	        } catch (IOException e) {
	        	
	        	e.printStackTrace();
			return false;
		}		
	}

	private boolean Update_Config(){
		try {
		
			String buffer;


			buffer="<?xml version='1.0' encoding='iso-8859-1\'' ?>\r\n"+
			"<user>"+ g_user_name+ "</user>\r\n"+
			"<password>"+g_password+"</password>\r\n"+
			"<httpport>"+g_ezserver_port+"</httpport>\r\n"+
			"<cur_chno>"+g_cur_ch_no+"</cur_chno>\r\n"+
			"<ezserver_ip>"+g_ezserver_ip+"</ezserver_ip>\r\n"+
			"<management_ip>"+g_ezserver_ip+"</management_ip>\r\n";
			
			
			FileOutputStream fos = openFileOutput(g_config_filename, 0);
			fos.write(buffer.getBytes());
			fos.flush();
			fos.close();
	
		    new Login_Register_EZserver().execute(g_ezserver_ip,Integer.toString(g_ezserver_port),g_user_name,g_password);

			return true;
			

	        } catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(Login.this, "Failed to write",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		}		
	}

	private int Get_Favorite_From_Local_Storage(){
		String chlist = null;
		int keyword_pos=0;
		int crln_pos=0;
		byte[] file_info = new byte[512*1000];
		String filebuffer;
		String ch_name;
		int total_ch_no=0;

		try {
		
			File file =getFileStreamPath(g_favorite_filename);
			if(!file.exists()) 
			{
				Toast.makeText(Login.this, "Favorite is empty.", Toast.LENGTH_LONG).show();
				return 0;
			}
					
			FileInputStream fos = openFileInput(g_favorite_filename);
			fos.read(file_info);
			fos.close();
			
			filebuffer=new String(file_info);
			keyword_pos=filebuffer.indexOf("<favorite>");
			if (keyword_pos!=-1)
			{
				chlist=filebuffer.substring(filebuffer.indexOf("<favorite>"));
				do {
					keyword_pos=chlist.indexOf("<favorite>");
					if (keyword_pos!=-1)
					{
						crln_pos=chlist.indexOf("</favorite>",keyword_pos);
						ch_name= chlist.substring (keyword_pos+10,crln_pos);
						//g_message=" favorite ch_name="+ch_name;
						//Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();
						g_ch_favorite_array.AddCHFileName(total_ch_no,ch_name);
						chlist=chlist.substring(crln_pos);
						total_ch_no++;
					} else
					{
						break;
					}
				} while(true);	
			}
					
		        return total_ch_no;

	        } catch (IOException e) {
	        	
	        	e.printStackTrace();
			return 0;
		}		
	}

	private String User_Login(String ezserver_ip, int ezserver_port, String username, String password){

		String sendbuffer = null;
		String revbuffer = null;
		String token= null;
		String maciddot= null;
		String macid= null;
		int keyword_pos=0;
		int crln_pos=0;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		String User_Password;
		Socket socket;
		try 
		{
			socket= new Socket(ezserver_ip, ezserver_port);
			socket.setSoTimeout(30*1000); // 30 seconds
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
			
			// Send EZserver HTTP Command
			User_Password=username+':'+password;
			sendbuffer="GET HTTP/1.1 /token/createtokenbased64?encrpty="+Base64.encodeToString(User_Password.getBytes(), Base64.DEFAULT)+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
			
			if (revbuffer.contains("200 OK")== false)
			{
				return null;
			}else
			{
				keyword_pos=revbuffer.indexOf("token=");
				if (keyword_pos!=-1)
				{
					try 
					{
						crln_pos=revbuffer.indexOf("\r\n",keyword_pos);
						token= revbuffer.substring (keyword_pos+6,crln_pos);
					        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);   
					        maciddot =wifi.getConnectionInfo().getMacAddress(); 
					        macid=maciddot.substring(0,2)+maciddot.substring(3,5)+maciddot.substring(6,8)+
					        maciddot.substring(9,11)+maciddot.substring(12,14)+maciddot.substring(15,17);
						// Connect EZserver
						socket= new Socket(ezserver_ip, ezserver_port);
						outstream =socket.getOutputStream();
						instream =socket.getInputStream();
						
						// Send EZserver HTTP Command
						sendbuffer="GET HTTP/1.1 /server/set_player_mac_address?token="+token+":macid="+macid+"\r\nUser-Agent=EZhometech\r\n\r\n";
						outbuffer=sendbuffer.getBytes();
						outstream.write(outbuffer);
						
						// Receive EZserver HTTP Response
						toread=1024;
						len=0;
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
					}catch (Exception ex) {
						Log.e(TAG, "Use Wired Cable...", ex);
					}
					
					
					return token;
				} else
				{
					return null;
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return null;
		}
		
	}
	private boolean Check_User_Rating_Password(String token, String ezserver_ip, int ezserver_port, String username, String password){
	
		String sendbuffer = null;
		String revbuffer = null;
		String maciddot= null;
		String macid= null;
		int keyword_pos=0;
		int crln_pos=0;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		String User_Password;
		Socket socket;
		try 
		{
			socket= new Socket(ezserver_ip, ezserver_port);
			socket.setSoTimeout(30*1000); // 30 seconds
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
			
			// Send EZserver HTTP Command
			User_Password=username+':'+password;
			sendbuffer="GET HTTP/1.1 /server/check_user_ratings_password?token="+token+"&encrpty="+Base64.encodeToString(User_Password.getBytes(), Base64.DEFAULT)+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
			
			if (revbuffer.contains("200 OK")== false)
			{
				return false;
			}else
			{
						
				keyword_pos=revbuffer.indexOf("\r\n\r\n");
				if (keyword_pos!=-1)
				{
					String ret= revbuffer.substring(keyword_pos+4,keyword_pos+5);
					if (Integer.valueOf(ret)==1)
					{
						return true;
					}else
					{
						return false;
					}
					
				} else
				{
					return false;
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return false;
		}
		
	}

	private String User_Logout(String token, String ezserver_ip, int ezserver_port, String username, String password){

		String sendbuffer = null;
		String revbuffer = null;
		int keyword_pos=0;
		int crln_pos=0;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		Socket socket;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
			
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /token/destroytoken?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
			if (revbuffer.contains("200 OK")== false)
			{
				g_token=null;
				return null;
			}else
			{
				keyword_pos=revbuffer.indexOf("token=");
				if (keyword_pos!=-1)
				{
					crln_pos=revbuffer.indexOf("\r\n",keyword_pos);
					token= revbuffer.substring (keyword_pos+6,crln_pos);
					return token;
				} else
				{
					return null;
				}
			}
			
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return null;
		}
		
	}
	private boolean Get_HTTP_Port(String token, String ezserver_ip, int ezserver_port){

		String sendbuffer = null;
		String revbuffer = null;
		int keyword_pos=0;
		int crln_pos=0;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		Socket socket;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/inquery_server_httpport?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
				keyword_pos=revbuffer.indexOf("httpport=");
				if (keyword_pos!=-1)
				{
					crln_pos=revbuffer.indexOf("\r\n",keyword_pos);
					g_httpport= revbuffer.substring (keyword_pos+9,crln_pos);
					return true;
				} else
				{
					return false;
				}
				
			}else
			{
				return false;
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return false;
		}
		
	}
	
	private int Query_User_Days_Left(String token, String ezserver_ip, int ezserver_port){

		String sendbuffer = null;
		String revbuffer = null;
		int keyword_pos=0;
		int crln_pos=0;
		byte inbuffer[] = new byte[1024];
		byte outbuffer[] = new byte[1024];
		Socket socket;
		int daysleft=0;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/query_user_days_left?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
				keyword_pos=revbuffer.indexOf("daysleft=");
				if (keyword_pos!=-1)
				{
					crln_pos=revbuffer.indexOf("\r\n",keyword_pos);
					daysleft= Integer.valueOf(revbuffer.substring (keyword_pos+9,crln_pos));
					//g_message="daysleft=["+daysleft+"]";
			        	//Toast.makeText(Login.this, g_message,Toast.LENGTH_LONG).show();
					return daysleft;
				} else
				{
					return 0;
				}
				
			}else
			{
				return 0;
			}
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return 0;
		}
		
	}
	
	private void StartSetting(String message) {
		Intent intent = new Intent(Login.this, Setting.class);
		intent.putExtra("token", g_token);
		intent.putExtra("ezserver_ip", g_ezserver_ip);
		intent.putExtra("http_port", Integer.toString(g_ezserver_port));
		intent.putExtra("user_name", g_user_name);
		intent.putExtra("passowrd", g_password);
		Login.this.startActivityForResult(intent, SETTING_REQUEST);
		

	}
	private void StartEditFavorite() {
		Intent intent = new Intent(Login.this, Edit_Favorite.class);
		intent.putExtra("token", g_token);
		intent.putExtra("ezserver_ip", g_ezserver_ip);
		intent.putExtra("http_base_port", Integer.toString(g_ezserver_port));
		Login.this.startActivityForResult(intent, EDIT_FAVORITE);
	}


	private void StartIPTV() {
		

		Intent intent = new Intent(Login.this, Ovp.class);
		int i=0;
		String ch_url;
		intent.putExtra("token", g_token);
		intent.putExtra("ezserver_ip", g_ezserver_ip);
		intent.putExtra("http_base_port", Integer.toString(g_ezserver_port));
		intent.putExtra("http_port", g_httpport);
		String CHFileName=g_list_in_array.GetCHFileName(g_cur_player_ch_index);
		ch_url=CHFileName;
		intent.putExtra("churl", ch_url);
		intent.putExtra("cur_ch_no", Integer.toString(g_cur_ch_no));
		intent.putExtra("total_ch_no", Integer.toString(g_total_ch_no));
		intent.putExtra("user_name", g_user_name);
		intent.putExtra("passowrd", g_password);
		Login.this.startActivityForResult(intent, IPTV_REQUEST);
		

	}
	private void StartVOD() {
		

		Intent intent = new Intent(Login.this, Ovp_VOD.class);
		int i=0;
		String movie_url;
		
		intent.putExtra("token", g_token);
		intent.putExtra("ezserver_ip", g_ezserver_ip);
		intent.putExtra("http_base_port", Integer.toString(g_ezserver_port));
		intent.putExtra("http_port", g_httpport);
		String MovieFileName=g_m_list_in_array.GetMovieFileName(g_cur_player_movie_index);
		movie_url=MovieFileName;
		
		new AsyncTask_Get_Movie_Duration_Bitrate().execute(g_token,g_ezserver_ip,Integer.toString(g_ezserver_port),Integer.toString(g_current_movie_folder_list_no),Integer.toString(g_cur_player_movie_index));
		double duration=g_m_list_in_array.GetMovieDRM_Duration(g_cur_player_movie_index);
		double bitrate=g_m_list_in_array.GetMovieDRM_Bitrate(g_cur_player_movie_index);
  		intent.putExtra("movieurl", movie_url);
		intent.putExtra("user_name", g_user_name);
		intent.putExtra("passowrd", g_password);
		intent.putExtra("duration", Double.toString(duration));
		intent.putExtra("bitrate", Double.toString(bitrate));
		Login.this.startActivityForResult(intent, VOD_REQUEST);
		

	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == SETTING_REQUEST) {
	        if (resultCode == RESULT_OK) {
				if (g_token!=null)
				{
		         	g_token=User_Logout(g_token,g_ezserver_ip,g_ezserver_port,g_user_name,g_password);
		         	g_token=null;

				}
	        	g_ezserver_ip=data.getStringExtra("ezserver_ip");
	        	g_management_ip=data.getStringExtra("mamagement_ip");
	        	g_ezserver_port=Integer.valueOf(data.getStringExtra("http_port"));
	        	g_user_name=data.getStringExtra("user_name");
	        	g_password=data.getStringExtra("passowrd");
				Update_Config();
	        }
	        else if(resultCode == RESULT_CANCELED){
	            
	        }
	    }else if (requestCode == IPTV_REQUEST) {
	        if (resultCode == RESULT_OK) {
	        	g_cur_ch_no=Integer.valueOf(data.getStringExtra("cur_ch_no"));

	        }
	    }else if (requestCode == VOD_REQUEST) {
	        if (resultCode == RESULT_OK) {
	        }
	    }else if (requestCode == EDIT_FAVORITE) {
	        if (resultCode == RESULT_OK) {
	        	g_ch_favorite_array.ClearFavoriteFlag();
			g_total_ch_favorite_no=Get_Favorite_From_Local_Storage();
			if (g_total_ch_favorite_no==0)
			{
				Toast.makeText(Login.this, "Favorite is empty.",Toast.LENGTH_LONG).show();
			}else
			{
				mGridview.setAdapter(new Favorite_ImageAdapter(Login.this));         
			}    
	        }
	    }
	 
	}
	// Get Channel Category
	private class AsyncTask_Get_Channel_Category extends AsyncTask<String, String, String> {
	    	private int total_folder_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_folder_no=Get_Channel_Category(async_input[0],async_input[1],Integer.valueOf(async_input[2]));			
	
	           return Integer.toString(total_folder_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	
	        	g_total_folder_no=Integer.valueOf(result);
	        }
        }

    	private int Get_Channel_Category(String token, String ezserver_ip, int ezserver_port){

		String TempBuffer = null;
		int FirstContentLen=0;
		byte netbuffer[] = new byte[1024];
		String netrevbuffer = null;
		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[];
		byte inbuffer1[];
		byte outbuffer[] = new byte[1024];
		String folderlist = null;
		String folder_name;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		int total_folder_no=0;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			
			
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/get_channel_category?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
				outstream.close();
				instream.close();
				return 0;
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
			
	
				folderlist=revbuffer.substring(revbuffer.indexOf("category="));
				do {
					keyword_pos=folderlist.indexOf("category=");
					if (keyword_pos!=-1)
					{
						crln_pos=folderlist.indexOf("\r\n",keyword_pos);
						folder_name= folderlist.substring (keyword_pos+9,crln_pos);
						g_folder_list_in_array[total_folder_no]=folder_name;
						folderlist=folderlist.substring(crln_pos);
						total_folder_no++;
					} else
					{
						break;
					}
				} while(true);	
				
				return total_folder_no;
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return 0;
		}
		
	}
	private int Get_Channel_List(String category){
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
		String ch_name;
		String icon_path;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		int total_ch_no=0;
		int i=0;
		for (i=0;i<g_total_ch_no;i++)
		{	
			String retCategory=g_ch_list_in_array.GetCHCategory(i);
			if (retCategory.equals(category))
			{
				ch_name=g_ch_list_in_array.GetCHFileName(i);
				g_list_in_array.AddCHFileName(total_ch_no,ch_name);
				icon_path=g_ch_list_in_array.GetCHPictureFileName(i);
				g_list_in_array.AddCHPictureFileName(total_ch_no,icon_path);
				g_list_in_array.AddChannelCategory(total_ch_no,category);
				total_ch_no++;
			}
		}
		return total_ch_no;
		
	}
	// Get All Channel List
	private class AsyncTask_Get_All_Channel_List extends AsyncTask<String, String, String> {
	    	private int total_ch_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_ch_no=Get_All_Channel_List(async_input[0],async_input[1],Integer.valueOf(async_input[2]),1);			
	
	           return Integer.toString(total_ch_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	
	        	g_total_ch_no=Integer.valueOf(result);
	        }
        }
        // Get AI Channel List
	private class AsyncTask_Get_AI_Channel_List extends AsyncTask<String, String, String> {
	    	private int total_ch_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_ch_no=Get_All_Channel_List(async_input[0],async_input[1],Integer.valueOf(async_input[2]),2);			
	
	           return Integer.toString(total_ch_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	
	        	g_total_ch_no=Integer.valueOf(result);
	        	g_message=g_total_ch_no+" Channels are active.";
			Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();
	        }
        }
	 private int Get_All_Channel_List(String token, String ezserver_ip, int ezserver_port, int nDynamic){

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
		String ch_name;
		String icon_path;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		int total_ch_no=0;
		try 
		{
			
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			
			
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			if (nDynamic!=1)
			{
				sendbuffer="GET HTTP/1.1 /server/query_channel_list?token="+token+":dynamic="+nDynamic+"\r\nUser-Agent=EZhometech\r\n\r\n";
			}else
			{
				sendbuffer="GET HTTP/1.1 /server/query_channel_list?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
			}
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
				outstream.close();
				instream.close();
				return 0;
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
			
				chlist=revbuffer.substring(revbuffer.indexOf("name="));
				do {
					keyword_pos=chlist.indexOf("name=");

					if (keyword_pos!=-1)
					{
						crln_pos=chlist.indexOf("\r\n",keyword_pos);
						ch_name= chlist.substring (keyword_pos+5,crln_pos);
						g_ch_list_in_array.AddCHFileName(total_ch_no,ch_name);
						g_list_in_array.AddCHFileName(total_ch_no,ch_name);
						chlist=chlist.substring(crln_pos);
						keyword_pos=chlist.indexOf("icon=");
						if (keyword_pos!=-1)
						{
							
							crln_pos=chlist.indexOf("\r\n",keyword_pos);
							icon_path= chlist.substring (keyword_pos+5,crln_pos);
							if (icon_path.length()==0)
							{				
								icon_path="file://middleware/pictures/ch_default_icon.jpg";
							}
										
							g_ch_list_in_array.AddCHPictureFileName(total_ch_no,icon_path);
							g_list_in_array.AddCHPictureFileName(total_ch_no,icon_path);
							chlist=chlist.substring(crln_pos);
							
							keyword_pos=chlist.indexOf("category=");
							if (keyword_pos!=-1)
							{
								
								crln_pos=chlist.indexOf("\r\n",keyword_pos);
								String ch_category= chlist.substring (keyword_pos+9,crln_pos);
								g_ch_list_in_array.AddChannelCategory(total_ch_no,ch_category);
								g_list_in_array.AddChannelCategory(total_ch_no,ch_category);
								chlist=chlist.substring(crln_pos);
								
								keyword_pos=chlist.indexOf("type=");
								if (keyword_pos!=-1)
								{
									
									crln_pos=chlist.indexOf("\r\n",keyword_pos);
									String ch_type= chlist.substring (keyword_pos+5,crln_pos);
									g_ch_list_in_array.AddChannelType(total_ch_no,ch_type);
									g_list_in_array.AddChannelType(total_ch_no,ch_type);
									chlist=chlist.substring(crln_pos);
									
								}else
								{
									break;
								}
								
								
							}else
							{
								break;
							}
							
						}else
						{
							break;
						}
						
						
						total_ch_no++;
					} else
					{
						break;
					}
				} while(true);	
				
				return total_ch_no;
			
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return 0;
		}
		
	}

	private int Get_Favorite_Channel_List()
	{
		int total_ch_no=0;
		int i=0;
		for (i=0;i<g_total_ch_no;i++)
		{	
			String ch_name=g_ch_list_in_array.GetCHFileName(i);
			boolean IsFavoriteChannel=g_ch_favorite_array.CheckFavoriteFlag(ch_name);
			if (IsFavoriteChannel)
			{
				ch_name=g_ch_list_in_array.GetCHFileName(i);
				g_list_in_array.AddCHFileName(total_ch_no,ch_name);
				String icon_path=g_ch_list_in_array.GetCHPictureFileName(i);
				g_list_in_array.AddCHPictureFileName(total_ch_no,icon_path);
				String category=g_ch_list_in_array.GetCHCategory(i);
				g_list_in_array.AddChannelCategory(total_ch_no,category);
				total_ch_no++;
			}
		}
		return total_ch_no;
	}
	// Get Movie Category
	private class AsyncTask_Get_Movie_Category extends AsyncTask<String, String, String> {
	    	private int total_folder_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_folder_no=Get_Movie_Category(async_input[0],async_input[1],Integer.valueOf(async_input[2]));			
	
	           return Integer.toString(total_folder_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	
	        	g_total_folder_no=Integer.valueOf(result);
	        }
        }
	private int Get_Movie_Category(String token, String ezserver_ip, int ezserver_port){

		String TempBuffer = null;
		int FirstContentLen=0;
		byte netbuffer[] = new byte[1024];
		String netrevbuffer = null;
		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[];
		byte inbuffer1[];
		byte outbuffer[] = new byte[1024];
		String folderlist = null;
		String folder_name;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		int total_folder_no=0;
		try 
		{
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			
			
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			sendbuffer="GET HTTP/1.1 /server/get_movie_category?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
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
				outstream.close();
				instream.close();
				return 0;
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
			
//			mStatus.setText(revbuffer);
	
			// Get token

				//total_ch_no=0;
				folderlist=revbuffer.substring(revbuffer.indexOf("category="));
				do {
					keyword_pos=folderlist.indexOf("category=");
					if (keyword_pos!=-1)
					{
						
						crln_pos=folderlist.indexOf("\r\n",keyword_pos);
						folder_name= folderlist.substring (keyword_pos+9,crln_pos);
						g_folder_list_in_array[total_folder_no]=folder_name;
						folderlist=folderlist.substring(crln_pos);
						total_folder_no++;
					} else
					{
						break;
					}
				} while(true);	
				
				return total_folder_no;
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return 0;
		}
		
	}
	private int Get_Movie_List(String category){

		int total_movie_no=0;
		int i=0;
		for (i=0;i<g_total_movie_no;i++)
		{	
			String retCategory=g_movie_list_in_array.GetMovieCategory(i);
			if (retCategory.equals(category))
			{
				String movie_name=g_movie_list_in_array.GetMovieFileName(i);
				g_m_list_in_array.AddMovieFileName(total_movie_no,movie_name);
				String icon_path=g_movie_list_in_array.GetMoviePictureFileName(i);
				g_m_list_in_array.AddMoviePictureFileName(total_movie_no,icon_path);
				g_m_list_in_array.AddMovieCategory(total_movie_no,category);
								
				total_movie_no++;
			}
		}
		return total_movie_no;
			
		
	}
	// Get All Movie List
	
	private class AsyncTask_Get_All_Movie_List extends AsyncTask<String, String, String> {
	    	private int total_movie_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_movie_no=Get_All_Movie_List(async_input[0],async_input[1],Integer.valueOf(async_input[2]),async_input[3],1);			
	
	           return Integer.toString(total_movie_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	
	        	g_total_movie_no=Integer.valueOf(result);
	        }
        }
     private class AsyncTask_Get_AI_Movie_List extends AsyncTask<String, String, String> {
	    	private int total_movie_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_movie_no=Get_All_Movie_List(async_input[0],async_input[1],Integer.valueOf(async_input[2]),async_input[3],2);			
	
	           return Integer.toString(total_movie_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	
	        	g_total_movie_no=Integer.valueOf(result);
	        }
        }   
        private int Get_All_Movie_List(String token, String ezserver_ip, int ezserver_port, String category,int nDynamic){

		String TempBuffer = null;
		int FirstContentLen=0;
		byte netbuffer[] = new byte[1024];
		String netrevbuffer = null;
		String sendbuffer = null;
		String revbuffer = null;
		byte inbuffer[];
		byte inbuffer1[];
		byte outbuffer[] = new byte[1024];
		String movielist = null;
		String movie_name;
		String icon_path;
		Socket socket;
		int keyword_pos=0;
		int crln_pos=0;
		int total_movie_no=0;
		try 
		{
			//g_pre_view=null;
			
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			
			
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			if (nDynamic!=1)
			{
				sendbuffer="GET HTTP/1.1 /server/query_movie_list?token="+token+":dynamic="+nDynamic+"\r\nUser-Agent=EZhometech\r\n\r\n";
			}else
			{
				sendbuffer="GET HTTP/1.1 /server/query_movie_list?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
//				sendbuffer="GET HTTP/1.1 /server/query_movie_list?token="+token+"\r\nUser-Agent=EZhometech\r\n\r\n";
			}
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
				outstream.close();
				instream.close();
				return 0;
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
			
				movielist=revbuffer.substring(revbuffer.indexOf("name="));
				do {
					keyword_pos=movielist.indexOf("name=");
					if (keyword_pos!=-1)
					{

						crln_pos=movielist.indexOf("\r\n",keyword_pos);
						movie_name= movielist.substring (keyword_pos+5,crln_pos);
						g_movie_list_in_array.AddMovieFileName(total_movie_no,movie_name);
						g_m_list_in_array.AddMovieFileName(total_movie_no,movie_name);
						movielist=movielist.substring(crln_pos);
						keyword_pos=movielist.indexOf("img=");
						if (keyword_pos!=-1)
						{
							
							crln_pos=movielist.indexOf("\r\n",keyword_pos);
							icon_path= movielist.substring (keyword_pos+4,crln_pos);
							if (icon_path.length()==0)
							{				
								icon_path="middleware/pictures/movie_default_icon.png";
							}				
							g_movie_list_in_array.AddMoviePictureFileName(total_movie_no,icon_path);
							g_m_list_in_array.AddMoviePictureFileName(total_movie_no,icon_path);
							movielist=movielist.substring(crln_pos);
							
							keyword_pos=movielist.indexOf("category=");
							if (keyword_pos!=-1)
							{
								
								crln_pos=movielist.indexOf("\r\n",keyword_pos);
								String movie_category= movielist.substring (keyword_pos+9,crln_pos);
												
								g_movie_list_in_array.AddMovieCategory(total_movie_no,movie_category);
								g_m_list_in_array.AddMovieCategory(total_movie_no,movie_category);
								movielist=movielist.substring(crln_pos);
								
							}else
							{
								break;
							}
						}else
						{
							break;
						}
						
						
						total_movie_no++;
					} else
					{
						break;
					}
				} while(true);	
				
				return total_movie_no;
		} catch (Exception ex) {
			Log.e(TAG, "error: " + ex.getMessage(), ex);
				return 0;
		}
		
	}
	// Get Movie Duration/Bitrate
	private class AsyncTask_Get_Movie_Duration_Bitrate extends AsyncTask<String, String, String> {
	        protected String doInBackground(String... async_input) {
	
	             	Get_Movie_Duration_Bitrate(async_input[0],async_input[1],Integer.valueOf(async_input[2]),Integer.valueOf(async_input[3]),Integer.valueOf(async_input[4]));
	             	return null;
	        }
	        
        }	
	private int Get_Movie_Duration_Bitrate(String token, String ezserver_ip, int ezserver_port,int folder_index, int movie_index){

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
		String moviename;
		String sduration;
		String sbitrate;
		double duration;
		double bitrate;
		try 
		{
			/* if (!g_register_flag) 
			{
				Toast.makeText(Login.this, "EZsplayer Evalution Version",Toast.LENGTH_LONG).show();
			}
			*/
			// Connect EZserver
			socket= new Socket(ezserver_ip, ezserver_port);
			
			
			OutputStream outstream =socket.getOutputStream();
			InputStream instream =socket.getInputStream();
	
			// Send EZserver HTTP Command
			moviename=g_movie_list_in_array.GetMovieFileName(movie_index);
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
							bitrate=Double.parseDouble(sbitrate);
							duration=Double.parseDouble(sduration);
							g_m_list_in_array.AddMovieDRM_Info(movie_index,duration,bitrate);
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
	public class VOD_ImageAdapter extends BaseAdapter { 
	    private Context mContext; 
	    private int movie_no=1;
	    private int total_movie_no=0;
	    String movie_icon_url[];
	    View grid_array[];
	
	    // Keep all Images in array 
	    public Integer[] mThumbIds = { 
	    }; 
	  
	    // Constructor 
	    public VOD_ImageAdapter(Context c){ 
	        mContext = c; 
		total_movie_no=Get_Movie_List(g_folder_list_in_array[g_current_movie_folder_list_no]);
		g_message=total_movie_no+" movies are online.";
		Toast.makeText(Login.this, g_message, Toast.LENGTH_LONG).show();
		movie_icon_url=new String[total_movie_no];
		grid_array= new View[total_movie_no];
	    } 
	  
	    @Override
	    public int getCount() { 
   		return total_movie_no; 
	    } 
	      @Override
	  public Object getItem(int position) { 
	        return null;
	    } 
	  
	    @Override
	    public long getItemId(int position) { 
	        return 0; 
	    } 
	  
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) { 
        	
        	View grid=null;
 		String movie_name_wo_extent_name;
        	if (convertView==null)
         	{
   			    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					grid = new View(mContext);
					grid = inflater.inflate(R.layout.grid_single, null);
		}else
		{
			grid=convertView;
		}
		if (bVersion5plus)
		{
			LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
			grid.setLayoutParams(parms);
		}

		
          	grid_array[position]=grid;

		TextView textView = (TextView) grid.findViewById(R.id.grid_text);
		ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
	 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
		parms_w.gravity=Gravity.CENTER;
		imageView.setLayoutParams(parms_w);

		movie_no=g_start_position+position;
		
		String UnicodeString;
		String MovieFileName;
		String MovieUicodeName;
		
		MovieFileName=g_m_list_in_array.GetMovieFileName(movie_no);
		UnicodeString=MovieFileName.replaceAll("%u", "\\\\u");
		MovieUicodeName=decodeUnicode(UnicodeString);
		textView.setText(MovieUicodeName);
			
		String MoviePictureFileName=g_m_list_in_array.GetMoviePictureFileName(movie_no);
		
		movie_name_wo_extent_name=MoviePictureFileName.substring (7);
	        movie_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+movie_name_wo_extent_name;
	        new DownloadImageTask(imageView).execute(movie_icon_url[position]);
			       
		        
	       return grid; 
	    } 
        }
	public class All_Movie_ImageAdapter extends BaseAdapter { 
	    private Context mContext; 
	    private int movie_no=1;
	    String movie_icon_url[]= new String[MAX_MOVIE_NO];
	View grid_array[]=new View[MAX_MOVIE_NO];
	
	    // Keep all Images in array 
	    public Integer[] mThumbIds = { 
	    }; 
	  
	    // Constructor 
	    public All_Movie_ImageAdapter(Context c){ 
	        mContext = c; 
		g_total_movie_no=Get_All_Movie_List(g_token,g_ezserver_ip,g_ezserver_port,g_folder_list_in_array[g_current_movie_folder_list_no],1);			


	    } 
	  
	    @Override
	    public int getCount() { 
		//bGridView_IPTV_VOD_Use=2;
	    	return g_total_movie_no; 
	    } 
	      @Override
  	    public Object getItem(int position) { 
	        return null;
	    } 
	  
	    @Override
	    public long getItemId(int position) { 
	        return 0; 
	    } 
	  
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) { 
	     View grid=null;
       		int keyword_pos=0;
		int crln_pos=0;
		String movie_name_wo_extent_name;

			
		       	if (convertView==null)
			{
				    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						grid = new View(mContext);
						grid = inflater.inflate(R.layout.grid_single, null);
			}else
			{
				grid=convertView;
			}
			grid_array[position]=grid;
			if (bVersion5plus)
			{
				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
				grid.setLayoutParams(parms);
			}
			
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
			
		 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
			parms_w.gravity=Gravity.CENTER;
			imageView.setLayoutParams(parms_w);

			
			movie_no=position;
			String UnicodeString;
			String MovieFileName;
			String MovieUicodeName;
			
			MovieFileName=g_movie_list_in_array.GetMovieFileName(movie_no);
			UnicodeString=MovieFileName.replaceAll("%u", "\\\\u");
			MovieUicodeName=decodeUnicode(UnicodeString);
 			textView.setText(MovieUicodeName);
			
			String MoviePictureFileName=g_movie_list_in_array.GetMoviePictureFileName(movie_no);
			
			
			movie_name_wo_extent_name=MoviePictureFileName.substring (7);
		        movie_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+movie_name_wo_extent_name;
		        
		        new DownloadImageTask(imageView).execute(movie_icon_url[position]);
				        

	       return grid; 
	    } 
	  }
	  public class AI_Movie_ImageAdapter extends BaseAdapter { 
	    private Context mContext; 
	    private int movie_no=1;
	    String movie_icon_url[]= new String[MAX_MOVIE_NO];
	View grid_array[]=new View[MAX_MOVIE_NO];
	
	    // Keep all Images in array 
	    public Integer[] mThumbIds = { 
	    }; 
	  
	    // Constructor 
	    public AI_Movie_ImageAdapter(Context c){ 
	        mContext = c; 
		g_total_movie_no=Get_All_Movie_List(g_token,g_ezserver_ip,g_ezserver_port,g_folder_list_in_array[g_current_movie_folder_list_no],2);			
	

	    } 
	  
	    @Override
	    public int getCount() { 
	    	return g_total_movie_no; 
	    } 
	      @Override
	    public Object getItem(int position) { 
	        return null;
	    } 
	  
	    @Override
	    public long getItemId(int position) { 
	        return 0; 
	    } 
	  
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) { 
	    View grid=null;
       		int keyword_pos=0;
		int crln_pos=0;
		String movie_name_wo_extent_name;

			
		       	if (convertView==null)
			{
				    		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						grid = new View(mContext);
						grid = inflater.inflate(R.layout.grid_single, null);
			}else
			{
				grid=convertView;
			}
			grid_array[position]=grid;
			if (bVersion5plus)
			{
				LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(grid_width,grid_height);
				grid.setLayoutParams(parms);
			}
			
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
			
		 	LinearLayout.LayoutParams parms_w = new LinearLayout.LayoutParams(image_width,image_height);
			parms_w.gravity=Gravity.CENTER;
			imageView.setLayoutParams(parms_w);

			
			movie_no=position;
			String UnicodeString;
			String MovieFileName;
			String MovieUicodeName;
			
			MovieFileName=g_movie_list_in_array.GetMovieFileName(movie_no);
			UnicodeString=MovieFileName.replaceAll("%u", "\\\\u");
			MovieUicodeName=decodeUnicode(UnicodeString);
 			textView.setText(MovieUicodeName);
			
			String MoviePictureFileName=g_movie_list_in_array.GetMoviePictureFileName(movie_no);
			
			
			movie_name_wo_extent_name=MoviePictureFileName.substring (7);
		        movie_icon_url[position]="http://"+g_ezserver_ip+":"+g_ezserver_port+"/"+movie_name_wo_extent_name;
		        
		        new DownloadImageTask(imageView).execute(movie_icon_url[position]);
				        

	       return grid; 
	    } 
	  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem mnuName;
			getMenuInflater().inflate(R.menu.main, menu);
			if (menu!=null)
			{
				g_menu=menu;
				mnuName=g_menu.add(0,CHANNEL_MENU_ID,0, "Channels");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
				
				mnuName=g_menu.add(0,MOVIE_MENU_ID,1, "Movies");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
				
				mnuName=g_menu.add(0,AI_CHANNEL_MENU_ID,0, "My Channels");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
				
				mnuName=g_menu.add(0,AI_MOVIE_MENU_ID,1, "My Movies");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

				mnuName=g_menu.add(0,FAVORITE_MENU_ID,1, "My Favorite");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

				mnuName=g_menu.add(0,EDIT_FAVORITE_MENU_ID,1, "Edit Favorite");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			

				
				mnuName=g_menu.add(0,SETTING_MENU_ID,2, "Setting");
				mnuName.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
				return true;
			}else
			{
				return false;
			}		
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {

              return true;

}
	private class AsyncTask_onOptionsItemSelected_Get_Channel_Category extends AsyncTask<String, String, String> {
	    	private int total_folder_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_folder_no=Get_Channel_Category(async_input[0],async_input[1],Integer.valueOf(async_input[2]));			
	
	           return Integer.toString(total_folder_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	int i;
		   	MenuItem mnuName;
		   	String UnicodeString;
		   	String folder_name;
			 int nheight=0;
			int nwidth = 0;
	        	g_total_folder_no=Integer.valueOf(result);
			
			mCategoryview.setNumColumns(g_total_folder_no);
			
			if (bVersion5plus)
	             	{
				nheight=mCategoryview.getHeight();
				nwidth = mCategoryview.getColumnWidth();
			}else
			{
				nheight=90;
				nwidth =120;
			}
			
			LinearLayout.LayoutParams parms_c= new LinearLayout.LayoutParams(g_total_folder_no*nwidth,nheight);
			mCategoryview.setLayoutParams(parms_c);
			
   		         mCategoryview.setAdapter(new CategoryViewAdapter(Login.this)); 
			mGridview.setAdapter(new ImageAdapter(Login.this));  
	        }
        }
 	private class AsyncTask_onOptionsItemSelected_Get_AI_Channel_Category extends AsyncTask<String, String, String> {
	    	private int total_folder_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_folder_no=Get_Channel_Category(async_input[0],async_input[1],Integer.valueOf(async_input[2]));			
	
	           return Integer.toString(total_folder_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	int i;
		   	MenuItem mnuName;
		   	String UnicodeString;
		   	String folder_name;
			 int nheight=0;
			int nwidth = 0;

	        	g_total_folder_no=Integer.valueOf(result);
	        	
			
			mCategoryview.setNumColumns(g_total_folder_no);
			
			if (bVersion5plus)
	             	{
				nheight=mCategoryview.getHeight();
				nwidth = mCategoryview.getColumnWidth();
			}else
			{
				nheight=90;
				nwidth =120;
			}
			
			LinearLayout.LayoutParams parms_c= new LinearLayout.LayoutParams(g_total_folder_no*nwidth,nheight);
			mCategoryview.setLayoutParams(parms_c);
			
   		         mCategoryview.setAdapter(new CategoryViewAdapter(Login.this)); 
			mGridview.setAdapter(new AI_Channel_ImageAdapter(Login.this));  
	        }
        }
	private class AsyncTask_onOptionsItemSelected_Get_Movie_Category extends AsyncTask<String, String, String> {
	    	private int total_folder_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_folder_no=Get_Movie_Category(async_input[0],async_input[1],Integer.valueOf(async_input[2]));
	
	           return Integer.toString(total_folder_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	int i;
		   	MenuItem mnuName;
		   	String UnicodeString;
		   	String folder_name;
	        	g_total_folder_no=Integer.valueOf(result);
			 int nheight=0;
			int nwidth = 0;
			bGridView_IPTV_VOD_Use=2;
			mCategoryview.setNumColumns(g_total_folder_no);
			
			if (bVersion5plus)
	             	{
				nheight=mCategoryview.getHeight();
				nwidth = mCategoryview.getColumnWidth();
			}else
			{
				nheight=90;
				nwidth =120;
			}
			
			LinearLayout.LayoutParams parms_c= new LinearLayout.LayoutParams(g_total_folder_no*nwidth,nheight);
			mCategoryview.setLayoutParams(parms_c);
   		        mCategoryview.setAdapter(new MovieCategoryViewAdapter(Login.this)); 
		        mGridview.setAdapter(new All_Movie_ImageAdapter(Login.this));  
	        }
        }    
        private class AsyncTask_onOptionsItemSelected_Get_AI_Movie_Category extends AsyncTask<String, String, String> {
	    	private int total_folder_no=0;
	        protected String doInBackground(String... async_input) {
	
	             	total_folder_no=Get_Movie_Category(async_input[0],async_input[1],Integer.valueOf(async_input[2]));			
	
	           return Integer.toString(total_folder_no);
	        }
	        
	        protected void onPostExecute(String result) {
		   	int i;
		   	MenuItem mnuName;
		   	String UnicodeString;
		   	String folder_name;
	        	g_total_folder_no=Integer.valueOf(result);
			 int nheight=0;
			int nwidth = 0;
			bGridView_IPTV_VOD_Use=2;
			mCategoryview.setNumColumns(g_total_folder_no);
			
			if (bVersion5plus)
	             	{
				nheight=mCategoryview.getHeight();
				nwidth = mCategoryview.getColumnWidth();
			}else
			{
				nheight=90;
				nwidth =120;
			}
			
			LinearLayout.LayoutParams parms_c= new LinearLayout.LayoutParams(g_total_folder_no*nwidth,nheight);
			mCategoryview.setLayoutParams(parms_c);
   		         mCategoryview.setAdapter(new MovieCategoryViewAdapter(Login.this)); 
		        mGridview.setAdapter(new AI_Movie_ImageAdapter(Login.this));  
	        }
        }       
        @Override public boolean onOptionsItemSelected(MenuItem item) {     
	// Handle presses on the action bar items   
	int nitemIndex;
	MenuItem mnuName;
	nitemIndex=item.getItemId();
		
		if (nitemIndex==android.R.id.home)
		{
		}else 
		
		if (nitemIndex==CHANNEL_MENU_ID)
		{
			setTitle("CHANNELS");
			new AsyncTask_onOptionsItemSelected_Get_Channel_Category().execute(g_token,g_ezserver_ip,Integer.toString(g_ezserver_port));

		}else if (nitemIndex==MOVIE_MENU_ID)
		{
			setTitle("MOVIES");
			new AsyncTask_onOptionsItemSelected_Get_Movie_Category().execute(g_token,g_ezserver_ip,Integer.toString(g_ezserver_port));

		}else if (nitemIndex==AI_CHANNEL_MENU_ID)
		{
			setTitle("My CHANNELS");
			new AsyncTask_onOptionsItemSelected_Get_AI_Channel_Category().execute(g_token,g_ezserver_ip,Integer.toString(g_ezserver_port));
			
		}else if (nitemIndex==AI_MOVIE_MENU_ID)
		{
			setTitle("My MOVIES");
			
			new AsyncTask_onOptionsItemSelected_Get_AI_Movie_Category().execute(g_token,g_ezserver_ip,Integer.toString(g_ezserver_port));

		}else if (nitemIndex==FAVORITE_MENU_ID)
		{
			setTitle("FAVORITE");
			g_ch_favorite_array.ClearFavoriteFlag();
			g_total_ch_favorite_no=Get_Favorite_From_Local_Storage();
			if (g_total_ch_favorite_no==0)
			{
				Toast.makeText(Login.this, "Favorite is empty.",Toast.LENGTH_LONG).show();
//				mGridview.setAdapter(new Favorite_ImageAdapter(Login.this));         
			}else
			{
				mGridview.setAdapter(new Favorite_ImageAdapter(Login.this));         
			}			
		}
		
		else if (nitemIndex==EDIT_FAVORITE_MENU_ID)
		{
			
			StartEditFavorite(); 
			
		}
		
		else if (nitemIndex==SETTING_MENU_ID)
		{
			
			StartSetting(null);             
		}
		else
		{
			
			if (bGridView_IPTV_VOD_Use==1)
			{
			  	g_current_ch_folder_list_no=nitemIndex;
				mGridview.setAdapter(new CH_ImageAdapter(Login.this)); 
			}else if (bGridView_IPTV_VOD_Use==2)
			{
			  	g_current_movie_folder_list_no=nitemIndex;
				mGridview.setAdapter(new VOD_ImageAdapter(Login.this)); 
			}
		}

	return true;  
	}
	
	private boolean Show_Channel_No()
	{
		mChannelNoInputField.setCursorVisible(false);
		mChannelNoInputField.setVisibility(View.VISIBLE);
		mChannelNoInputField.requestFocus();

	    return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                 
		        builder
		        .setMessage("Exit?")
		        .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int id) {
		                finish();
		            }
		        })
		        .setNegativeButton("No", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog,int id) {
		                dialog.cancel();
		            }
		        })
		        .show();
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
					        	g_cur_ch_no=nch_no;
					        	g_cur_player_ch_index=g_cur_ch_no;
					        	String retCategory=g_ch_list_in_array.GetCHCategory(g_cur_player_ch_index);
							Button mPassword_ok;
							Button mPassword_cancel;	
		
							
							if (retCategory.indexOf("18+")>=0)
							{
								final Dialog dialog = new Dialog(Login.this);
								dialog.setContentView(R.layout.password_check);
								dialog.setTitle("Please input rating password.");
								mRating_Password = (EditText) dialog.findViewById(R.id.rating_password);
							
								mPassword_ok = (Button) dialog.findViewById(R.id.password_ok);
								mPassword_cancel = (Button) dialog.findViewById(R.id.password_cancel);
								mPassword_ok.requestFocus();
							
								mPassword_ok.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										String input_password=mRating_Password.getText().toString();
										//dialog.setTitle(input_password);
										boolean bCheck=Check_User_Rating_Password(g_token,g_ezserver_ip,g_ezserver_port,g_user_name,input_password);
										if (bCheck)
										{
											dialog.cancel();
											StartIPTV();
											
										}else
										{
											dialog.setTitle("Invalid Rating Password.");
										}				
									}
									
								});
							
								mPassword_cancel.setOnClickListener(new OnClickListener() {
									public void onClick(View view) {
										//dialog.dismiss();
										dialog.cancel();
									}
								});
								dialog.show();	
							}else
							{
								StartIPTV();
							}
						}
						bFirstInputChannelNo=true;
						mChannelNoInputField.setVisibility(View.GONE);
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
	
	      	return super.onKeyDown(keyCode, event);
	}

}
