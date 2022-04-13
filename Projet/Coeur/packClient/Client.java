

import dataAndTraitement.*;

class Client {
    public static void main(String args[]) {
        final String serverHost = "localhost"; //176.190.198.106
        Link linkToServer = new Link(serverHost,43320);
        try{
            linkToServer.getListener().join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        
    }

    public static String getWord(String phrase, int nb) {
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