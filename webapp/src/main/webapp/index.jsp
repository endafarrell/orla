<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/orla.js"></script>
    <link  rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/orla.css">

    <script id="eventsByDayListTmpl" type="text/x-jquery-tmpl">
        <table>
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
                    <div style="padding-left:${8*time_pct}px;">
                        {{if clazz=="PumpDailyDoseEvent" || (clazz=="PumpBasalEvent" && text == "null") }}
                            <!-- ${hhmm} ${clazz} ${source} ${text} ${value} ${unit} -->
                        {{else clazz=="CarbEvent"}}
                            <span class="time">${hhmm}</span>
                            <span>${value}</span>
                            <span class="unit">g carbs</span>
                        {{else clazz=="BloodGlucoseEvent"}}
                            <span class="time">${hhmm}</span>
                            <span class="bG">${value}</span>
                            <span class="unit">mmol/L</span>
                        {{else clazz=="TwitterEvent"}}
                            <span class="time">${hhmm}</span>
                            <span class="tweet">${text}</span>
                        {{else (clazz=="PumpBasalEvent" || clazz=="PumpEvent") && value=="null"}}
                            <span class="time">${hhmm}</span>
                            <span>Pump: ${text}</span>
                        {{else clazz=="PumpBolusEvent"}}
                            <span class="time">${hhmm}</span>
                            <span>${value}</span>
                            <span class="unit">IU</span>
                        {{else clazz=="SportEvent"}}
                            <span class="time">${hhmm}</span>
                            <span>${text}</span>
                        {{else}}
                            <span class="time">${hhmm}</span>
                            <span>&laquo;${text}&nbsp;&bull;&nbsp;</span>
                            <span class="unit">${unit}&nbsp;&bull;&nbsp;</span>
                            <span>${source}&nbsp;&bull;&nbsp;</span>
                            <span>${clazz}&nbsp;&bull;&nbsp;</span>
                            <span>${value}&raquo;</span>
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
<%@include file="nav.jsp" %>
<section>
    <div id="events"></div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    $(document).ready(function () {
        $("#eventsByDayListTmpl").template("thisEventsListTemplate");
        $.getJSON("<%=application.getContextPath()%>/api/home/events/byDay?w=8", function (model) {
            var data = {days:model};
            var newMarkup = $.tmpl("thisEventsListTemplate", data);
            newMarkup.appendTo("#events");
            window.scrollTo(0, document.body.scrollHeight);
        });
    });
</script>
</body>
</html>
