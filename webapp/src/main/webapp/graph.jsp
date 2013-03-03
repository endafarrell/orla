<!DOCTYPE HTML>
<html>
<head>
    <title>Orla - diabetes diary</title>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/jquery.flot.js"></script>
    <script type="text/javascript" src="<%=application.getContextPath()%>/js/orla.js"></script>
    <link  rel="stylesheet" type="text/css" href="<%=application.getContextPath()%>/css/orla.css">
</head>
<body>
<%@include file="nav.jsp" %>
<section>
    <div class="graph">
        <h3>Glucose readings</h3>
        <p>These are the bG readings in this time-frame.</p>
        <div id="ph0" style="width: 90%; height: 300px"></div>
    </div>
    <div class="graph">
        <h3>Mean bG</h3>
        <p>These are the time-weighted average glucose readings during the day.</p>
        <div id="phm" style="width: 90%; height: 300px"></div>
    </div>
    <div class="graph">
        <h3>Ascent/descent</h3>
        <p>These show how the bG readings have moved up/down in the day. Note though that days with lots of readings
        will have a higher figure here, days with few readings will have a lower figure here. None-the-less, lower
        readings here suggest stability.</p>
        <div id="pha" style="width: 90%; height: 300px"></div>
    </div>
    <div class="graph">
        <h3>Daily overlay</h3>
        <p>Message</p>
        <div id="pho" style="width: 90%; height: 300px"></div>
    </div>
</section>
<footer>&laquo;footer&raquo;</footer>
<script type="text/javascript">
    $(document).ready(function () {
        var plots = [];

        // Glucose readings
        var previousPoint = null;
        $.getJSON("<%=application.getContextPath()%>/api/home/glucose" + window.location.search, function (model) {
            var bGs = [];
            for (var index = 0; index < model.length; index++) {
                bGs.push([new Date(model[index].startTime), model[index].value]);
            }
            plots.push($.plot($("#ph0"), [bGs], $.extend(true,{}, flotOptions,{yaxes:[{tickFormatter: bGFormatter, tickDecimals:1}]})));
        });

        $.getJSON("<%=application.getContextPath()%>/api/home/dailyStats" + window.location.search, function (model) {
            var ads = [];
            var mbgs = [];
            for (var index = 0; index < model.length; index++) {
                ads.push([new Date(model[index].date), model[index].ascDesc]);
                mbgs.push([new Date(model[index].date), model[index].meanBG]);
            }
            plots.push($.plot($("#pha"), [ads],
                    $.extend(true,{}, flotOptions,{yaxes:[{tickFormatter: bGFormatter, tickDecimals:1}]})));
            plots.push($.plot($("#phm"), [{ data: mbgs, label: "Time-weighted average bG" }],
                    $.extend(true,{}, flotOptions,{yaxes:[{tickFormatter: bGFormatter, tickDecimals:1}]})));
        });

        $.getJSON("<%=application.getContextPath()%>/api/home/glucose-overlay" + window.location.search, function (model) {
            console.log("Graphing {0} dates from {1} to {2}".format(model.length, model[0].bGs[0].startTime, model[model.length-1].bGs[0].startTime));
            var overlays = [];
            for (var day = 0; day < model.length; day++) {
                // A new overlay
                var bGs = [];
                var dayBGs = model[day].bGs;
                for (var readingId = 0; readingId < dayBGs.length; readingId++){
                    var readingDate = dayBGs[readingId].startTime;
                    var date = new Date();
                    if (readingId == dayBGs.length-1 && day < model.length-1) {
                        date.setDate(date.getDate()+1);
                    }
                    date.setHours(readingDate.substr(11,2));
                    date.setMinutes(readingDate.substr(14,2));
                    bGs.push([date, dayBGs[readingId].value, readingDate + "@"+readingId]);
                }
                overlays.push({data: bGs, label: day});

            }
            plots.push($.plot($("#pho"), overlays,
                    $.extend(true,{}, flotOptions,{yaxes:[{tickFormatter: bGFormatter, tickDecimals:1}]})));
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
