package org.rrx.jcache.server.hots;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.server.election.SchedulerLeader;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/30 13:30
 * @Description:热点探测定时器
 */
public class SchedulerHotsDetect extends AbstractScheduledService {

    private static final Logger log = LogFactory.getLogger(SchedulerHotsDetect.class);

    private HotsService hotsService;

    public SchedulerHotsDetect(HotsService hotsService) {
        this.hotsService = hotsService;
    }

    @Override
    protected void runOneIteration() throws Exception {
        log.debug("state=" + SchedulerLeader.stage);
        if (SchedulerLeader.stage.equals(SchedulerLeader.LeaderStage.LEADER)) {
            hotsService.hotsDetect();
        }
    }

    @Override
    protected AbstractScheduledService.Scheduler scheduler() {
        //5秒一次
        return AbstractScheduledService.Scheduler.newFixedDelaySchedule(5, 5, TimeUnit.SECONDS);
    }
}
