<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/orla.js"></script>
    <link rel="stylesheet" type="text/css" media="all" href="<%=application.getContextPath()%>/css/orla.css">
</head>
<body>
<%@include file="../nav.jsp" %>
<section>
    <p>These readings are the average glucose values for the date.</p>

    <div id="dailys"></div>
    <hr/>
</section>
<section>
    <div id="hourlys"></div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    $(document).ready(function () {
        $.getJSON("<%=application.getContextPath()%>/api/home/dailyStats" + window.location.search, function (model) {
            var earliest = model[0].date;
            var latest = model[model.length - 1].date;
            setWeeklyByDay(earliest, latest, $('#dailys'), "cal");

            for (i = 0; i < model.length; i++) {
                $('#cal' + model[i].date).append(dailyInfo(model[i].meanBG, model[i].ascDesc, model[i].numReadings));
                $('#cal' + model[i].date).attr('title', model[i].date);
                $('#cal' + model[i].date).css({'background-color':heatmapColor(model[i].meanBG)});
                $('#cal' + model[i].date + ' > .calBG').css({'color':heatmapText(model[i].meanBG)});
            }
            window.scrollTo(0, document.body.scrollHeight);
        });

        $.getJSON("<%=application.getContextPath()%>/api/home/hourlyStats" + window.location.search, function (model) {
            var earliest = model[0].date;
            var latest = model[model.length - 1].date;
            setDailyByHour(earliest, latest, $('#hourlys'), "day");

            for (i = 0; i < model.length; i++) {
                var selector = '#day' + model[i].date + ' :nth-child('+(model[i].hour + 2)+')';
                var bg = model[i].meanBG;
                $(selector).html(bg.toFixed(1));
                $(selector).css({'background-color':heatmapColor(bg), 'color':heatmapText(bg),
                                 'text-align':'right'});
            }
            window.scrollTo(0, document.body.scrollHeight);
        });
    });

</script>
</body>
</html>
