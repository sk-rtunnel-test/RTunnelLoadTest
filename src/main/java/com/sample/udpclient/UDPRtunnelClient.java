package com.sample.udpclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class UDPRtunnelClient extends Thread {

    private String TUNNEL_SERVER_HOST = "localhost";
    private int TUNNEL_PORT = 60000;

    private static final int TOTAL_PARALLEL_THREADS = 100000;
    private static final int REPORT_EACH_THREAD_AFTER_NUM_MESSAGES = 10;

    private static volatile long GRAND_TOTAL_MESSAGEs_FROM_ALL_THREADS = 0;
    private static volatile long GRAND_TOTAL_SUCCESSFUL_MESSAGES_FROM_ALL_THREADS = 0;
    private static volatile boolean drop = false;
    private static volatile int TOTAL_THREADS = 0;
    private static volatile int TOTAL_FAILED_THREADS = 0;

    public static void main(String [] args) {
        for (int i = 0; i < TOTAL_PARALLEL_THREADS; i++) {
            new UDPRtunnelClient().start();
        }
    }

    public void run() {
        DatagramSocket socket = null;
        InetAddress address = null;
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(TUNNEL_SERVER_HOST);
        } catch (Exception ex) {
            System.out.println("Error Initialising THread");
            ex.printStackTrace();
            TOTAL_FAILED_THREADS++;
            return;
        }

        TOTAL_THREADS++;
        int total_messages = 0;
        int total_successful_messages = 0;

        while (true) {

            String testmessage =
                            new Date().toString() + " the latest and greatest format !!!" + GRAND_TOTAL_MESSAGEs_FROM_ALL_THREADS;
            total_messages++;
            try {
                String received = sendEcho(testmessage, socket, address);
                if (!testmessage.equals(received)) {
                    System.out.println("Error Connecting");
                    System.out.println("Expected : " + testmessage + " , But found : " + received);
                    drop = true;
                } else {
                    total_successful_messages++;
                }
                if (total_messages % REPORT_EACH_THREAD_AFTER_NUM_MESSAGES == 0) {
                    GRAND_TOTAL_MESSAGEs_FROM_ALL_THREADS += total_messages;
                    GRAND_TOTAL_SUCCESSFUL_MESSAGES_FROM_ALL_THREADS += total_successful_messages;
                    System.out.println("Thread Id: " + this.getId() + " , Time = " + new Date().toString());
                    System.out.println("Total Messages = " + total_messages);
                    System.out.println("Total FRP successful messages = " + total_successful_messages);
                    System.out.println("Grand Total Messages from all threads = " + GRAND_TOTAL_MESSAGEs_FROM_ALL_THREADS);
                    System.out.println("Grand Total Successful Messages from all threads = "
                                    + GRAND_TOTAL_SUCCESSFUL_MESSAGES_FROM_ALL_THREADS);
                    System.out.println("Total Threads = " + TOTAL_THREADS);
                    System.out.println("Total Failed Threads = " + TOTAL_FAILED_THREADS);
                    System.out.println("Atleast 1 connection drop = " + drop);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public String sendEcho(String msg, DatagramSocket socket, InetAddress address) throws IOException {
        byte[] buf = msg.getBytes();
        DatagramPacket packet
                        = new DatagramPacket(buf, buf.length, address, TUNNEL_PORT);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
                        packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close(DatagramSocket socket) {
        socket.close();
    }
}
