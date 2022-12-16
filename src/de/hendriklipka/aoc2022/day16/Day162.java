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
public class Day162
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
                                 .map(Day162::parseCave)
                                 .collect(Collectors.toMap(Cave::getName, c -> c));

            for (Cave cave : caves.values())
            {
                if (cave.rate>0)
                {
                    valveCount++;
                }
                mapPath(cave);
            }

            // NOTE: this is brute-force, and takes too long. See Day162b for a faster solution
            Set<String> opened = new HashSet<>();
            // we fake a first step into 'AA' as the start
            traverse(null, null, "AA", 0, "AA", 0, 26, 0, opened);

            System.out.println(bestRelieve);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void traverse(Cave caveMe, Cave caveEl, String targetMe, int timeLeftMe, String targetEl,
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
        // we need still to move
        if (timeLeftMe > 1)
        {
            timeLeftMe--;
            // traverse elephant with current state
            traverseElephant(caveMe, caveEl, targetMe, timeLeftMe, targetEl, timeLeftEl, remainingTime, currentRelieve,
                    opened);
        }
        else // we have arrived
        {
            Cave nextCaveMe = caves.get(targetMe);
            if (!opened.contains(nextCaveMe.name))
            {
                Set<String> newOpened = new HashSet<>(opened);
                newOpened.add(nextCaveMe.name);
                int nextRelieve = currentRelieve + nextCaveMe.getRate() * (remainingTime - 1);
                if (nextRelieve > bestRelieve)
                {
                    System.out.println("new best " + nextRelieve);
                    bestRelieve = nextRelieve;
                }

                // traverse all paths, and go to checking the elephant (without a time step, we will do this there)
                for (Map.Entry<String, Integer> exit : nextCaveMe.paths.entrySet())
                {
                    traverseElephant(nextCaveMe, caveEl, exit.getKey(), exit.getValue(), targetEl, timeLeftEl,
                            remainingTime, nextRelieve, newOpened);
                }
            }

            // traverse all paths, and go to checking the elephant (without a time step, we will do this there)
            for (Map.Entry<String, Integer> exit : nextCaveMe.paths.entrySet())
            {
                traverseElephant(nextCaveMe, caveEl, exit.getKey(), exit.getValue(), targetEl, timeLeftEl,
                        remainingTime, currentRelieve, opened);
            }
        }
    }

    private static void traverseElephant(Cave caveMe, Cave caveEl, String targetMe, int timeLeftMe, String targetEl,
                                         int timeLeftEl, int remainingTime, int currentRelieve, Set<String> opened)
    {
        // we need still to move
        if (timeLeftEl > 1)
        {
            timeLeftEl--;
            // traverse with current next time step
            traverse(caveMe, caveEl, targetMe, timeLeftMe, targetEl, timeLeftEl, remainingTime - 1, currentRelieve,
                    opened);
        }
        else // we have arrived
        {
            Cave nextCaveEl = caves.get(targetEl);
            if (!opened.contains(nextCaveEl.name))
            {
                Set<String> newOpened = new HashSet<>(opened);
                newOpened.add(nextCaveEl.name);
                int nextRelieve = currentRelieve + nextCaveEl.getRate() * (remainingTime - 1);
                if (nextRelieve > bestRelieve)
                {
                    System.out.println("new best " + nextRelieve);
                    bestRelieve = nextRelieve;
                }

                // traverse all paths, and go to checking ourselves at the next time step
                for (Map.Entry<String, Integer> exit : nextCaveEl.paths.entrySet())
                {
                    traverse(caveMe, nextCaveEl, targetMe, timeLeftMe, exit.getKey(), exit.getValue(),
                            remainingTime - 1, nextRelieve, newOpened);
                }
            }

            // traverse all paths, and go to checking ourselves at the next time step
            for (Map.Entry<String, Integer> exit : nextCaveEl.paths.entrySet())
            {
                traverse(caveMe, nextCaveEl, targetMe, timeLeftMe, exit.getKey(), exit.getValue(), remainingTime - 1,
                        currentRelieve, opened);
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
        System.out.println(cave);
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

        public List<String> getExits()
        {
            return exits;
        }

        public Map<String, Integer> getPaths()
        {
            return paths;
        }

        public void addPath(String cave, int time)
        {
            paths.put(cave, time);
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
