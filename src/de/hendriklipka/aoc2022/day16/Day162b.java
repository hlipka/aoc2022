package de.hendriklipka.aoc2022.day16;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 16.12.22
 * Time: 08:00
 */
public class Day162b
{
    static int bestRelieve = 0;
    private static Map<String, Cave> caves;
    private static int valveCount=0;

    public static void main(String[] args)
    {
        try
        {
            caves = AocParseUtils.getLines("day16")
                                 .stream()
                                 .map(Day162b::parseCave)
                                 .collect(Collectors.toMap(Cave::getName, c -> c));

            for (Cave cave : caves.values())
            {
                // count the caves with functioning valves
                if (cave.rate>0)
                {
                    valveCount++;
                }
                // and map all the directly reachable caves with functional valves (reachable via caves with non-functioning vales)
                mapPath(cave);
            }

            // determine the shortest paths between the caves with functioning valves
            // we do this by expanding the path for each cave step-by-step and checking if we get
            // either a new target, or a shorter path to an already known target
            while (true)
            {
                boolean oneMissing=false;
                for (Cave cave : caves.values())
                {
                    if (cave.paths.size()!=valveCount)
                    {
                        oneMissing=true;
                        // look at all known paths, and then the caves they are leading to whether we have something new
                        List<Map.Entry<String, Integer>> paths = new ArrayList<>(cave.paths.entrySet());
                        for (Map.Entry<String, Integer> pathCave: paths)
                        {
                            Cave nextCave = caves.get(pathCave.getKey());
                            int time=pathCave.getValue();
                            for (Map.Entry<String, Integer> nextPathCave: nextCave.paths.entrySet())
                            {
                                cave.addPath(nextPathCave.getKey(), time + nextPathCave.getValue());
                            }
                        }
                    }
                }
                if (!oneMissing)
                    break;
            }

            Set<String> opened = new HashSet<>();
            // we fake a first step into 'AA' as the start, with a travel time of 0
            // we traverse now only between caves with functioning valves, directly, without any steps inbetween, and
            // take the travel time into account
            traverse("AA", 0, "AA", 0, 26, 0, opened);

            System.out.println(bestRelieve);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void traverse(String targetMe, int timeLeftMe, String targetEl,
                                 int timeLeftEl, int remainingTime, int currentRelieve, Set<String> opened)
    {
        // when we have turned on all valves, there is nothing we can do anymore
        if (opened.size()==valveCount)
        {
            return;
        }
        // when we have only 1 minute left, we cannot open a valve nor walk to any place
        if (remainingTime <= 1)
        {
            return;
        }
        // we need still to move to our target
        if (timeLeftMe > 0)
        {
            // so we traverse the elephant using the current state
            // (time is unchanged, this will be done once we handled the elephant)
            traverseElephant(targetMe, timeLeftMe-1, targetEl, timeLeftEl, remainingTime, currentRelieve,
                    opened);
        }
        else // we have arrived
        {
            // since we visit only un-opened valves, and look at all of them anyway, we open the valve in any case
            Cave nextCaveMe = caves.get(targetMe);
            Set<String> newOpened = new HashSet<>(opened);
            int nextRelieve = currentRelieve;
            // ensure both us and the elephant don't open the same valve twice
            // also, since we visit the first cave we need to skip it
            if (!newOpened.contains(targetMe) && nextCaveMe.rate>0)
            {
                newOpened.add(targetMe);
                nextRelieve += nextCaveMe.getRate() * (remainingTime);
                if (nextRelieve > bestRelieve)
                {
                    System.out.println("new best " + nextRelieve);
                    bestRelieve = nextRelieve;
                }
            }
            // traverse all paths, and go to checking the elephant (without a time step, we will do this there)
            Set<Map.Entry<String, Integer>> nextPaths = nextCaveMe.paths.entrySet();
            for (Map.Entry<String, Integer> exit : nextPaths)
            {
                // since the paths have all potential targets, we can skip the ones we have opened already
                if (newOpened.contains(exit.getKey()))
                    continue;
                traverseElephant(exit.getKey(), exit.getValue(), targetEl, timeLeftEl,
                        remainingTime, nextRelieve, newOpened);
            }
        }
    }

    // the same as 'traverse', but handling elephant
    // once we do the next step, we handle the time step as well
    private static void traverseElephant(String targetMe, int timeLeftMe, String targetEl,
                                         int timeLeftEl, int remainingTime, int currentRelieve, Set<String> opened)
    {
        // we need still to move
        if (timeLeftEl > 0)
        {
            // traverse with current next time step
            traverse(targetMe, timeLeftMe, targetEl, timeLeftEl-1, remainingTime - 1, currentRelieve,
                    opened);
        }
        else // we have arrived
        {
            Cave nextCaveEl = caves.get(targetEl);
            Set<String> newOpened = new HashSet<>(opened);
            int nextRelieve = currentRelieve;
            // ensure both us and the elephant don't open the same valve twice
            // also, since we visit the first cave we need to skip it
            if (!newOpened.contains(targetEl)&& nextCaveEl.rate>0)
            {
                newOpened.add(targetEl);
                nextRelieve += nextCaveEl.getRate() * (remainingTime);
                if (nextRelieve > bestRelieve)
                {
                    System.out.println("new best " + nextRelieve);
                    bestRelieve = nextRelieve;
                }
            }
            // traverse all paths, and go to checking ourselves at the next time step
            Set<Map.Entry<String, Integer>> targetPaths = nextCaveEl.paths.entrySet();
            for (Map.Entry<String, Integer> exit : targetPaths)
            {
                if (newOpened.contains(exit.getKey()))
                    continue;
                traverse(targetMe, timeLeftMe, exit.getKey(), exit.getValue(),
                        remainingTime - 1, nextRelieve, newOpened);
            }
        }
    }

    private static void mapPath(Cave cave)
    {
        for (String next : cave.exits)
        {
            Cave nextCave = caves.get(next);
            if (nextCave.rate > 0)
            {
                cave.addPath(next, 1);
            }
            else
            {
                mapNextCaves(cave, cave, nextCave, 1);
            }
        }
    }

    private static void mapNextCaves(Cave original, Cave last, Cave cave, int depth)
    {
        if (cave == original)
        {
            return;
        }
        for (String next : cave.exits)
        {
            Cave nextCave = caves.get(next);
            if (nextCave == last)
            {
                continue;
            }
            if (nextCave.rate > 0)
            {
                original.addPath(next, depth + 1);
            }
            else
            {
                mapNextCaves(original, cave, nextCave, depth + 1);
            }
        }
    }

    private static Cave parseCave(String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line,
                "Valve ([A-Z]+) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? ([A-Z ,]+)");
        final String[] exits = parts.get(2).split(", ");
        return new Cave(parts.get(0), Integer.parseInt(parts.get(1)), Arrays.asList(exits));
    }

    private static class Cave
    {
        String name;
        int rate;
        List<String> exits;

        Map<String, Integer> paths = new HashMap<>();

        public Cave(String name, int rate, List<String> exits)
        {
            this.name = name;
            this.rate = rate;
            this.exits = exits;
        }

        public String getName()
        {
            return name;
        }

        public int getRate()
        {
            return rate;
        }

        public void addPath(String cave, int time)
        {
            if (paths.containsKey(cave))
            {
               int old=paths.get(cave);
               if (time<old)
               {
                   paths.put(cave, time);
               }
            }
            else
            {
                paths.put(cave, time);
            }
        }

        @Override
        public String toString()
        {
            return "Cave{" +
                    "name='" + name + '\'' +
                    ", rate=" + rate +
                    ", exits=" + exits +
                    ", paths=" + paths +
                    '}';
        }
    }
}
