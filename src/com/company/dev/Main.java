package com.company.dev;

import com.company.learning.Triple;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<Triple> list = new ArrayList<>();
        HashMap<String, Pair> countWord = new HashMap<>();
        ArrayList<String> tags = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\src\\com\\company\\corpus\\la_llct-ud-dev.conllu"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.length()!=0){
                    if (line.charAt(0)!='#'){
                        Triple triple;
                        String[] tagger = line.split("\t");
                        triple = new Triple(tagger[1],tagger[2],tagger[3]);
                        list.add(triple);
                        if(!tags.contains(tagger[3])){
                            tags.add(tagger[3]);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Triple tripla: list){
            if (countWord.keySet().contains(tripla.getWord())){
                Pair value = countWord.get(tripla.getWord());
                countWord.remove(tripla.getWord());
                countWord.put(tripla.getWord(), new Pair(tripla.getTag(), (double)value.getCout()+1));
            }else{
                countWord.put(tripla.getWord(), new Pair(tripla.getTag(), 1));
            }
        }

        int coutSingleWord = 0;
        HashMap<String, String> singleWord = new HashMap<>();
        for (String key : countWord.keySet()){
            if(countWord.get(key).getCout()==1){
                singleWord.put(key, countWord.get(key).getTag());
                coutSingleWord++;
            }
        }

        ArrayList<Pair> coutTag = new ArrayList<>();
        for (String tag: tags){
            coutTag.add(new Pair(tag, 0));
        }
        for (String key: singleWord.keySet()){
            for (Pair tag : coutTag){
                if (tag.getTag().equals(singleWord.get(key))){
                    tag.setCout(tag.getCout()+1);
                }
            }
        }

        FileWriter f1 = new FileWriter("devSmoothing.txt");
        String newLine = System.getProperty("line.separator");
        for (Pair p : coutTag){
            p.setCout(p.getCout()/coutSingleWord);
            f1.write(p.getTag() + "\t" +p.getCout()+ newLine);
        }
        System.out.println("fatto");
        f1.close();



    }
}
