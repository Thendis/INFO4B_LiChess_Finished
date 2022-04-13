package comsObjectStuff;

import java.io.*;
import java.net.*;
import dataAndTraitement.*;

public class ObjectSender {
    private Socket oBridge;
    private ObjectOutputStream writerMan;

    public ObjectSender(Socket sockIn, Link myLink){
        this.oBridge = sockIn;
        try{
            this.writerMan = new ObjectOutputStream(this.oBridge.getOutputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean sendData(ChessData obj){
        try{
            writerMan.writeObject(obj);
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
