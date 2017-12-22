package jp.ats.uploader;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

	@Override
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		UploadManager.store(request);

		response.sendRedirect(request.getContextPath() + "/");
	}
}
