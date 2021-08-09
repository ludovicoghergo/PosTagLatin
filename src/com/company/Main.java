package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ArrayList<Triple> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\src\\com\\company\\corpus\\la_llct-ud-train.conllu"))) {
            String line;
            Triple triple2 = new Triple("inizioFrase","inizioFrase","inizioFrase");
            list.add(triple2);
            while ((line = br.readLine()) != null) {
                if(line.length()!=0){
                    if (line.charAt(0)!='#' && line.charAt(0)!='1'){
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
        String precTag = "inizioFrase";
        for(Triple triple: list){
            if(triple.tag.equals("inizioFrase") || triple.tag.equals("fineFrase")){
                precTag = triple.getTag();
            }else{
                if (map.keySet().contains(triple.getTag())){
                    if(map.get(triple.getTag()).keySet().contains(precTag)){
                        int value= map.get(triple.getTag()).get(precTag);
                        HashMap app = map.get(triple.getTag());
                        app.remove(precTag);
                        app.put(precTag, value+1);
                    }else{
                        map.get(triple.getTag()).put(precTag,1);
                    }
                }else{
                    HashMap<String, Integer> app= new HashMap<>();
                    app.put(precTag, 1);
                    map.put(triple.getTag(), app);
                }
                precTag = triple.getTag();
            }
            }
        System.out.println("fine");

    }

}
