package components.cruncher;

import mvc.app.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class FileCruncher extends RecursiveTask<Map<String, Integer>> {

    private static int L = Integer.parseInt(Config.getProperty("counter_data_limit"));
    
    private String text;
    private Integer arity;
    private int start;
    private int end;

    public FileCruncher(String text, int arity, int start, int end) {
        this.text = text;
        this.arity = arity;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Map<String, Integer> compute() {
        if (start >= text.length() || start >= end)
            return new HashMap<>();

        Map<String, Integer> bagOfWords;
        if (end - start <= L)
            bagOfWords = findBagOfWords(start, end);
        else bagOfWords = divideAndFork();

        return bagOfWords;
    }

    private Map<String, Integer> divideAndFork() {
        int length = text.length();

        int newStart = start;
        int newEnd = newStart + L;

        if (newEnd > length)
            newEnd = length;

        while(newEnd > newStart && text.charAt(newEnd) != ' ')
            newEnd--;


        System.out.println(newStart + " " + newEnd);

        FileCruncher forkFileCruncher = new FileCruncher(text, arity, newStart, newEnd);

        newStart = newEnd + 1;
        newEnd = length;

        if (newStart > length)
            newStart = length;
        else if (arity > 1) {
            int wordCount = 0;
            while(wordCount < arity-1) {
                while(newStart > 0 && text.charAt(newStart) != ' ') {
                    newStart--;
                }
                wordCount++;
                
                if (newStart > 0) {
                    newStart--;
                } else break;
            }
            if (newStart > 0)
                newStart+=2;
        }

        FileCruncher computeFileCruncher = new FileCruncher(text, arity, newStart, newEnd);

        forkFileCruncher.fork();
        Map<String, Integer> computeResult = computeFileCruncher.compute();
        Map<String, Integer> forkResult = forkFileCruncher.join();

//        return mergeBagOfWords(computeFileCruncher.compute(), forkFileCruncher.join());
        return null;
    }

    private Map<String, Integer> findBagOfWords(int startIndex, int endIndex) {
        Map<String, Integer> bagOfWords = new HashMap<>();
        List<String> bag = new ArrayList<>();
        List<String> words = extractWords(startIndex, endIndex);

        for(int i = 0; i < words.size(); i++) {

        }

        return null;
    }

    private List<String> extractWords(int startIndex, int endIndex) {
        ArrayList<String> words = new ArrayList<>();
        int beginWord = startIndex;

        for(int i = startIndex; i < endIndex; i++) {
            if (text.charAt(i) == ' ') {
                words.add(text.substring(beginWord, i).intern());
                startIndex = i+1;
            }
        }
        words.add(text.substring(startIndex, endIndex).intern());
        return words;
    }


    private Map<String, Integer> mergeBagOfWords(Map<String, Integer> compute, Map<String, Integer> join) {

        return null;
    }


//    private void findForkStartAndEnd(int[] array, int length) {
//        int newStart = array[0];
//        int newEnd = array[1];
//        newStart = this.start;
//        newEnd = newStart + L;
//
//        while (text.charAt(newEnd) != ' ' && text.charAt(newEnd) != length-1)
//            newEnd++;
//
//        array[0] = newStart;
//        array[1] = newEnd;
//    }
//
//    private void findComputeStartAndEnd(int[] array, int length) {
//        int newStart = array[0];
//        int newEnd = array[1];
//        newStart = newEnd + 1;
//        newEnd = length;
//
//        if (newStart > length)
//            newStart = length;
//        else if (arity > 1) {
//            for (int wordCount = 0; wordCount < arity-1; wordCount++) {
//                while(newStart > 0 && text.charAt(newStart) != ' ') {
//                    newStart--;
//                }
//
//                if (newStart > 0) {
//                    newStart--;
//                } else break;
//            }
//            if (newStart > 0)
//                newStart+=2;
//        }
//
//        array[0] = newStart;
//        array[1] = newEnd;
//    }
}
