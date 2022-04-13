package dataAndTraitement;

import java.io.*;
import java.util.*;

public class ServerWorker extends Thread {
    private int commande;
    private ChessData dataIn; // Players
    private final int MAX_THREAD = 6;
    private int threads = 0;
    private int myId;
    private Hoster hoster;
    private ArrayList<String> args;
    private ArrayList<String> answerString;
    private String returnAnswer;
    private ArrayList<Integer> answerInt;
    private ServerWorker summoner;

    public ServerWorker(Hoster hoster,ServerWorker summoner,int commande, int id, ArrayList<String> args) {
        this.commande = commande;
        this.dataIn = hoster.getPlayersList();
        this.hoster = hoster;
        this.myId = id;
        this.args = args;
        this.answerString = new ArrayList<>();
        this.answerInt = new ArrayList<>();
        this.returnAnswer="";
        this.summoner = summoner;
    }

    @Override
    public void run() {
        switch(this.commande){
            case 2:
                this.dataIn=fromArrayToStringInt();
            break;
            case 3:
                if(this.args.size()>1){
                    String tmp = "";
                    for(int i =1;i<this.args.size();i++){
                        tmp+=getMostUsedOpeningFor(this.args.get(i)); 
                    }
                    setReturnAnswer(tmp);
                }else{
                    setReturnAnswer(getMostUsedOpening()); 
                }
            break;
            case 4:
            for(int i=1;i<this.args.size();i++){
                setReturnAnswer(getReturnAnswer()+getDetailsFor(args.get(i)));
            }
            break;
            case 5:
            try{
                String nom = this.args.get(1);
                String response = "";
                for(int i =2;i<this.args.size();i++){
                    int partie = Integer.parseInt(this.args.get(i));
                    response = getPartieFrom(nom, this.hoster.getPlayersList().getDataPlayer().get(nom).get(partie));
                    setReturnAnswer(getReturnAnswer()+response);
                }
            }catch(IndexOutOfBoundsException e){
                setReturnAnswer("Partie inexistante");
            }catch(NumberFormatException e){
                setReturnAnswer("Valeur invalide");
            }
            break;
        
        }
    }


