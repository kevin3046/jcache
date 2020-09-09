package org.rrx.jcache.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/31 18:50
 * @Description:
 */
public class LogFactory {

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
