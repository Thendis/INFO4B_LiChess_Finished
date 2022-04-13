package comsStuff;

import java.io.*;
import java.util.*;
import java.net.*;
import dataAndTraitement.*;

public class Sender extends Thread {
    final public String[] orders = {
        "help",
        "getClientsNumber",
        "getPlayers",
        "mostUsedOpening",
        "nbPartieFor", 
        "showGameFor"};
    private Socket sBridge;
    private Scanner inputLook;
    private BufferedWriter writerMan;
    private boolean close = false;
    private Link myLink;

    public Sender(Socket sockIn, Link toShutDown) {
        this.inputLook = new Scanner(System.in);
        this.sBridge = sockIn;
        this.myLink = toShutDown;
        try {
            this.writerMan = new BufferedWriter(new OutputStreamWriter(this.sBridge.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String s;
        boolean getIn = true;
        ;
        try {
            while (!this.close && (s = this.inputLook.nextLine()) != null) {
                if (isAnOrder(s) == 2) {
                    System.out.println(2);
                    if (this.myLink.getObjectListenerMan().getPlayers().getDataInt() != null) {
                        String signe = "<>";
                        int valeur1 = 1;
                        int valeur2 = 10;
                        if (!getFirstWord(s).equalsIgnoreCase(getWord(s, 2))) {
                            try{
                                signe = getWord(s, 2);
                                valeur1 = Integer.parseInt(getWord(s, 3));
                                valeur2 = Integer.parseInt(getWord(s, 4));
                            }catch(NumberFormatException e){
                                System.out.println("Valeur(s) invalide(s)");
                                getIn = false;
                            }
                        }
                        if(signe.length()>2 || (!signe.equals("<") && !signe.equals("<=") && !signe.equals(">") && !signe.equals(">=") && !signe.equals("==") && !signe.equals("<>"))){
                            System.out.println("Signe invalide");
                            getIn = false;
                        } 
                            
                            

                        if(getIn ){
                            if (valeur2 >= valeur1)
                            affichePlayer(triPlayers(signe, valeur1, valeur2));
                            else
                            System.out.println("Valeur des marges invalides");
                        }
                        getIn = false;
                    }
                }

                if (s != null && !s.equals("") && getIn) {
                    this.writerMan.write(s);
                    this.writerMan.newLine();
                    this.writerMan.flush();
                    if (s.equals("END")) {
                        myLink.shutDownAll();
                    }
                }
                getIn = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void affichePlayer(Hashtable<Character, String> table) {
        for (char i = 'A'; i <= 'Z'; i++) {
            System.out.println("\t\tLETTER = " + i);
            System.out.println(table.get(i));
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

    public Hashtable<Character, String> triPlayers(String signe, int nb, int nb2) { // tri les joueurs par ordre
                                                                                    // alphabetique et les affiches
        Hashtable<Character, String> nomParOrdre = new Hashtable<>();
        char firstChar;
        String tmpStorage;
        switch (signe) {
            case "<":
                for (Map.Entry<String, Integer> player : this.myLink.getObjectListenerMan().getPlayers().getDataInt()
                        .entrySet()) {
                    if (player.getValue() < nb) {
                        firstChar = player.getKey().charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        if (nomParOrdre.containsKey(firstChar)) {
                            tmpStorage = nomParOrdre.get(firstChar);
                            nomParOrdre.replace(firstChar, tmpStorage, tmpStorage + " * " + player.getKey());
                        } else {
                            nomParOrdre.put(firstChar, player.getKey());
                        }
                    }
                }
                break;
            case "<=":
                for (Map.Entry<String, Integer> player : this.myLink.getObjectListenerMan().getPlayers().getDataInt()
                        .entrySet()) {
                    if (player.getValue() <= nb) {
                        firstChar = player.getKey().charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        if (nomParOrdre.containsKey(firstChar)) {
                            tmpStorage = nomParOrdre.get(firstChar);
                            nomParOrdre.replace(firstChar, tmpStorage, tmpStorage + " * " + player.getKey());
                        } else {
                            nomParOrdre.put(firstChar, player.getKey());
                        }
                    }
                }
                break;
            case ">":
                for (Map.Entry<String, Integer> player : this.myLink.getObjectListenerMan().getPlayers().getDataInt()
                        .entrySet()) {
                    if (player.getValue() > nb) {
                        firstChar = player.getKey().charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        if (nomParOrdre.containsKey(firstChar)) {
                            tmpStorage = nomParOrdre.get(firstChar);
                            nomParOrdre.replace(firstChar, tmpStorage, tmpStorage + " * " + player.getKey());
                        } else {
                            nomParOrdre.put(firstChar, player.getKey());
                        }
                    }
                }
                break;
            case ">=":
                for (Map.Entry<String, Integer> player : this.myLink.getObjectListenerMan().getPlayers().getDataInt()
                        .entrySet()) {
                    if (player.getValue() >= nb) {
                        firstChar = player.getKey().charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        if (nomParOrdre.containsKey(firstChar)) {
                            tmpStorage = nomParOrdre.get(firstChar);
                            nomParOrdre.replace(firstChar, tmpStorage, tmpStorage + " * " + player.getKey());
                        } else {
                            nomParOrdre.put(firstChar, player.getKey());
                        }
                    }
                }
                break;
            case "=":
                for (Map.Entry<String, Integer> player : this.myLink.getObjectListenerMan().getPlayers().getDataInt()
                        .entrySet()) {
                    if (player.getValue() == nb) {
                        firstChar = player.getKey().charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        if (nomParOrdre.containsKey(firstChar)) {
                            tmpStorage = nomParOrdre.get(firstChar);
                            nomParOrdre.replace(firstChar, tmpStorage, tmpStorage + " * " + player.getKey());
                        } else {
                            nomParOrdre.put(firstChar, player.getKey());
                        }
                    }
                }
                break;
            case "<>":
                for (Map.Entry<String, Integer> player : this.myLink.getObjectListenerMan().getPlayers().getDataInt()
                        .entrySet()) {
                    if (player.getValue() >= nb && player.getValue() <= nb2) {
                        firstChar = player.getKey().charAt(0);
                        firstChar = Character.toUpperCase(firstChar);
                        if (nomParOrdre.containsKey(firstChar)) {
                            tmpStorage = nomParOrdre.get(firstChar);
                            nomParOrdre.replace(firstChar, tmpStorage, tmpStorage + " * " + player.getKey());
                        } else {
                            nomParOrdre.put(firstChar, player.getKey());
                        }
                    }
                }
                break;
        }
        return nomParOrdre;
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

    public void shutDown() {
        try {
            this.writerMan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.close = true;
    }

    // Retourne l'indice de l'ordre s'il existe. Sinon retourne -1
    private int isAnOrder(String s) {
        String[] orders = this.orders;
        s = s.toUpperCase();
        for (int i = 0; i < orders.length; i++) {
            int counter = 0;
            int orderWordSize = orders[i].length();
            for (int j = 0; j < orderWordSize; j++) {
                String s2 = orders[i].toUpperCase();
                if ((s.length() >= s2.length()) && (s.charAt(j) == s2.charAt(j))) {
                    counter++;
                } else {
                    j = orders[i].length();
                }
            }
            if (counter == orderWordSize) {
                return i;
            }
        }

        return -1;
    }

    public String afficheControls() {
        String[] tmpOrders = this.orders;
        String toReturn = "COMMANDS : \n";
        for (int i = 0; i < tmpOrders.length; i++) {
            toReturn += tmpOrders[i] + "\n";
        }
        return toReturn;
    }
}
