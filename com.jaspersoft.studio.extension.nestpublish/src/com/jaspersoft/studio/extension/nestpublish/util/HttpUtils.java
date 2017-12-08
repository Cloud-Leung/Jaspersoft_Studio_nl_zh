package com.jaspersoft.studio.extension.nestpublish.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

	/**
	 * 文件上传
	 */
	public static String uploadFile(String url, String auth, File file)
			throws Exception {
		// 换行符
		final String newLine = "\r\n";
		final String boundaryPrefix = "--";
		// 定义数据分隔线
		String BOUNDARY = "========7d4a6d158c9";
		OutputStream out = null;
		try {
			HttpURLConnection conn = buildConnection(url, auth, BOUNDARY);
			out = new DataOutputStream(conn.getOutputStream());

			String paramHead = paramHead(file, boundaryPrefix, BOUNDARY,
					newLine);
			// 将参数头的数据写入到输出流中
			out.write(paramHead.getBytes());

			// 文件写入输出流
			outputStream(out, file, newLine, boundaryPrefix, BOUNDARY);
			
			// 定义BufferedReader输入流来读取URL的响应
			return getResult(conn);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * 获取返回结果
	 */
	private static String getResult(HttpURLConnection conn) throws Exception {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		InputStreamReader streamReader = null;
		InputStream stream = null;
		try {
			int code = conn.getResponseCode();

			stream = conn.getInputStream();
			streamReader = new InputStreamReader(stream, "utf-8");
			reader = new BufferedReader(streamReader);
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			if (code >= 400) {
				throw new Exception("请求错误:code=" + code + ", "
						+ builder.toString());
			}
		} finally {
			if (null != stream) {
				stream.close();
			}
			if (null != streamReader) {
				streamReader.close();
			}
			if (null != reader) {
				reader.close();
			}
		}
		return builder.toString();
	}

	/**
	 * 文件写入输出流
	 */
	private static void outputStream(OutputStream out, File file,
			String newLine, String boundaryPrefix, String BOUNDARY)
			throws IOException {
		// 数据输入流,用于读取文件数据
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		byte[] bufferOut = new byte[1024];
		int bytes = 0;
		// 每次读1KB数据,并且将文件数据写入到输出流中
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		// 最后添加换行
		out.write(newLine.getBytes());
		in.close();

		// 定义最后数据分隔线，即--加上BOUNDARY再加上--。
		byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)
				.getBytes();
		// 写上结尾标识
		out.write(end_data);
		out.flush();

	}

	/**
	 * 获取http连接
	 */
	private static HttpURLConnection buildConnection(String url, String auth,
			String BOUNDARY) throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		// 设置为POST情
		conn.setRequestMethod("POST");
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		// 设置请求头参数
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Accept", "application/json, text/plain, */*");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ BOUNDARY);
		conn.setRequestProperty("Auth", auth);
		return conn;
	}

	/**
	 * 获取参数头
	 */
	private static String paramHead(File file, String boundaryPrefix,
			String BOUNDARY, String newLine) {
		// 上传文件
		StringBuilder sb = new StringBuilder();
		sb.append(boundaryPrefix);
		sb.append(BOUNDARY);
		sb.append(newLine);
		// 文件参数,photo参数名可以随意修改
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
				+ file.getName() + "\"" + newLine);
		sb.append("Content-Type:application/octet-stream");
		// 参数头设置完以后需要两个换行，然后才是参数内容
		sb.append(newLine);
		sb.append(newLine);
		return sb.toString();
	}

}
