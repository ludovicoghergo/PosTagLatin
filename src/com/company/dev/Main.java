package com.company.dev;

import com.company.learning.Triple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ArrayList<Triple> list = new ArrayList<>();
        HashMap<String, Integer> countWord = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\Games\\PosTagLatin\\src\\com\\company\\corpus\\la_llct-ud-dev.conllu"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.length()!=0){
                    if (line.charAt(0)!='#'){
                        Triple triple;
                        String[] tagger = line.split("\t");
                        triple = new Triple(tagger[1],tagger[2],tagger[3]);
                        list.add(triple);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Triple tripla: list){

        }


    }
}
