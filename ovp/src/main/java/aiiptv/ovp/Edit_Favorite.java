package aiiptv.ovp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;

public class Edit_Favorite extends Activity {
	private Button mSave;
	private Button mCancel;
	private ListView mCH_list;
	private ArrayList<String> ch_list_array = new ArrayList<String>();
	private String g_token = null;
	private String g_ezserver_ip="192.168.0.7";
	private int g_ezserver_port = 18000;
	private Channel_Info g_channel_info=new Channel_Info();
	private   Intent intent;
	private int g_total_ch_no=1;
	private String g_favorite_filename= "favorite.xml";
	private String g_message;
	//private int RESULT_FAVORITE=3;



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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_favorite);
		intent = getIntent();
		g_token=intent.getStringExtra("token");
		g_ezserver_ip=intent.getStringExtra("ezserver_ip");
		g_ezserver_port=Integer.valueOf(intent.getStringExtra("http_base_port"));

		mCH_list= (ListView) findViewById(R.id.ch_list);
		mSave = (Button) findViewById(R.id.save);
		mCancel = (Button) findViewById(R.id.cancel);
		mCancel.requestFocus();

		ch_list_array=Get_CH_information(g_token,g_ezserver_ip,g_ezserver_port);
		Get_CH_information(g_token,g_ezserver_ip,g_ezserver_port);
		Get_Favorite_From_Local_Storage();


		MySimpleArrayAdapter adapter=new MySimpleArrayAdapter(Edit_Favorite.this,R.layout.listview_item_row, ch_list_array);
		mCH_list.setAdapter(adapter);
		
		mCH_list.setOnItemClickListener(new CheckBoxClick());
    		
		mSave.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				try {
					g_channel_info.SaveChannelFavorite();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setResult(RESULT_OK, intent);
				Toast.makeText(Edit_Favorite.this, "New Favorite Saved.", Toast.LENGTH_LONG).show();
				finish();
				
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});


	}
	public class CheckBoxClick implements OnItemClickListener{
	 
	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
			CheckedTextView v = (CheckedTextView) arg1.findViewById(R.id.checkedTextView1);
	               if(((CheckedTextView) v).isChecked()){
		            ((CheckedTextView) v).setChecked(false);
		        }else{
		            ((CheckedTextView) v).setChecked(true);            
		        }
		        g_channel_info.SetChannelFavoriteFlag(index);
        	    }   
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	  private final Context context;
	  private final ArrayList<String> values;
	
	  public MySimpleArrayAdapter(Context context, int listviewItemRow, ArrayList<String> ch_list_array) {
	    super(context, R.layout.listview_item_row, ch_list_array);
	    this.context = context;
	    this.values = ch_list_array;
	  }
	
	 @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
        	if (row==null)
        	{
        		LayoutInflater inflater = (LayoutInflater) context
	        	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		row = inflater.inflate(R.layout.listview_item_row, parent, false); 
        		
		}
	   	CheckedTextView v = (CheckedTextView) row.findViewById(R.id.checkedTextView1);
		 if (g_channel_info.GetChannelFavoriteFlag(position)==true)
		{
			((CheckedTextView) v).setChecked(true);  
		}else
		{
			((CheckedTextView) v).setChecked(false);
		}
		
		v.setText(values.get(position));
			
		
	
	    return row;
	    
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
				Toast.makeText(Edit_Favorite.this, "Favorite is empty.", Toast.LENGTH_LONG).show();
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
						//g_message=" ch_name="+ch_name;
						//Toast.makeText(Edit_Favorite.this, g_message, Toast.LENGTH_LONG).show();
						g_channel_info.SetChannelFavoriteFlagbyName(ch_name);
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
	public class Channel_Info {
		private int MAX_CHANNEL_NO=10000;
		private int TotalChannelNo=0;
	    	public String ChannelName[]=new String[MAX_CHANNEL_NO];
	    	public boolean ChannelFavoriteFlag[]=new boolean [MAX_CHANNEL_NO];
	    	
	    	// Add functions	
	    	public boolean AddCHFileName(int index, String channelname)
	    	{
	     		ChannelName[index]=channelname;
	     		ChannelFavoriteFlag[index]=false;
	    		TotalChannelNo++;
	    		return true;
	    	}  
	    	public String GetCHFileName(int index)
	    	{
	    		
	    		return ChannelName[index];
	    	}
	    	public boolean GetChannelFavoriteFlag(int index)
	    	{
	    		return ChannelFavoriteFlag[index];
	    	}
	    	
	    	public void SetChannelFavoriteFlag(int index)
	    	{
	    		if (ChannelFavoriteFlag[index]==false)
	    		{
	    			ChannelFavoriteFlag[index]=true;
    			}else
    			{
	    			ChannelFavoriteFlag[index]=false;
			}
	    		return;
	    	}
	    	public void SetChannelFavoriteFlagbyName(String channelname)
	    	{
	    		int index;
	    		for (index=0;index<TotalChannelNo;index++)
	    		{
		    		if (ChannelName[index].equals(channelname))
		    		{
		    			ChannelFavoriteFlag[index]=true;
			    		break;
	    			}
			}
	    		return;
	    	}
	    	public void SaveChannelFavorite() throws IOException
	    	{
	    		int index;
	    		FileOutputStream fos = openFileOutput(g_favorite_filename, 0);
			String buffer;


			buffer="<?xml version='1.0' encoding='iso-8859-1\'' ?>\r\n";
			fos.write(buffer.getBytes());
			fos.flush();
			
			for (index=0;index<TotalChannelNo;index++)
	    		{
	    			if (ChannelFavoriteFlag[index]==true)
	    			{
					buffer="<favorite>"+ChannelName[index]+"</favorite>\r\n";
					fos.write(buffer.getBytes());
					fos.flush();
				}
			}
			fos.close();
	    		return;
	    	}
 	
	}
	private ArrayList<String> Get_CH_information(String token, String ezserver_ip, int ezserver_port){

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
		ArrayList<String> ch_list_in_array = new ArrayList<String>();
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
				return null;
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
						
						UnicodeCHNameWithNo="    "+ch_no+": " +UnicodeCHName;
						ch_list_in_array.add(UnicodeCHNameWithNo);
						
						g_channel_info.AddCHFileName(ch_no_temp,ch_name);
						chlist=chlist.substring(crln_pos);
						keyword_pos=chlist.indexOf("type=");
						if (keyword_pos!=-1)
						{
							crln_pos=chlist.indexOf("\r\n",keyword_pos);
							ch_type= chlist.substring (keyword_pos+5,crln_pos);
							
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
				return ch_list_in_array;
		} catch (Exception ex) {
					return null;
		}
		
	}

}