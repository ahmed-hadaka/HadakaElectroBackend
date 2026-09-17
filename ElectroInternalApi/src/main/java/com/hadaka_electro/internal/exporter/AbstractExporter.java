package com.hadaka_electro.internal.exporter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class

AbstractExporter {

	public void setupResponse(String contentType, String prefix, String extension, HttpServletResponse response) {
		response.setContentType(contentType);

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dateFormatter.format(new Date());
		String fileName = prefix + timestamp + extension;

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;

		response.setHeader(headerKey, headerValue);
	}
}
