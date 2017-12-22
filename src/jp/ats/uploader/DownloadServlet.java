package jp.ats.uploader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ats.substrate.U;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	@Override
	protected void doGet(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		Record record = UploadManager.getRecord(id);

		String userAgent = request.getHeader("User-Agent");
		String fileNamePart;
		if (userAgent != null && userAgent.indexOf("MSIE") != -1) {
			fileNamePart = "attachment; filename=\""
				+ URLEncoder.encode(record.fileName, "UTF-8")
				+ "\"";
		} else {
			fileNamePart = "attachment; filename*=UTF-8''"
				+ URLEncoder.encode(record.fileName, "UTF-8");
		}

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", fileNamePart);

		BufferedInputStream input = new BufferedInputStream(
			Files.newInputStream(record.file));
		BufferedOutputStream output = new BufferedOutputStream(
			response.getOutputStream());

		U.sendBytes(input, output);

		output.close();
		input.close();
	}
}
