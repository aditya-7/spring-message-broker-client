package com.ltts.utility;

import com.ltts.listener.ServiceMessageEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EventListenerTwin implements ApplicationListener<ServiceMessageEvent> {

    public static HashMap<String, Object> message;
    public static String topic;

    @Override
    public void onApplicationEvent(ServiceMessageEvent event) {
        message = event.getMessage();
        topic = event.getTopic();
    }

}
