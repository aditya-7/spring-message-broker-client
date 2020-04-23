package com.ltts.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public interface MessageBrokerClient {

    static <T> T getDao(HashMap<String, Object> map, Class daoClass) {
        final ObjectMapper mapper = new ObjectMapper();
        T dao = (T) mapper.convertValue(map, daoClass);
        return dao;
    }

    void consume(Object message);

    <T> void produce(String topic, T message);

}
