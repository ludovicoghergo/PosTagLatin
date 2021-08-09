package com.company;

public class Triple {
    String word;
    String base;
    String tag;

    public Triple(String word, String base, String tag) {
        this.word = word;
        this.base = base;
        this.tag = tag;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "word='" + word + '\'' +
                ", base='" + base + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
