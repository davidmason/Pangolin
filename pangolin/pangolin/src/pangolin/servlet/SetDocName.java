package pangolin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Changes the name of the current document
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class SetDocName extends HttpServlet {

	private static final long serialVersionUID = 6814095703925914524L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		// Set character encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String newName = request.getParameter("new-doc-name");
		
		if (newName != null && !"".equals(newName)) {
			request.getSession().setAttribute("file-name", newName);
			response.sendRedirect("editor.jsp?success=doc-name-changed");
		} else {
			response.sendRedirect("editor.jsp?error=invalid-doc-name");
		}
	}
}
