package dataAndTraitement;
import java.io.*;
import java.net.*;
import java.util.*;

public class Hoster extends Thread{
    
    final public String[] orders = {
        "help : \n\tAffiche les commandes",
        "getClientsNumber : \n\tRetourne le nombre de clients actuellement connecté",
        "getPlayers : \n\tRetourne tous les joueurs ainsi que leur nombre de parties\n\t\t Peut aussi être utilisé pour afficher les joueurs trié selon leur nombre de partie avec les opération <,>,= et <> suivi d'une ou de deux valeurs",
        "mostUsedOpening : \n\tRetourne les 5 ouvertures les plus utilisé de totue la partie. \n\t\t Si la commande est suivi du nom d'un joueur, elle retourne les 5 ouverture les plus utilisé du joueur",
        "nbPartieFor : \n\tAffiche le nombre de parties d'un joueur", 
        "showGameFor : \n\tAffiche le ou les parties d'un joueurs (Faire suivre la commande du nom du joueur ainsi que du numero des parties"};
    final public int usedPort;
    
    private ServerSocket listener;
    private Socket socketOfServer;
    private Socket socketForObject;
    private Hashtable<String,Utilisateur> users;

    public File dataToUse;
    public ChessData playersList;
    public ChessData hashedTable;

    public Hoster(int port, File dataToUse){
        this.dataToUse = dataToUse;
        this.usedPort = port;
        this.users = new Hashtable<>();
        try{
            this.listener = new ServerSocket(port);
        } catch(IOException e){
            e.printStackTrace();
        }
        this.socketOfServer = null;
    }

    @Override
    public void run(){
        int i=0;
        try{
            //Uploading datas
            System.out.println("Starting Hoster "+ new Date());
            ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(new File("savedDatas/playersList.txt")));
            this.playersList = (ChessData) objectReader.readObject();
            objectReader.close();
            objectReader = new ObjectInputStream(new FileInputStream(new File("savedDatas/hashedTable5000000.txt")));
            this.hashedTable = (ChessData)objectReader.readObject();
            objectReader.close();
            System.out.println("Hoster running "+ new Date());

            //Starting hosting
            while(true){
                this.socketOfServer = listener.accept();
                this.socketForObject = listener.accept();
                System.out.println(""+socketOfServer.getInetAddress()+" is connected");
                Utilisateur u = new Utilisateur("user"+i, this.socketOfServer,this.socketForObject,this);
                users.put("user"+i,u);
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
       
    }

    public synchronized Hashtable<String,Utilisateur> getUsers(){
        return this.users;
    }

    public String[] getOrders() {
        return orders;
    }

    public File getDataToUse() {
        return dataToUse;
    }

    public void setDataToUse(File dataToUse) {
        this.dataToUse = dataToUse;
    }

    public void getMeOut(String nom){
        System.out.println(nom+" just left");
        this.users.remove(nom);
    }

    public ChessData getPlayersList() {
        return playersList;
    }

    public void setPlayersList(ChessData playersList) {
        this.playersList = playersList;
    }

    public ChessData getHashedTable() {
        return hashedTable;
    }

    public void setHashedTable(ChessData hashedTable) {
        this.hashedTable = hashedTable;
    }

     

    
}
