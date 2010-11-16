package pangolin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.okapi.lib.segmentation.SRXDocument;

/**Updates the sample text for segmentation
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public final class UpdateSample extends HttpServlet {

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
		
		// Get the SRX document
		SRXDocument srxDoc;
		try {
			srxDoc = (SRXDocument)request.getSession().getAttribute("srxDoc");
		} catch (ClassCastException e) {
			srxDoc = null;
		}
		if (srxDoc == null) {
			response.sendRedirect("chooseFile.jsp?error=no-document-loaded");
			return;
		}
		
		String sample = request.getParameter("sample-text");
		if (sample == null) {
			sample = "";
		}
		
		srxDoc.setSampleText(sample);
		
		response.sendRedirect("editor.jsp?success=sample-updated");
		
		
	}
}