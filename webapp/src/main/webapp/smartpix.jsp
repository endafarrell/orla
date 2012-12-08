<%@ page import="endafarrell.orla.api.home.SmartPixUploadServlet" %>
<html>
<head></head>
<body>
<form action="<%=request.getServletContext().getContextPath() + SmartPixUploadServlet.URL%>"
      enctype="multipart/form-data" method="POST">
    <input type="file" name="file"/><br/>
    <input type="Submit" value="Upload SmartPix data"><br>
</form>
</body>
</html>