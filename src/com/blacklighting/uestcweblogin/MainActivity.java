package com.blacklighting.uestcweblogin;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.blacklighting.uestcweblogin.internet.LogThread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public final static int INTERNET_UNAVIABLE = 0, ALREADY_LOGIN = 1,
			PASSWD_ERROR = 2, LOGIN_SUCCESS = 3, UNKNOWN_ERROR = 4,
			LOGOUT_SUCCESS = 5;

	final String loginUrl = "http://202.115.254.251/";
	EditText accoutEditText, passwdEditText;
	Button loginButton, logoutButton;
	CheckBox remberCheckBox;
	private String account, passwd;
	MHandler mHandler = new MHandler(this);
	SharedPreferences sp;
	boolean rember;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始换控件变量
		accoutEditText = (EditText) findViewById(R.id.accountEditText);
		passwdEditText = (EditText) findViewById(R.id.passwdEditText);
		loginButton = (Button) findViewById(R.id.loginButton);
		logoutButton = (Button) findViewById(R.id.logoutButton);
		remberCheckBox = (CheckBox) findViewById(R.id.remberCheckBox);

		// 为按钮绑定监听器
		loginButton.setOnClickListener(this);
		logoutButton.setOnClickListener(this);

		// 从本地记录回复保存的用户名密码
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		account = sp.getString("account", null);
		passwd = sp.getString("passwd", null);
		rember = sp.getBoolean("rember", false);

		if (account != null) {
			accoutEditText.setText(account);
		}

		if (passwd != null) {
			passwdEditText.setText(passwd);
		}

		remberCheckBox.setChecked(rember);

		remberCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						rember = isChecked;
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Editor ed = sp.edit();
		ed.putString("account", account);
		ed.putString("passwd", passwd);
		ed.putBoolean("rember", rember);
		ed.commit();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			Builder builder = new Builder(MainActivity.this);
			builder.setTitle("关于")
					.setMessage("作者：刘亚军 @黑色之光\nEmail：liuyajun52@gmail.com")
					.setPositiveButton("确认", null);
			builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginButton:
			account = accoutEditText.getText().toString();
			passwd = passwdEditText.getText().toString();

			// 准备要向网站传递的参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("DDDDD", account);
			params.put("upass", passwd);
			params.put("0MKKey", "%B5%C7%C2%BC+Login");
			params.put("Submit", "Login/登陆");

			// 开始登录
			new LogThread(loginUrl, params, mHandler, true).start();

			break;
		case R.id.logoutButton:
			account = accoutEditText.getText().toString();
			passwd = passwdEditText.getText().toString();
			// 准备要向网站传递的参数
			Map<String, String> params2 = new HashMap<String, String>();
			params2.put("DDDDD", account);
			params2.put("upass", passwd);
			params2.put("0MKKey", "%B5%C7%C2%BC+Login");
			params2.put("Submit2", "Logout/注销");

			// 开始注销
			// 开始登录
			new LogThread(loginUrl, params2, mHandler, false).start();
			break;
		default:
			break;
		}
	}

	static class MHandler extends Handler {
		private WeakReference<MainActivity> act;

		public MHandler(MainActivity act) {
			this.act = new WeakReference<MainActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case INTERNET_UNAVIABLE:
				Toast.makeText(act.get(), "无网络链接", Toast.LENGTH_SHORT).show();
				break;
			case LOGIN_SUCCESS:
				Toast.makeText(act.get(), "登录成功", Toast.LENGTH_SHORT).show();
				break;
			case UNKNOWN_ERROR:
				Toast.makeText(act.get(), "未知错误", Toast.LENGTH_SHORT).show();
				break;
			case ALREADY_LOGIN:
				Toast.makeText(act.get(), "网络已经链接，不需要登录", Toast.LENGTH_SHORT)
						.show();
				break;
			case LOGOUT_SUCCESS:
				Toast.makeText(act.get(), "注销成功", Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}

}
