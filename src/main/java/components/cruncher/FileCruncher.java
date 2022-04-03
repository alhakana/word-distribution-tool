package components.cruncher;

import java.util.*;
import java.util.concurrent.RecursiveTask;

public class FileCruncher extends RecursiveTask<Map<String, Integer>> {


    private final String text;
    private final Integer arity;
    private final int start;
    private final int end;

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
        if (end - start <= CounterCruncherComp.L)
            bagOfWords = findBagOfWords(start, end);
        else bagOfWords = divideAndFork();

        return bagOfWords;
    }

    private Map<String, Integer> divideAndFork() {
        int length = text.length();

        int[] startAndEnd = new int[2];
        findForkLimits(startAndEnd, length);
//        System.out.println(startAndEnd[0] + " " + startAndEnd[1]);
        FileCruncher forkFileCruncher = new FileCruncher(text, arity, startAndEnd[0], startAndEnd[1]);

        findComputeLimits(startAndEnd, length);
        FileCruncher computeFileCruncher = new FileCruncher(text, arity, startAndEnd[0], startAndEnd[1]);

        forkFileCruncher.fork();
        Map<String, Integer> computeResult = computeFileCruncher.compute();
//        System.out.println("before join");
        Map<String, Integer> forkResult = forkFileCruncher.join();

//        System.out.println("before merge");
        return mergeBagOfWords(computeResult, forkResult);
    }

    private Map<String, Integer> findBagOfWords(int startIndex, int endIndex) {
//        System.out.println("FIND BAG OF WORDS");
        Map<String, Integer> bagOfWords = new HashMap<>();
        List<String> words = extractWords(startIndex, endIndex);
        List<String> bag = new ArrayList<>();
        List<String> bagCopy = new ArrayList<>();

//        System.out.println(words);
        for(int i = 0; i < words.size(); i++) {
            bag.add(words.get(i));
            bagCopy.add(words.get(i));

            if (i > arity - 2) {
                Collections.sort(bagCopy);
                String key = getBagOfString(bagCopy);
                if (bagOfWords.containsKey(key)) {
                    bagOfWords.put(key, bagOfWords.get(key)+1);
                } else {
                    bagOfWords.put(key, 1);
                }

                bagCopy.remove(bag.remove(0));
            }
        }

//        System.out.println("bag of words found ");
        return bagOfWords;
    }

    private String getBagOfString(List<String> bag) {
        StringBuilder sb = new StringBuilder();
        bag.iterator().forEachRemaining(str -> sb.append(str).append(" "));
//        System.out.println(sb.toString());
        return sb.toString().trim();
    }

    private List<String> extractWords(int startIndex, int endIndex) {
//        System.out.println("EXTRACT WORDS");
        ArrayList<String> words = new ArrayList<>();
        int beginWord = startIndex;

        for(int i = startIndex; i < endIndex; i++) {
            if (text.charAt(i) == ' ') {
                words.add(text.substring(beginWord, i).intern());
                beginWord = i+1;
                i++;
            }
        }
//        System.out.println("gotov extract");
        words.add(text.substring(beginWord, endIndex).intern());
        return words;
    }


    private Map<String, Integer> mergeBagOfWords(Map<String, Integer> compute, Map<String, Integer> join) {
        for(Map.Entry<String, Integer> entry : join.entrySet()) {
            if (compute.containsKey(entry.getKey()))
                compute.put(entry.getKey(), compute.get(entry.getKey()) + entry.getValue());
            else
                compute.put(entry.getKey(), entry.getValue());
        }
//        System.out.println("COMPUTE " + compute.toString());
        return compute;
    }


    private void findForkLimits(int[] array, int length) {
        int newStart = start;
        int newEnd = newStart + CounterCruncherComp.L;

        if (newEnd > length)
            newEnd = length;

        while(newEnd > newStart && text.charAt(newEnd) != ' ')
            newEnd--;

        array[0] = newStart;
        array[1] = newEnd;
    }

    private void findComputeLimits(int[] array, int length) {
        int newStart = array[1] + 1;

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

        array[0] = newStart;
        array[1] = length;
    }
}
