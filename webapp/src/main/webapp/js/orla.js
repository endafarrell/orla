(function ($) {
    $.QueryString = (function (a) {
        if (a == "") return {};
        var b = {};
        for (var i = 0; i < a.length; ++i) {
            var p = a[i].split('=');
            if (p.length != 2) continue;
            b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
        }
        return b;
    })(window.location.search.substr(1).split('&'));
})(jQuery);

(function ($) {
    function ODate(datestring_, formatstring) {
        var format = formatstring ? formatstring : "YYYY-MM-DD HH:mm:ssS",
            datestring = datestring_,
            odate = this;

        odate.parseDate = function (datestring, format) {
            var a = datestring.split(" ");
            var dt = a[0], tm = a[1];
            var yMd = dt.split("-");
            var y = yMd[0], M = yMd[1], d = yMd[2];
            if (tm === undefined) {
                return new Date(
                    parseInt(y, 10), parseInt(M, 10) - 1, parseInt(d, 10),
                    0, 0, 0, 0);
            } else {
                var hmsSZ = tm.split(":");
                var h = hmsSZ[0], m = hmsSZ[1], sSZ = hmsSZ[2];
                var s = sSZ.substr(0, 2);
                var S = sSZ.substr(3, 3);
                var Z = sSZ.substr(6);
                var Zh = Z.substr(1, 2);
                var Zm = Z.substr(3, 2);
                var tzhours = parseInt(Zh, 10);
                tzhours *= (Z.substr(0, 1) == "+" ? 1 : -1);
                var tzmin = parseInt(Zm, 10);
                tzmin *= (Z.substr(0, 1) == "+" ? 1 : -1);
                var hours = parseInt(h, 10) + tzhours;
                var mins = parseInt(m, 10) + tzmin;
                var sec = parseInt(s, 10);
                var millisec = parseInt(S, 10);
                return new Date(
                    parseInt(y, 10), parseInt(M, 10) - 1, parseInt(d, 10),
                    hours, mins, sec, millisec);
            }
        }
        odate.toDate = function () {
            return this.parseDate(datestring, format);
        }
    }

    $.date = function (datestring, format) {
        return new ODate(datestring, format);
    };
})(jQuery);


/**
 * String formatting can be useful - add it if not there already.
 * Usage: var formattedString = "My string with {0}, or {1} curly braces".format(0, "or more");
 */
if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}
;

var flotOptions = {
    xaxis:{ mode:"time" },
    grid:{ hoverable:true, clickable:true, markings:nightAndWeekendAreas },
    yaxis:{ labelWidth:80},
    points:{ show:false },
    lines:{ show:true },
    legend:{ type:"canvas" }
};

//var heatmapPalette = [
//    [ 4.0, "rgb(223, 42, 44)"],
//    [ 5.0, "rgb(117,174, 55)"],
//    [ 6.0, "rgb(176,178, 60)"],
//    [ 7.0, "rgb( 98,160, 51)"],
//    [ 8.0, "rgb(  5,148, 47)"],
//    [10.0, "rgb(  0,136, 42)"],
//    [12.0, "rgb(150,150,150)"],
//    [14.0, "rgb(115,115,115)"],
//    [16.0, "rgb( 92, 82, 82)"],
//    [99.9, "rgb(  0,  0,  0)"]
//];
var heatmapPalette = [
    [ 4.0, "rgb(255,  0,  0)", "black"],
    [ 5.0, "rgb(  0,255,  0)", "black"],
    [ 6.0, "rgb( 51,255, 51)", "black"],
    [ 6.0, "rgb(153,255,153)", "black"],
    [ 7.0, "rgb(178,255,178)", "black"],
    [ 8.0, "rgb(229,229,255)", "black"],
    [10.0, "rgb(178,178,229)", "black"],
    [12.0, "rgb(153,153,229)", "black"],
    [14.0, "rgb( 51, 51,229)", "white"],
    [16.0, "rgb( 10, 10,198)", "white"],
    [99.9, "rgb(  0,  0,  0)", "white"]
];
function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

function heatmapColor(val) {
    var i = 0;
    var rgb = "rgb(255,  0,  0)";
    while (heatmapPalette[i][0] < val) {
        rgb = heatmapPalette[i][1];
        i++;
    }
    var rx = rgb.substr(4, 11).split(",");
    return rgbToHex(parseInt(rx[0]),parseInt(rx[1]),parseInt(rx[2]));
}

function heatmapText(val) {
    var i = 0;
    var text = "black";
    while (heatmapPalette[i][0] < val) {
        text = heatmapPalette[i][2];
        i++;
    }
    return text;
}
function showTooltip(x, y, contents) {
    $('<div id="tooltip">' + contents + '</div>').css({
        position:'absolute',
        display:'none',
        top:y + 5,
        left:x + 5,
        border:'1px solid #fdd',
        padding:'2px',
        'background-color':'#fee',
        opacity:0.80
    }).appendTo("body").fadeIn(200);
}

