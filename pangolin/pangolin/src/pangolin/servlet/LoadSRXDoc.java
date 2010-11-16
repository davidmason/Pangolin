package pangolin.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.lib.segmentation.SRXDocument;
import static pangolin.parsers.DocNameParser.*;
import pangolin.services.RepositoryService;
import pangolin.services.SRXDocumentService;

/**Processes the document loading form to load appropriate documents
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public final class LoadSRXDoc extends HttpServlet {

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
		
		SRXDocument srxDoc = null;
		String error = null;
		
		//wrap the request to handle multipart form type
		MultipartParser parser = new MultipartParser(request, 65535, false, false, "UTF-8");
		
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		
		Part nextPart = null;
		while ((nextPart = parser.readNextPart()) != null) {
			if (nextPart.isParam()) {
				ParamPart p = (ParamPart)nextPart;
				params.put(p.getName(), p.getStringValue());
			} else if (nextPart.isFile()) {
				FilePart f = (FilePart)nextPart;
				String fileName = f.getFileName();
				if (fileName != null) {
					//load the file
					SRXDocument newSRX = SRXDocumentService.buildBlankSRXDoc();
					try {
						newSRX.loadRules(f.getInputStream());
					} catch (OkapiIOException e) {
						newSRX = null;
						error = "invalid-file";
					}
					if (newSRX != null) {
						srxDoc = newSRX;
						//set the filename session attribute
						request.getSession().setAttribute("file-name", extractDocName(fileName));
					}
				}
			}
		}
		
		
		boolean loadNew = params.get("load-new") != null;
		boolean loadTest = params.get("load-test") != null;
		boolean loadLocal = params.get("load-local") != null;
		boolean loadUrl = params.get("load-url") != null;
		boolean loadRepositoryDoc = params.get("load-repository") != null;
		
		String urlString = params.get("file-url");
		String repositoryDoc = params.get("selected-document");
		boolean refreshDocs = params.get("refresh-repository") != null;
		
		
		if (refreshDocs) {
			request.getSession().setAttribute("repository-docs", RepositoryService.getDocList());
			response.sendRedirect("chooseFile.jsp");
			return;
		}
		
		
		if (loadRepositoryDoc) {
			try {
				srxDoc = loadFromURL(RepositoryService.getDocURL(repositoryDoc), request);
			} catch (MalformedURLException e) {
				srxDoc = null;
				error = "invalid-url";
			} catch (IOException e) {
				srxDoc = null;
				//generic error will be used
			} catch (OkapiIOException e) {
				srxDoc = null;
				error = "invalid-file";
			}
		} else if (loadNew) {
			srxDoc = SRXDocumentService.buildBlankSRXDoc();
			request.getSession().removeAttribute("file-name");
		}
		else if (loadTest) {
			srxDoc = SRXDocumentService.buildTestSRXDoc();
			request.getSession().setAttribute("file-name", "test_doc.srx");
		}
		else if (loadLocal) {
			if (srxDoc == null && error == null) {
				error = "local-file-load-error";
			}
			//else it loaded successfully
		}
		else if (loadUrl) {
			try {
				srxDoc = loadFromURL(urlString, request);
			} catch (MalformedURLException e) {
				srxDoc = null;
				error = "invalid-url";
			} catch (IOException e) {
				srxDoc = null;
				//generic error will be used
			} catch (OkapiIOException e) {
				srxDoc = null;
				error = "invalid-file";
			}
		}
		else {
			//no valid option selected
			error = "no-load-option";
		}
		
		if (error == null && srxDoc == null) {
			error = "document-load-error";
		}
		
		if (error != null) {
			response.sendRedirect("chooseFile.jsp?error=" + error);
		} else {
			//clear session attributes from previous document
			request.getSession().removeAttribute("languageRuleName");
			request.getSession().removeAttribute("selectedRuleIndex");
			
			request.getSession().setAttribute("srxDoc", srxDoc);
			response.sendRedirect("editor.jsp");
			//TODO success message when loading a file
		}
	}
	
	
	
	/* Loads an SRX document from a URL
	 * 
	 * @param urlString
	 * @param request
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws OkapiIOException
	 */
	private SRXDocument loadFromURL(String urlString, HttpServletRequest request) throws MalformedURLException, IOException, OkapiIOException {
		SRXDocument newSRX = SRXDocumentService.buildBlankSRXDoc();
		String fileName = null;
		URL url = new URL(urlString);
		fileName = url.getFile();
		newSRX.loadRules(url.openStream());
		//set the filename session attribute
		request.getSession().setAttribute("file-name", extractDocName(fileName));
		
		return newSRX;
	}
}
