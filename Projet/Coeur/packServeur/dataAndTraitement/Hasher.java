package dataAndTraitement;

import java.util.*;
import java.io.*;

public class Hasher extends Thread {
    private File fileForWork;
    private int idCounter;

    public Hasher(String command, String fileName) {
        this.fileForWork = new File("../../../../" + fileName);
        this.idCounter = 0;
    }

    @Override
    public void run() {
        hashPlayers();
        hashGames(5000000);

    }

    // Creer une hashtable de joueurs avec le nombre de leur partie en valeur
    private void hashPlayers() {
        
        boolean secureStop = false; //Au cas ou fileReader aurait un problème
        ChessData players = new ChessData("server", this.idCounter);
        Hashtable<String,ArrayList<Long>> dataForPlayers = new Hashtable<>();
        int counterIn = 0; //Pour loadBarre
        String line = null;
        String nomJoueur = null;
        long nbChar = 0;
        long nbCharToNextPartie = 0;

        this.idCounter=0;
        System.out.println("START BASHING Players : "+new Date());
        System.out.print("[");
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(this.fileForWork));
            while ((line = fileReader.readLine()) != null && !secureStop && counterIn<100000) {
                nbChar+=line.length()+1;
                
                if (getFirstWord(line).equalsIgnoreCase("[White") || getFirstWord(line).equalsIgnoreCase("[Black")) 
                {
                    nomJoueur = getWordBetwen(line);
                    //Ajout jouer et/ou partie
                    if(!dataForPlayers.containsKey(nomJoueur)){
                        dataForPlayers.put(nomJoueur, new ArrayList<>());
                    }
                    dataForPlayers.get(nomJoueur).add(nbCharToNextPartie); // /!\ Recupère une ArrayList a chaque fois plus grosse
                    counterIn++;
                }
                if(getFirstWord(line).equalsIgnoreCase("1.")){
                    //Passe la ligne vide
                    line = fileReader.readLine();
                    if (line!=null)
                        nbChar +=line.length()+1;
                    else 
                        secureStop = true; 
                    //
                    nbCharToNextPartie = nbChar;
                }
                //LoadBarre
                /*if (counterIn >= 10000000) {
                    counterIn = 0;
                    System.out.print("I");
                }*/
            }
            players.setDataPlayer(dataForPlayers);
            System.out.println("] End bashing players at : "+new Date());
            System.out.println("Size Hashtable = "+players.getDataPlayer().size()+" joueurs");
            ObjectOutputStream objectWrtier = new ObjectOutputStream(
                    new FileOutputStream(new File("savedDatas/playersList.txt")));

            objectWrtier.writeObject(players);
            objectWrtier.flush();
            objectWrtier.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("END BASHING Players: "+new Date());
        
    }

    // Va créer une hashtable de classeur 
    private void hashGames(int nbPartieParClasseur) {
        this.idCounter = 0;
        Hashtable<Integer, Long> hashingGame = new Hashtable<>();
        String line = null;
        long nbChar = 0;
        String firstWord = null;
        int partieCounter = 0;

        System.out.println("START BASHING PGN " + nbPartieParClasseur + " : " + new Date());
        System.out.print("[");
        try {

            // Hashing
            BufferedReader fileReader = new BufferedReader(new FileReader(this.fileForWork));
            hashingGame.put(0, (long) 0);
            this.idCounter++;
            while ((line = fileReader.readLine()) != null) {
                firstWord = getFirstWord(line);
                nbChar += line.length() + 1;
                if (firstWord.equalsIgnoreCase("1.")) {
                    partieCounter++;
                    // Passage de la ligne vide
                    line = fileReader.readLine();
                    nbChar += line.length() + 1;
                    //
                }
                if (partieCounter == nbPartieParClasseur) {

                    hashingGame.put(this.idCounter, nbChar);
                    this.idCounter++;
                    partieCounter = 0;
                    System.out.print("I");
                }
            }
            System.out.println("] End Bashing games at " + new Date());
            fileReader.close();

            // Stockage dans un fichier
            ObjectOutputStream objectWrtier = new ObjectOutputStream(
                    new FileOutputStream(new File("savedDatas/hashedTable" + nbPartieParClasseur + ".txt")));
            ChessData tmp = new ChessData("Server", -1);
            tmp.setDataHashedTable(hashingGame);
            objectWrtier.writeObject(tmp);
            objectWrtier.flush();
            objectWrtier.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("END BASHING PGN : " + new Date());

    }

    // renvoie le mot present dans word qui est entouré de ""
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

    // Renvoie le premier mot avant un espace
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

}
