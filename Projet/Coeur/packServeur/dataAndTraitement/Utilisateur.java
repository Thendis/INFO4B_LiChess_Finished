package dataAndTraitement;
import java.net.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import comsObjectStuff.*;
import comsStuff.*;

public class Utilisateur {
    private String nom;
    private Socket orderSocket;
    private Socket objectSocket;
    private Listener userListener; // Utiliser pour écouter
    private Sender userSender; //Utiliser pour parler
    private ObjectListener userObjectListener;
    private ObjectSender userObjectSender;
    private Hoster myHoster;
    //Datas traitment
    private Dealer myDealer;
    private ArrayList<String> allResponsesPaths;
    

    public Utilisateur(String nom, Socket mysocket, Socket socketForObjects,Hoster hoster){
        this.nom = nom;
        this.orderSocket=mysocket;
        this.objectSocket=socketForObjects;
        this.myHoster = hoster;
        this.allResponsesPaths = new ArrayList<String>();
        this.myDealer = null;
        try{
            //Creat Folder
            Path pathToFolder = Paths.get("usersDatas/"+this.nom+"_datas");
            Files.createDirectories(pathToFolder);
            //String Listener and Send
            this.userListener = new Listener(this);
            this.userSender = new Sender(this);
            //Object Listener and Sender
            this.userObjectSender = new ObjectSender(this);
            this.userObjectListener = new ObjectListener(this);
        }catch(IOException e){
            e.printStackTrace();
        }

        this.userListener.start();
        this.userObjectListener.start();
    }

    public void addToResponse(ChessData toAdd){
        String pathToFile = "usersDatas/"+this.nom+"_datas/package_"+this.allResponsesPaths.size()+".txt";
        File fileToAdd = new File(pathToFile);
        removeFromWaitingList(toAdd.getId());
        try{
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileToAdd));
            
            writer.writeObject(toAdd);
            writer.flush();
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        this.allResponsesPaths.add(pathToFile);
    }

    public void addToReturned(ChessData toAdd){
        if(this.myDealer.isAlive()){
            this.myDealer.addToReturned(toAdd);
        }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Socket getOrderSocket() {
        return orderSocket;
    }

    public Listener getUserListener() {
        return userListener;
    }

    public void setUserListener(Listener userListener) {
        this.userListener = userListener;
    }

    public Sender getUserSender() {
        return userSender;
    }

    public void setUserSender(Sender userSender) {
        this.userSender = userSender;
    }

    public Hoster getMyHoster() {
        return myHoster;
    }

    public ObjectListener getUserObjectListener() {
        return userObjectListener;
    }

    public void setUserObjectListener(ObjectListener userObjectListener) {
        this.userObjectListener = userObjectListener;
    }

    public ObjectSender getUserObjectSender() {
        return userObjectSender;
    }

    public Socket getObjectSocket() {
        return objectSocket;
    }

    public void shutMeDown(){
        this.userObjectListener.shutDown();
        this.userListener.shutDown();
        this.userObjectSender.shutDown();
        this.userSender.shutDown();
        try{
            this.objectSocket.close();
            this.orderSocket.close();
            //Delete directory here
        }catch(IOException e){
            e.printStackTrace();
        }
        myHoster.getMeOut(this.nom);

    }

    public void removeFromWaitingList(int id){
        if(myDealer.isAlive()){
            this.myDealer.removeFromWaitingList(id);
        }
    }

    //Créer un objet Dealer et le lance
    public void launchDealer(int order,ArrayList<String> args){
        this.myDealer = new Dealer(getMyHoster(),this,order,args);
        this.myDealer.start();
    }
    
}
