<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/orla.js"></script>
    <link  rel="stylesheet" type="text/css" media="all" href="<%=application.getContextPath()%>/css/orla.css">
</head>
<body>
<%@include file="../nav.jsp" %>
<section>
    <p>These readings are the average glucose values for the date.</p>
    <div id="events"></div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    $(document).ready(function () {
        var monthsSeen=[];
        $.getJSON("<%=application.getContextPath()%>/api/home/dailyStats" + window.location.search, function (model) {
            var monthsSeen = [];
            for(i = 0; i<model.length; i++){
                if ($.inArray(model[i].date.substr(0,7), monthsSeen) == -1){
                    drawCalendar($.date(model[i].date).toDate(), $('#events'));
                    monthsSeen.push(model[i].date.substr(0,7));
                }
                $('#cal'+model[i].date).append(dailyInfo(model[i].meanBG, model[i].ascDesc, model[i].numReadings));
            }
            window.scrollTo(0, document.body.scrollHeight);
        });
    });
</script>
</body>
</html>
