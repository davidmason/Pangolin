package pangolin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.okapi.lib.segmentation.SRXDocument;

/**Updates the mask rule for the sample segmentation area
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class UpdateMaskRule extends HttpServlet {

	private static final long serialVersionUID = 4724281773569224162L;

	
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

		String newMask = request.getParameter("mask-rule-text");
		String oldMask = srxDoc.getMaskRule();
		
		if ((newMask != null && !newMask.equals(oldMask))) {
			srxDoc.setModified(true);
		} else if (oldMask != null) {
			srxDoc.setModified(true);
		}
		srxDoc.setMaskRule(newMask);
		response.sendRedirect("editor.jsp?success=mask-rule-updated");
	}
	
}
