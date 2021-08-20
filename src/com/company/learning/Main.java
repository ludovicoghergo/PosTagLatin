package com.company.learning;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws IOException {
        ArrayList<Triple> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/src/com/company/corpus/la_llct-ud-dev.conllu"))) {
            String line;
            Triple triple2 = new Triple("inizioFrase","inizioFrase","inizioFrase");
            list.add(triple2);
            while ((line = br.readLine()) != null) {
                if(line.length()!=0){
                    if (line.charAt(0)!='#'){
                        Triple triple;
                        String[] tagger = line.split("\t");
                        triple = new Triple(tagger[1],tagger[2],tagger[3]);
                        list.add(triple);
                    }
                }else{
                    Triple triple = new Triple("fineFrase","fineFrase","fineFrase");
                    Triple triple23 = new Triple("inizioFrase","inizioFrase","inizioFrase");
                    list.add(triple);
                    list.add(triple23);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        HashMap<String, HashMap<String, Integer>> numeratore = new HashMap<>();
        HashMap <String, Integer> denominatore = new HashMap<>();
        String precTag = "inizioFrase";

        for(Triple triple: list){
            //calcolo denominatore
            if(denominatore.keySet().contains(triple.getTag())){
                Integer appoggio = denominatore.get(triple.getTag());
                denominatore.remove(triple.getTag());
                denominatore.put(triple.getTag(), appoggio+1);
            }else{
                denominatore.put(triple.getTag(), 1);
            }

            //calcolo numeratore
            if(triple.tag.equals("inizioFrase")){
                precTag = triple.getTag();
            }else{
                if (numeratore.keySet().contains(triple.getTag())){
                    if(numeratore.get(triple.getTag()).keySet().contains(precTag)){
                        int value= numeratore.get(triple.getTag()).get(precTag);
                        HashMap app = numeratore.get(triple.getTag());
                        app.remove(precTag);
                        app.put(precTag, value+1);
                    }else{
                        numeratore.get(triple.getTag()).put(precTag,1);
                    }
                }else{
                    HashMap<String, Integer> app= new HashMap<>();
                    app.put(precTag, 1);
                    numeratore.put(triple.getTag(), app);
                }
                precTag = triple.getTag();
            }
        }







        //PROVA LUDO


        // calcolo probabilità pos-->pos
        HashMap<String, HashMap<String, Integer>> suffix_tag_map = new HashMap<>();
        HashMap <String, Integer> suffix_cnt = new HashMap<>();
        String precTag2 = "inizioFrase";

        for(Triple triple: list) {
            //calcolo denominatore
            if (!triple.tag.equals("inizioFrase") && !triple.tag.equals("fineFrase")) {
                var word = triple.getWord();
                int j = 0;
                for (int i = word.length() - 1; i >= 0 && j < 4; i--,j++) {
                    // CALCOLO NUMERO VOLTE CHE SI VERIFICA UN SUFFISSO
                    if (suffix_cnt.keySet().contains(word.substring(i))) {
                        Integer appoggio = suffix_cnt.get(word.substring(i));
                        suffix_cnt.remove(word.substring(i));
                        suffix_cnt.put(word.substring(i), appoggio + 1);
                    } else {
                        suffix_cnt.put(word.substring(i), 1);
                    }

                    //CALCOLO NUMERO RELAZIONI TAG-SUFFISSO
                    /* FORMATO  HASHMAP ->
                     *   <STRING : SUFFISSO> ->  <STRING : TAG> -> <INT : COUNTER>
                     *
                     */
                    if (suffix_tag_map.keySet().contains(word.substring(i))) {
                        if (suffix_tag_map.get(word.substring(i)).keySet().contains(triple.getTag())) {
                            int value = suffix_tag_map.get(word.substring(i)).get(triple.getTag());
                            HashMap app = suffix_tag_map.get(word.substring(i));
                            app.remove(triple.getTag());
                            app.put(triple.getTag(), value + 1);
                        } else {
                            suffix_tag_map.get(word.substring(i)).put(triple.getTag(), 1);
                        }
                    } else {
                        HashMap<String, Integer> app = new HashMap<>();
                        app.put(triple.getTag(), 1);
                        suffix_tag_map.put(word.substring(i), app);
                    }


                }
            }
        }

        FileWriter f0 = new FileWriter("suff_tag.txt");
        String newLine = System.getProperty("line.separator");
        for(String key: suffix_tag_map.keySet())
        {
            for (String key_val: suffix_tag_map.get(key).keySet()){
                f0.write(key + "\t" + key_val+ "\t"+suffix_tag_map.get(key).get(key_val) + newLine);
            }

        }
        f0.close();

        FileWriter f_print = new FileWriter("suff_cnt.txt");
        for(String key: suffix_cnt.keySet())
        {
                f_print.write(key + "\t" +suffix_cnt.get(key) + newLine);
        }
        f0.close();



        //FINE PROVA LUD
        //calcolo probabilità
        HashMap<String, Double> probabilita = new HashMap<>();
        Double prob = new Double(0);
       for (String tag: numeratore.keySet()){
           for (String tag2: denominatore.keySet()){
               try{
                  prob= new Double((double) numeratore.get(tag).get(tag2)/(double) denominatore.get(tag2));
               }catch (Exception e){}
                probabilita.put(tag+" - "+tag2,  prob);
               prob=new Double(0);
           }

       }
        FileWriter f4 = new FileWriter("output.txt");
        String newLine1 = System.getProperty("line.separator");
        for(String key: probabilita.keySet())
        {
            f4.write(key + "\t" +probabilita.get(key)+ newLine1);
        }
        f4.close();
        System.out.println("fine probabilità");

        //calcolo Pos --> Word
        HashMap<String, HashMap<String, Integer>> numeratoreW = new HashMap<>();

        HashMap <String, Integer> denominatoreW = new HashMap<>();
        for(Triple triple: list){
            //calcolo numeratore
            if(triple.tag.equals("inizioFrase") || triple.tag.equals("fineFrase")){
            }else{
                if (numeratoreW.keySet().contains(triple.getWord())){
                    if(numeratoreW.get(triple.getWord()).keySet().contains(triple.getTag())){
                        int value= numeratoreW.get(triple.getWord()).get(triple.getTag());
                        HashMap app = numeratoreW.get(triple.getWord());
                        app.remove(triple.getTag());
                        app.put(triple.getTag(), value+1);
                    }else{
                        numeratoreW.get(triple.getWord()).put(triple.getTag(),1);
                    }
                }else{
                    HashMap<String, Integer> app= new HashMap<>();
                    app.put(triple.getTag(), 1);
                    numeratoreW.put(triple.getWord(), app);
                }
            }
        }

        HashMap<String, Double> probabilitaW = new HashMap<>();
        Double probW = new Double(0);
        for (String word: numeratoreW.keySet()){
            for (String tag: numeratoreW.get(word).keySet()){
                try{
                    probW= new Double((double) numeratoreW.get(word).get(tag)/(double) denominatore.get(tag));
                }catch (Exception e){}

                probabilitaW.put(word+" - "+tag,  probW);

                probW=new Double(0);
            }

        }

        FileWriter f1 = new FileWriter("outputW.txt");
        for(String key: probabilitaW.keySet())
        {
            f1.write(key + "\t" +probabilitaW.get(key)+ newLine);
        }
        f1.close();
        System.out.println("fine probabilitàW");
    }

}
