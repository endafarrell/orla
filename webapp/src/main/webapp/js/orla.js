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