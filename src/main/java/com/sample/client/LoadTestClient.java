package com.sample.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class LoadTestClient extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(LoadTestClient.class);

    private static final int NUM_PARALLEL_THREADS = 1000;
    private static final int REPORT_EACH_THREAD_AFTER_NUM_MESSAGES = 10;

    private static final String RTUNNEL_SERVER_HOST = "localhost";
    private static final int RTUNNEL_SERVER_FORWARD_PORT = 8080;

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PARALLEL_THREADS; i++) {
            new LoadTestClient().start();
        }
    }

    public void run() {

        RestTemplate restTemplate = new RestTemplate();
        String pingResourceUrl = "http://" + RTUNNEL_SERVER_HOST + ":" + RTUNNEL_SERVER_FORWARD_PORT + "/ping";

        int total_messages = 0;
        int total_rtunnel_successful_messages = 0;

        while (true) {
            try {
                total_messages++;

                HttpStatus response = restTemplate.getForEntity(pingResourceUrl, String.class).getStatusCode();

                if (response.equals(HttpStatus.OK)) {
                    total_rtunnel_successful_messages++;
                } else {
                    logger.info("There was an Rtunnel failure message");
                    logger.info(response.toString());
                }
                if (total_messages % REPORT_EACH_THREAD_AFTER_NUM_MESSAGES == 0) {
                    logger.info(new Date().toString());
                    logger.info("Total Messages = " + total_messages);
                    logger.info("Total Rtunnel successful messages = " + total_rtunnel_successful_messages);
                }
            } catch (Exception ex) {
                logger.error("Error Processing Packet.", ex);
            }
        }
    }
}
