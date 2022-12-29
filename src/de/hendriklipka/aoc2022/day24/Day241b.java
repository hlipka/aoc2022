package de.hendriklipka.aoc2022.day24;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day241b
{
    public static void main(String[] args) throws IOException
    {
        List<List<String>> fieldData = AocParseUtils.getLinesAsChars("day24");

        long now=System.currentTimeMillis();
        BlizzardWorld world = new BlizzardWorld(fieldData);
        long now2 = System.currentTimeMillis();
        BestFirstSearch<BlizzardWorld, MySearchState> search = new BestFirstSearch<>(world);
        search.search();
        long end=System.currentTimeMillis();
        System.out.println("result="+world.bestResult);
        if (world.bestResult==297)
        {
            System.out.println("OK");
        }
        else
        {
            System.out.println("Wrong");
        }
        System.out.println("time needed: "+(end-now)+"ms");
        System.out.println("time init: "+(now2-now)+"ms");
        System.out.println("time search: "+(end-now2)+"ms");
    }

    private static List<Blizzard> moveBlizzards(List<Blizzard> blizzards)
    {
        List<Blizzard> newBlizzards = new ArrayList<>(blizzards.size());
        for (Blizzard b: blizzards)
        {
            newBlizzards.add(b.move());
        }
        return newBlizzards;
    }

    private enum Dir
    {
        U,D,L,R
    }

    private static class Pos
    {
        int row, col;

        public Pos(int row, int col)
        {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Pos pos = (Pos) o;

            return new EqualsBuilder().append(row, pos.row).append(col, pos.col).isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(17, 37).append(row).append(col).toHashCode();
        }

        @Override
        public String toString()
        {
            return "Pos{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }
    }
    private static class Blizzard
    {
        private final Pos pos;
        private final Dir dir;
        private final BlizzardWorld world;

        public Blizzard(int row, int col, String dirF, BlizzardWorld world)
        {
            this.world = world;
            pos = new Pos(row, col);
            switch(dirF)
            {
                case ">": dir= Dir.R; break;
                case "<": dir= Dir.L; break;
                case "v": dir= Dir.D; break;
                case "^": dir= Dir.U; break;
                default: throw new IllegalArgumentException("unknown direction "+dirF);
            }
        }

        public Blizzard(int row, int col, Dir dir, BlizzardWorld world)
        {
            this.world = world;
            pos=new Pos(row, col);
            this.dir = dir;
        }

        @Override
        public String toString()
        {
            return "Blizzard{" +
                    "pos=" + pos +
                    ", dir=" + dir +
                    '}';
        }

        public Blizzard move()
        {
            switch (dir)
            {

                case U: return moveUp();
                case D: return moveDown();
                case L: return moveLeft();
                case R: return moveRight();
            }
            throw new IllegalStateException("unknown direction "+dir);
        }

        private Blizzard moveUp()
        {
            if (pos.row>1)
            {
                return new Blizzard(pos.row-1, pos.col, dir, world);
            }
            else
            {
                return new Blizzard(world.height -2, pos.col, dir, world);
            }
        }

        private Blizzard moveDown()
        {
            if (pos.row< world.height -2)
            {
                return new Blizzard(pos.row+1, pos.col, dir, world);
            }
            else
            {
                return new Blizzard(1, pos.col, dir, world);
            }
        }

        private Blizzard moveLeft()
        {
            if (pos.col>1)
            {
                return new Blizzard(pos.row, pos.col-1, dir, world);
            }
            else
            {
                return new Blizzard(pos.row, world.width-2, dir, world);
            }
        }

        private Blizzard moveRight()
        {
            if (pos.col< world.width-2)
            {
                return new Blizzard(pos.row, pos.col+1, dir, world);
            }
            else
            {
                return new Blizzard(pos.row, 1, dir, world);
            }
        }
    }

    private static class MySearchState implements SearchState
    {
        public MySearchState(Pos pos, int round)
        {
            this.pos = pos;
            this.round = round;
        }

        Pos pos;
        int round;

        @Override
        public String calculateStateKey()
        {
            return round + "-" + pos.row + "-" + pos.col;
        }
    }

    private static class BlizzardWorld implements SearchWorld<MySearchState>
    {
        private final int width;
        private final int height;

        private final Pos startPos = new Pos(0, 1);
        private final Pos targetPos;

        public int blizzardPeriod;
        Set<Pos>[] blizzardMovement;

        int bestResult = Integer.MAX_VALUE;

        public BlizzardWorld(List<List<String>> fieldData)
        {
            width = fieldData.get(0).size();
            height = fieldData.size();
            blizzardPeriod = (width - 2) * (height - 2); // that's the max period for the blizzards
            //noinspection unchecked
            blizzardMovement = new Set[blizzardPeriod];
            List<Blizzard> blizzards = new ArrayList<>();
            for (int row = 1; row < height - 1; row++)
            {
                List<String> rowData = fieldData.get(row);
                for (int col = 1; col < width - 1; col++)
                {
                    String f = rowData.get(col);
                    if (!f.equals("."))
                    {
                        blizzards.add(new Blizzard(row, col, f, this));
                    }
                }
            }
            // pre-calculate the blizzard position
            // the index is what happens during this minute (so index 0 is what we see after the first minute is over)
            for (int i = 0; i < blizzardPeriod; i++)
            {
                List<Blizzard> newBlizzards = moveBlizzards(blizzards);
                blizzardMovement[i] = newBlizzards.stream().map(b -> b.pos).collect(Collectors.toSet());
                blizzards = newBlizzards; // store for next round
            }
            targetPos = new Pos(height - 1, width - 2);
        }

        @Override
        public MySearchState getFirstState()
        {
            return new MySearchState(startPos, 0);
        }

        @Override
        public List<MySearchState> calculateNextStates(MySearchState currentState)
        {
            ArrayList<MySearchState> result = new ArrayList<>(5);
            // use the pre-calculated set of blizzard positions
            Set<Pos> blizzards = blizzardMovement[currentState.round % blizzardPeriod];
            // heuristic: prefer going down or right to get closer to the exit
            // do up and left only when its needed (they will exit early when they are too worse)

            // simulate going down when possible
            Pos downPos = new Pos(currentState.pos.row + 1, currentState.pos.col);
            if (downPos.row < height - 1 && !blizzards.contains(downPos))
            {
                result.add(new MySearchState(downPos, currentState.round + 1));
            }
            // when we can go down to the end, do it (the exit will always be free)
            else if (downPos.equals(targetPos))
            {
                result.add(new MySearchState(downPos, currentState.round + 1));
            }

            // simulate going right when possible (not possible on the start position)
            Pos rightPos = new Pos(currentState.pos.row, currentState.pos.col + 1);
            if (rightPos.row != 0 && rightPos.col < width - 1 && !blizzards.contains(rightPos))
            {
                result.add(new MySearchState(rightPos, currentState.round + 1));
            }

            // simulate going up when possible
            Pos upPos = new Pos(currentState.pos.row - 1, currentState.pos.col);
            // don't ever go back to start (when we want that, we can just wait there)
            if (!upPos.equals(startPos) && upPos.row > 0 && !blizzards.contains(upPos))
            {
                result.add(new MySearchState(upPos, currentState.round + 1));
            }

            // simulate going left when possible
            Pos leftPos = new Pos(currentState.pos.row, currentState.pos.col - 1);
            if (leftPos.col > 0 && !blizzards.contains(leftPos))
            {
                result.add(new MySearchState(leftPos, currentState.round + 1));
            }

            // simulate waiting at the start
            if (currentState.pos.equals(startPos))
            {
                result.add(new MySearchState(currentState.pos, currentState.round + 1));
            }
            // simulate standing still (as long as we left the start already, this will be handled at the outer call)
            else if (!blizzards.contains(currentState.pos))
            {
                result.add(new MySearchState(currentState.pos, currentState.round + 1));
            }
            return result;        }

        @Override
        public boolean reachedTarget(MySearchState currentState)
        {
            if (currentState.pos.equals(targetPos))
            {
                bestResult = Math.min(bestResult, currentState.round);
                return true;
            }
            return false;
        }

        @Override
        public boolean canPruneBranch(MySearchState currentState)
        {
            return currentState.round + calculateGoodness(currentState) >= bestResult;
        }


        private int calculateGoodness(MySearchState currentState)
        {
            return targetPos.row - currentState.pos.row + targetPos.col - currentState.pos.col;
        }

        @Override
        public Comparator<MySearchState> getComparator()
        {
            return (o1, o2) -> Integer.compare(calculateGoodness(o1), calculateGoodness(o2));
        }
    }
}
