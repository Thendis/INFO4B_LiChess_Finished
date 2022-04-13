package comsStuff;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import dataAndTraitement.*;

public class Listener extends Thread {
    private BufferedReader readerMan;
    private String lastInput;
    private String lastOrder;
    private Socket sBridge;
    private Utilisateur myUser;
    private boolean close;

    public Listener(Utilisateur u) {
        this.close = false;
        this.sBridge = u.getOrderSocket();
        try {
            this.readerMan = new BufferedReader(new InputStreamReader(this.sBridge.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.lastOrder = "";
        this.lastInput = "";
        this.myUser = u;
    }

    @Override
    public void run() {
        String s = null;
        ArrayList<String> sList = null;
        try {
            while (!this.close && (s = readerMan.readLine()) != null) {

                if (isAnOrder(s) >= 0) {
                    System.out.println("ORDER");
                    setLastOrder(s);
                    sList = getArguments(s);
                    this.myUser.launchDealer(isAnOrder(s),sList);
                } else {
                    setLastInput(s);
                    System.out.println(this.myUser.getNom()+" : "+s);
                    if (s.equalsIgnoreCase("END")) {
                        this.myUser.shutMeDown();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getLastOrder() {
        return this.lastOrder;
    }

    public synchronized void setLastOrder(String s) {
        this.lastOrder = s;
    }

    public String getLastInput() {
        return lastInput;
    }

    public void setLastInput(String lastInput) {
        this.lastInput = lastInput;
    }

    //Retourne tous les seconds arguments d'un ordre
    private ArrayList<String> getArguments(String s){
        ArrayList<String> mot = new ArrayList<>();
        for(int i =0;i<s.length();i++){
            if(!mot.contains(getWord(s, i+1))){
                mot.add(getWord(s, i+1));
            }
        }
        return mot;
        
    }   

    // Retourne l'indice de l'ordre s'il existe. Sinon retourne -1
    private int isAnOrder(String s) {
        String[] orders = this.myUser.getMyHoster().getOrders();
        for(int i =0;i<orders.length;i++){
            if(getWord(s, 1).equalsIgnoreCase(getWord(orders[i], 1))) return i;
        }

        return -1;
    }

    public void shutDown() {
        try {
            this.readerMan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.close = true;
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
}
