package endafarrell.orla.api;

import endafarrell.orla.monitoring.OrlaMonitor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = {"*"}, filterName = "(time) monitor", displayName = "(time) monitor")
public class MonitoringFilter implements Filter {
    OrlaMonitor monitor;
    FilterConfig config;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.monitor = OrlaMonitor.getInstance();
        this.config = filterConfig;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        long before = System.nanoTime();
        chain.doFilter(req, res);
        long after = System.nanoTime();
        String path = ((HttpServletRequest) req).getRequestURI();
        this.monitor.recordResponseTime(path, after - before, OrlaMonitor.TimeScale.NANO);
    }

    public void destroy() {
        this.monitor.dumpAllData();
    }
}
