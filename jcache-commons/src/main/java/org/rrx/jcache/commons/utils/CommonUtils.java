package org.rrx.jcache.commons.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/26 13:34
 * @Description:
 */
public class CommonUtils {

    private static Integer WORKER_ID = 0;

    public static synchronized Integer workerId() throws UnknownHostException {
        if (WORKER_ID != 0) {
            return WORKER_ID;
        }
        Integer workerId = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostName = addr.getHostName();
            workerId = Math.abs(hostName.hashCode());
        } catch (UnknownHostException e) {
            throw e;
        }
        if (workerId == null || workerId == 0) {
            workerId = Math.abs(ThreadLocalRandom.current().nextInt()) % 1024;
        }
        WORKER_ID = workerId;
        return WORKER_ID;
    }

    public static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }

    public static String generateId() {
        String uuid = getUuid();
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(uuid);
        String uuidNumber = m.replaceAll("").trim();
        uuidNumber = uuidNumber.replaceAll(" ", "");
        String timeStr = String.valueOf(System.currentTimeMillis());

        if (uuidNumber.length() > 10) {
            uuidNumber = uuidNumber.substring(uuidNumber.length() - 10);
        }

        String orderId = timeStr + uuidNumber;

        return orderId;
    }
}
