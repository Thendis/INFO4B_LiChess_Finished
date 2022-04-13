package dataAndTraitement;

import java.io.*;
import java.util.*;
import comsStuff.*;

//Gerer le this.data.close en sortie du run
/*Va distribuer tous le pgn et se fermer quand tout distribué*/
public class Dealer extends Thread {
    private Hoster currentHoster;
    private BufferedReader data;
    private int myOrder;
    private Utilisateur summoner;
    private File file;
    private int idCounter;
    private ArrayList<String> args;
    private Hashtable<Integer, ChessData> noResponseYet;
    private Hashtable<Integer, ChessData> returned;

    public Dealer(Hoster hoster, Utilisateur mySummoner, int order,ArrayList<String> args) {

        this.idCounter = 0;
        this.noResponseYet = new Hashtable<Integer, ChessData>();
        this.returned = new Hashtable<Integer, ChessData>();
        this.summoner = mySummoner;
        this.currentHoster = hoster;
        this.myOrder = order;
        this.args = args;
        try {
            this.file = this.currentHoster.getDataToUse();
            data = new BufferedReader(new FileReader(this.file));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        System.out.println("Start dealing for "+this.summoner.getNom()+ " at "+new Date());
        final Hashtable<String, Utilisateur> users = this.currentHoster.getUsers();
        final Sender sender = this.summoner.getUserSender();
        System.out.println("Order " + this.myOrder + " from "+this.summoner.getNom()+" at "+new Date());
        if(this.myOrder>2){
            String order="";
            for(int i =0;i<this.args.size();i++){
                order+= args.get(i)+" ";
            }
            ServerWorker s = new ServerWorker(this.summoner.getMyHoster(), null, this.myOrder, -1,this.args);
                s.start();
                try{
                    s.join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                String tmp = "\n*Reponse pour : "+order+"\n"+s.getReturnAnswer();
                this.summoner.getUserSender().send(tmp);
        } else{
            switch (this.myOrder) {
                case 0:
                    sender.send(afficheControls());
                    break;
                case 1:
                    sender.send("Nombre de clients connecte : " + users.size());
                    break;
                case 2:
                    ChessData listJoueurs = null;
                    
                    ServerWorker s2 = new ServerWorker(this.summoner.getMyHoster(), null, this.myOrder, -1,this.args);
                    s2.start();
                    try{
                        s2.join();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    listJoueurs = s2.getDataIn();
                    this.summoner.getUserObjectSender().sendData(listJoueurs);
                    System.out.println("Send listJoueurs for "+this.summoner.getNom()+" at "+new Date());
                    break;
                default:
                sender.send("Commande non reconnu");
        }
        }

        // Fermeture des Buffers et autres
        try {
            this.data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Renvoie le nb_eme mot de la phrase(Séparateur, espaces)
    public String getWord(String phrase, int nb) {
        String toReturn = "";
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == ' ') {
                nb--;
                if (nb <= 0) {
                    return toReturn;
                } else {
                    toReturn = "";
                }

            } else {
                toReturn += phrase.charAt(i);
            }
        }
        return toReturn;
    }

    //Retourne les prochaines occurence selon la liste de String passé en parametre.
    public Hashtable<String, String> getNextOccurenceOf(Hashtable<String,String> tableIn,String[] listKey,int number) {
        boolean running = true;
        String line = null;
        String firstWord = null;
        String usableKey = null;
        try{
            while ((line = data.readLine()) != null && running) {
                firstWord = getFirstWord(line);
    
                for (int i = 0; i < listKey.length; i++) {
    
                    // Gestion de 1.
                    if (firstWord.equalsIgnoreCase("1.")) {
                        running = false;
                        usableKey = listKey[i];
                    } else {
                        usableKey = "[" + listKey[i];
                    }
                    /* Fin gestion 1. */
                    if (firstWord.equalsIgnoreCase(usableKey)) {
                        tableIn.put(listKey[i].toUpperCase()+number, getWordBetwen(line));
                        i = listKey.length;
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return tableIn;
    }

    public ChessData getPartiesFor(int nbPartie, String[] listKey) {
        ChessData dataToReturn = new ChessData(this.summoner.getNom(), this.idCounter);
        this.idCounter++;
        for(int i =0;i<nbPartie;i++){
           // dataToReturn.setObjectData(getNextOccurenceOf(dataToReturn.getObjectData(), listKey, i));
        }
        return dataToReturn;
    }

    public String afficheControls() {
        String[] tmpOrders = this.summoner.getMyHoster().getOrders();
        String toReturn = "COMMANDS : \n";
        for (int i = 0; i < tmpOrders.length; i++) {
            toReturn += tmpOrders[i] + "\n\n";
        }
        return toReturn;
    }

    // Retourne la prochaine partie si elle existe. Retourne null sinon. num
    // represente le num de la Partie
    public Hashtable<String, String> getNextPartie() { // Incomplet

        boolean running = true;
        String firstWord = null;
        String line = null;
        Hashtable<String, String> partie = new Hashtable<>();

        while (running) {
            try {
                line = data.readLine();
                firstWord = getFirstWord(line);
                if (line == null) {
                    return null;
                }
                if (firstWord.equalsIgnoreCase("1.")) {
                    running = false;
                }

                switch (firstWord) {
                    case "[White":
                        partie.put("whitePlayer", line);
                        break;
                    case "[Black":
                        partie.put("blackPlayer", line);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return partie;
    }

    // Retourne un objet ChessData avec nbParties (Retourne null si plus de partie)
    /*public ChessData getTable(int nbParties) {
        ChessData toReturn = null;
        Hashtable<String, Hashtable<String, String>> partie = new Hashtable<>();
        for (int i = 0; i < nbParties; i++) {
            Hashtable<String, String> donnees = getNextPartie();
            if (donnees != null) {
                partie.put("Partie" + i, donnees);
            } else {
                i = nbParties;
            }
        }
        toReturn = new ChessData(partie, this.summoner.getNom(), this.idCounter);
        this.idCounter++;
        return toReturn;
    }*/

    public void removeFromWaitingList(int id) {
        this.noResponseYet.remove(id);
    }

    public String getRandomUserName(final Hashtable<String, Utilisateur> users) {
        if (users.isEmpty()) {
            return null;
        } else {
            int rand = (int) (Math.random() * users.size());

            return "user" + rand;
        }
    }

    public synchronized ChessData getOldestAndRemove(Hashtable<Integer, ChessData> dataToUse) {
        int len = dataToUse.size();
        ChessData toReturn = dataToUse.get(len - 1);
        dataToUse.remove(len - 1);
        return toReturn;
    }

    public synchronized Hashtable<Integer, ChessData> getReturned() {
        return this.returned;
    }

    public synchronized void addToReturned(ChessData toAdd) {

        getReturned().put(getReturned().size(), toAdd);
    }

    public String getFirstWord(String mot) {
        String toReturn = "";
        for (int i = 0; i < mot.length(); i++) {
            if (mot.charAt(i) == ' ') {
                i = mot.length();
            } else {
                toReturn += mot.charAt(i);
            }

        }
        return toReturn;
    }

    public String getWordBetwen(String word) {
        String toReturn = "";
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == '"') {
                for (int j = i + 1; j < word.length(); j++) {
                    if (word.charAt(j) != '"') {
                        toReturn += word.charAt(j);
                    } else {
                        j = word.length();
                        i = j;
                    }
                }
            }
        }
        return toReturn;
    }
}
