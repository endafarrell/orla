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
        }

        td.Mon, td.Tue, td.Wed, td.Thu, td.Fri {
            border-right: 3px white solid;
        }

        td.Sat, td.Sun {
            border-right: 3px blue solid;
        }

        td, span.time {
            padding: 0 0.5em
        }

        tr.day td {
            padding-bottom: 1em;
        }

        td.dayDetails {
            position: relative;
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
            color: silver
        }

    </style>
    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="js/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="js/jquery.flot.js"></script>
    <script type="text/javascript">
        (function($) {
            $.QueryString = (function(a) {
                if (a == "") return {};
                var b = {};
                for (var i = 0; i < a.length; ++i)
                {
                    var p=a[i].split('=');
                    if (p.length != 2) continue;
                    b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
                }
                return b;
            })(window.location.search.substr(1).split('&'))
        })(jQuery);
    </script>
</head>
<body>
<nav>&laquo;
    <ul>
        <li><a rel="home" href="index.jsp">home</a></li>
        <li>&crarr;</li>
        <li><a href="api/home/glucose?w=4&l=25&h=75">glucose readings</a></li>
    </ul>
    &raquo;</nav>
<section>
    <div class="graph">
        <h3>Glucose readings</h3>
        <div id="ph0" style="width: 90%; height: 300px"></div>
    </div>
    <div class="graph">
        <h3>Ascent/descent</h3>
        <div id="pha" style="width: 90%; height: 300px"></div>
    </div>

    <div class="graph">
        <h3>Carbs ratio per day</h3>
        <div id="ph3" style="width: 90%; height:300px;"></div>
    </div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">



    $(document).ready(function () {
        var plots = [];
        var data = [];
        var hours = {};
        var hourMean = [];
        var hourCounts = [];
        hourLabels = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"];
        for (index in hourLabels) {
            hours[hourLabels[index]] = [];
        }
        var flotOptions = {
            xaxis:{ mode:"time" },
            grid:{ hoverable:true, clickable:true, markings: nightAndWeekendAreas },
            yaxis:{ labelWidth:70},
            points: { show: true },
            lines: { show: true }
        };
        function showTooltip(x, y, contents) {
                $('<div id="tooltip">' + contents + '</div>').css( {
                    position: 'absolute',
                    display: 'none',
                    top: y + 5,
                    left: x + 5,
                    border: '1px solid #fdd',
                    padding: '2px',
                    'background-color': '#fee',
                    opacity: 0.80
                }).appendTo("body").fadeIn(200);
        }
        function carbsFormatter(v, axis) {
            return v.toFixed(axis.tickDecimals) +"g";
        }
        function bolusFormatter(v, axis) {
            return v.toFixed(axis.tickDecimals) +"IU";
        }
        function carbRatioFormatter(v, axis) {
            return v.toFixed(axis.tickDecimals) + "U/10g";
        }
        function bGFormatter(v, axis) {
            return v.toFixed(axis.tickDecimals) + " mmol/L";
        }
        // helper for returning the weekends in a period
        function weekendAreas(axes) {
            var markings = [];
            var d = new Date(axes.xaxis.min);
            // go to the first Saturday
            d.setUTCDate(d.getUTCDate() - ((d.getUTCDay() + 1) % 7));
            d.setUTCSeconds(0);
            d.setUTCMinutes(0);
            d.setUTCHours(0);
            var i = d.getTime();
            do {
                // when we don't set yaxis, the rectangle automatically
                // extends to infinity upwards and downwards
                markings.push({ xaxis: { from: i, to: i + 2 * 24 * 60 * 60 * 1000 }, color: "#32cd32" });
                i += 7 * 24 * 60 * 60 * 1000;
            } while (i < axes.xaxis.max);

            return markings;
        }

        // helper for returning the nights in a period
        function nightAreas(axes) {
            var markings = [];
            var d = new Date(axes.xaxis.min);
            // go to the first 11pm
            d.setUTCSeconds(0);
            d.setUTCMinutes(0);
            d.setUTCHours(23);
            var i = d.getTime();
            do {
                markings.push({ xaxis: { from: i, to: i + 8 * 60 * 60 * 1000}, color: "#dddddd"});
                i += 24 * 60 * 60 * 1000;
            } while (i < axes.xaxis.max);

            return markings;
        }

        function nightAndWeekendAreas(axes) {
            var markings = [];

            // Nights
            var d = new Date(axes.xaxis.min);
            // go to the first 11pm
            d.setUTCSeconds(0);
            d.setUTCMinutes(0);
            d.setUTCHours(23);
            var i = d.getTime();
            do {
                markings.push({
                    xaxis: { from: i, to: i + 8 * 60 * 60 * 1000},
                    yaxis: { from: 0, to: axes.yaxis.max * 0.1 },
                    color: "#dddddd"
                });
                i += 24 * 60 * 60 * 1000;
            } while (i < axes.xaxis.max);

            // Weekends
            d = new Date(axes.xaxis.min);
            // go to the first Saturday
            d.setUTCDate(d.getUTCDate() - ((d.getUTCDay() + 1) % 7));
            d.setUTCSeconds(0);
            d.setUTCMinutes(0);
            d.setUTCHours(0);
            i = d.getTime();
            do {
                // when we don't set yaxis, the rectangle automatically
                // extends to infinity upwards and downwards
                markings.push({
                    xaxis: { from: i, to: i + 2 * 24 * 60 * 60 * 1000 },
                    yaxis: { from: 0, to: axes.yaxis.max * 0.05 },
                    color: "#32cd32"
                });
                i += 7 * 24 * 60 * 60 * 1000;
            } while (i < axes.xaxis.max);
            return markings;
        }

        // Glucose readings
        var previousPoint = null;
        $.getJSON("api/home/glucose" + window.location.search, function (model) {
            var bGs = [];
            for (var index = 0; index < model.length; index++) {
                bGs.push([new Date(model[index].startTime), model[index].value]);
            }
            plots.push($.plot($("#ph0"), [bGs], $.extend(true,{}, flotOptions,{yaxes:[{tickFormatter: bGFormatter, tickDecimals:1}]})));
        });

        $.getJSON("api/home/ascentDescent" + window.location.search, function (model) {
            var ads = [];
            for (var index = 0; index < model.length; index++) {
                ads.push([new Date(model[index].date), model[index].ascDesc]);
            }
            plots.push($.plot($("#pha"), [ads], $.extend(true,{}, flotOptions,{yaxes:[{tickFormatter: bGFormatter, tickDecimals:1}]})));
        });

        // Carbs vs bolus ratio
        $.getJSON("api/home/events/byDay?skipEventsList=true&" + window.location.search.replace("?",""), function (model) {
            var carbs=[];
            var bolus=[];
            for (var index = 0; index < model.length; index++) {
                var ratio = 1
                if (model[index].carbs != 0) {
                    ratio = model[index].bolus / (model[index].carbs / 10);
                }
                carbs.push([new Date(model[index].date), ratio]);
            }
            plots.push($.plot($("#ph3"),
                   [ { data: carbs, label: "Daily bolus U/10g carbs ratio" }],
                    $.extend(true, {}, flotOptions, {
                       xaxes: [ { mode: 'time' } ],
                       yaxes: [ { tickFormatter: carbRatioFormatter }],
                       legend: { position: 'se' }
                   })));
        });
        setTimeout(function(){
            for (var index in plots){
                plots[index].getPlaceholder().bind("plothover", function (event, pos, item) {
                    $("#x").text(pos.x.toFixed(2));
                    $("#y").text(pos.y.toFixed(2));

                    if (item) {
                        if (previousPoint != item.dataIndex) {
                            previousPoint = item.dataIndex;

                            $("#tooltip").remove();
                            var x = item.datapoint[0],
                                y = item.datapoint[1];

                            var content = "";
                            if (item.series.xaxis.options.mode == "time") {
                                content = item.series.label + " on " + ((new Date(x)).toUTCString().replace(" 00:00:00 GMT","").replace(":00 GMT",""))
                                        + " = " + item.series.yaxis.tickFormatter(y,item.series.yaxis);
                            } else {
                                content = item.series.label + " of " + x + " = " + item.series.yaxis.tickFormatter(y,item.series.yaxis);
                            }
                            showTooltip(item.pageX, item.pageY, content);
                        }
                    } else {
                        $("#tooltip").remove();
                        previousPoint = null;
                    }

                });
            }
        }, 1000);
    });
</script>
</body>
</html>
