package pangolin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import net.sf.okapi.lib.segmentation.SRXDocument;
import pangolin.services.RepositoryService;
import static pangolin.parsers.DocNameParser.*;

/**Adds the current document to, or updates the current document in the repository.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class UploadSRXDoc extends HttpServlet {

	private static final long serialVersionUID = 4685186649932785136L;

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
		
		String documentName = (String)request.getSession().getAttribute("file-name");
		boolean replaceDoc = request.getParameter("replace") != null;

		int statusCode = RepositoryService.addToRepository(srxDoc, extractDocName(documentName), replaceDoc);
		
		if (statusCode == HttpStatus.SC_CREATED) {
			response.sendRedirect("editor.jsp?success=document-uploaded");
			return;
		}
		if (statusCode == HttpStatus.SC_OK) {
			response.sendRedirect("editor.jsp?success=repo-document-replaced");
			return;
		}
		if (statusCode == HttpStatus.SC_CONFLICT) {
			response.sendRedirect("editor.jsp?error=repository-document-name-conflict");
			return;
		}
		if (statusCode == HttpStatus.SC_NOT_FOUND) {
			response.sendRedirect("editor.jsp?error=repo-document-missing");
			return;
		}
		
		response.sendRedirect("editor.jsp?error=document-upload-error");
		//response.sendRedirect("editor.jsp?ststusCode=" + statusCode);
		return;
	}
}
