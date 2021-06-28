package com.sample.udpserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    private int port = 60000;

    public static void main(String [] args) {
        new UDPEchoServer().start();
    }

    public UDPEchoServer() {
        try {
            socket = new DatagramSocket(port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        running = true;

        while (running) {

            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + received);

                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                socket.send(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        socket.close();
    }
}
