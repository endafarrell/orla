<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.flot.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.flot.time.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.flot.fillbetween.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/orla.js"></script>
    <link rel="stylesheet" type="text/css" media="all" href="<%=application.getContextPath()%>/css/orla.css">
    <script type="text/javascript">
        var plots = [];
        var previousPoint = null;
        var bGOptions = $.extend(true, {}, flotOptions, {yaxes:[
            {tickFormatter:bGFormatter, tickDecimals:1}
        ]});
    </script>
</head>
<body>
<%@include file="nav.jsp" %>
<section>
    <%--<div class="graph">--%>
    <%--<h3>Glucose readings</h3>--%>
    <%--<p>These are the bG readings in this time-frame.</p>--%>
    <%--<div id="ph0" style="width: 85%; height: 300px"></div>--%>
    <%--</div>--%>
    <div id="weeklyBGs">
        <h3>Below are detailed charts of the blood glucose readings for each week.</h3>

        <p>In each of these graphs, the grey areas show night-time and the green areas show the weekends - but there
            is no clinical information implied by them.</p>
    </div>
    <div id="nonWeeklys">
        <div class="graph">
            <h3>Mean bG</h3>

            <p>These are the time-weighted average glucose readings during the day.</p>

            <div id="phm" style="width: 85%; height: 300px"></div>
        </div>
        <div class="graph">
            <h3>Ascent/descent</h3>

            <p>These show how the bG readings have moved up/down in the day. Note though that days with lots of readings
                will have a higher figure here, days with few readings will have a lower figure here. None-the-less,
                lower
                readings here suggest stability.</p>

            <div id="pha" style="width: 85%; height: 300px"></div>
        </div>

        <div class="graph">
            <h3>Hourly basal rates</h3>

            <p>Here are the most recent hourly basal rates: totalling <span id="basalTotal"></span> IU/day.</p>

            <div id="hourlyBasal"></div>
            <div id="phh" style="width: 85%; height: 300px"></div>
        </div>
        <div class="graph">
            <h3>Hourly percentile values</h3>

            <p>Here are percentiled values for each hour</p>

            <div id="php" style="width: 85%; height: 300px"></div>
        </div>

        <div class="graph">
            <h3>Daily overlay</h3>

            <p>Here are the daily readings overlaid for this time-frame.</p>

            <div id="pho" style="width: 85%; height: 600px"></div>
        </div>
    </div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    $(document).ready(function () {
        var weeksOnly = ($.QueryString["weeksonly"] == "true");
        $.getJSON("<%=application.getContextPath()%>/api/home/glucose" + window.location.search, function (model) {
            var dow = {"Sun":6, "Mon":0, "Tue":1, "Wed":2, "Thu":3, "Fri":4, "Sat":5};
            var bGs = [];
            var previousDay = 0;
            var weekId = 0;
            var weeklyOptions = $.extend(true, {},
                    flotOptions, {
                        xaxis:{
                            mode:"time",
                            timeformat:"%a %d-%b",
                            dayNames:["Sun<br/>", "Mon<br/>", "Tue<br/>", "Wed<br/>", "Thu<br/>", "Fri<br/>", "Sat<br/>"]
                        },
                        yaxes:[
                            {tickFormatter:bGFormatter, tickDecimals:1}
                        ]
                    }
            );
            for (var index = 0; index < model.length; index++) {
                var readingDate = $.date(model[index].startTime).toDate();
                if (dow[model[index].day] < dow[previousDay]) {
                    // We are back to Sunday. Close out the previous week (but include this too!)
                    bGs.push([readingDate, model[index].value]);
                    var cId = "#phw" + weekId;
                    var newContainer = $("<div></div>", {id:cId, style:"width: 85%; height: 300px"});
                    var newGraph = $("<div></div>", {class:"graph"});
                    var title = "<h4>{0} glucose readings for the week starting {1}</h4>".format(bGs.length, bGs[0][0].toString().substr(0, 15));
                    newGraph.append(title);
                    newGraph.append(newContainer);
                    $("#weeklyBGs").append(newGraph);
                    plots.push($.plot(newContainer, [ {data:bGs, label:"bG"} ], weeklyOptions));

                    // And reset
                    weekId++;
                    bGs = [];
                }
                bGs.push([readingDate, model[index].value]);
                previousDay = model[index].day;
            }
            // At the end, we need to write what we had
            var cId = "#phw" + weekId;
                                var newContainer = $("<div></div>", {id:cId, style:"width: 85%; height: 300px"});
                                var newGraph = $("<div></div>", {class:"graph"});
                                var title = "<h4>{0} glucose readings for the week starting {1}</h4>".format(bGs.length, bGs[0][0].toString().substr(0, 15));
                                newGraph.append(title);
                                newGraph.append(newContainer);
                                $("#weeklyBGs").append(newGraph);
                                plots.push($.plot(newContainer, [ {data:bGs, label:"bG"} ], weeklyOptions));

        });

        if (!weeksOnly) {
            $.getJSON("<%=application.getContextPath()%>/api/home/dailyStats" + window.location.search, function (model) {
                var ads = [];
                var mbgs = [];
                for (var index = 0; index < model.length; index++) {
                    //noinspection JSUnresolvedVariable
                    ads.push([new Date(model[index].date.replace(/-/g, "/")), model[index].ascDesc]);
                    //noinspection JSUnresolvedVariable
                    mbgs.push([new Date(model[index].date.replace(/-/g, "/")), model[index].meanBG]);
                }
                plots.push($.plot($("#pha"), [ads],
                        $.extend(true, {}, flotOptions, {yaxes:[
                            {tickFormatter:bGFormatter, tickDecimals:1}
                        ]})));
                plots.push($.plot($("#phm"), [
                    { data:mbgs, label:"Time-weighted average bG" }
                ],
                        $.extend(true, {}, flotOptions, {yaxes:[
                            {tickFormatter:bGFormatter, tickDecimals:1}
                        ]})));
            });

            $.getJSON("<%=application.getContextPath()%>/api/home/glucose-overlay" + window.location.search, function (model) {
                var overlays = [];
                for (var day = 0; day < model.length; day++) {
                    // A new overlay
                    var bGs = [], labels = [];
                    //noinspection JSUnresolvedVariable
                    var dayBGs = model[day].bGs;
                    for (var readingId = 0; readingId < dayBGs.length; readingId++) {
                        var readingDate = dayBGs[readingId].startTime;
                        var date = new Date();
                        if (readingId == dayBGs.length - 1 && day < model.length - 1) {
                            date.setDate(date.getDate() + 1);
                        }
                        date.setHours(Number(readingDate.substr(11, 2)));
                        date.setMinutes(Number(readingDate.substr(14, 2)));
                        bGs.push([date, dayBGs[readingId].value]);
                        labels.push(dayBGs[readingId].value + " at "
                                + (new Date(readingDate.substr(0, 10).replace(/-/g, "/"))).toString().substr(0, 4) + readingDate.substr(0, 16));
                    }
                    overlays.push({data:bGs, label:labels});

                }
                plots.push($.plot($("#pho"), overlays,
                        $.extend(true, {},
                                flotOptions, {
                                    grid:{markings:nightAreas },
                                    yaxes:[
                                        {tickFormatter:bGFormatter, tickDecimals:1}
                                    ],
                                    legend:{show:false}
                                })));
            });

            $.getJSON("<%=application.getContextPath()%>/api/home/hourlyBasal" + window.location.search, function (model) {
                var basal = [];
                var html = '<table width="85%">';
                for (index = 0; index < 6; index++) {
                    html += '<tr>';
                    for (var subindex = 0; subindex < 24; subindex += 6) {
                        var hour = index + subindex;
                        //noinspection JSUnresolvedVariable
                        html += '<td width="25%" style="text-align: right">' + (hour) + ":00 - " + (hour + 1) + ":00 " + (model.hours[hour]).toFixed(2) + " IU/hr</td>";
                    }
                    html += '</tr>'
                }
                html += '</table>';
                $('#hourlyBasal').html(html);
                $('#basalTotal').html(model["daily_total"]);
                //noinspection JSUnresolvedVariable
                for (var index = 0; index < model.hours.length; index++) {
                    var dt = new Date();
                    dt.setMinutes(0);
                    dt.setHours(index - (dt.getTimezoneOffset() / 60));
                    //noinspection JSUnresolvedVariable
                    basal.push([dt, model.hours[index]]);
                }

                plots.push($.plot($("#phh"), [
                    { data:basal, label:"Hourly basal rates", lines:{ show:true, steps:true }}
                ],
                        $.extend(true, {},
                                flotOptions, {
                                    yaxes:[
                                        {tickFormatter:bolusRateFormatter, tickDecimals:1}
                                    ]
                                }
                        )));
            });

            $.getJSON("<%=application.getContextPath()%>/api/home/hourlyPercentiles" + window.location.search, function (model) {
                var dataset = [
                    { label:'Glucose percentiles\n15,25,50,75,85%', data:model['50'], lines:{ show:true }, color:"rgb(255,50,50)" },
                    { id:'bg15%', data:model['15'], lines:{ show:true, lineWidth:0, fill:false }, color:"rgb(255,50,50)" },
                    { id:'bg25%', data:model['25'], lines:{ show:true, lineWidth:0, fill:0.2 }, color:"rgb(255,50,50)", fillBetween:'bg15%' },
                    { id:'bg50%', data:model['50'], lines:{ show:true, lineWidth:0.5, fill:0.4, shadowSize:0 }, color:"rgb(255,50,50)", fillBetween:'bg25%' },
                    { id:'bg75%', data:model['75'], lines:{ show:true, lineWidth:0, fill:0.4 }, color:"rgb(255,50,50)", fillBetween:'bg50%' },
                    { id:'bg85%', data:model['85'], lines:{ show:true, lineWidth:0, fill:0.2 }, color:"rgb(255,50,50)", fillBetween:'bg75%' }
                ];
                plots.push($.plot($("#php"), dataset, $.extend(true, {},
                        flotOptions, {
                            xaxis:{ mode:null, min:0, max:23, ticks:[2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22] },
                            yaxes:[
                                {tickFormatter:bGFormatter, tickDecimals:1}
                            ],
                            legend:{ position:'se' }
                        })));
            });
        } else {
            $("#nonWeeklys").hide();
        }

        setTimeout(function () {
            for (var index = 0; index < plots.length; index++) {
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
                            if (Object.prototype.toString.call(item.series.label) === "[object Array]") {
                                content = item.series.label[item.dataIndex];
                            } else if (item.series.xaxis.options.mode == "time") {
                                content = item.series.label + " on " + ((new Date(x)).toUTCString().replace(" 00:00:00 GMT", "").replace(":00 GMT", ""))
                                        + " = " + item.series.yaxis.tickFormatter(y, item.series.yaxis);
                            } else {
                                content = item.series.label + " of " + x + " = " + item.series.yaxis.tickFormatter(y, item.series.yaxis);
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
