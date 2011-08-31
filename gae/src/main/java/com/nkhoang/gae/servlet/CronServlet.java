package com.nkhoang.gae.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CronServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String message = URLEncoder.encode("my message", "UTF-8");

		try {
			URL url = new URL(
					"http://hoangmy-chara.appspot.com/item/viewAll.html");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection
					.getOutputStream());
			writer.write("message=" + message);
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
			} else {
				// Server returned HTTP error code.
			}
		} catch (MalformedURLException e) {
			// ...
		} catch (IOException e) {
			// ...
		}
	}
}
