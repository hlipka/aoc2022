package de.hendriklipka.aoc.bfs;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: hli
 * Date: 28.12.22
 * Time: 22:55
 */
public class BestFirstSearch<W extends SearchWorld<S>,S extends SearchState>
{
    private final W world;

    public BestFirstSearch(W world)
    {
        this.world=world;
    }

    public void search()
    {
        PriorityQueue<S> moves = new PriorityQueue<>(100000, world.getComparator());
        ConcurrentHashMap<String, String> memoize = new ConcurrentHashMap<>();

        moves.add(world.getFirstState());
        int count = 0;
        while (!moves.isEmpty())
        {
            count++;
            S currentState = moves.poll();

            // when we reach the exit, store the current round if it is better than the best way so far
            if (world.reachedTarget(currentState))
                continue;
            // prune branch
            if (world.canPruneBranch(currentState))
            {
                continue;
            }

            // memoize positions which we have seen before, and skip them if this happens
            String key = currentState.calculateStateKey();
            // this is an atomic set - if it returns some other than null, there was a mapping before
            if (null != memoize.putIfAbsent(key, key))
            {
                continue;
            }
            List<S> newStates = world.calculateNextStates(currentState);

            moves.addAll(newStates);
        }
        System.out.println("states looked at: " + count);

    }

}
