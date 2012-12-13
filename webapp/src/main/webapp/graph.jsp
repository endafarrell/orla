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
    <p>Percentiled values</p>

    <div id="ph1" style="width:90%; height:300px;"></div>
    <p>Percentiled values by hour of day</p>

    <div id="ph2" style="width:90%; height:300px;"></div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    var hourMean = [];
    $(document).ready(function () {
        var data = [];
        var hours = {};
        hourLabels = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"];
        for (index in hourLabels) {
            console.log("index in hourLabels", index);
            hours[hourLabels[index]] = [];
        }
        $.getJSON("api/home/glucose" + window.location.search, function (model) {
            for (index in model) {
                data.push([new Date(model[index].date), model[index].value]);
                hours[model[index].date.substr(11, 2)].push(model[index].value);
            }
            $.plot($("#ph1"), [data], { xaxis:{ mode:"time" } });

            for (var hourIndex in hourLabels) {
                var sum = 0;
                if (hours[hourLabels[hourIndex]]) {
                    for (var i = 0, l = hours[hourLabels[hourIndex]].length; i < l; i++) {
                        sum += hours[hourLabels[hourIndex]][i]
                    }
                    if (hours[hourLabels[hourIndex]].length > 0) {
                        hourMean.push([parseInt(hourIndex), sum / hours[hourLabels[hourIndex]].length])
                    } else {
                        console.log("Skipping was good for " + hourIndex);
                    }
                }
            }
            $.plot($("#ph2"), [hourMean], { });
        });
    });
</script>
</body>
</html>
