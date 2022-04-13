package comsObjectStuff;

import java.io.*;
import java.net.*;
import dataAndTraitement.*;


public class ObjectListener extends Thread{

    private Utilisateur summoner;
    private Socket oBridge;
    private ObjectInputStream readerMan;
    private ChessData lastDataIn;
    private boolean close;

    public ObjectListener(Utilisateur user){
        this.close = false;
        this.summoner=user;
        this.oBridge = this.summoner.getObjectSocket();
        try{
            this.readerMan = new ObjectInputStream(this.oBridge.getInputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
        
        this.lastDataIn=null;
    }

    @Override
    public void run() {
        ChessData dataIn=null;
        try{
            while(!this.close){
                dataIn = (ChessData)this.readerMan.readObject();
                if(dataIn != null){
                    setLastDataIn(dataIn);
                    String destination = this.getLastDataIn().getOwner();
                    if(getLastDataIn().isReturned()){
                        System.out.println("Retuned : "+getLastDataIn().getId());
                        this.summoner.getMyHoster().getUsers().get(destination).addToReturned(getLastDataIn());
                    }else{
                        System.out.println("Response : "+getLastDataIn().getId());
                        this.summoner.getMyHoster().getUsers().get(destination).addToResponse(getLastDataIn());
                    }
                    

                }
            }
        }catch(SocketException e){
            System.out.println("Fermeture du socket");
        }catch(EOFException e){
            System.out.println("Fermeture du flux objet");
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public synchronized ChessData getLastDataIn() {
        return lastDataIn;
    }

    public synchronized void setLastDataIn(ChessData lastDataIn) {
        this.lastDataIn = lastDataIn;
    }

    public void shutDown(){
        try{
            this.readerMan.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        this.close = true;
    }
}
