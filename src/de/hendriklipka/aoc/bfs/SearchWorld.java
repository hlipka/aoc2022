package de.hendriklipka.aoc.bfs;

import java.util.Comparator;
import java.util.List;

/**
 * Implements a best-first search
 * using a given 'world' model and tracking search states
 */
public interface SearchWorld<S extends SearchState>
{
    /**
     * @return the initial state (where to start)
     */
    S getFirstState();

    /**
     * From a given state, calculate which states can we access / reach next
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
     *
     */
    Comparator<S> getComparator();
}
