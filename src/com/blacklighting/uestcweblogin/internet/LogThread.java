/**
 * 
 */
package com.blacklighting.uestcweblogin.internet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.blacklighting.uestcweblogin.MainActivity;

import android.os.Handler;

/**
 * @author liuyajun
 * 
 */
public class LogThread extends Thread {
	private String url;
	private Map<String, String> params;
	Handler mHandler;
	final String testUrl = "http://baidu.com";
	boolean usingPost;

	public LogThread(String url, Map<String, String> params, Handler mHandler,
			boolean usingPost) {
		this.url = url;
		this.params = params;
		this.mHandler = mHandler;
		this.usingPost = usingPost;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		if (usingPost) {
			try {
				if (testInternetAccess(testUrl)) {
					mHandler.sendEmptyMessage(MainActivity.ALREADY_LOGIN);
					return;
				} else {
					HttpURLConnection conn = null;
					byte[] data = getRequestData(params, "UTF-8").toString()
							.getBytes();// 获得请求体

					conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setConnectTimeout(5000);
					conn.setDoOutput(true); // 打开输出流，以便向服务器提交数据
					conn.setRequestMethod("POST"); // 设置以Post方式提交数据
					conn.setRequestProperty("Content-Length",
							String.valueOf(data.length));
					// 获得输出流，向服务器写入数据
					OutputStream out = conn.getOutputStream();
					out.write(data);
					conn.disconnect();

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (testInternetAccess(testUrl)) {
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
				if (!testInternetAccess(testUrl)) {
					mHandler.sendEmptyMessage(MainActivity.ALREADY_LOGIN);
					return;
				} else {
					HttpURLConnection conn = null;
					byte[] data = getRequestData(params, "UTF-8").toString()
							.getBytes();// 获得请求体

					conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setConnectTimeout(5000);
					conn.setDoOutput(true); // 打开输出流，以便向服务器提交数据
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Content-Length",
							String.valueOf(data.length));
					// 获得输出流，向服务器写入数据
					OutputStream out = conn.getOutputStream();
					out.write(data);
					conn.disconnect();

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (!testInternetAccess(testUrl)) {
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
	private boolean testInternetAccess(String testUrl)
			throws MalformedURLException, IOException {
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) new URL(testUrl).openConnection();
		conn.setConnectTimeout(5000);
		int returncode = conn.getResponseCode();
		conn.disconnect();
		return returncode == HttpURLConnection.HTTP_OK;
	}

	/**
	 * 将要传递给网站接口的参数转化成二进制数组
	 * 
	 * @param params
	 *            要传递给网站接口的参数
	 * @param encode
	 *            传递参数的编码格式
	 * @return
	 */
	private StringBuffer getRequestData(Map<String, String> params,
			String encode) {

		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
}
