/**
 * 
 */
package com.blacklighting.uestcweblogin2.internet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;

import com.blacklighting.uestcweblogin2.MainActivity;

/**
 * @author liuyajun
 * 
 */
public class LogThread extends Thread {
	private String url;
	Handler mHandler;
	final String testUrl = "http://baidu.com";
	final String testHostServer = "DRCOM-IIS-YanDaDa/2.00";
	boolean usingPost;
	private String account, passwd;

	public LogThread(String url, String accout, String passwd,
			Handler mHandler, boolean usingPost) {
		this.url = url;
		this.account = accout;
		this.passwd = passwd;
		this.mHandler = mHandler;
		this.usingPost = usingPost;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		if (usingPost) {
			try {
				if (testInternetAccess(testUrl, testHostServer)) {
					mHandler.sendEmptyMessage(MainActivity.ALREADY_LOGIN);
					return;
				} else {
					HttpURLConnection conn = null;

					conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setConnectTimeout(5000);
					conn.setDoInput(true);
					conn.setDoOutput(true); // 打开输出流，以便向服务器提交数据
					conn.setRequestMethod("POST"); // 设置以Post方式提交数据
					conn.setRequestProperty(
							"Content-Length",
							String.valueOf(("DDDDD=" + account + "&upass="
									+ passwd + "&0MKKey=%25B5%25C7%25C2%25BC%2BLogin&Submit=Login%2F%E7%99%BB%E9%99%86")
									.getBytes().length));
					// 获得输出流，向服务器写入数据
					OutputStream out = conn.getOutputStream();
					out.write(("DDDDD=" + account + "&upass=" + passwd + "&0MKKey=%25B5%25C7%25C2%25BC%2BLogin&Submit=Login%2F%E7%99%BB%E9%99%86")
							.getBytes());
					conn.getInputStream();
					conn.disconnect();

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (testInternetAccess(testUrl, testHostServer)) {
							mHandler.sendEmptyMessage(MainActivity.LOGIN_SUCCESS);
						} else {
							mHandler.sendEmptyMessage(MainActivity.PASSWD_ERROR);
						}
					}

				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(MainActivity.UNKNOWN_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(MainActivity.UNKNOWN_ERROR);
			}
		} else {
			try {
				if (!testInternetAccess(testUrl, testHostServer)) {
					mHandler.sendEmptyMessage(MainActivity.ALREADY_LOGIN);
					return;
				} else {
					HttpURLConnection conn = null;
					conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
//					conn.setDoOutput(true); // 打开输出流，以便向服务器提交数据
					// 获得输出流，向服务器写入数据
					conn.getInputStream();
//					OutputStream out = conn.getOutputStream();
//					out.write(("DDDDD=" + account + "&upass=" + passwd)
//							.getBytes());
					// conn.connect();
					// 获得输出流，向服务器写入数据
					conn.disconnect();

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (!testInternetAccess(testUrl, testHostServer)) {
							mHandler.sendEmptyMessage(MainActivity.LOGOUT_SUCCESS);
						} else {
							mHandler.sendEmptyMessage(MainActivity.PASSWD_ERROR);
						}
					}

				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(MainActivity.UNKNOWN_ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(MainActivity.UNKNOWN_ERROR);
			}

		}
	}

	/**
	 * 测试设备是否能链接到指定网站
	 * 
	 * @param testUrl
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private boolean testInternetAccess(String testUrl, String server)
			throws MalformedURLException, IOException {
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) new URL(testUrl).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
		conn.setRequestProperty("Host", "www.baidu.com");

		int returncode = conn.getResponseCode();
		String headerServer = conn.getHeaderField("Server");
		conn.disconnect();
		return (returncode == HttpURLConnection.HTTP_OK && (headerServer == null || !headerServer
				.equals(server)));
	}
}
