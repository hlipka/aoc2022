package de.hendriklipka.aoc.search;

import java.util.Comparator;
import java.util.List;

/**
 * Defines the 'world' / environment to do a search for a best result in.
 * @param <S> the type for tracking the current search state
 */
public interface SearchWorld<S extends SearchState>
{
    /**
     * @return the initial state (where to start)
     */
    S getFirstState();

    /**
     * From a given state, calculate which states can we access / reach next
     * For a best-first search, these must be comparable by the comparator instance returned by 'getComparator()'.
     * For a depth-first search, when no comparator is provided the order in the list is used for further search.
     */
    List<S> calculateNextStates(S currentState);

    /**
     * Check whether the given state has actually reached the target
     * This also must track the best result somehow
     */
    boolean reachedTarget(S currentState);

    /**
     * Check whether the current branch can be pruned (when we know it will never be better than the current best result)
     */
    boolean canPruneBranch(S currentState);

    /**
     * create a comparator to sort the states according to which one should be looked at next.
     * (e.g. when this is 'distance to target', do
     * Integer.compare(state1.dist, state2.dist)
     * , and reverse when a higher value should go first)
     * Must return an instance for best-first search, might be null for a depth-first search.
     */
    Comparator<S> getComparator();
}
