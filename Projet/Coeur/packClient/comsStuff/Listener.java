package comsStuff;

import java.io.*;
import java.net.*;
/*
    Class qui va écouter en permanence sur le port de sortie. La dernière input recu différente de null sera enregistré dans lastRead
*/
public class Listener extends Thread {
    private Socket sBridge;
    private BufferedReader readerMan;
    private String lastRead; 
    private boolean close;

    public Listener(Socket sockIn){
        this.close = false;
        this.sBridge = sockIn;
        try{
            this.readerMan = new BufferedReader(new InputStreamReader(this.sBridge.getInputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        this.lastRead = "";
    }

    @Override
    public void run(){
        String s = null;
        try{
            while(!this.close){
                s = readerMan.readLine();
                if(s!=null){
                    setLastRead(s);
                    System.out.println(s);
                }
            }
        }catch(IOException e){
            System.out.println("Listener closed");
        }
    }

    public synchronized String getLastRead(){
        return this.lastRead;
    }

    private synchronized void setLastRead(String s){
        this.lastRead = s;
    }

    public void shutDown(){
        this.close = true;
        try{
            this.readerMan.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
}
