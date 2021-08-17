package com.company.viterbi;

import com.company.learning.Triple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> tagList = new ArrayList<>();
        HashMap<String, Double> probabilita = new HashMap<>();
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
        HashMap<String, Double> probabilitaW = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\outputW.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                probabilitaW.put(linea[0], new Double(Double.parseDouble(linea[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("outputW letto");

        ArrayList<String> frase = new ArrayList<>();
        frase.add("In");        frase.add("Dei");
        frase.add("omnipotentis");        frase.add("nomine");
        frase.add(",");        frase.add("regnante");
        frase.add("domno");        frase.add("nostro");
        frase.add("Karolus");        frase.add("divina");
        frase.add("faventem");        frase.add("clementia");
        frase.add("imperatore");        frase.add("augusto");
        frase.add(",");frase.add("anno");frase.add("imperii");frase.add("eius");frase.add("septimo");frase.add(",");
        frase.add("pridie");frase.add("idus");frase.add("augusti");frase.add("indictione");frase.add("quinta");frase.add(".");


        //inizio viterbi
        int N= frase.size()+2;
        int T=tagList.size();
        Double[][] matrix = new Double[T][N+2];
        Integer[][] pointer = new Integer[T][N+2];

        //step di inizializzazione
        for (int i=0; i<=N; i++){
            Double a0s = new Double(0);
            Double bso1 = new Double(0);
            try{
                bso1 = new Double(probabilitaW.get(frase.get(0)+ " - "+ tagList.get(i) ));
            }catch (Exception e){}
            try{
                a0s = new Double(probabilita.get(tagList.get(i) + " - "+ "inizioFrase"));
            }catch (Exception e){}

            matrix[0][i] = a0s * bso1;
            pointer[0][i] = -1; // abbiamo messo -1 e non 0 perchè 0 è un tag, mentre qua indichiamo inizio frase

        }
        //recursion step
        for (int t=1; t<=T; t++){
            for (int s=0; s<=N; s++){
                Double max = new Double(0);
                Integer maxPointer = new Integer(-1);

                for (int i=0; i<=N; i++){
                    Double a = new Double(0);
                    Double b = new Double(0);
                    Double viterbi = new Double(0);
                    Double ris;
                    try{
                        b = new Double(probabilitaW.get(frase.get(t)+ " - "+ tagList.get(s) ));
                    }catch (Exception e){}
                    try{
                        a = new Double(probabilita.get(tagList.get(s) + " - "+ tagList.get(i)));
                    }catch (Exception e){}
                    viterbi = new Double(matrix[t-1][i]);
                    ris = new Double(viterbi*a*b);
                    if(max<ris){
                        max = ris;
                        maxPointer = i;
                    }
                }
                matrix[t][s]=max;
                pointer[t][s]=maxPointer;
            }
        }

        System.out.println("fatto");

    }



}
