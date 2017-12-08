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
	 * �ļ��ϴ�
	 */
	public static String uploadFile(String url, String auth, File file)
			throws Exception {
		// ���з�
		final String newLine = "\r\n";
		final String boundaryPrefix = "--";
		// �������ݷָ���
		String BOUNDARY = "========7d4a6d158c9";
		OutputStream out = null;
		try {
			HttpURLConnection conn = buildConnection(url, auth, BOUNDARY);
			out = new DataOutputStream(conn.getOutputStream());

			String paramHead = paramHead(file, boundaryPrefix, BOUNDARY,
					newLine);
			// ������ͷ������д�뵽�������
			out.write(paramHead.getBytes());

			// �ļ�д�������
			outputStream(out, file, newLine, boundaryPrefix, BOUNDARY);
			
			// ����BufferedReader����������ȡURL����Ӧ
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
	 * ��ȡ���ؽ��
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
				throw new Exception("�������:code=" + code + ", "
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
	 * �ļ�д�������
	 */
	private static void outputStream(OutputStream out, File file,
			String newLine, String boundaryPrefix, String BOUNDARY)
			throws IOException {
		// ����������,���ڶ�ȡ�ļ�����
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		byte[] bufferOut = new byte[1024];
		int bytes = 0;
		// ÿ�ζ�1KB����,���ҽ��ļ�����д�뵽�������
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		// �����ӻ���
		out.write(newLine.getBytes());
		in.close();

		// ����������ݷָ��ߣ���--����BOUNDARY�ټ���--��
		byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)
				.getBytes();
		// д�Ͻ�β��ʶ
		out.write(end_data);
		out.flush();

	}

	/**
	 * ��ȡhttp����
	 */
	private static HttpURLConnection buildConnection(String url, String auth,
			String BOUNDARY) throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		// ����ΪPOST��
		conn.setRequestMethod("POST");
		// ����POST�������������������
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		// ��������ͷ����
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Accept", "application/json, text/plain, */*");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ BOUNDARY);
		conn.setRequestProperty("Auth", auth);
		return conn;
	}

	/**
	 * ��ȡ����ͷ
	 */
	private static String paramHead(File file, String boundaryPrefix,
			String BOUNDARY, String newLine) {
		// �ϴ��ļ�
		StringBuilder sb = new StringBuilder();
		sb.append(boundaryPrefix);
		sb.append(BOUNDARY);
		sb.append(newLine);
		// �ļ�����,photo���������������޸�
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
				+ file.getName() + "\"" + newLine);
		sb.append("Content-Type:application/octet-stream");
		// ����ͷ�������Ժ���Ҫ�������У�Ȼ����ǲ�������
		sb.append(newLine);
		sb.append(newLine);
		return sb.toString();
	}

}
