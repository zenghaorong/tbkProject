package com.aebiz.baseframework.redis.pubsub;

public interface PubSub {

    void onMessage(String channel, String message);
}
