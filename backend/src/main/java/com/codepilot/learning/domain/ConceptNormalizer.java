package com.codepilot.learning.domain;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConceptNormalizer {

    public Set<Concept> normalize(List<String> rawTags, String aiConcept) {
        Set<Concept> result = new HashSet<>();
        
        if (rawTags != null) {
            for (String tag : rawTags) {
                result.add(mapStringToConcept(tag));
            }
        }
        
        if (aiConcept != null && !aiConcept.trim().isEmpty()) {
            result.add(mapStringToConcept(aiConcept));
        }
        
        if (result.isEmpty()) {
            result.add(Concept.UNKNOWN);
        }
        
        return result;
    }

    private Concept mapStringToConcept(String value) {
        if (value == null) return Concept.UNKNOWN;
        
        String normalized = value.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        
        return switch (normalized) {
            case "array", "arrays" -> Concept.ARRAYS;
            case "string", "strings" -> Concept.STRINGS;
            case "hash", "hashing", "hashmap", "hashtable" -> Concept.HASHING;
            case "twopointer", "twopointers" -> Concept.TWO_POINTERS;
            case "slidingwindow" -> Concept.SLIDING_WINDOW;
            case "stack", "stacks" -> Concept.STACK;
            case "queue", "queues" -> Concept.QUEUE;
            case "linkedlist", "linkedlists" -> Concept.LINKED_LIST;
            case "binarysearch" -> Concept.BINARY_SEARCH;
            case "sort", "sorting" -> Concept.SORTING;
            case "recursion", "recursive" -> Concept.RECURSION;
            case "backtracking", "backtrack" -> Concept.BACKTRACKING;
            case "tree", "trees" -> Concept.TREES;
            case "binarytree", "binarytrees" -> Concept.BINARY_TREES;
            case "bst", "binarysearchtree" -> Concept.BST;
            case "heap", "priorityqueue", "pq" -> Concept.HEAP_PRIORITY_QUEUE;
            case "graph", "graphs" -> Concept.GRAPHS;
            case "bfs", "breadthfirstsearch" -> Concept.BFS;
            case "dfs", "depthfirstsearch" -> Concept.DFS;
            case "dp", "dynamicprogramming" -> Concept.DYNAMIC_PROGRAMMING;
            case "greedy" -> Concept.GREEDY;
            case "bitmanipulation", "bit" -> Concept.BIT_MANIPULATION;
            case "math", "mathematics" -> Concept.MATH;
            default -> {
                for (Concept c : Concept.values()) {
                    if (c.name().replace("_", "").toLowerCase().equals(normalized)) {
                        yield c;
                    }
                }
                yield Concept.UNKNOWN;
            }
        };
    }
}
