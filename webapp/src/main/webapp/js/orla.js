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

if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) {
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
};

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
};

function drawCalendar(date, container) {
    console.log("Drawing calendar for " +date);
     var htmlContent ="";
     var FebNumberOfDays ="";
     var counter = 1;

     var dateNow = new Date(date);
     var month = dateNow.getMonth();

     var nextMonth = month + 1; //+1; //Used to match up the current month with the correct start date.
     var prevMonth = month - 1;
     var day = dateNow.getDate();
     var year = dateNow.getFullYear();

     if ( new Date( year, 1, 29 ).getMonth() === 1 ){
          FebNumberOfDays = 29;
     }else{
          FebNumberOfDays = 28;
     }

     // names of months and week days.
     var monthNames = ["January","February","March","April","May","June","July","August","September","October","November", "December"];
     //var dayNames = ["Sunday","Monday","Tuesday","Wednesday","Thrusday","Friday", "Saturday"];
     var dayPerMonth = ["31", ""+FebNumberOfDays+"","31","30","31","30","31","31","30","31","30","31"]


     // days in previous month and next one , and day of week.
     var nextDate = new Date(nextMonth +' 1 ,'+year);
     var weekdays= nextDate.getDay();
     var weekdays2 = weekdays;
     var numOfDays = dayPerMonth[month];

     // this leave a white space for days of pervious month.
     while (weekdays > 0){
        htmlContent += "<td class='monthPre'></td>";
        // used in next loop.
         weekdays--;
     }

     // loop to build the calender body.
     while (counter <= numOfDays){
         var ymd = new Date(year, month,counter);
         var calId = "cal" + ymd.getFullYear() + "-";
         if (ymd.getMonth() < 9) calId += "0";
         calId += (ymd.getMonth() + 1) + "-";
         if (ymd.getDate() < 10) calId += "0";
         calId += ymd.getDate();
         // When to start new line.
        if (weekdays2 > 6){
            weekdays2 = 0;
            htmlContent += "</tr><tr>";
        }
        // if counter is current day.
        // highlight current day using the CSS defined in header.
        if (counter == day){
            htmlContent +="<td class='dayNow' id='"+ calId+"'>"+counter+"</td>";
        } else {
            htmlContent +="<td class='monthNow' id='"+calId+"'>"+counter+"</td>";
        }
        weekdays2++;
        counter++;
     }
     // building the calendar html body.
     var calendarBody = "<table class='calendar'> <tr class='monthNow'><th colspan='7'>" + monthNames[month]+" "+ year +"</th></tr>";
     calendarBody +="<tr class='dayNames'>  <td>Sun</td>  <td>Mon</td> <td>Tue</td>"+
     "<td>Wed</td> <td>Thu</td> <td>Fri</td> <td>Sat</td> </tr>";
     calendarBody += "<tr>";
     calendarBody += htmlContent;
     calendarBody += "</tr></table>";
     container.append($(calendarBody));
};

function dailyInfo(meanBG, ascDesc, numReadings) {
    return "<span class='meanBG'>"+meanBG+"</span>"; //<br/>&dagger;: " + ascDesc + ", #: " + numReadings;
};