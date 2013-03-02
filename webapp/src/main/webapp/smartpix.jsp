<%@ page import="endafarrell.orla.api.home.SmartPixUploadServlet" %>
<html>
<head></head>
<body>
<%@include file="nav.jsp" %>
<form action="<%=application.getContextPath() + SmartPixUploadServlet.URL%>"
      enctype="multipart/form-data" method="POST">
    <input type="file" name="file" multiple=""/><br/>
    <input type="Submit" value="Upload SmartPix data"><br>
</form>
</body>
</html>