// Axis formatters
function carbsFormatter(v, axis) {
    return v.toFixed(axis.tickDecimals) + "g";
}
function bolusFormatter(v, axis) {
    return v.toFixed(axis.tickDecimals) + " IU";
}
function bolusRateFormatter(v, axis) {
    return v.toFixed(axis.tickDecimals) + " IU/hr";
}
function carbRatioFormatter(v, axis) {
    return v.toFixed(axis.tickDecimals) + " U/10g";
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
        markings.push({ xaxis:{ from:i, to:i + 2 * 24 * 60 * 60 * 1000 }, color:"#32cd32" });
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
        markings.push({
            xaxis:{ from:i, to:i + 8 * 60 * 60 * 1000},
            yaxis:{from:0, to:axes.yaxis.max * 0.05},
            color:"#dddddd"});
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
            xaxis:{ from:i, to:i + 8 * 60 * 60 * 1000},
            yaxis:{ from:0, to:axes.yaxis.max * 0.1 },
            color:"#dddddd"
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
            xaxis:{ from:i, to:i + 2 * 24 * 60 * 60 * 1000 },
            yaxis:{ from:0, to:axes.yaxis.max * 0.05 },
            color:"#32cd32"
        });
        i += 7 * 24 * 60 * 60 * 1000;
    } while (i < axes.xaxis.max);
    return markings;
}

/////////////////////////////////
/////////////////////////////////
var dow = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

function monthLength(month, year) {
    var d = new Date();
    var z = d.getTimezoneOffset();
    var dd = new Date(year, month, 0, 0, 0, 0);
    return dd.getDate();
}
function setDailyCell(f, day, col, id) {
    var c = [];
    var t = '<td';
    if (id) t += ' id="' + id + '"';
    if (f == 0 || f == 9) c.push('monthPrePost');
    if (c.length > 0) t += ' class="' + c.join(' ') + '"';
    t += '><span class="calDate">' + day + '<\/span><\/td>\n';
    return t;
}

function setWeeklyByDay(earliest, latest, container, idPrefix) {
    var earliestDate = $.date(earliest, "YYYY-MM-dd").toDate();
    var latestDate = $.date(latest, "YYYY-MM-dd").toDate();
    var earliestMonth = earliestDate.getMonth();
    var earliestYear = earliestDate.getFullYear();
    earliestDate.setDate(1);
    var latestMonth = latestDate.getMonth();
    var latestYear = latestDate.getFullYear();

    var html = '<table class="calendar">\n<thead>\n<tr><th></th>\n';
    for (var i = 0; i < 7; i++) {
        html += '<th class="calDay"';
        html += '>' + dow[i % 7] + '<\/th>\n';
    }
    html += '<\/tr>\n<\/thead>\n<tbody>\n<tr>\n';

    // Builds the last days of the "previous" month - if needed.
    var dm = monthLength(earliestMonth, earliestYear);
    var dowIndex = earliestDate.getDay() % 7;
    for (var i = dowIndex; i > 0; i--) {
        html += setDailyCell(0, dm - i + 1, (dowIndex - i) % 7, null);
    }

    // Build the "earliest" to "latest" months, complicated by being across different years.
    var yyyy = earliestYear;
    var m = earliestMonth;
    while (yyyy < latestYear || yyyy == latestYear && (m%12) <= latestMonth) {
        dm = monthLength(m + 1, yyyy);
        for (var i = 1; i <= dm; i++){
            if ((dowIndex % 7) == 0) {
                html += '<\/tr><tr>\n';
                if (i<8) {
                    html += '<td>'+monthNames[m%12]+' '+ yyyy + '<\/td>';
                } else {
                    html += '<td>&nbsp;<\/td>';
                }
                dowIndex = 0;
            }
            var id = idPrefix + yyyy + ((m%12)<9 ? '-0' : '-') + ((m%12)+1) + (i<10 ? '-0' : '-') + i;
            html += setDailyCell(1, i, dowIndex % 7, id);
            dowIndex++;
        }
        m++;
        if (m%12 == 0) {
            yyyy++;
        }
    }

    // The remaining days of the last week
    var j = 1;
    for (var i = dowIndex; i < 7; i++) {
        html += setDailyCell(9, j, i % 7, null);
        j++;
    }
    html += '<\/tr>\n<\/tbody>\n<\/table>';
    container.append($(html));
}

function dailyInfo(meanBG, ascDesc, numReadings) {
    return "<div class='calBG'>" + parseFloat(meanBG).toFixed(1) + "</div>"; //<br/>&dagger;: " + ascDesc + ", #: " + numReadings;
}

function setDailyByHour(earliest, latest, container, idPrefix) {
    var earliestDate = $.date(earliest, "YYYY-MM-dd").toDate();
    var latestDate = $.date(latest, "YYYY-MM-dd").toDate();
    console.log("setDailyByHour", earliest, earliestDate, latest, latestDate);
    var html = '<p style="page-break-before: always" /><table class="calendar bottom">\n<thead>\n<tr><th></th>\n';
    for (var i = 0; i < 24; i++) {
        html += '<th';
        html += '>' + i + '<\/th>\n';
    }
    html += '<\/tr>\n<\/thead>\n<tbody>\n';

    var currentDate = new Date(earliestDate);
    while (currentDate <= latestDate) {
        var id = idPrefix + currentDate.getFullYear() +
            (currentDate.getMonth()<9 ? '-0' : '-') + (currentDate.getMonth()+1) +
            (currentDate.getDate()<10 ? '-0' : '-') + currentDate.getDate();

        html += '<tr id="'+id+'"><td>'+currentDate.toDateString().replace("2013","").replace(/ /g, "&nbsp;")+'</td>';
        for (var hour = 0; hour < 24; hour++){
            html += '<td>&nbsp;</td>';
        }
        html += '<\/tr>' ;

        // move along to the next day, be careful with daylight savings
        currentDate.setTime(currentDate.getTime() + 86400000);
    }
    html += '<tr><th>&nbsp;</th>';
    for (var i = 0; i < 24; i++) {
        html += '<th';
        html += '>' + i + '<\/th>\n';
    }
    html += '</tr></table>';
    container.append($(html));
};

