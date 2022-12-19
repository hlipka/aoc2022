package de.hendriklipka.aoc2022.day19;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * instead of a class with copy constructor we have all the parameters on the stack.
 * (its about twice as fast as the version using a class, or the int[9] array version)
 * */
public class Day192b
{
    final static int ROUNDS = 32;

    public static void main(String[] args)
    {
        try
        {
            List<BluePrint> blueprints = AocParseUtils.getLines("day19")
                                                      .stream()
                                                      .map(Day192b::getBluePrint).limit(3)
                                                      .collect(Collectors.toList());
            ExecutorService executor = Executors.newFixedThreadPool(4);
            long start=System.currentTimeMillis();
            List<Future<Integer>> results = new ArrayList<>();
            for (BluePrint bp : blueprints)
            {
                Callable<Integer> callableTask = () -> simulate(bp);
                results.add(executor.submit(callableTask));
            }
            executor.shutdown();
            try
            {
                executor.awaitTermination( 10, TimeUnit.DAYS );
            }
            catch ( InterruptedException e )
            {
                System.err.println( "interrupted while waiting for tasks: "+e.getMessage() );
            }
            int result = 1;
            for (Future<Integer> f:results)
            {
                int state=f.get();
                System.out.println("geodes: "+state);
                result *= state;
            }
            System.out.println(result);
            long now=System.currentTimeMillis();
            System.out.println("time="+(now-start)/1000+"s");
        }
        catch (IOException | InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int simulate(BluePrint bp)
    {
        System.out.println("simulate for "+bp);
        int result = doSimulate(bp, 0, 0, 0, 0, 1, 0, 0, 0, 0, -1);
        System.out.println("done");
        return result;
    }

    private static int doSimulate(BluePrint bp, int tick, int ore, int clay, int obs, int oreRobots, int clayRobots,
                                    int obsRobots, int geodeRobots, int geodes, int build)
    {
        if (tick>=ROUNDS)
        {
            return geodes;
        }
        // check how many geodes we could find when we build a new robot each step
        // when this is less than the current best, stop here
        int timeLeft=ROUNDS-tick;
        int maxGeodesToBeFound=geodes+timeLeft*(geodeRobots*2+timeLeft);
        if (maxGeodesToBeFound<=bp.bestGeodes)
        {
            return geodes;
        }
        bp.count++;
        if (0==(bp.count%1000000000))
        {
            System.out.println("bp="+bp.num);
            System.out.println("rounds="+bp.count);
            System.out.println("tick="+tick);
            System.out.println("best="+bp.bestGeodes);
        }
        // first we get the earnings from the last tick, and only then the new robot will be finished
        ore += oreRobots;
        clay += clayRobots;
        obs += obsRobots;
        geodes += geodeRobots;

        switch (build)
        {
            case -1: break;
            case 1: oreRobots++; break;
            case 2: clayRobots++; break;
            case 3: obsRobots++; break;
            case 4: geodeRobots++; break;
        }
        // when we can build a robot, simulate what happens when we do so
        // we bound the number of robots for each resource to the max resource we need to build another robot to avoid getting a surplus
        // we also track what the best outcome for these decisions was
        int maxG=-1;
        // the order here is to maximize building the geode robots, so hopefully we can exit early some branches
        if (ore >= bp.oreForGeode && obs>=bp.obsForGeode)
        {
            int result=doSimulate(bp, tick+1, ore- bp.oreForGeode, clay, obs- bp.obsForGeode, oreRobots, clayRobots, obsRobots, geodeRobots,
                    geodes, 4);
            if (result>maxG)
            {
                maxG=result;
            }
        }
        if (ore >= bp.oreForObs && clay>=bp.clayForObs && obsRobots<bp.getMaxObs())
        {
            int result=doSimulate(bp, tick+1, ore- bp.oreForObs, clay- bp.clayForObs, obs, oreRobots, clayRobots, obsRobots, geodeRobots,
                    geodes, 3);
            if (result>maxG)
            {
                maxG=result;
            }
        }
        if (ore >= bp.oreForClay && clayRobots<bp.getMaxClay())
        {
            int result=doSimulate(bp, tick+1, ore- bp.oreForClay, clay, obs, oreRobots, clayRobots, obsRobots, geodeRobots,
                    geodes, 2);
            if (result>maxG)
            {
                maxG=result;
            }
        }
        if (ore >= bp.oreForOre && oreRobots<bp.getMaxOre())
        {
            int result=doSimulate(bp, tick+1, ore- bp.oreForOre, clay, obs, oreRobots, clayRobots, obsRobots, geodeRobots,
                    geodes, 1);
            if (result>maxG)
            {
                maxG=result;
            }
        }
        // and also simulate what happens when we don't build a robot
        int result=doSimulate(bp, tick+1, ore, clay, obs, oreRobots, clayRobots, obsRobots, geodeRobots,
                geodes, -1);
        if (result>maxG)
        {
            maxG=result;
        }
        // keep record of the best outcome so far (so we can skip rounds)
        if (maxG>bp.bestGeodes)
        {
            bp.bestGeodes = maxG;
        }
        return maxG;
    }

    private static BluePrint getBluePrint(String s)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(s,
                "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
        return new BluePrint(Integer.parseInt(parts.get(0)), Integer.parseInt(parts.get(1)),
                Integer.parseInt(parts.get(2)),
                Integer.parseInt(parts.get(3)), Integer.parseInt(parts.get(4)), Integer.parseInt(parts.get(5)),
                Integer.parseInt(parts.get(6)));
    }

    private static class BluePrint
    {
        private final int num;
        private final int oreForOre;
        private final int oreForClay;
        private final int oreForObs;
        private final int clayForObs;
        private final int oreForGeode;
        private final int obsForGeode;

        private final int maxOre;
        private final int maxClay;
        private final int maxObs;

        int bestGeodes=-1;
        long count=0;

        public BluePrint(int num, int oreForOre, int oreForClay, int oreForObs, int clayForObs, int oreForGeode, int obsForGeode)
        {
            this.num = num;
            this.oreForOre = oreForOre;
            this.oreForClay = oreForClay;
            this.oreForObs = oreForObs;
            this.clayForObs = clayForObs;
            this.oreForGeode = oreForGeode;
            this.obsForGeode = obsForGeode;
            maxOre=Math.max(oreForOre, Math.max(oreForClay, oreForObs));
            maxClay=clayForObs;
            maxObs=obsForGeode;
        }

        @Override
        public String toString()
        {
            return "BluePrint{" +
                    "num=" + num +
                    ", oreForOre=" + oreForOre +
                    ", oreForClay=" + oreForClay +
                    ", oreForObs=" + oreForObs +
                    ", clayForObs=" + clayForObs +
                    ", oreForGeode=" + oreForGeode +
                    ", obsForGeode=" + obsForGeode +
                    ", maxOre=" + maxOre +
                    ", maxClay=" + maxClay +
                    ", maxObs=" + maxObs +
                    '}';
        }

        public int getMaxOre()
        {
            return maxOre;
        }

        public int getMaxClay()
        {
            return maxClay;
        }

        public int getMaxObs()
        {
            return maxObs;
        }
    }
}
