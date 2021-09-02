package com.company.test;

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
        ArrayList<Triple> list = new ArrayList<>();
        HashMap<String,Integer> errori= new HashMap<>();
        ArrayList<String> allTags = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/com/company/corpus/la_llct-ud-test.conllu"))) {
            String line;
            Triple triple2 = new Triple("inizioFrase","inizioFrase","inizioFrase");
            list.add(triple2);
            while ((line = br.readLine()) != null) {
                if(line.length()!=0){
                    if (line.charAt(0)!='#' ){
                        String[] tagger = line.split("\t");
                        Triple triple = new Triple(tagger[1],tagger[2],tagger[3]);
                        list.add(triple);
                        if(!allTags.contains(tagger[3])){
                            allTags.add(tagger[3]);
                            errori.put(tagger[3], 0);
                        }
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
        Viterbi viterbi = new Viterbi();
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
                    result = viterbi.viterbi(frase);

                    //conteggio errori
                    int corretti = 0;
                    int sbagliati = 0;
                    for (int i=0; i<tag.size(); i++){
                        if(tag.get(i).equals(result.get(i))){
                            corretti++;
                        }else{
                            sbagliati++;
                            int prec = errori.get(tag.get(i));
                            errori.remove(tag.get(i));
                            errori.put(tag.get(i), prec+1);
                        }
                    }
                    System.out.println("corretti= "+corretti+" - sbagliati= "+sbagliati);
                    totGiusti += corretti;
                    totSbaglaiti += sbagliati;
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
        System.out.println(errori);

    }

    /*
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

     */
}
