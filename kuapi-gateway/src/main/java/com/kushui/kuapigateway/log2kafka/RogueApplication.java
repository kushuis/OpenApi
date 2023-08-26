package com.kushui.kuapigateway.log2kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RogueApplication {
    private static final Logger LOG = LoggerFactory.getLogger("kafka-event");
    public static void main(String[] args) throws Exception {
        int slowCount = 6;
        int fastCount = 15;
        while (true)        {
            for(int i = 0; i < slowCount; i++){
                LOG.info("This is a warning (亚斯娜).");
                Thread.sleep(5000);
            }
            for(int i = 0; i < fastCount; i++){
                LOG.warn("This is a warning (楪祈).");
                Thread.sleep(1000);
            }
            for(int i = 0; i < slowCount; i++){
                LOG.warn("This is a warning (枯水).");
                Thread.sleep(5000);
            }
        }

    }
}

