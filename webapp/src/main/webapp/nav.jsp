<%@ page import="endafarrell.orla.service.OrlaDateTimeFormat,org.joda.time.DateTime" %><%
    String today = OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(DateTime.now());
    String w12 = OrlaDateTimeFormat.PRETTY_yyyyMMdd.print(DateTime.now().minusWeeks(12));
%><nav>&laquo;
    <ul>
        <li><a rel="home" href="<%=application.getContextPath()%>/">home</a></li>
        <li><a href="<%=application.getContextPath()%>/calendar/?from=<%=w12%>&to=<%=today%>">calendar</a></li>
        <li><a href="<%=application.getContextPath()%>/graph.jsp?from=<%=w12%>&to=<%=today%>">graphs</a></li>
        <li>&crarr;</li>
        <li><a href="<%=application.getContextPath()%>/smartpix.jsp">add SmartPix</a></li>
        <li><a href="<%=application.getContextPath()%>/api/home/healthgraph">check exercise</a></li>
        <li><a href="<%=application.getContextPath()%>/api/home/twitter">check messages</a></li>
        <li>&crarr;</li>
        <li><a href="<%=application.getContextPath()%>/api/home/events">see events</a></li>
        <li><a href="<%=application.getContextPath()%>/api/home/events/byDay?from=<%=w12%>&to=<%=today%>">see events by day</a></li>
        <li><a href="<%=application.getContextPath()%>/api/home/glucose?from=<%=w12%>&to=<%=today%>">glucose readings</a></li>
        <li><a href="<%=application.getContextPath()%>/api/home/dailyStats?from=<%=w12%>&to=<%=today%>">daily stats</a></li>
        <li><a href="<%=application.getContextPath()%>/api/sys/config">see config</a></li>
    </ul>
    &raquo;</nav>