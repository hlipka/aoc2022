package de.hendriklipka.aoc2022.day19;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 19.12.22
 * Time: 07:41
 */
public class Day192
{
    final static int ROUNDS = 32;

    public static void main(String[] args)
    {
        try
        {
            List<BluePrint> blueprints = AocParseUtils.getLines("day19")
                                                      .stream()
                                                      .map(Day192::getBluePrint).limit(3)
                                                      .collect(Collectors.toList());
            long start = System.currentTimeMillis();
            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<Future<SimState>> results = new ArrayList<>();
            for (BluePrint bp : blueprints)
            {
                Callable<SimState> callableTask = () -> simulate(bp);
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
            for (Future<SimState> f:results)
            {
                SimState state=f.get();
                System.out.println(state);
                result *= state.geodes;
            }
            System.out.println(result);
            long now = System.currentTimeMillis();
            System.out.println("time=" + (now - start) / 1000 + "s");
        }
        catch (IOException | InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static SimState simulate(BluePrint bp)
    {
        SimState state = new SimState();
        System.out.println("simulate for "+bp);
        SimState simState = doSimulate(bp, state, 0);
        System.out.println("done");
        return simState;
    }

    private static SimState doSimulate(BluePrint bp, SimState state, int tick)
    {
        if (tick>=ROUNDS)
        {
            return state;
        }
        // check how many geodes we could find when we build a new robot each step
        // when this is less than the current best, stop here
        int timeLeft=ROUNDS-tick;
        int maxGeodesToBeFound=state.geodes+timeLeft*(state.geodeRobots*2+timeLeft);
        if (maxGeodesToBeFound<=bp.bestGeodes)
        {
            return state;
        }
        bp.count++;
        if (0==(bp.count%1000000000))
        {
            System.out.println(bp);
            System.out.println(bp.count);
            System.out.println(tick);
            System.out.println(bp.bestGeodes);
            System.out.println(state);
        }
        // first we get the earnings from the last tick, and only then the new robot will be finished
        state.ore += state.oreRobots;
        state.clay += state.clayRobots;
        state.obs += state.obsRobots;
        state.geodes += state.geodeRobots;

        switch (state.build)
        {
            case -1: break;
            case 1: state.oreRobots++; break;
            case 2: state.clayRobots++; break;
            case 3: state.obsRobots++; break;
            case 4: state.geodeRobots++; break;
        }
        state.build=-1; // stop building anything
        // when we can build a robot, simulate what happens when we do so
        // we bound the number of robots for each resource to the max resource we need to build another robot to avoid getting a surplus
        int maxG=-1;
        SimState bestState=null;
        // the order here is to maximize building the geode robots, so hopefully we can exit early some branches
        if (state.ore >= bp.oreForGeode && state.obs>=bp.obsForGeode)
        {
            SimState nextState = new SimState(state);
            nextState.build = 4;
            nextState.ore -= bp.oreForGeode;
            nextState.obs -= bp.obsForGeode;
            SimState result=doSimulate(bp, nextState, tick+1);
            if (result.geodes>maxG)
            {
                maxG=result.geodes;
                bestState=result;
            }
        }
        if (state.ore >= bp.oreForObs && state.clay>=bp.clayForObs && state.obsRobots<bp.getMaxObs())
        {
            SimState nextState = new SimState(state);
            nextState.build = 3;
            nextState.ore -= bp.oreForObs;
            nextState.clay -= bp.clayForObs;
            SimState result=doSimulate(bp, nextState, tick+1);
            if (result.geodes>maxG)
            {
                maxG=result.geodes;
                bestState=result;
            }
        }
        if (state.ore >= bp.oreForClay && state.clayRobots<bp.getMaxClay())
        {
            SimState nextState = new SimState(state);
            nextState.build = 2;
            nextState.ore -= bp.oreForClay;
            SimState result=doSimulate(bp, nextState, tick+1);
            if (result.geodes>maxG)
            {
                maxG=result.geodes;
                bestState=result;
            }
        }
        if (state.ore >= bp.oreForOre && state.oreRobots<bp.getMaxOre())
        {
            SimState nextState=new SimState(state);
            nextState.build=1;
            nextState.ore-=bp.oreForOre;
            SimState result=doSimulate(bp, nextState, tick+1);
            if (result.geodes>maxG)
            {
                maxG=result.geodes;
                bestState=result;
            }
        }
        // and also simulate what happens when we don't build a robot
        SimState result=doSimulate(bp, new SimState(state), tick+1);
        if (result.geodes>maxG)
        {
            maxG=result.geodes;
            bestState=result;
        }

        if (maxG>bp.bestGeodes)
        {
            bp.bestGeodes = maxG;
        }
        return bestState;
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


    private static class SimState
    {
        public SimState(SimState other)
        {
            clay=other.clay;
            ore=other.ore;
            obs=other.obs;
            clayRobots=other.clayRobots;
            oreRobots=other.oreRobots;
            obsRobots=other.obsRobots;
            geodeRobots=other.geodeRobots;
            geodes=other.geodes;
            build=other.build;
        }

        private int clay = 0;
        private int ore = 0;
        private int obs = 0;
        private int clayRobots = 0;
        private int oreRobots = 1;
        private int obsRobots = 0;
        private int geodeRobots = 0;
        private int geodes = 0;
        private int build=-1;

        public SimState()
        {

        }

        @Override
        public String toString()
        {
            return "SimState{" +
                    "build=" + build +
                    ", ore=" + ore +
                    ", clay=" + clay +
                    ", obs=" + obs +
                    ", oreRobots=" + oreRobots +
                    ", clayRobots=" + clayRobots +
                    ", obsRobots=" + obsRobots +
                    ", geodeRobots=" + geodeRobots +
                    ", geodes=" + geodes +
                    '}';
        }
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
