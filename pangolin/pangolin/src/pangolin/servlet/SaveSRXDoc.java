package pangolin.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.okapi.lib.segmentation.SRXDocument;

/**Saves the current document to the user's local system
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public final class SaveSRXDoc extends HttpServlet {

	private static final long serialVersionUID = 3489779953197887095L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    										throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	    									throws IOException, ServletException {
		// Set character encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		
		//get srx doc from session attribute
		SRXDocument srxDoc = null;
		try {
			srxDoc = (SRXDocument)request.getSession().getAttribute("srxDoc");
		} catch (ClassCastException e) {
			srxDoc = null;
		}
		if (srxDoc == null) {
			response.sendRedirect("chooseFile.jsp?error=no-document-loaded");
			return;
		}
		
		//get the filename from session variable
		String fileName;
		try {
			fileName = (String)request.getSession().getAttribute("file-name");
		} catch (ClassCastException e) {
			fileName = null;
		}
		if (fileName == null) {
			fileName = "SRX_Rules.srx";
		}
		

		//set response content type so the user gets a save dialog
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename="+fileName);
		
		StringBuffer sb = new StringBuffer(srxDoc.saveRulesToString(true, false));
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		ServletOutputStream out = response.getOutputStream();
		
		byte[] outputByte = new byte[1024];
		int bytesRead = 0;
		//copy binary content to output stream
		while((bytesRead = in.read(outputByte, 0, 1024)) != -1) {
			out.write(outputByte, 0, bytesRead);
		}
		in.close();
		out.flush();
		out.close();
	}
}
