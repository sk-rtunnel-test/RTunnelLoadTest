package com.sample.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class LoadTestClient extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(LoadTestClient.class);

    private static final int NUM_PARALLEL_THREADS = 1000;
    private static final int REPORT_EACH_THREAD_AFTER_NUM_MESSAGES = 100;

    private static final String TUNNEL_SERVER_HOST = "vmskecm10630.eng12.ocl";
    private static final int TUNNEL_SERVER_FORWARD_PORT = 7002;

    private static volatile int GRAND_TOTAL_NUMBER_OF_MESSAGES = 0;
    private static volatile int GRAND_TOTAL_NUMBER_OF_SUCCESSFUL_MESSAGES = 0;
    private static volatile int GRAND_TOTAL_NUMBER_OF_UNSUCCESSFUL_MESSAGES = 0;
    private static volatile int TOTAL_THREADS = 0;
    private static volatile boolean fail = false;

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PARALLEL_THREADS; i++) {
            new LoadTestClient().start();
        }
    }

    public void run() {

        RestTemplate restTemplate = new RestTemplate();
        String pingResourceUrl = "http://" + TUNNEL_SERVER_HOST + ":" + TUNNEL_SERVER_FORWARD_PORT + "/ping";

        boolean counted = false;

        while (true) {
            try {
                GRAND_TOTAL_NUMBER_OF_MESSAGES++;

                HttpStatus response = restTemplate.getForEntity(pingResourceUrl, String.class).getStatusCode();

                if (response.equals(HttpStatus.OK)) {
                    GRAND_TOTAL_NUMBER_OF_SUCCESSFUL_MESSAGES++;
                } else {
                    logger.info("There was a Tunnel failure message");
                    logger.info(response.toString());
                    fail = true;
                    GRAND_TOTAL_NUMBER_OF_UNSUCCESSFUL_MESSAGES++;
                }
                if (GRAND_TOTAL_NUMBER_OF_MESSAGES % REPORT_EACH_THREAD_AFTER_NUM_MESSAGES == 0) {
                    logger.info(new Date().toString());
                    logger.info("Total Messages = " + GRAND_TOTAL_NUMBER_OF_MESSAGES);
                    logger.info("Total Tunnel successful messages = " + GRAND_TOTAL_NUMBER_OF_SUCCESSFUL_MESSAGES);
                    logger.info("Total Tunnel Unsuccessful messages = " + GRAND_TOTAL_NUMBER_OF_UNSUCCESSFUL_MESSAGES);
                    logger.info("Total Threads = " + TOTAL_THREADS);
                    logger.info("At least 1 fail = " + fail);
                }
                if (!counted) {
                    counted = true;
                    TOTAL_THREADS++;
                }
            } catch (Exception ex) {
                fail = true;
                GRAND_TOTAL_NUMBER_OF_UNSUCCESSFUL_MESSAGES++;
                logger.error("Error Processing Packet.", ex);
            }
        }
    }
}
