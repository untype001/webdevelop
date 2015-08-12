package com.ehcache;

import java.io.Serializable;



import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * 对象缓存
 * @author 洪艳蓉
 *
 */
public class MethodCacheInterceptor implements MethodInterceptor, InitializingBean{
	
	private static final Logger log = Logger.getLogger(MethodCacheInterceptor.class);
	
	private Cache cache;
	
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		 log.info(cache + " A cache is required. Use setCache(Cache) to provide one.");
	}

	/**
	 * 缓存中有存在该方法的话，直接返回缓存中的结果，反之则执行该方法
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// TODO Auto-generated method stub
		String targetName = invocation.getThis().getClass().getName();
		String methodName = invocation.getMethod().getName();
		Object[] arguments = invocation.getArguments();
		Object result;
		String cacheKey = this.getCacheKey(targetName, methodName, arguments);
		Element element = null;
		synchronized (this) {
			element = cache.get(cacheKey);
			if(element == null){
				log.info(cacheKey+"加入到缓存："+cache.getName());
				result = invocation.proceed();
				element = new Element(cacheKey,(Serializable)result);
				cache.put(element);
				log.info(cacheKey+"成功保存到缓存："+cache.getName());
			}else{
				log.info(cacheKey+"使用缓存："+cache.getName());
			}
		}
		return element.getValue();
	}
	
    /**
     * <b>function:</b> 返回具体的方法全路径名称 参数
     * @author hoojo
     * @createDate 2012-7-2 下午06:12:39
     * @param targetName 全路径
     * @param methodName 方法名称
     * @param arguments 参数
     * @return 完整方法名称
     */
    private String getCacheKey(String targetName, String methodName, Object[] arguments) {
        StringBuffer sb = new StringBuffer();
        sb.append(targetName).append(".").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append(".").append(arguments[i]);
            }
        }
        return sb.toString();
    }

}
