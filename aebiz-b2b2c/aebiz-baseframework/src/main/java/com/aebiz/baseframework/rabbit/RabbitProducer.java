package com.aebiz.baseframework.rabbit;

import com.aebiz.commons.utils.SpringUtil;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息生产者
 * Created by wizzer on 2016/12/29.
 */
@Service
public class RabbitProducer {
    private Log log = Logs.get();

    private RabbitTemplate rabbitTemplate;

   /* @PostConstruct
    public void init() {
        if (SpringUtil.isRabbitEnabled()) {
            this.rabbitTemplate = SpringUtil.getBean("rabbitTemplate", RabbitTemplate.class);
        }
    }*/

    /**
     * 发送信息
     *
     * @param msg
     */
    public void sendMessage(RabbitMessage msg) {
        try {
            if (SpringUtil.isRabbitEnabled()) {
                this.rabbitTemplate = SpringUtil.getBean("rabbitTemplate", RabbitTemplate.class);
            }
            log.debug("rabbit host::" + rabbitTemplate.getConnectionFactory().getHost());
            log.debug("rabbit port::" + rabbitTemplate.getConnectionFactory().getPort());
            //发送信息
            rabbitTemplate.convertAndSend(msg.getExchange(), msg.getRouteKey(), msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("RabbitProducer error::" + e.getMessage());
        }
    }
}
