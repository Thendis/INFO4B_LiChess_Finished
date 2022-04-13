package dataAndTraitement;

import java.io.*;
import java.net.*;
import comsStuff.*;
import comsObjectStuff.*;

public class Link {
    private String target;
    public Socket clientSocketStream;// public
    public Socket clientSocketObjectStream;// public
    private Listener listenerMan;
    private Sender senderMan;
    private ObjectListener objectListenerMan;
    private ObjectSender objectSenderMan;

    public Link(String host, int port) {
        this.target = host;
        try {
            this.clientSocketStream = new Socket(this.target, port);
            this.clientSocketObjectStream = new Socket(this.target, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reader-Listener String
        this.listenerMan = new Listener(clientSocketStream);
        this.senderMan = new Sender(clientSocketStream, this);
        this.listenerMan.start();
        this.senderMan.start();
        // Reader-Listener Objects
        this.objectSenderMan = new ObjectSender(clientSocketObjectStream, this);
        this.objectListenerMan = new ObjectListener(clientSocketObjectStream, this);
        this.objectListenerMan.start();

    }

    public Listener getListener() {
        return this.listenerMan;
    }

    public void shutDownAll() {
        this.objectListenerMan.shutDown();
        this.listenerMan.shutDown();

        this.senderMan.shutDown();
        this.objectSenderMan.shutDown();
        try {
            this.clientSocketObjectStream.close();
            this.clientSocketStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected");
    }

    public ObjectListener getObjectListenerMan() {
        return objectListenerMan;
    }

    public ObjectSender getObjectSenderMan() {
        return objectSenderMan;
    }

}
