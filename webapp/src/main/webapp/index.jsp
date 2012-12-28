<%@ page import="endafarrell.orla.service.data.BaseEvent" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <style type="text/css">
            /* html5doctor.com Reset v1.6.1 (http://html5doctor.com/html-5-reset-stylesheet/) - http://cssreset.com */
        html, body, div, span, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, abbr, address, cite, code, del, dfn, em, img, ins, kbd, q, samp, small, strong, sub, sup, var, b, i, dl, dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, article, aside, canvas, details, figcaption, figure, footer, header, hgroup, menu, nav, section, summary, time, mark, audio, video {
            margin: 0;
            padding: 0;
            border: 0;
            outline: 0;
            font-size: 100%;
            vertical-align: baseline;
            background: transparent
        }

        body {
            line-height: 1;
            font-family: Menlo, Verdana, sans-serif;
            font-size-adjust: 0.58;
            font-size: 10pt;
            xposition: relative;
            width: 100%;
        }

        article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section {
            display: block
        }

        nav {
            background-color: #ccc;
            padding: 0 2px 3px 3px;
            position: fixed;
            top: 0;
            width: 100%;
            margin: 0 auto 1em;
            z-index: 1;
        }

        nav ul {
            list-style: none
        }

        nav ul, nav ul li {
            display: inline
        }

        blockquote, q {
            quotes: none
        }

        blockquote:before, blockquote:after, q:before, q:after {
            content: none
        }

        a {
            margin: 0;
            padding: 0;
            font-size: 100%;
            vertical-align: baseline;
            background: transparent
        }

        ins {
            background-color: #ff9;
            color: #000;
            text-decoration: none
        }

        mark {
            background-color: #ff9;
            color: #000;
            font-style: italic;
            font-weight: bold
        }

        del {
            text-decoration: line-through
        }

        abbr[title], dfn[title] {
            border-bottom: 1px dotted;
            cursor: help
        }

        table {
            border-collapse: collapse;
            border-spacing: 0
        }

        hr {
            display: block;
            height: 1px;
            border: 0;
            border-top: 1px solid #ccc;
            margin: 1em 0;
            padding: 0
        }

        input, select {
            vertical-align: middle
        }

        section {
            margin-top: 5em;
        }

        tr.day {
            width: 100%;
            border-top: 1px silver solid;
        }

        td.day {
            width: 14em;
        }

        td.Mon, td.Tue, td.Wed, td.Thu, td.Fri {
            border-right: 1px silver solid;
        }

        td.Sat, td.Sun {
            border-right: 8px #32cd32 solid;
        }

        td, span.time {
            padding: 0 0.5em
        }

        tr.day td {
            padding-bottom: 1em;
        }

        td.dayDetails {

        }

        .red {
            background-color: red;
        }

        .orange {
            background-color: orange;
        }

        span.none:before {
            display: block;
            content: '\201C';
            font-size: 120%;
        }

        span.none:after {
            display: block;
            content: '\201D';
            font-size: 120%;
        }

        .mmol_L:before {
            content: url(img/drop.png);
            padding-right: 5px;
        }

        .time {
            color: silver;
            font-size: 80%;
        }

    </style>
    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery.tmpl.min.js"></script>
    <script id="eventsByDayListTmpl" type="text/x-jquery-tmpl">
        <table class="width:100%">
            {{each days}}
            <tr class="day ${day}" id="events_${date}">
                <td class="day ${day}">
                    <table>
                        <tr><td colspan="2"><strong>${day}</strong> ${date}</td></tr>
                        <tr><td>carbs:</td><td>${carbs}</td></tr>
                        <tr><td>basal+bolus:</td><td>${<%=BaseEvent.BOLUS_PLUS_BASAL%>} IU</td></tr>
                        <tr><td>bolus:</td><td>${bolus} IU</td></tr>
                        <tr><td>IU/10g:</td><td>${IU_10g}</td></tr>
                    </table>
                </td>
                <td class="dayDetails">
                    {{each events}}
                    <div style="padding-left:${5*time_pct}px;">
                        {{if unit == "km" || unit == "none"}}
                        <span class="time">${date.split(" ")[1]}</span><span class="${unit}">${text}</span>
                        {{else}}
                        {{if unit == "mmol_L"}}
                        <span style="border-right: 8px ${bG_color} solid; padding-right: 4px;"><span class="time">${date.split(" ")[1]}</span><span class="${unit}">${value}</span><span class="unit"> mmol/L</span></span>
                        {{else}}
                        <span class="time">${date.split(" ")[1]}</span>${value}<span class="unit">${unit}</span> ${text}</span>
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
        <li><a href="graph.jsp?w=4&l=25&h=75">graphs</a></li>
        <li>&crarr;</li>
        <li><a href="smartpix.jsp">add SmartPix</a></li>
        <li><a href="api/home/endomondo">check exercise</a></li>
        <li><a href="api/home/twitter">check messages</a></li>
        <li>&crarr;</li>
        <li><a href="api/home/events">see events</a></li>
        <li><a href="api/home/events/byDay">see events by day</a></li>
        <li><a href="api/home/glucose?m=3&l=25&h=75">glucose readings</a></li>
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
