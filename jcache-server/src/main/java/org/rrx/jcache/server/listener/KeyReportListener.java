package org.rrx.jcache.server.listener;

import org.rrx.jcache.commons.dto.MessageBean;
import org.rrx.jcache.commons.logging.LogFactory;
import org.rrx.jcache.commons.rocketmq.AbstractConcurrentlyListener;
import org.rrx.jcache.commons.rocketmq.MqMessageBean;
import org.rrx.jcache.server.hots.HotsService;
import org.slf4j.Logger;

/**
 * @Auther: kevin3046@163.com
 * @Date: 2020/8/26 13:52
 * @Description:
 */
public class KeyReportListener extends AbstractConcurrentlyListener {

    private static final Logger log = LogFactory.getLogger(KeyReportListener.class);

    private HotsService hotsService;

    public KeyReportListener(HotsService hotsService) {
        this.hotsService = hotsService;
    }

    @Override
    public void onReceived(MqMessageBean mqMessageBean) throws Exception {
        MessageBean messageBean = (MessageBean) mqMessageBean.getBody();
        hotsService.reportHots(messageBean);
    }
}
