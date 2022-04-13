package comsObjectStuff;

import java.io.*;
import java.net.*;
import java.util.*;

import dataAndTraitement.*;

public class ObjectListener extends Thread {
    private Socket oBridge;
    private ObjectInputStream readerMan;
    private ChessData lastDataIn;
    private Link myLInk;
    private boolean close;
    private int actualWorkerCount = 0;
    private ChessData players;

    public ObjectListener(Socket sockIn, Link myLInk) {
        this.myLInk = myLInk;
        this.close = false;
        this.oBridge = sockIn;
        try {
            this.readerMan = new ObjectInputStream(this.oBridge.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.lastDataIn = null;
        this.players = new ChessData("me", -1);
    }

    @Override
    public void run() {
        ChessData dataIn = null;
        try {
            while (!this.close) {
                if((dataIn = (ChessData) this.readerMan.readObject()) != null){
                    System.out.println("Receiving data from server at "+new Date());
                    setLastDataIn(dataIn);
                    this.setPlayers(getLastDataIn());
                    close = true;
                }
                
            }
        } catch (SocketException e) {
            System.out.println("ObjectListener closed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized ChessData getLastDataIn() {
        return lastDataIn;
    }

    public synchronized void setLastDataIn(ChessData lastDataIn) {
        this.lastDataIn = lastDataIn;
    }

    public void shutDown() {
        this.close = true;
        try {
            this.readerMan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Link getMyLInk() {
        return myLInk;
    }

    

    public ChessData getPlayers() {
        return players;
    }

    public void setPlayers(ChessData players) {
        this.players = players;
    }

    // Gestion maxWorkers
    public synchronized int getActualWorkerCount() {
        return this.actualWorkerCount;
    }

    public synchronized void incWorker() {
        this.actualWorkerCount++;
    }

    public synchronized void decWorker() {
        this.actualWorkerCount--;
    }

}
