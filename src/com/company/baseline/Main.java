package com.company.baseline;

import com.company.dev.Pair;
import com.company.learning.Triple;
import com.company.viterbi.Viterbi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<String, Pair> probabilitaW = new HashMap<>();
        ArrayList<String> wordList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\outputW.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                if (!wordList.contains(linea[0].split(" - ")[0])) {
                    wordList.add(linea[0].split(" - ")[0]);
                    probabilitaW.put(linea[0].split(" - ")[0], new Pair(linea[0].split(" - ")[1],Double.parseDouble(linea[1])));
                }else {
                    Double max = probabilitaW.get(linea[0].split(" - ")[0]).getCout();
                    if (max<Double.parseDouble(linea[1])) {
                        probabilitaW.remove(linea[0].split(" - ")[0]);
                        probabilitaW.put(linea[0].split(" - ")[0], new Pair(linea[0].split(" - ")[1], Double.parseDouble(linea[1])));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("outputW letto");


        ArrayList<Triple> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\src\\com\\company\\corpus\\la_llct-ud-test.conllu"))) {
            String line;
            Triple triple2 = new Triple("inizioFrase","inizioFrase","inizioFrase");
            list.add(triple2);
            while ((line = br.readLine()) != null) {
                if(line.length()!=0){
                    if (line.charAt(0)!='#' ){
                        String[] tagger = line.split("\t");
                        Triple triple = new Triple(tagger[1],tagger[2],tagger[3]);
                        list.add(triple);
                    }
                }else{
                    Triple triple = new Triple("fineFrase","fineFrase","fineFrase");
                    Triple triple23 = new Triple("inizioFrase","inizioFrase","inizioFrase");
                    list.add(triple);
                    list.add(triple23);
                }
            }
            Triple triple = new Triple("fineFrase","fineFrase","fineFrase");
            list.add(triple);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int nFrase = 1;
        ArrayList<String> frase = new ArrayList<>();
        ArrayList<String> tag = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        int totGiusti=0;
        int totSbaglaiti=0;
        for (Triple tripla: list){
            if( tripla.getTag().equals("inizioFrase")){}
            else if (tripla.getTag().equals("fineFrase")){
                if(frase.size()!=0){
                    System.out.print("frase n= "+ nFrase+" : ");
                    result = baseline(frase, probabilitaW);
                    ArrayList<Integer> ris =check(tag, result, frase);
                    totGiusti += ris.get(0);
                    totSbaglaiti += ris.get(1);
                    frase.clear();
                    tag.clear();
                    nFrase++;
                }

            }else{
                frase.add(tripla.getWord());
                tag.add(tripla.getTag());
            }

        }
        System.out.println("giusti="+totGiusti+" - sbagliati="+totSbaglaiti);

    }

    private static ArrayList<String> baseline(ArrayList<String> frase, HashMap<String, Pair> probabilitaW) {
        ArrayList<String> result = new ArrayList<>();
        for(String s : frase) {
            if(probabilitaW.containsKey(s)) {
                result.add(probabilitaW.get(s).getTag());
            }else {
                result.add("NOUN");
            }
        }

        return result;
    }

    private static ArrayList<Integer> check(ArrayList<String> tag, ArrayList<String> result, ArrayList<String> frase) {
        int corretti = 0;
        int sbagliati = 0;
        ArrayList<Integer> ris= new ArrayList<>();
        for (int i=0; i<tag.size(); i++){
            if(tag.get(i).equals(result.get(i))){
                corretti++;
                //System.out.println("corretto= "+frase.get(i)+ " - "+ tag.get(i) + " - "+ result.get(i));
            }else{
                sbagliati++;
                //System.out.println("sbagliato= "+frase.get(i)+ " - "+ tag.get(i) + " - "+ result.get(i));
            }
        }
        System.out.println("corretti= "+corretti+" - sbagliati= "+sbagliati);
        ris.add(corretti);
        ris.add(sbagliati);
        return ris;

    }

}
