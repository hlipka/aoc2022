package de.hendriklipka.aoc2022.day24;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Day241
{
    private static int width;
    private static int height;

    private static final Pos START = new Pos(0, 1);
    private static Pos END;

    public static int CACHESIZE = -1;
    static  Set<Pos>[] BLIZZARDS;
    
    static final int WAITS=100;
    static final int[] RESULTS = new int[WAITS];

    static final ConcurrentHashMap<String, String> CACHE=new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException
    {
        List<List<String>> fieldData = AocParseUtils.getLinesAsChars("day24");
        width=fieldData.get(0).size();
        height =fieldData.size();
        CACHESIZE=(width-2)*(height-2); // that's the max period for the blizzards
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
        Arrays.fill(RESULTS, Integer.MAX_VALUE);
        Pos current = new Pos(0, 1);
        END=new Pos(height-1, width-2);
        System.out.println(START);
        System.out.println(END);
        // simulate while waiting at the start, but not for too long
        // (note: this should be longer than width-2, as this is the period of the horizontal row)

        for (int waitTime=0;waitTime<Math.min(WAITS, CACHESIZE);waitTime++)
        {
            System.out.println("start with waitTime "+ waitTime);
            simulate(waitTime, current, waitTime);
            System.out.println("finished simulation for start round "+ waitTime +", best was "+RESULTS[waitTime]);
        }

        System.out.println(Arrays.stream(RESULTS).min().getAsInt());
    }

    private static void simulate(int round, Pos currentPos, int initialWait)
    {
        if (round>0)
        {
            Set<Pos> oldPos = BLIZZARDS[(round - 1) % CACHESIZE];
            if (oldPos.contains(currentPos))
            {
                throw new IllegalStateException("there was a blizzard on our previous position, round="+round+", pos="+currentPos);
            }
        }
        // we track the best way in that global array, so we don't need to return it
        // when we reach the exit, store the current round if its better than the best way so far
        if (currentPos.equals(END))
        {
            RESULTS[initialWait]=Math.min(RESULTS[initialWait], round);
            System.out.println("found exit, current best is "+RESULTS[initialWait]);
            return;
        }
        // round + manhattan distance must beat the best way, so we can terminate early
        int dist=END.row-currentPos.row+END.col-currentPos.col;
        if (round+dist>=RESULTS[initialWait])
        {
            return;
        }

        // the blizzards have a period of (w-2)*(h-2) (when both hor. and vert. blizzards are at their start)
        // so we should not be in the same position after such a period, because we are then in a loop
        // but then it seems way are at the exit much earlier, so we don't need this check

        // we cannot use a set of blizzards directly, since the set might have fewer entries than we have blizzards
        // (because blizzards can overlap)

        // when we can go down to the end, do it (the exit will always be free)
        if (currentPos.row==END.row-1 && currentPos.col==END.col)
        {
            // we are also done then, there is nothing better
            simulate(round+1, new Pos(currentPos.row+1, currentPos.col), initialWait);
            return;
        }

        // check whether we have another branch visiting the same grid position in the same round
        // if so, we can stop here
        // since the round is the final result, neither of the branch is better than the other, we
        // just skip duplicate work

        String key = round + "-" + currentPos.row + "-" + currentPos.col;
        // this is an atomic set
        // if it returns some other than null, there was a mapping before
        if (null!=CACHE.putIfAbsent(key, key))
        {
            return;
        }

        // use memoized set of blizzard positions
        Set<Pos> blizzards = BLIZZARDS[round% CACHESIZE];

        // heuristic: prefer going down or right to get closer to the exit
        // do up and left only when its needed (they will exit early when they are too worse)

        // simulate going down when possible
        Pos downPos=new Pos(currentPos.row+1, currentPos.col);
        if (downPos.row<height-1 && !blizzards.contains(downPos))
        {
            simulate(round+1, downPos, initialWait);
        }
        // when the first step downwards does not work, return - we will handle this in another simulation run
        if (currentPos.equals(START))
        {
            return;
        }


        // simulate going right when possible
        Pos rightPos=new Pos(currentPos.row, currentPos.col+1);
        if (rightPos.col<width-1 && !blizzards.contains(rightPos))
        {
            simulate(round+1, rightPos, initialWait);
        }

        // simulate going up when possible
        Pos upPos=new Pos(currentPos.row-1, currentPos.col);
        // don't ever go back to start
        if (!upPos.equals(START) && upPos.row>0 && !blizzards.contains(upPos))
        {
            simulate(round+1, upPos, initialWait);
        }

        // simulate going left when possible
        Pos leftPos=new Pos(currentPos.row, currentPos.col-1);
        if (leftPos.col>0 && !blizzards.contains(leftPos))
        {
            simulate(round+1, leftPos, initialWait);
        }

        // simulate standing still (as long as we left the start already, this will be handled at the outer call)
        if (!currentPos.equals(START) && !blizzards.contains(currentPos))
        {
            simulate(round+1, currentPos, initialWait);
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

        public Blizzard(int row, int col, String dirF)
        {
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

        public Blizzard(int row, int col, Dir dir)
        {
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
