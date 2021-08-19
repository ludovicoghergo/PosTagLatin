package com.company.viterbi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Viterbi {
    ArrayList<String> tagList = new ArrayList<>();
    ArrayList<String> wordList = new ArrayList<>();
    HashMap<String, Double> probabilitaW = new HashMap<>();
    HashMap<String, Double> probabilita = new HashMap<>();

    public Viterbi(){
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\output.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] linea = line.split("\t");
                probabilita.put(linea[0], new Double(Double.parseDouble(linea[1])));
                if(!tagList.contains(linea[0].split(" - ")[0])){
                    tagList.add(linea[0].split(" - ")[0]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("output letto");
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\outputW.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                probabilitaW.put(linea[0], new Double(Double.parseDouble(linea[1])));
                if (!wordList.contains(linea[0].split(" - ")[0])){
                    wordList.add(linea[0].split(" - ")[0]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("outputW letto");
    }

    public ArrayList<String> viterbi (ArrayList<String> frase){

        int nTag= tagList.size();
        int nFrase= frase.size();
        //prima righe poi colonne
        Double[][] matrix = new Double[nTag][nFrase];
        Integer[][] pointer = new Integer[nTag][nFrase];

        //step di inizializzazione
        for (int i=0; i<nTag; i++){
            Double a0s = new Double(0);
            Double bso1 = new Double(0);
            try{
                bso1 = new Double(probabilitaW.get(frase.get(0)+ " - "+ tagList.get(i) ));
            }catch (Exception e){}
            try{
                a0s = new Double(probabilita.get(tagList.get(i) + " - "+ "inizioFrase"));
            }catch (Exception e){}

            matrix[i][0] = a0s * bso1;
            pointer[i][0] = -1; // abbiamo messo -1 e non 0 perchè 0 è un tag, mentre qua indichiamo inizio frase

        }
        //recursion step
        for (int t=1; t<nFrase; t++){
            Boolean trovato = false;
            for (int s=0; s<nTag; s++){
                Double max = new Double(0);
                Integer maxPointer = new Integer(-1);

                for (int i=0; i<nTag; i++){
                    Double a = new Double(0);
                    Double b = new Double(0);
                    Double viterbi = new Double(0);
                    Double ris;
                    try{
                        b = new Double(probabilitaW.get(frase.get(t)+ " - "+ tagList.get(s) ));
                    }catch (Exception e){
                        //la parole è sconosciuta, bisogna fare smoothing
                        if (!wordList.contains(frase.get(t))  && (tagList.get(s).equals("NOUN") || tagList.get(s).equals("VERB"))){
                            b = 0.5;
                        }
                    }
                    try{
                        a = new Double(probabilita.get(tagList.get(s) + " - "+ tagList.get(i)));
                    }catch (Exception e){}
                    viterbi = new Double(matrix[i][t-1]);
                    ris = new Double(viterbi*a*b);
                    if(max<ris){
                        trovato =true;
                        max = ris;
                        maxPointer = i;
                        //System.out.println("ho trovato un max alla colonna="+ t+ " . Il tag è="+ tagList.get(i));
                    }
                }
                matrix[s][t]=max;  // valore
                pointer[s][t]=maxPointer;  // puntatore
            }
            //se la colonna è tutta 0, metto la prob di emissione massima e puntatore a il massimo della colonna precedente.
            if(!trovato){
                Double max=0.0;
                Integer pointerMax =0;
                Double b = 0.0;
                for (int s=0; s<nTag; s++) {
                    try{
                        b = new Double(probabilitaW.get(frase.get(t)+ " - "+ tagList.get(s) ));
                    }catch (Exception e){ }
                    if (b>max){
                        max=b;
                        pointerMax=s;
                    }
                }
                matrix[pointerMax][t]=max;
                Double maxPrec=0.0;
                Integer pointerMaxPrec =0;
                for (int s=0; s<nTag; s++){
                    if(maxPrec<matrix[s][t-1]){
                        maxPrec=matrix[s][t-1];
                        pointerMaxPrec=s;
                    }
                }
                pointer[pointerMax][t]=pointerMaxPrec;
            }

        }

        //termination step
        Double max = new Double(0);
        Integer maxPointer = new Integer(-1);
        for (int i=0; i<nTag; i++){
            Double a = new Double(0);
            Double viterbi = new Double(0);
            Double ris;
            try{
                a = new Double(probabilita.get("fineFrase" + " - "+ tagList.get(i)));
            }catch (Exception e){}
            viterbi = new Double(matrix[i][nFrase-1]);
            ris = new Double(viterbi*a);
            if(max<=ris){
                max = ris;
                maxPointer = i;
            }
        }

        //System.out.println("fatto viterbi");

        //calcolo "return" di viterbi
        ArrayList<String> result = new ArrayList<>();
        result.add(tagList.get(maxPointer));
        int prec = pointer[maxPointer][nFrase-1];
        result.add(tagList.get(prec));
        for(int i=nFrase-2; i>0; i--){
            result.add(tagList.get(pointer[prec][i]));
            prec = pointer[prec][i];
        }
        Collections.reverse(result);
        return result;
    }
}
