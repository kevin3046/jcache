package org.rrx.jcache.server.election;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.setcd.commons.utils.SetcdClientUtils;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/1 13:20
 * @Description:
 */
public class SchedulerLeader extends AbstractScheduledService {

    private static final Logger log = LogFactory.getLogger(SchedulerElectionCandidate.class);

    public static volatile LeaderStage stage = LeaderStage.INIT;

    public enum LeaderStage {
        INIT, WAIT, LEADER
    }

    @Override
    protected void runOneIteration() throws Exception {
        log.debug("state=" + SchedulerLeader.stage);
        if (stage.equals(LeaderStage.INIT)) {
            stage = LeaderStage.WAIT;
            try {
                SetcdClientUtils.election("/leader/lock", 3L, new SchedulerElectionCandidate());
            } catch (Exception e) {
                log.error("SchedulerLeader.runOneIteration Exception:{}", e);
                stage = LeaderStage.INIT;
                log.info("SchedulerLeader.runOneIteration set state=INIT");
            }
        }
    }

    @Override
    protected AbstractScheduledService.Scheduler scheduler() {
        //3秒一次
        return AbstractScheduledService.Scheduler.newFixedDelaySchedule(3, 3, TimeUnit.SECONDS);
    }

}
