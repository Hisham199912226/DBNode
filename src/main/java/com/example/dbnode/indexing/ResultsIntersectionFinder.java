package com.example.dbnode.indexing;

import java.util.HashSet;
import java.util.List;

public class ResultsIntersectionFinder {
    public HashSet<Integer> findIntersections(List<HashSet<Integer>> indexingResults){
        if(indexingResults == null)
            throw new IllegalArgumentException();
        HashSet<Integer> result = new HashSet<>(indexingResults.get(0));
        for (int i = 1; i < indexingResults.size(); i++) {
            result.retainAll(indexingResults.get(i));
        }
        return result;
    }
}
