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
public class Day161
{
    static int bestRelieve=0;
    private static Map<String, Cave> caves;

    public static void main(String[] args)
    {
        try
        {
            caves = AocParseUtils.getLines("day16").stream().map(Day161::parseCave).collect(Collectors.toMap(Cave::getName, c->c));

            for (Cave cave: caves.values())
            {
                mapPath(cave);
            }

            Cave start = caves.get("AA");
            List<String> opened=new ArrayList<>();
            traverse(start, 30, 0, opened);

            System.out.println(bestRelieve);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void mapPath(Cave cave)
    {
        for (String next: cave.exits)
        {
            Cave nextCave = caves.get(next);
            if (nextCave.rate>0)
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
        if (cave==original)
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
                original.addPath(next, depth+1);
            }
            else
            {
                mapNextCaves(original, cave, nextCave, depth+1);
            }
        }
    }

    private static void traverse(Cave cave, int remainingTime, int currentRelieve, List<String> opened)
    {
        // when we have only 1 minute left, we cannot open a valve nor walk to any place
        if (remainingTime<=1)
        {
            return;
        }
        if (cave.getRate()>0 && !opened.contains(cave.name))
        {
            // remember that we opened the valve
            List<String> newOpened = new ArrayList<>(opened);
            newOpened.add(cave.name);
            // turn on the valve, and check all exits
            int nextRelieve = currentRelieve + cave.getRate()*(remainingTime-1);
            if (nextRelieve>bestRelieve)
            {
                System.out.println("new best "+nextRelieve);
//                System.out.println("remaining "+remainingTime);
//                System.out.println(newOpened);
                bestRelieve=nextRelieve;
            }
            for (Map.Entry<String, Integer> exit: cave.paths.entrySet())
            {
                Cave exitCave = caves.get(exit.getKey());
                traverse(exitCave, remainingTime-1-exit.getValue(), nextRelieve, newOpened);
            }
        }
        // check next caves, without turning on the valve
        for (Map.Entry<String, Integer> exit : cave.paths.entrySet())
        {
            Cave exitCave = caves.get(exit.getKey());
            traverse(exitCave, remainingTime - exit.getValue(), currentRelieve, opened);
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
