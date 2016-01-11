<%@ page isELIgnored ="true" %><%
    String to = OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(DateTime.now());
    String from = OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(DateTime.now().minusWeeks(12));
%><!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/orla.js"></script>
    <link  rel="stylesheet" type="text/css" media="all" href="<%=application.getContextPath()%>/css/orla.css">

    <script id="eventsByDayListTmpl" type="text/x-jquery-tmpl">
        <table class="day">
            {{each(index, item) days}}
            {{if events.length}}
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
                        {{if clazz=="PumpDailyDoseEvent" || clazz=="PumpBasalProfileConfig" ||
                            (clazz=="PumpBasalEvent" && text == "null") }}
                            <!-- ${hhmm} ${clazz} ${source} ${text} ${value} ${unit} -->
                        {{else clazz=="CarbEvent"}}
                            <span class="time">${hhmm}</span>
                            <span>${value}</span>
                            <span class="unit">g carbs</span>
                        {{else clazz=="BloodGlucoseEvent"}}
                            <span class="time">${hhmm}</span>
                            <span class="bG">${value.toFixed(1)}</span>
                            <span class="unit">mmol/L</span>
                        {{else clazz=="TwitterEvent"}}
                            <span class="time">${hhmm}</span>
                            <span class="tweet">${text}</span>
                        {{else (clazz=="PumpBasalEvent" || clazz=="PumpEvent") && value=="null"}}
                            <span class="time">${hhmm}</span>
                            <span>Pump: ${text.replace(" null","")}</span>
                        {{else clazz=="PumpBolusEvent"}}
                            <span class="time">${hhmm}</span>
                            <span>${value}</span>
                            <span class="unit">IU</span>
                        {{else clazz=="BolusEvent"}}
                            <span class="time">${hhmm}</span>
                            <span>${value}</span>
                            <span class="unit">~IU</span>
                        {{else clazz=="SportEvent"}}
                            <span class="time">${hhmm}</span>
                            <span class="sport">${text}</span>
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
            {{/if}}{{/each}}
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
        var fm = $.QueryString["from"] ? $.QueryString["from"] : "<%=from%>";
        var  t = $.QueryString["to"]   ? $.QueryString["to"]   : "<%=to%>";

        function getEvents(url) {
            $.getJSON(url, function (model) {
                var data = {days:model.days};
                $("#eventsByDayListTmpl").tmpl(data).appendTo("#events");
                if (model.next) {
                    getEvents("<%=application.getContextPath()%>"+model.next);
                }
                window.scrollTo(0, document.body.scrollHeight);
            });
        }
        getEvents("<%=application.getContextPath()%>/api/home/events/byDay?from="+fm+"&to="+t);
    });
</script>
</body>
</html>
