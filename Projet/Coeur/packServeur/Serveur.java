import java.io.*;

import dataAndTraitement.*;

class Serveur {
    public static void main(String args[]) {

        // PGN de test
        String fileName = "lichess_db_standard_rated_2018-11.pgn";
        // lichess_db_standard_rated_2018-11.pgn
        // package0.txt

        Hoster mainHoster = new Hoster(43320, new File("../../../../" + fileName));
        mainHoster.start();

        try{
            mainHoster.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        /*Hasher gameHash = new Hasher("hashGame", fileName);
        gameHash.start();
        try{
            gameHash.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }*/
    }

}