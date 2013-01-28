package endafarrell.orla.service.data.parser;

import endafarrell.orla.service.data.BaseEvent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Set;

// --Commented out by Inspection START (28/01/2013 23:31):
// --Commented out by Inspection START (28/01/2013 23:31):
////public class EndomondoHtmlHandler {
////    // --Commented out by Inspection (28/01/2013 23:31):public static DateFormat DATEFORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
////
////    public ArrayList<BaseEvent> getNewEvents(Set<BaseEvent> oldEvents) {
////        ArrayList<BaseEvent> events = new ArrayList<BaseEvent>(100);
//////        try {
//////            URL url = new URL("http://www.endomondo.com/embed/user/workouts?id=6422023&measure=0&width=680&height=4000");
//////            Document doc = Jsoup.parse(url, 10000);
//////            Elements links = doc.select("tr > td.duration > a[href]");
//////            for (Element link : links) {
//////                URL workoutUrl = new URL(link.attr("abs:href"));
//////                Document workoutDoc = Jsoup.parse(workoutUrl, 10000);
//////                Elements workoutTrs = workoutDoc.select("div.workoutDetails > table > tbody > tr");
//////                Date date = null;
//////                StringBuilder text = new StringBuilder();
//////                Double value = 0d;
//////                for (Element tr : workoutTrs) {
//////                    for (Element child : tr.children()) {
//////                        if ("Sport".equals(child.html())) {
//////                            text.append(child.nextElementSibling().text());
//////                            text.append(" for ");
//////                        } else if ("Start Time".equals(child.html())) {
//////                            date = DATEFORMAT.parse(child.nextElementSibling().text());
//////                        } else if ("Distance".equals(child.html())) {
//////                            String distance = child.nextElementSibling().text();
//////                            value = Double.parseDouble(distance.replace(" km", ""));
//////                            text.append(distance);
//////                        } else if ("Duration".equals(child.html())) {
//////                            text.append(" in ");
//////                            text.append(child.nextElementSibling().text());
//////                        } else if ("Avg Speed".equals(child.html())) {
//////                            text.append(" at ").append(child.nextElementSibling().text());
//////                        }
//////                    }
//////                }
//////                if (date == null) {
//////                    throw new RuntimeException("Null date from: " + workoutTrs.outerHtml());
//////                }
//////                BaseEvent workout = new BaseEvent(date, Source.Endomondo, text.toString(), value, Unit.km);
//////                ;
//////
//////                if (oldEvents.contains(workout)) {
//////                    // OK: we're done! There's nothing new to see here.
//////                    return events;
//////                } else {
//////                    events.add(workout);
//////                }
//////            }
////            return events;
//////        } catch (MalformedURLException e) {
//////            throw new RuntimeException(e);
// --Commented out by Inspection STOP (28/01/2013 23:31)
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        } catch (ParseException e) {
////            throw new RuntimeException(e);
// --Commented out by Inspection STOP (28/01/2013 23:31)
//        }
//    }
//}
