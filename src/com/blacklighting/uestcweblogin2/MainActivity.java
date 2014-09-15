package com.blacklighting.uestcweblogin2;

import java.lang.ref.WeakReference;

import com.blacklighting.uestcweblogin2.internet.LogThread;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public final static int INTERNET_UNAVIABLE = 0, ALREADY_LOGIN = 1,
			PASSWD_ERROR = 2, LOGIN_SUCCESS = 3, UNKNOWN_ERROR = 4,
			LOGOUT_SUCCESS = 5;

	final String loginUrl = "http://202.115.254.251/",
			logoutUrl = "http://202.115.254.251/F.htm?Submit2=Logout%2F%E6%B3%A8%E9%94%80";
	EditText accoutEditText, passwdEditText;
	Button loginButton, logoutButton;
	CheckBox remberCheckBox;
	ProgressBar progressBar;
	private String account, passwd;
	MHandler mHandler = new MHandler(this);
	SharedPreferences sp;
	boolean rember;
	LogThread loginThread, logoutThread;

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
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
						rember = isChecked;
					}
				});
	}

	@Override
	protected void onDestroy() {
		Editor ed = sp.edit();
		ed.putString("account", account);
		if (rember) {
			ed.putString("passwd", passwd);
		} else {
			ed.putString("passwd", "");
		}
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
		switch (item.getItemId()) {
		case R.id.action_settings:
			Builder builder = new Builder(MainActivity.this);
			builder.setTitle("关于")
					.setMessage("作者：刘亚军 @微软创新工作室\nEmail：liuyajun1@hotmail.com")
					.setPositiveButton("确认", null);
			builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginButton:
			account = accoutEditText.getText().toString();
			passwd = passwdEditText.getText().toString();

			// 开始登录
			if (loginThread == null || !loginThread.isAlive()) {
				loginThread = new LogThread(loginUrl, account, passwd,
						mHandler, true);
				loginThread.start();
				progressBar.setVisibility(View.VISIBLE);
				
				
			} else {
				Toast.makeText(MainActivity.this, "请等待当前登录进程结束",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.logoutButton:
			account = accoutEditText.getText().toString();
			passwd = passwdEditText.getText().toString();
			// 开始注销
			if (logoutThread == null || !logoutThread.isAlive()) {
				logoutThread = new LogThread(logoutUrl, account, passwd,
						mHandler, false);
				logoutThread.start();
				progressBar.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(MainActivity.this, "请等待当前注销进程结束",
						Toast.LENGTH_SHORT).show();
			}
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
			case PASSWD_ERROR:
				Toast.makeText(act.get(), "密码错误", Toast.LENGTH_SHORT).show();
				break;
			}
			act.get().progressBar.setVisibility(View.INVISIBLE);
		}

	}

}
