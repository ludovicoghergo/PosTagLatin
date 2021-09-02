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
    HashMap<String, Double> suff_cnt = new HashMap<>();
    HashMap<String, Double> suff_tag = new HashMap<>();
    HashMap<String, Double> smoothing = new HashMap<>();
    HashMap<String, Double> tag_freq = new HashMap<>();


    public double interpolateProb(String word,String tag, int cnt, double val){
        if(cnt < 1 ||word.length()-1-4+cnt < 0 ) {
            return val;
        }

        String cut_word = word.substring(word.length()-1-4+cnt);
        double nom = 0;
        double denom =1;
        try {
            nom = suff_tag.get(cut_word + " " + tag);
        }catch (Exception e){}
        try{
            denom = tag_freq.get(tag);
        }catch (Exception e){}
        double prob_mle = Math.max(val,nom/denom);
        return interpolateProb(word,tag,cnt-1, prob_mle);
    }

    public Viterbi() {
        //TEST SUFF

        //tag_count

        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/tag_freq.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                tag_freq.put(linea[0], new Double(Double.parseDouble(linea[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //suff_cnt
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/suff_cnt.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                suff_cnt.put(linea[0], new Double(Double.parseDouble(linea[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //suff_tag.txt

        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/suff_tag.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                suff_tag.put(linea[0]+" "+linea[1], new Double(Double.parseDouble(linea[2])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // FINE TEST SUFF
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/output.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                probabilita.put(linea[0], new Double(Double.parseDouble(linea[1])));
                if (!tagList.contains(linea[0].split(" - ")[0])) {
                    tagList.add(linea[0].split(" - ")[0]);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("output letto");
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/outputW.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                probabilitaW.put(linea[0], new Double(Double.parseDouble(linea[1])));
                if (!wordList.contains(linea[0].split(" - ")[0])) {
                    wordList.add(linea[0].split(" - ")[0]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("outputW letto");

        try (BufferedReader br = new BufferedReader(new FileReader("/home/ludov/Documents/PosTagLatin/devSmoothing.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split("\t");
                smoothing.put(linea[0], Double.parseDouble(linea[1]));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("smoothing  letto");
    }

    public ArrayList<String> viterbi(ArrayList<String> frase) {

        int nTag = tagList.size();
        int nFrase = frase.size();
        //prima righe poi colonne
        Double[][] matrix = new Double[nTag][nFrase];
        Integer[][] pointer = new Integer[nTag][nFrase];

        //step di inizializzazione
        for (int i = 0; i < nTag; i++) {
            Double a0s = new Double(0);
            Double bso1 = new Double(0);
            try {
                bso1 = new Double(probabilitaW.get(frase.get(0) + " - " + tagList.get(i)));
            } catch (Exception e) {
            }
            try {
                a0s = new Double(probabilita.get(tagList.get(i) + " - " + "inizioFrase"));
            } catch (Exception e) {
            }

            matrix[i][0] = a0s * bso1;
            pointer[i][0] = -1; // abbiamo messo -1 e non 0 perchè 0 è un tag, mentre qua indichiamo inizio frase

        }
        //recursion step
        for (int t = 1; t < nFrase; t++) {
            Boolean trovato = false;
            for (int s = 0; s < nTag; s++) {
                Double max = new Double(0);
                Integer maxPointer = new Integer(-1);

                for (int i = 0; i < nTag; i++) {
                    Double a = new Double(0);
                    Double b = new Double(0);
                    Double viterbi = new Double(0);
                    Double ris;
                    try {
                        b = new Double(probabilitaW.get(frase.get(t) + " - " + tagList.get(s)));
                    } catch (Exception e) {
                        // la parole è sconosciuta, bisogna fare smoothing
                        //sempre nomi, verbi
                        //if (!wordList.contains(frase.get(t))  && (tagList.get(s).equals("NOUN") || tagList.get(s).equals("VERB"))){b = 0.5;}

                        // sempre nomi
                        //if (!wordList.contains(frase.get(t))  && tagList.get(s).equals("NOUN"))){b = 0.5;}

                        // 1/#(pos_tags)
                        //if (!wordList.contains(frase.get(t)) ){ b= (double) 1/16;}

                        //dev set single word
                        /*
                        if (!wordList.contains(frase.get(t))) {
                            if (tagList.get(s).equals("fineFrase")) {
                                b = 0.0;
                            } else {
                                b = smoothing.get(tagList.get(s));
                            }
                        }
                         */

                        // suffix-test
                        if (!wordList.contains(frase.get(t))) {
                            if (tagList.get(s).equals("fineFrase")) {
                                b = 0.0;
                            } else {
                                b = interpolateProb(frase.get(t),tagList.get(s),4, 0);
                            }
                        }


                    }
                    try {
                        a = new Double(probabilita.get(tagList.get(s) + " - " + tagList.get(i)));
                    } catch (Exception e) {
                    }
                    viterbi = new Double(matrix[i][t - 1]);
                    if (a == null || b == null || viterbi == null) {
                        System.out.println("null");
                    }

                    ris = new Double(viterbi * a * b);
                    if (max < ris) {
                        trovato = true;
                        max = ris;
                        maxPointer = i;
                        //System.out.println("ho trovato un max alla colonna="+ t+ " . Il tag è="+ tagList.get(i));
                    }
                }
                matrix[s][t] = max;  // valore
                pointer[s][t] = maxPointer;  // puntatore
            }
            //se la colonna è tutta 0, metto la prob di emissione massima e puntatore a il massimo della colonna precedente.
            if (!trovato) {
                Double max = 0.0;
                Integer pointerMax = 0;
                Double b = 0.0;
                for (int s = 0; s < nTag; s++) {
                    try {
                        b = new Double(probabilitaW.get(frase.get(t) + " - " + tagList.get(s)));
                    } catch (Exception e) {
                    }
                    if (b > max) {
                        max = b;
                        pointerMax = s;
                    }
                }
                matrix[pointerMax][t] = max;
                Double maxPrec = 0.0;
                Integer pointerMaxPrec = 0;
                for (int s = 0; s < nTag; s++) {
                    if (maxPrec < matrix[s][t - 1]) {
                        maxPrec = matrix[s][t - 1];
                        pointerMaxPrec = s;
                    }
                }
                pointer[pointerMax][t] = pointerMaxPrec;
            }

        }

        //termination step
        Double max = new Double(0);
        Integer maxPointer = new Integer(-1);
        for (int i = 0; i < nTag; i++) {
            Double a = new Double(0);
            Double viterbi = new Double(0);
            Double ris;
            try {
                a = new Double(probabilita.get("fineFrase" + " - " + tagList.get(i)));
            } catch (Exception e) {
            }
            viterbi = new Double(matrix[i][nFrase - 1]);
            // dovremo sistemarlo con i logaritmi
            // ris = new Double(viterbi*a);
            if (viterbi == 0 || a == 0) {
                ris = 0.0;
            } else {
                ris = new Double(Math.max(viterbi * a, Double.MIN_VALUE));
            }
            if (max <= ris) {
                max = ris;
                maxPointer = i;
            }
        }

        //System.out.println("fatto viterbi");

        //calcolo "return" di viterbi
        ArrayList<String> result = new ArrayList<>();
        result.add(tagList.get(maxPointer));
        int prec = pointer[maxPointer][nFrase - 1];
        result.add(tagList.get(prec));
        for (int i = nFrase - 2; i > 0; i--) {
            result.add(tagList.get(pointer[prec][i]));
            prec = pointer[prec][i];
        }
        Collections.reverse(result);
        return result;
    }
}
