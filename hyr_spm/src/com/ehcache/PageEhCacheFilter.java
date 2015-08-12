package com.ehcache;

import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

/**
 * 页面缓存
 * @author 洪艳蓉
 *
 */
public class PageEhCacheFilter extends SimplePageCachingFilter{
	
	private final static String FILTER_URL_PATTERNS = "patterns";
	
	private static String[] cacheURLs;
	
	private void init(){
		String patterns = filterConfig.getInitParameter(FILTER_URL_PATTERNS);
		
	}
	
	@Override
	protected void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws AlreadyGzippedException, AlreadyCommittedException,
			FilterNonReentrantException, LockTimeoutException, Exception {
		// TODO Auto-generated method stub
		if(cacheURLs == null)
			init();
		String url = request.getRequestURI();
		boolean flag = false;
        if (cacheURLs != null && cacheURLs.length > 0) {
            for (String cacheURL : cacheURLs) {
                if (url.contains(cacheURL.trim())) {
                    flag = true;
                    break;
                }
            }
        }
        if (flag) {
            String query = request.getQueryString();
            if (query != null) {
                query = "?" + query;
            }
            super.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);

        }     
	}
	
	
    @SuppressWarnings("unchecked")
    private boolean headerContains(final HttpServletRequest request, final String header, final String value){
        logRequestHeaders(request);
        final Enumeration accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            final String headerValue = (String) accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
        boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
        return acceptsEncoding(request, "gzip") || ie6 || ie7;
    }
	
	

}
