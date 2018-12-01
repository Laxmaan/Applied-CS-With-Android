/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    private int wordLength=DEFAULT_WORD_LENGTH;
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String,ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer,ArrayList<String>> sizetoWords = new HashMap<>();
    private HashMap<String,ArrayList<String>> bigAnagrams = new HashMap<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordSet.add(word);
            ArrayList<String> temp = lettersToWord.get(sortLetters(word));
            if(temp==null)
                temp = new ArrayList<>();
            temp.add(word);
            lettersToWord.put(sortLetters(word),temp);

            temp= (ArrayList<String>) getAnagramsWithOneMoreLetter(word);
            if(temp.size()>=MIN_NUM_ANAGRAMS) {
                bigAnagrams.put(word,temp); //Hash the big anagrams to the word
                temp = sizetoWords.get(word.length());  //add the word to start words
                if (temp == null)
                    temp = new ArrayList<>();
                temp.add(word);
                sizetoWords.put(word.length(), temp);
            }
        }
    }

    String sortLetters(String base){
        if (base ==null)
            return null;
        char[] letters = base.toCharArray();
        Arrays.sort(letters);
        return String.valueOf(letters);
    }

    public boolean isGoodWord(String word, String base) {

        return wordSet.contains(word)&&!word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {

        return lettersToWord.get(sortLetters(targetWord));
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = bigAnagrams.get(word); // SEE if entry exists

        if(result==null) { //NO entry, generate the anagrams for the word
            result = new ArrayList<>();
            for (char x = 'a'; x <= 'z'; x++) {
                String longer_word = word + x;
                ArrayList<String> temp = (ArrayList<String>) getAnagrams(longer_word);
                if (temp != null)
                    for (String s : temp)
                        if (isGoodWord(s, word))
                            result.add(s);


            }
            Collections.sort(result);

            bigAnagrams.put(word,result); //MAP the anagrams to the word
        }return result;
    }

    public String pickGoodStarterWord() {
        int searches=0;
        while(true){
            wordList = sizetoWords.get(wordLength);
            int index = random.nextInt(wordList.size());
            if(getAnagramsWithOneMoreLetter(wordList.get(index)).size()>=MIN_NUM_ANAGRAMS) {
                wordLength=Math.min(wordLength+1,MAX_WORD_LENGTH);
                Log.d("SEARCH_CT", String.valueOf(searches));
                return wordList.get(index);
            }
            searches++;
        }

    }
}
