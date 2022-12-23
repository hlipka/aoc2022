package de.hendriklipka.aoc2022.day23;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.*;

public class Day231
{
    private static final Elf NOMOVE=new Elf(Integer.MAX_VALUE, Integer.MAX_VALUE);

    public static void main(String[] args)
    {
        try
        {
            List<List<String>> fieldData = AocParseUtils.getLinesAsChars("day23");
            List<Elf> elves = new ArrayList<>();
            for (int row = 0; row < fieldData.size(); row++)
            {
                List<String> rowData = fieldData.get(row);
                for (int col = 0; col < rowData.size(); col++)
                {
                    String field = rowData.get(col);
                    if (field.equals("#"))
                    {
                        elves.add(new Elf(col, row));
                    }
                }
            }
            int elfCount = elves.size();

            dumpElves(elves);

            for (int round = 0; round < 10; round++)
            {
                simulateRound(round, elves);
                dumpElves(elves);
            }

            int minX=Integer.MAX_VALUE;
            int minY=Integer.MAX_VALUE;
            int maxX=Integer.MIN_VALUE;
            int maxY=Integer.MIN_VALUE;
            for (Elf elf : elves)
            {
                System.out.println(elf);
                minX=Math.min(minX, elf.x);
                minY=Math.min(minY, elf.y);
                maxX=Math.max(maxX, elf.x);
                maxY=Math.max(maxY, elf.y);
            }
            System.out.println("area: x="+minX+"-"+maxX+" , y="+minY+"-"+maxY);
            int area=(maxX-minX+1)*(maxY-minY+1);
            System.out.println(area-elfCount);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void dumpElves(List<Elf> elves)
    {
        int minX=Integer.MAX_VALUE;
        int minY=Integer.MAX_VALUE;
        int maxX=Integer.MIN_VALUE;
        int maxY=Integer.MIN_VALUE;
        for (Elf elf : elves)
        {
            minX=Math.min(minX, elf.x);
            minY=Math.min(minY, elf.y);
            maxX=Math.max(maxX, elf.x);
            maxY=Math.max(maxY, elf.y);
        }
        int yl = maxY - minY + 1;
        int xl = maxX - minX + 1;
        char[][] field=new char[yl][xl];
        for (int x=0;x<xl;x++)
        {
            for (int y=0;y<yl;y++)
            {
                field[y][x]='.';
            }
        }
        for (Elf elf : elves)
        {
            field[elf.y-minY][elf.x-minX]='#';
        }
        for (int y=0;y<yl;y++)
        {
            for (int x=0;x<xl;x++)
            {
                System.out.print(field[y][x]);
            }
            System.out.println();
        }
    }

    private static void simulateRound(int round, List<Elf> elfs)
    {
        System.out.println("doing round "+(round+1));
        System.out.println("consider first: "+Dir.values()[round%4].name());
        Set<Elf> elfPos = new HashSet<>();
        for (Elf elf : elfs)
        {
            elfPos.add(elf);
        }
        Map<Elf, Integer> moves = new HashMap<>();
        // ask all elves to consider their move
        for (Elf elf : elfs)
        {
            Elf target = considerMove(elf, round, elfPos);
            // when the elf has considered a move, we store the intended target
            // a target might be NOMOVE, then we store it (to detect when all are done)
            if (null != target)
            {
                elf.storeMove(target.x, target.y);
                Integer eMove = moves.getOrDefault(target, 0);
                // count how many elves want to move to that target
                moves.put(target, eMove + 1);
            }
            else
            {
                elf.removeMove();
            }
        }
        if (moves.isEmpty())
        {
            System.out.println("no more moves");
            return;
        }
        // ask all elves for where they wanted to move to
        // only when the count for that position is '1', do the move
        for (Elf elf : elfs)
        {
            Elf target = elf.getMove();
            // when the elf has considered a move, we store the intended target
            if (null != target && !target.equals(NOMOVE))
            {
                Integer eMove = moves.get(target);
                if (eMove == 1)
                {
                    // do then move (the elf updates itself)
                    elf.doMove();
                }
            }
        }
    }

    private static Elf considerMove(Elf elf, int round, Set<Elf> elfPos)
    {
        // store where else we have elves
        boolean[] targets=new boolean[9];
        // we store the top left twice to do the rest automatically
        // the 'Dir' knows the offset of thr three fields to look at
        // the origin of the field is on the top left
        targets[0]=elfPos.contains(new Elf(elf.x-1, elf.y-1)); // left up
        targets[1]=elfPos.contains(new Elf(elf.x  , elf.y-1));
        targets[2]=elfPos.contains(new Elf(elf.x+1, elf.y-1)); // right up
        targets[3]=elfPos.contains(new Elf(elf.x+1, elf.y));
        targets[4]=elfPos.contains(new Elf(elf.x+1, elf.y+1)); // right down
        targets[5]=elfPos.contains(new Elf(elf.x  , elf.y+1));
        targets[6]=elfPos.contains(new Elf(elf.x-1, elf.y+1)); // left down
        targets[7]=elfPos.contains(new Elf(elf.x-1, elf.y));
        targets[8]=elfPos.contains(new Elf(elf.x-1, elf.y-1)); // again left up
        boolean needsMove=false;
        // test whether we need to move at all
        for (int i=0;i<8;i++)
        {
            if (targets[i])
            {
                needsMove = true;
                break;
            }
        }
        if (!needsMove)
        {
            return null;
        }
        // consider the 4 directions, starting the one which goes first for the current round
        for (int dir = 0; dir < 4; dir++)
        {
            Dir cDir=Dir.values()[((round%4)+dir)%4];
            boolean isFree=true;
            // check the three target fields for the current direction
            for (int i=0;i<3;i++)
            {
                if (targets[i + cDir.offset])
                {
                    isFree = false;
                    break;
                }
            }
            if (isFree)
            {
                switch (cDir)
                {
                    case N:
                        return new Elf(elf.x, elf.y-1);
                    case S:
                        return new Elf(elf.x, elf.y+1);
                    case W:
                        return new Elf(elf.x-1, elf.y);
                    case E:
                        return new Elf(elf.x+1, elf.y);
                }
            }

        }
        return NOMOVE;
    }

    private enum Dir
    {
        N(0),
        S(4),
        W(6),
        E(2);

        private final int offset;

        Dir(int offset)
        {
            this.offset = offset;
        }
    }

    private static class Elf
    {
        public int x, y;

        public Elf target = null;

        public Elf(int col, int row)
        {
            x = col;
            y = row;
        }

        public void storeMove(int x, int y)
        {
            target = new Elf(x, y);
        }

        public Elf getMove()
        {
            return target;
        }

        public void doMove()
        {
            x = target.x;
            y = target.y;
            target = null;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Elf elf = (Elf) o;

            return new EqualsBuilder().append(x, elf.x).append(y, elf.y).isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(17, 37).append(x).append(y).toHashCode();
        }

        @Override
        public String toString()
        {
            return "Elf{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public void removeMove()
        {
            target=null;
        }
    }
}
