<%@ page import="endafarrell.orla.api.home.SmartPixUploadServlet" %>
<html>
<head>
    <title>Add SmartPix data</title>
    <link rel="stylesheet" type="text/css" media="all" href="<%=application.getContextPath()%>/css/orla.css">
</head>
<body>
<%@include file="nav.jsp" %>
<section>
    <p>Use this form to upload new SmartPix data.</p>

    <form action="<%=application.getContextPath() + SmartPixUploadServlet.URL%>"
          enctype="multipart/form-data" method="POST">
        <input type="file" name="file" multiple="" autofocus="autofocus" /><br/>
        <input type="Submit" value="Upload SmartPix data"><br>
    </form>
</section>
</body>
</html>