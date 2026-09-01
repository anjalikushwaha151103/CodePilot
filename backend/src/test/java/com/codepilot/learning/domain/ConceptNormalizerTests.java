package com.codepilot.learning.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConceptNormalizerTests {

    private final ConceptNormalizer normalizer = new ConceptNormalizer();

    @Test
    void testNormalize_knownTags() {
        Set<Concept> concepts = normalizer.normalize(List.of("Array", "Binary Search"), null);
        assertTrue(concepts.contains(Concept.ARRAYS));
        assertTrue(concepts.contains(Concept.BINARY_SEARCH));
    }

    @Test
    void testNormalize_caseInsensitive() {
        Set<Concept> concepts = normalizer.normalize(List.of("aRrAy", "bInaRy sEaRCh"), null);
        assertTrue(concepts.contains(Concept.ARRAYS));
        assertTrue(concepts.contains(Concept.BINARY_SEARCH));
    }

    @Test
    void testNormalize_aiConceptFallback() {
        Set<Concept> concepts = normalizer.normalize(null, "Sliding Window");
        assertTrue(concepts.contains(Concept.SLIDING_WINDOW));
    }

    @Test
    void testNormalize_unknownTag() {
        Set<Concept> concepts = normalizer.normalize(List.of("Some Weird Tag"), null);
        assertTrue(concepts.contains(Concept.UNKNOWN));
    }
}