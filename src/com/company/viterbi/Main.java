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
        Double[][] matrix = new Double[N+2][T];
        Integer[][] pointer = new Integer[N+2][T];

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

            matrix[i][0] = a0s * bso1;
            pointer[i][0] = -1; // abbiamo messo -1 e non 0 perchè 0 è un tag, mentre qua indichiamo inizio frase

        }
        System.out.println("fatto");

    }



}
