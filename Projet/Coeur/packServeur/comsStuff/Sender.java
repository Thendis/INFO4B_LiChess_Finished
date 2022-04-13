package comsStuff;

import java.io.*;
import java.net.*;
import dataAndTraitement.*;

public class Sender{
    private Socket sBridge;
    private BufferedWriter writerMan;
    private Utilisateur myUser;

    public Sender(Utilisateur u){
        this.myUser = u;
        this.sBridge = this.myUser.getOrderSocket();
        try{
            this.writerMan = new BufferedWriter(new OutputStreamWriter(this.sBridge.getOutputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public synchronized boolean send(String s){
        try{
            this.writerMan.write(s);
            this.writerMan.newLine();
            this.writerMan.flush();
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
