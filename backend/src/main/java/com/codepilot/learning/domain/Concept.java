package com.codepilot.learning.domain;

public enum Concept {
    ARRAYS("Arrays"),
    STRINGS("Strings"),
    HASHING("Hashing"),
    TWO_POINTERS("Two Pointers"),
    SLIDING_WINDOW("Sliding Window"),
    STACK("Stack"),
    QUEUE("Queue"),
    LINKED_LIST("Linked List"),
    BINARY_SEARCH("Binary Search"),
    SORTING("Sorting"),
    RECURSION("Recursion"),
    BACKTRACKING("Backtracking"),
    TREES("Trees"),
    BINARY_TREES("Binary Trees"),
    BST("Binary Search Tree"),
    HEAP_PRIORITY_QUEUE("Heap / Priority Queue"),
    GRAPHS("Graphs"),
    BFS("Breadth-First Search"),
    DFS("Depth-First Search"),
    DYNAMIC_PROGRAMMING("Dynamic Programming"),
    GREEDY("Greedy"),
    BIT_MANIPULATION("Bit Manipulation"),
    MATH("Math"),
    UNKNOWN("Unknown");

    private final String displayName;

    Concept(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
