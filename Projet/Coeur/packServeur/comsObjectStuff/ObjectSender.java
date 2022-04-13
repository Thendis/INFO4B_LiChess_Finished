package comsObjectStuff;

import java.io.*;
import dataAndTraitement.*;

public class ObjectSender {
    private ObjectOutputStream writerMan;
    private Utilisateur summoner;

    public ObjectSender(Utilisateur myUser){
        this.summoner = myUser;
        try{
            this.writerMan = new ObjectOutputStream(this.summoner.getObjectSocket().getOutputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean sendData(ChessData obj){
        try{
            writerMan.writeObject(obj);
            writerMan.flush();
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public void shutDown(){
        try{
            this.writerMan.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    
}
