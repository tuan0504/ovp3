package aiiptv.ovp;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Setting extends Activity {
	private Button mSave;
	private Button mCancel;
	private TextView mVersion;
	private EditText mUsername;
	private EditText mPassword;
	private EditText mEzserver_ip;
	private EditText mHttp_port;
	private   Intent intent;
	private   String ezserver_ip;
	private   String http_port;
	private   String user_name;
	private   String password;
	private String g_token = null;
	private int g_cur_ch_no=1;

	private String g_ezserver_ip="192.168.0.7";
	private String g_management_ip;
	private int g_ezserver_port = 8000;
	private String g_user_name = null;
	private String g_password = null;
	private int IPTV_REQUEST=1;
	private int orientation;

    	private String getSoftwareVersion() { 
        try { 
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0); 
 
                return packageInfo.versionName; 
        } catch (PackageManager.NameNotFoundException e) { 
                //Log.e(TAG, "Package name not found", e); 
         }; 
        return null;
       }
       
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		String versionno=getSoftwareVersion();
		intent = getIntent();
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mEzserver_ip = (EditText) findViewById(R.id.ezserver_ip);
		mHttp_port = (EditText) findViewById(R.id.http_port);
		mVersion= (TextView) findViewById(R.id.version);

		LinearLayout.LayoutParams llTextView1 = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams llTextView2 = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams llTextView3 = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams llTextView5 = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView TextView1= (TextView) findViewById(R.id.textView1);
		TextView TextView2= (TextView) findViewById(R.id.textView2);
		TextView TextView3= (TextView) findViewById(R.id.textView3);
		TextView TextView5= (TextView) findViewById(R.id.textView5);
		if(orientation==Configuration.ORIENTATION_PORTRAIT)
		{
			llTextView1.setMargins(10,0,0,0);
			TextView1.setLayoutParams(llTextView1);
			llTextView2.setMargins(10,0,0,0);
			TextView2.setLayoutParams(llTextView2);
			llTextView3.setMargins(10,0,0,0);
			TextView3.setLayoutParams(llTextView3);
			llTextView5.setMargins(10,0,0,0);
			TextView5.setLayoutParams(llTextView5);
		}else
		{
			llTextView1.setMargins(100,0,0,0);
			TextView1.setLayoutParams(llTextView1);
			llTextView2.setMargins(100,0,0,0);
			TextView2.setLayoutParams(llTextView2);
			llTextView3.setMargins(100,0,0,0);
			TextView3.setLayoutParams(llTextView3);
			llTextView5.setMargins(100,0,0,0);
			TextView5.setLayoutParams(llTextView5);
		}
		mSave = (Button) findViewById(R.id.save);
		mCancel = (Button) findViewById(R.id.cancel);
		
		user_name=intent.getStringExtra("user_name");
		password=intent.getStringExtra("passowrd");
		ezserver_ip=intent.getStringExtra("ezserver_ip");
		http_port=intent.getStringExtra("http_port");
		
		orientation = this.getResources().getConfiguration().orientation;
		String szversion="Version: "+versionno;
		mVersion.setText(szversion);
		mUsername.setText(user_name);
		mPassword.setText(password);
		mEzserver_ip.setText(ezserver_ip);
		mHttp_port.setText(http_port);
		
		mSave.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				
				user_name=mUsername.getText().toString();
				intent.putExtra("user_name",user_name);
				
				password=mPassword.getText().toString();
				intent.putExtra("passowrd",password);
				
				ezserver_ip=mEzserver_ip.getText().toString();
				intent.putExtra("ezserver_ip",ezserver_ip);

				
				http_port=mHttp_port.getText().toString();
				intent.putExtra("http_port",http_port);
								
				
				setResult(RESULT_OK, intent);



				finish();
				
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});

	}
}