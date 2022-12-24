package de.hendriklipka.aoc2022.day24;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Day242
{
    private static int width;
    private static int height;

    public static int CACHESIZE = -1;
    static  Set<Position>[] BLIZZARDS;
    
    static final int WAITS=100;
    static final int[] RESULTS = new int[WAITS];
    // cache visited positions
    static final ConcurrentHashMap<String, String> CACHE=new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException
    {
        List<List<String>> fieldData = AocParseUtils.getLinesAsChars("day24");
        width=fieldData.get(0).size();
        height =fieldData.size();
        CACHESIZE=(width-2)*(height-2); // that's the max period for the blizzards
        //noinspection unchecked
        BLIZZARDS= new Set[CACHESIZE];
        List<Blizzard> blizzards=new ArrayList<>();
        for (int row = 1; row< height -1; row++)
        {
            List<String> rowData=fieldData.get(row);
            for (int col=1;col<width-1;col++)
            {
                String f=rowData.get(col);
                if (!f.equals("."))
                {
                    blizzards.add(new Blizzard(row, col, f));
                }
            }
        }
        // pre-calculate the blizzard position
        // the index is what happens during this minute (so index 0 is what we see after the first minute is over)
        for (int i = 0; i< CACHESIZE; i++)
        {
            List<Blizzard> newBlizzards = moveBlizzards(blizzards);
            BLIZZARDS[i]= newBlizzards.stream().map(b->b.pos).collect(Collectors.toSet());
            blizzards=newBlizzards; // store for next round
        }

        // we simulate the three runs, each starting at where the previous one finished
        int round = doSimulation(new Position(0, 1), new Position(height - 1, width - 2), 0);
        System.out.println(round);
        int round1 = doSimulation(new Position(height - 1, width - 2), new Position(0, 1), round);
        System.out.println(round1-round);
        System.out.println("total="+round1);
        int round2 = doSimulation(new Position(0, 1), new Position(height - 1, width - 2), round1);
        System.out.println(round2-round1);
        System.out.println("total=" + round2);
    }

    private static int doSimulation(final Position startPos, final Position endPos, int offset)
    {
        Arrays.fill(RESULTS, Integer.MAX_VALUE);
        CACHE.clear();
        // simulate while waiting at the start, but not for too long
        // (note: this should be longer than width-2, as this is the period of the horizontal row)
        for (int waitTime=0;waitTime<Math.min(WAITS, CACHESIZE);waitTime++)
        {
            simulate(waitTime+offset, startPos, waitTime, startPos, endPos);
        }
        return Arrays.stream(RESULTS).min().getAsInt();
    }

    private static void simulate(int round, Position currentPos, int initialWait, final Position startPos, final Position endPos)
    {
        if (round>0)
        {
            Set<Position> oldPos = BLIZZARDS[(round - 1) % CACHESIZE];
            if (oldPos.contains(currentPos))
            {
                throw new IllegalStateException("there was a blizzard on our previous position, round="+round+", pos="+currentPos);
            }
        }
        // we track the best way in that global array, so we don't need to return it
        // when we reach the exit, store the current round if its better than the best way so far
        if (currentPos.equals(endPos))
        {
            RESULTS[initialWait]=Math.min(RESULTS[initialWait], round);
            System.out.println("found exit, current best is "+RESULTS[initialWait]);
            return;
        }
        // round + manhattan distance must beat the best way, so we can terminate early
        int dist= Math.abs(endPos.row-currentPos.row)+ Math.abs(endPos.col-currentPos.col);
        if (round+dist>=RESULTS[initialWait])
        {
            return;
        }

        // the blizzards have a period of (w-2)*(h-2) (when both hor. and vert. blizzards are at their start)
        // so we should not be in the same position after such a period, because we are then in a loop
        // but then it seems way are at the exit much earlier, so we don't need this check

        // we cannot use a set of blizzards directly, since the set might have fewer entries than we have blizzards
        // (because blizzards can overlap)

        // check whether we have another branch visiting the same grid position in the same round
        // if so, we can stop here
        // since the round is the final result, neither of the branch is better than the other, we
        // just skip duplicate work

        String key = round + "-" + currentPos.row + "-" + currentPos.col;
        // this is an atomic set
        // if it returns some other than null, there was a mapping before
        if (null != CACHE.putIfAbsent(key, key))
        {
            return;
        }

        // use memoized set of blizzard positions
        Set<Position> blizzards = BLIZZARDS[round% CACHESIZE];

        // heuristic: prefer going down or right to get closer to the exit
        // do up and left only when its needed (they will exit early when they are too worse)
        // for this part, we switch this strategy depending on where the start is
        // (when going upwards, we obviously prefer up and left
        if (startPos.row==0) // we go down
        {
            // when we can go down to the end, do it (the exit will always be free)
            // the condition below will otherwise skip this move as its into the last row
            if (currentPos.row == endPos.row - 1 && currentPos.col == endPos.col)
            {
                // we are also done then, there is nothing better
                simulate(round + 1, new Position(currentPos.row + 1, currentPos.col), initialWait, startPos, endPos);
                return;
            }

            // simulate going down when possible
            Position downPos = new Position(currentPos.row + 1, currentPos.col);
            if (downPos.row < height - 1 && !blizzards.contains(downPos))
            {
                simulate(round + 1, downPos, initialWait, startPos, endPos);
            }
            // when the first step from the start does not work, return - we will handle this in another simulation run
            if (currentPos.equals(startPos))
            {
                return;
            }


            // simulate going right when possible
            Position rightPos = new Position(currentPos.row, currentPos.col + 1);
            if (rightPos.col < width - 1 && !blizzards.contains(rightPos))
            {
                simulate(round + 1, rightPos, initialWait, startPos, endPos);
            }

            // simulate going up when possible
            Position upPos = new Position(currentPos.row - 1, currentPos.col);
            // don't ever go back to start
            if (!upPos.equals(startPos) && upPos.row > 0 && !blizzards.contains(upPos))
            {
                simulate(round + 1, upPos, initialWait, startPos, endPos);
            }

            // simulate going left when possible
            Position leftPos = new Position(currentPos.row, currentPos.col - 1);
            if (leftPos.col > 0 && !blizzards.contains(leftPos))
            {
                simulate(round + 1, leftPos, initialWait, startPos, endPos);
            }
        }
        else
        {
            // when we can go up to the end, do it (the exit will always be free)
            if (currentPos.row == endPos.row + 1 && currentPos.col == endPos.col)
            {
                // we are also done then, there is nothing better
                simulate(round + 1, new Position(currentPos.row - 1, currentPos.col), initialWait, startPos, endPos);
                return;
            }

            // simulate going up when possible
            Position upPos = new Position(currentPos.row - 1, currentPos.col);
            // don't ever go back to start
            if (!upPos.equals(startPos) && upPos.row > 0 && !blizzards.contains(upPos))
            {
                simulate(round + 1, upPos, initialWait, startPos, endPos);
            }

            // when the first step from the start does not work, return - we will handle this in another simulation run
            if (currentPos.equals(startPos))
            {
                return;
            }

            // simulate going left when possible
            Position leftPos = new Position(currentPos.row, currentPos.col - 1);
            if (leftPos.col > 0 && !blizzards.contains(leftPos))
            {
                simulate(round + 1, leftPos, initialWait, startPos, endPos);
            }

            // simulate going down when possible
            Position downPos = new Position(currentPos.row + 1, currentPos.col);
            if (downPos.row < height - 1 && !blizzards.contains(downPos))
            {
                simulate(round + 1, downPos, initialWait, startPos, endPos);
            }

            // simulate going right when possible
            Position rightPos = new Position(currentPos.row, currentPos.col + 1);
            if (rightPos.col < width - 1 && !blizzards.contains(rightPos))
            {
                simulate(round + 1, rightPos, initialWait, startPos, endPos);
            }

        }

        // simulate standing still (as long as we left the start already, this will be handled at the outer call)
        if (!currentPos.equals(startPos) && !blizzards.contains(currentPos))
        {
            simulate(round+1, currentPos, initialWait, startPos, endPos);
        }
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

    private static class Blizzard
    {
        private final Position pos;
        private final Dir dir;

        public Blizzard(int row, int col, String dirF)
        {
            pos = new Position(row, col);
            switch(dirF)
            {
                case ">": dir= Dir.R; break;
                case "<": dir= Dir.L; break;
                case "v": dir= Dir.D; break;
                case "^": dir= Dir.U; break;
                default: throw new IllegalArgumentException("unknown direction "+dirF);
            }
        }

        public Blizzard(int row, int col, Dir dir)
        {
            pos=new Position(row, col);
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
                return new Blizzard(pos.row-1, pos.col, dir);
            }
            else
            {
                return new Blizzard(height -2, pos.col, dir);
            }
        }

        private Blizzard moveDown()
        {
            if (pos.row< height -2)
            {
                return new Blizzard(pos.row+1, pos.col, dir);
            }
            else
            {
                return new Blizzard(1, pos.col, dir);
            }
        }

        private Blizzard moveLeft()
        {
            if (pos.col>1)
            {
                return new Blizzard(pos.row, pos.col-1, dir);
            }
            else
            {
                return new Blizzard(pos.row, width-2, dir);
            }
        }

        private Blizzard moveRight()
        {
            if (pos.col<width-2)
            {
                return new Blizzard(pos.row, pos.col+1, dir);
            }
            else
            {
                return new Blizzard(pos.row, 1, dir);
            }
        }
    }
}
