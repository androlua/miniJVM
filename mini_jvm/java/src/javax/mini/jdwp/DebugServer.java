/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javax.mini.jdwp;

import java.util.Enumeration;
import java.util.Vector;
import javax.cldc.io.Connector;
import javax.mini.net.ServerSocket;
import javax.mini.net.Socket;
import javax.mini.util.Iterator;

/**
 *
 * @author gust
 */
public class DebugServer {

    static int SERVER_PORT = 8000;
    Vector clients = new Vector();
    Listener listener;
    Dispacher dispacher;
    boolean exit = false;
    ServerSocket servSock;

    public void startServer() {
        listener = new Listener();
        listener.start();
        dispacher = new Dispacher();
        dispacher.start();
    }

    class Listener extends Thread {

        public void run() {
            try {
                servSock = (ServerSocket) Connector.open("serversocket://:" + SERVER_PORT, Connector.READ_WRITE);
                servSock.listen();
                System.out.println("jdwp listening in " + SERVER_PORT);
            } catch (Exception e) {
                exit = true;
                System.out.println(e);
            }
            while (!exit) {
                try {
                    Socket sock = servSock.accept();
                    DebugClient dc = new DebugClient(sock);
                    clients.addElement(dc);
                } catch (Exception e) {
                    exit = true;
                    System.out.println(e);
                }
            }
            System.out.println("jdwp server closed.");
        }
    }

    class Dispacher extends Thread {

        public void run() {
            while (!exit) {
                try {
                    for (Enumeration e = clients.elements(); e.hasMoreElements();) {
                        DebugClient dc = (DebugClient) e.nextElement();
                        dc.process();
                        clients.removeElement(dc);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}