package de.hendriklipka.aoc.search;

/**
 * Base interface to track the current search state
 */
public interface SearchState
{
    /**
     * @return a key used to check duplicates states (which can be pruned)
     */
    String calculateStateKey();
}
