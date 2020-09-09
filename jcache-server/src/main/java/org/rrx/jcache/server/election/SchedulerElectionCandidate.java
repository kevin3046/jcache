package org.rrx.jcache.server.election;

import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.setcd.commons.election.ElectionCandidate;
import org.slf4j.Logger;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/9/1 11:44
 * @Description:
 */
public class SchedulerElectionCandidate implements ElectionCandidate {

    private static final Logger log = LogFactory.getLogger(SchedulerElectionCandidate.class);

    @Override
    public void startLeadership() throws Exception {
        log.debug("SchedulerElectionCandidate.startLeadership====>开始leader状态");
        SchedulerLeader.stage = SchedulerLeader.LeaderStage.LEADER;
        log.debug("SchedulerElectionCandidate.startLeadership====>设置state=LEADER");

    }

    @Override
    public void onError(Throwable throwable) {
        log.error("SchedulerElectionCandidate.onError:{}", throwable);
        SchedulerLeader.stage = SchedulerLeader.LeaderStage.INIT;
        log.debug("SchedulerElectionCandidate.onError====>设置state=INIT");
    }

    @Override
    public void onCompleted() {
        log.error("SchedulerElectionCandidate.onCompleted");
        SchedulerLeader.stage = SchedulerLeader.LeaderStage.INIT;
        log.debug("SchedulerElectionCandidate.onCompleted====>设置state=INIT");
    }
}
