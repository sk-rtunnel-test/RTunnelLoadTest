package com.sample.udpclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class UDPRtunnelClient extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    
    //private String TUNNEL_SERVER_HOST = "vmskecm10630.eng12.ocl";
    //private int TUNNEL_PORT = 7002;

    private String TUNNEL_SERVER_HOST = "localhost";
    private int TUNNEL_PORT = 60000;

    private static final int TOTAL_PARALLEL_THREADS = 100000;
    private static final int REPORT_EACH_THREAD_AFTER_NUM_MESSAGES = 10;

    private static long GRAND_TOTAL_MESSAGEs_FROM_ALL_THREADS = 0;
    private static long GRAND_TOTAL_SUCCESSFUL_MESSAGES_FROM_ALL_THREADS = 0;

    private static boolean drop = false;
    private byte[] buf;

    public static void main(String [] args) {
        for (int i = 0; i < TOTAL_PARALLEL_THREADS; i++) {
            new UDPRtunnelClient().start();
        }
    }


    public void run() {
        try {
            UDPRtunnelClient client = new UDPRtunnelClient();

            int total_messages = 0;
            int total_successful_messages = 0;

            while (true) {
                String testmessage = new Date().toString() + " the latest and greatest format !!!";
                total_messages++;
                String received = client.sendEcho(testmessage);
                if (!testmessage.equals(received)) {
                    System.out.println("Error Connecting");
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
                    System.out.println("Grand Total Successful Messages from all threads = " + GRAND_TOTAL_SUCCESSFUL_MESSAGES_FROM_ALL_THREADS);
                    System.out.println("Atleast 1 connection drop = " + drop);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UDPRtunnelClient() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(TUNNEL_SERVER_HOST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                        = new DatagramPacket(buf, buf.length, address, TUNNEL_PORT);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
                        packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }
}
