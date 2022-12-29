package de.hendriklipka.aoc2022.day19;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.search.DepthFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * version of 'Day192' using the depth-first search utility class
 */
public class Day192c
{
    final static int ROUNDS = 32;

    public static void main(String[] args)
    {
        try
        {
            List<BluePrint> blueprints = AocParseUtils.getLines("day19")
                                                      .stream()
                                                      .map(Day192c::getBluePrint).limit(3)
                                                      .collect(Collectors.toList());
            long start = System.currentTimeMillis();

            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<RobotWorld> worlds = new ArrayList<>();
            for (BluePrint bp : blueprints)
            {
                RobotWorld world = new RobotWorld(bp);
                worlds.add(world); // here we also track the results
                DepthFirstSearch<RobotWorld, RobotState> dfs = new DepthFirstSearch<>(world);
                executor.submit(dfs::search);
            }
            executor.shutdown();
            try
            {
                //noinspection ResultOfMethodCallIgnored
                executor.awaitTermination(10, TimeUnit.DAYS);
            }
            catch (InterruptedException e)
            {
                System.err.println("interrupted while waiting for tasks: " + e.getMessage());
            }
            int result = 1;
            for (RobotWorld world : worlds)
            {
                System.out.println(world.bestGeodes);
                result *= world.bestGeodes;
            }
            System.out.println(result);
            // results must be 31, 8, 17
            if (result == 4216)
            {
                System.out.println("OK");
            }
            else
            {
                System.out.println("wrong");
            }
            long now = System.currentTimeMillis();
            System.out.println("time=" + (now - start) / 1000 + "s");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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

        public BluePrint(int num, int oreForOre, int oreForClay, int oreForObs, int clayForObs, int oreForGeode,
                         int obsForGeode)
        {
            this.num = num;
            this.oreForOre = oreForOre;
            this.oreForClay = oreForClay;
            this.oreForObs = oreForObs;
            this.clayForObs = clayForObs;
            this.oreForGeode = oreForGeode;
            this.obsForGeode = obsForGeode;
            maxOre = Math.max(oreForOre, Math.max(oreForClay, oreForObs));
            maxClay = clayForObs;
            maxObs = obsForGeode;
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

    private static class RobotWorld implements SearchWorld<RobotState>
    {
        private final BluePrint blueprint;

        int bestGeodes = -1;

        public RobotWorld(BluePrint blueprints)
        {
            this.blueprint = blueprints;
        }

        @Override
        public RobotState getFirstState()
        {
            final RobotState state = new RobotState();
            state.oreRobots = 1;
            return state;
        }

        @Override
        public List<RobotState> calculateNextStates(RobotState currentState)
        {
            // first we get the earnings from the last tick, and only then the new robot will be finished
            currentState.ore += currentState.oreRobots;
            currentState.clay += currentState.clayRobots;
            currentState.obs += currentState.obsRobots;
            currentState.geodes += currentState.geodeRobots;
            // we limit the amount of each material, so we get better detection of duplicates states
            // limit is the most we need for each material, times 2, minus what we would produce in this round
            // this should not limit production of any robot in the next rounds
            currentState.ore = Math.min(currentState.ore, 2 * blueprint.maxOre- currentState.oreRobots);
            currentState.clay = Math.min(currentState.clay, 2 * blueprint.maxClay - currentState.clayRobots);
            currentState.obs = Math.min(currentState.obs, 2 * blueprint.maxObs - currentState.obsRobots);

            switch (currentState.build)
            {
                case -1:
                    break;
                case 1:
                    currentState.oreRobots++;
                    break;
                case 2:
                    currentState.clayRobots++;
                    break;
                case 3:
                    currentState.obsRobots++;
                    break;
                case 4:
                    currentState.geodeRobots++;
                    break;
            }
            currentState.build = -1; // stop building anything

            List<RobotState> states = new ArrayList<>(5);

            // when we can build a robot, simulate what happens when we do so
            // we bound the number of robots for each resource to the max resource we need to build another robot to avoid getting a surplus
            
            // the order here is to maximize building the geode robots, so hopefully we can exit early some branches
            if (currentState.ore >= blueprint.oreForGeode && currentState.obs >= blueprint.obsForGeode)
            {
                RobotState nextState = new RobotState(currentState, currentState.ticks+1);
                nextState.build = 4;
                nextState.ore -= blueprint.oreForGeode;
                nextState.obs -= blueprint.obsForGeode;
                states.add(nextState);
            }
            if (currentState.ore >= blueprint.oreForObs && currentState.clay >= blueprint.clayForObs && currentState.obsRobots < blueprint.getMaxObs())
            {
                RobotState nextState = new RobotState(currentState, currentState.ticks + 1);
                nextState.build = 3;
                nextState.ore -= blueprint.oreForObs;
                nextState.clay -= blueprint.clayForObs;
                states.add(nextState);
            }
            if (currentState.ore >= blueprint.oreForClay && currentState.clayRobots < blueprint.getMaxClay())
            {
                RobotState nextState = new RobotState(currentState, currentState.ticks + 1);
                nextState.build = 2;
                nextState.ore -= blueprint.oreForClay;
                states.add(nextState);
            }
            if (currentState.ore >= blueprint.oreForOre && currentState.oreRobots < blueprint.getMaxOre())
            {
                RobotState nextState = new RobotState(currentState, currentState.ticks + 1);
                nextState.build = 1;
                nextState.ore -= blueprint.oreForOre;
                states.add(nextState);
            }
            // and also simulate what happens when we don't build a robot
            final RobotState nextState = new RobotState(currentState, currentState.ticks + 1);
            states.add(nextState);

            return states;
        }

        @Override
        public boolean reachedTarget(RobotState currentState)
        {
            if (currentState.ticks >= ROUNDS)
            {
                bestGeodes = Math.max(bestGeodes, currentState.geodes);
                return true;
            }
            return false;
        }

        @Override
        public boolean canPruneBranch(RobotState currentState)
        {
            // check how many geodes we could find when we build a new robot each step
            // when this is less than the current best, stop here
            int timeLeft = ROUNDS - currentState.ticks;
            int maxGeodesToBeFound = currentState.geodes + timeLeft * (currentState.geodeRobots * 2 + timeLeft);
            return maxGeodesToBeFound <= bestGeodes;
        }

        @Override
        public Comparator<RobotState> getComparator()
        {
            return null;
        }
    }

    private static class RobotState implements SearchState
    {
        public RobotState(RobotState other, int ticks)
        {
            clay = other.clay;
            ore = other.ore;
            obs = other.obs;
            clayRobots = other.clayRobots;
            oreRobots = other.oreRobots;
            obsRobots = other.obsRobots;
            geodeRobots = other.geodeRobots;
            geodes = other.geodes;
            build = other.build;
            this. ticks = ticks;
        }

        private int clay = 0;
        private int ore = 0;
        private int obs = 0;
        private int clayRobots = 0;
        private int oreRobots = 0;
        private int obsRobots = 0;
        private int geodeRobots = 0;
        private int geodes = 0;
        private int build = -1;

        private int ticks=0;

        public RobotState()
        {

        }

        @Override
        public String calculateStateKey()
        {
            return "RobotState{" +
                    "" + ticks +
                    "-" + ore +
                    "-" + clay +
                    "-" + obs +
                    "-" + geodes +
                    "-" + oreRobots +
                    "-" + clayRobots +
                    "-" + obsRobots +
                    "-" + geodeRobots +
                    "-" + build;
        }

        @Override
        public String toString()
        {
            return "RobotState{" +
                    "ticks=" + ticks +
                    ", build=" + build +
                    ", ore=" + ore +
                    ", clay=" + clay +
                    ", obs=" + obs +
                    ", oreRobots=" + oreRobots +
                    ", clayRobots=" + clayRobots +
                    ", obsRobots=" + obsRobots +
                    ", geodeRobots=" + geodeRobots +
                    ", geodes=" + geodes +
                    '}';
        }    }
}