    public String getMostUsedOpening(){
        int i =0;
        ArrayList<ServerWorker> workers = new ArrayList<>();
        
        if(this.summoner==null){
            //Master
            while(i<this.hoster.hashedTable.getDataHashedTable().size()){
                if(this.threads<MAX_THREAD){
                    setThreads(getThreads()+1);
                    System.out.println("Starting a Worker");
                    workers.add(new ServerWorker(this.hoster, this, this.commande, i,this.args));
                    workers.get(i).start();
                    i++;
                }
            }

            for(i = 0; i<workers.size();i++){
                try{
                    workers.get(i).join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            ArrayList<String> mostUsedS = new ArrayList<>();
            ArrayList<Integer> mostUsedI = new ArrayList<>();
            System.out.println("Tri les plus utilisé");
            while(mostUsedS.size()<5){ // Recupère les 5 ouvertures
                i=0;
                String opening = "";
                int openingNb=-1;
                int index = -1;
                for(i=0;i<workers.size();i++){
                    if(opening.equals("")){
                        opening = workers.get(i).getAnswerString().get(0);
                        index = i;
                        openingNb = workers.get(i).getAnswerInt().get(0);
                    }else{
                        if(openingNb<workers.get(i).getAnswerInt().get(0)){
                            opening = workers.get(i).getAnswerString().get(0);
                            index = i;
                            openingNb = workers.get(i).getAnswerInt().get(0);
                        }
                    }
                }
                if(opening.equals("") || opening==null) break;
                /*Créer une troisième liste a partir de celle des workers */
                for(i =0;i<workers.size();i++){
                    if(i!=index && workers.get(i).getAnswerString().get(0).equalsIgnoreCase(opening)){
                        openingNb+= workers.get(i).getAnswerInt().get(0);
                        workers.get(i).getAnswerInt().remove(0);
                        workers.get(i).getAnswerString().remove(0);
                    }
                }
                workers.get(index).getAnswerInt().remove(0);
                workers.get(index).getAnswerString().remove(0);
                mostUsedS.add(opening);
                mostUsedI.add(openingNb);
                /*Retir les doublons des listes*/
                String lastOpeningAdd = mostUsedS.get(mostUsedS.size()-1);
                for(i=0;i<workers.size();i++){
                    for(int j =0;j<workers.get(i).getAnswerString().size();j++){
                        if(workers.get(i).getAnswerString().get(j).equalsIgnoreCase(lastOpeningAdd)){
                            workers.get(i).getAnswerString().remove(j);
                            workers.get(i).getAnswerInt().remove(j);
                        }
                    }
                }
                
            }
            String toReturn ="";
            for(i=0;i<mostUsedS.size();i++){
                toReturn+=mostUsedS.get(i)+" "+mostUsedI.get(i)+"\n";
            }
            System.out.println("Final response = "+mostUsedS+mostUsedI);
            
            return toReturn;
        }else{
            //Worker
            System.out.println("Start at : "+this.hoster.getHashedTable().getDataHashedTable().get(this.myId));
            File fileForWork = this.hoster.getDataToUse();
            Hashtable<String,Integer> openingCounter = new Hashtable<>();
            long nbChar=this.hoster.getHashedTable().getDataHashedTable().get(this.myId);
            long charToStop;
            String line = null;
            String firstWord=null;
            int tmpSwitch =0;
            //Planification de l'arrêt
            if(this.hoster.getHashedTable().getDataHashedTable().get(this.myId+1)!=null){
                charToStop = this.hoster.getHashedTable().getDataHashedTable().get(this.myId+1);
            }else{
                charToStop = nbChar*nbChar; //Nombre qui ne pourra jamais être depassé avant la fin du document
            }
            try{
                BufferedReader reader = new BufferedReader(new FileReader(fileForWork));
                reader.skip(this.hoster.getHashedTable().getDataHashedTable().get(this.myId));
                while((line = reader.readLine())!=null && (nbChar+=line.length()+1)<charToStop){
                    firstWord = getWord(line, 1);
                    if(firstWord.equalsIgnoreCase("[Opening")){
                        //Present ou non dans la table
                        if(openingCounter.get(getWordBetwen(line))!=null){
                            tmpSwitch = openingCounter.get(getWordBetwen(line));
                            openingCounter.replace(getWordBetwen(line),tmpSwitch,tmpSwitch+1);
                        }else{
                            openingCounter.put(getWordBetwen(line), 1);
                        }
                        
                    }
                }
                reader.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            
            
            for(i=0;i<5;i++){
                
                this.answerString.add(getBigger(openingCounter));
                this.answerInt.add(openingCounter.get(getAnswerString().get(i)));
                openingCounter.remove(getAnswerString().get(i));
            }
            
            
            this.summoner.setThreads(this.summoner.getThreads()-1); // Indique la fin du traitement au Master
            System.out.println("End a Thread "+this.getName()+" : "+this.answerString+" "+this.answerInt);
            return "worker 0"; //Retourne dans un format qui ne provoque pas d'erreur
        }
    }


    public String getMostUsedOpeningFor(String joueur){
        if(this.hoster.getPlayersList().getDataPlayer().get(joueur)==null) return "Joueur non valide";
        ArrayList<Long> parties = this.hoster.getPlayersList().getDataPlayer().get(joueur);
        int i =0;
        int tmpSwitch=0;
        String line = null;
        boolean run = true;
        Hashtable<String,Integer> sorteOpening = new Hashtable<>();
        ArrayList<ServerWorker> workers = new ArrayList<>();
        if(this.summoner==null){
            //Master
            while(i<parties.size()){
                if(getThreads()<this.MAX_THREAD){
                     workers.add(new ServerWorker(this.hoster, this, this.commande, i, this.args));
                     workers.get(i/200).start();
                    i+=200;
                }
            }
            try{
                for(i=0;i<workers.size();i++){
                    workers.get(i).join();
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            //Traitement des réponses
            String toReturn ="";
            for(i =0;i<5;i++){
                int bigger = 0;
                for(int j=0;j<workers.size();j++){
                    if(!workers.get(j).getAnswerString().get(0).equals("Empty")){
                        if(workers.get(bigger).getAnswerInt().get(0)<workers.get(j).getAnswerInt().get(0)) bigger =i;
                    }
                    
                }
                toReturn+=workers.get(bigger).getAnswerString().get(0) +" - "+workers.get(bigger).getAnswerInt().get(0)+"\n";
                workers.get(bigger).getAnswerString().remove(0);
                workers.get(bigger).getAnswerInt().remove(0);
            }

            return toReturn;
        }else{
            //Workers
            try{
                i=0;
                Long nbChar=parties.get(this.myId);
                File file = this.hoster.getDataToUse();
                BufferedReader fileReader= new BufferedReader(new FileReader(file));
                fileReader.skip(nbChar);
                i++;
                while((line=fileReader.readLine())!=null && run){
                    nbChar+=line.length()+1;
                    if(getWord(line, 1).equals("1.")) {
                        line=fileReader.readLine();
                        nbChar+=line.length()+1;
                        if(i<parties.size()){
                            fileReader.skip(parties.get(i)-nbChar);
                            nbChar = parties.get(i);
                        } else run=false;
                        
                        i++;
                    }
                    if(i>=200){
                        run =false;
                    }

                    if(getWord(line, 1).equalsIgnoreCase("[Opening")){
                        if(sorteOpening.get(getWordBetwen(line))!=null){
                            tmpSwitch = sorteOpening.get(getWordBetwen(line));
                            sorteOpening.replace(getWordBetwen(line), tmpSwitch, tmpSwitch+1);
                        }else{
                            sorteOpening.put(getWordBetwen(line), 1);
                        }
                    }
                }
                fileReader.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            for(i =0;i<5;i++){
                this.answerString.add(getBigger(sorteOpening));
                this.answerInt.add(sorteOpening.get(answerString.get(i)));
                sorteOpening.remove(this.answerString.get(i));
            }
            this.summoner.setThreads(this.summoner.getThreads()-1);
            System.out.println(this.answerString+" "+this.answerInt);
            return "worker 0";
        }
    }
    //Retourne la clé avec la plus grosse valeur
    public String getBigger (Hashtable<String,Integer> table){
        String bigger="Empty";
        for(Map.Entry<String,Integer> datas : table.entrySet()){
            if(bigger.equalsIgnoreCase("Empty")){
                bigger=datas.getKey();
            }else{
                if(table.get(bigger)<datas.getValue()){
                    bigger=datas.getKey();
                }
            }
        }
        return bigger;
    }
    //Transforme dataIn en <String,Integer> pour l'envoie au client
    public ChessData fromArrayToStringInt() {
        ChessData toSend = new ChessData("Server", -1);
        toSend.setDataInt(new Hashtable<>());
        for (Map.Entry<String, ArrayList<Long>> datas : this.dataIn.getDataPlayer().entrySet()) {
            toSend.getDataInt().put(datas.getKey(), datas.getValue().size());
        }

        return toSend;
    }

    // Retourne la ligne commencant par -firstWord de la -partie du joueur
    // -nomJoueur
    /// !\ chercher les informations en groupe pour limiter l'utilisation mémoire
    public String getLine(String nomJoueur, String firstWord, int partie) {
        ArrayList<Long> list = dataIn.getDataPlayer().get(nomJoueur);
        String toReturn = "";
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("../../../../lichess_db_standard_rated_2018-11.pgn"));
            reader.skip(list.get(partie));
            while ((line = reader.readLine()) != null) {
                if (getWord(line, 1).equals(firstWord)) {
                    reader.close();
                    return line;
                } else if (getWord(line, 1).equals("1.")) {
                    reader.close();
                    return null;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
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

    private String getDetailsFor(String nom){
        String toReturn = "Joueur inconnu";
        if(this.hoster.getPlayersList().getDataPlayer().get(nom)!=null){
           
            toReturn = "Joueur "+nom + "  = "+this.hoster.getPlayersList().getDataPlayer().get(nom).size()+" parties\n";
        }
        return toReturn;
    }

    // Lie la prochaine partie en commencant par start jusqu'0 debut de prochaine partie 
    ///!\ methode de test uniquement
    private String getPartieFrom(String nom,Long start) {
        File file = this.hoster.getDataToUse();
        
        String line = null;
        String toReturn = "";
        boolean run = true;

        toReturn+="***"+nom+"***\n";
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            fileReader.skip(start);
            while ((line = fileReader.readLine()) != null && run) {
                if(getWord(line, 1).equalsIgnoreCase("1.")) run = false;
                toReturn+=line+"\n";
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /*GETTERS SETTERS */
    public synchronized int  getThreads() {
        return threads;
    }

    public synchronized void setThreads(int threads) {
        this.threads = threads;
    }

    
    public ArrayList<String> getAnswerString() {
        return answerString;
    }

    public void setAnswerString(ArrayList<String> answerString) {
        this.answerString = answerString;
    }

    public ArrayList<Integer> getAnswerInt() {
        return answerInt;
    }

    public void setAnswerInt(ArrayList<Integer> answerInt) {
        this.answerInt = answerInt;
    }

    public ServerWorker getSummoner() {
        return summoner;
    }

    public void setSummoner(ServerWorker summoner) {
        this.summoner = summoner;
    }

    public ChessData getDataIn() {
        return dataIn;
    }

    public void setDataIn(ChessData dataIn) {
        this.dataIn = dataIn;
    }

    public Hoster getHoster() {
        return hoster;
    }

    public void setHoster(Hoster hoster) {
        this.hoster = hoster;
    }

    public String getReturnAnswer() {
        return returnAnswer;
    }

    public synchronized void setReturnAnswer(String returnAnswer) {
        this.returnAnswer = returnAnswer;
    }

    


}