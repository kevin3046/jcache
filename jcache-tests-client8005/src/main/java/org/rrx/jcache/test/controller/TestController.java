package org.rrx.jcache.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.rrx.jcache.clients.utils.JcacheUtils;
import org.rrx.jcache.tests.annotation.clients.RedisClientsTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/30 14:59
 * @Description:
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    RedisClientsTest redisClientsTest;

    @GetMapping(value = "/test/set")
    public String set() {
        try {

            redisClientsTest.set("test-1", "test_value----1");
            redisClientsTest.set("test-2", "test_value----2");
            redisClientsTest.set("test-3", "test_value----3");
            redisClientsTest.set("test-4", "test_value----4");
            String ret = redisClientsTest.get("test-1");
            System.out.println(ret);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/test/get1")
    public String get1() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("i=" + i + "===>" + redisClientsTest.get("test-1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/test/get2")
    public String get2() {
        try {

            for (int i = 0; i < 20; i++) {
                System.out.println("i=" + i + "===>" + redisClientsTest.get("test-2"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/test/get3")
    public String get3() {
        try {
            for (int i = 0; i < 30; i++) {
                System.out.println("i=" + i + "===>" + redisClientsTest.get("test-3"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/test/get4")
    public String get4() {
        try {
            for (int i = 0; i < 45; i++) {
                System.out.println("i=" + i + "===>" + redisClientsTest.get("test-4"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/test/getAllCache")
    public String getAllCache() {

        JcacheUtils.getAllCache();
        return "success";

    }

}
