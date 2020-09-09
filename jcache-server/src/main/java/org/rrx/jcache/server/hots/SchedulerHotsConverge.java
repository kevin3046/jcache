package org.rrx.jcache.server.hots;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.server.election.SchedulerLeader;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/27 18:56
 * @Description:热度汇聚定时器
 */
public class SchedulerHotsConverge extends AbstractScheduledService {

    private static final Logger log = LogFactory.getLogger(HotsService.class);

    private HotsService hotsService;

    public SchedulerHotsConverge(HotsService hotsService) {
        this.hotsService = hotsService;
    }

    @Override
    protected void runOneIteration() throws Exception {
        log.debug("state=" + SchedulerLeader.stage);
        hotsService.hotsConverge();
    }

    @Override
    protected Scheduler scheduler() {
        //3秒一次
        return Scheduler.newFixedDelaySchedule(3, 3, TimeUnit.SECONDS);
    }
}
