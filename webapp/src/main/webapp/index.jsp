<%@ page import="endafarrell.orla.service.data.BaseEvent" %>
<%@ page import="endafarrell.orla.service.data.Event" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="js/orla.js"></script>
    <link  rel="stylesheet" type="text/css" href="css/orla.css">

    <script id="eventsByDayListTmpl" type="text/x-jquery-tmpl">
        <table class="width:100%">
            {{each days}}
            <tr class="day ${day}" id="events_${date}">
                <td class="day ${day}">
                    <table>
                        <tr><td colspan="2"><strong>${day}</strong> ${date}</td></tr>
                        <tr><td>carbs:</td><td>${carbs}g</td></tr>
                        <tr><td>bolus:</td><td>${bolus}IU</td></tr>
                    </table>
                </td>
                <td class="dayDetails">
                    {{each events}}
                    <div style="padding-left:${5*time_pct}px;">
                        {{if unit == "km" || unit == "none" || unit == null}}
                            <span class="time">${hhmm}</span>
                            <span class="${unit}">${text}</span>
                        {{else unit == "mmol_L"}}
                            <span style="border-right: 8px ${bG_color} solid; padding-right: 4px;">
                                <span class="time">${hhmm}</span>
                                <span class="mmol_L">${value}</span>
                                <span class="unit"> mmol/L</span>
                            </span>
                        {{else unit == "IU"}}
                            {{if text == value }}
                                <!-- nothing -->
                            {{else clazz == "PumpDailyDoseEvent" }}
                                <!-- nothing -->
                            {{else value == "null"}}
                                <span class="time">${hhmm}</span>
                                <span class="IU">Pump: ${text}</span>
                            {{else}}
                                <span class="time">${hhmm}</span>
                                <span class="IU">${value}</span>
                                <span class="unit">${unit} ${text}</span>
                            {{/if}}
                        {{/if}}
                    </div>
                    {{/each}}
                </td>
            </tr>
            {{/each}}
        </table>
    </script>
</head>
<body>
<nav>&laquo;
    <ul>
        <li><a rel="home" href="index.jsp">home</a></li>
        <li><a href="graph.jsp?w=4">graphs</a></li>
        <li>&crarr;</li>
        <li><a href="smartpix.jsp">add SmartPix</a></li>
        <li><a href="api/home/healthgraph">check exercise</a></li>
        <li><a href="api/home/twitter">check messages</a></li>
        <li>&crarr;</li>
        <li><a href="api/home/events">see events</a></li>
        <li><a href="api/home/events/byDay?w=8">see events by day</a></li>
        <li><a href="api/home/glucose?w=8">glucose readings</a></li>
        <li><a href="api/sys/config">see config</a></li>
    </ul>
    &raquo;</nav>
<section>
    <div id="events"></div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    $(document).ready(function () {
        $("#eventsByDayListTmpl").template("thisEventsListTemplate");
        $.getJSON("api/home/events/byDay?w=8", function (model) {
            var data = {days:model};
            var newMarkup = $.tmpl("thisEventsListTemplate", data);
            newMarkup.appendTo("#events");
            window.scrollTo(0, document.body.scrollHeight);
        });
    });
</script>
</body>
</html>
