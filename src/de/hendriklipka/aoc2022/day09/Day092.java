package de.hendriklipka.aoc2022.day09;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day092
{
    static int[][] rope=new int[10][2];

    static Set<String> visited = new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            List<String> moves = AocParseUtils.getLines("day09");
            visit(rope[9][0], rope[9][1]);
            for (String move : moves)
            {
                moveHead(move);
            }
            System.out.println(visited.size());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void moveHead(String move)
    {
        String[] m = move.split(" ");
        String dir = m[0];
        long dist = Long.parseLong(m[1]);
        for (long i = 0; i < dist; i++)
        {
            switch (dir)
            {
                case "R":
                    moveRight();
                    break;
                case "L":
                    moveLeft();
                    break;
                case "U":
                    moveUp();
                    break;
                case "D":
                    moveDown();
                    break;
            }
            System.out.println("rope:");
            for (int[] knot: rope)
                System.out.println("x="+knot[0]+", y="+knot[1]);
        }
    }

    private static void moveRight()
    {
        rope[0][0] += 1;
        followTail();
    }

    private static void moveLeft()
    {
        rope[0][0] -= 1;
        followTail();
    }

    private static void moveUp()
    {
        rope[0][1] += 1; // origin is at bottom left - it's a real grid, not an image
        followTail();
    }

    private static void moveDown()
    {
        rope[0][1] -= 1;
        followTail();
    }

    private static void followTail()
    {
        for (int i=1;i<10;i++)
        {
            int[] knot = rope[i];
            long diffX = rope[i-1][0] - knot[0];
            long diffY = rope[i-1][1] - knot[1];
            // same
            if (diffX==0 && diffY==0)
            {
                return;
            }
            if (diffX==0)
            {
                if (Math.abs(diffY)==1)
                {
                    return;
                }
                knot[1]+=diffY>0?1:-1;
            }
            else if (diffY==0)
            {
                if (Math.abs(diffX)==1)
                {
                    return;
                }
                knot[0]+=diffX>0?1:-1;
            }
            else
            {
                if (Math.abs(diffX)>1 || Math.abs(diffY)>1)
                {
                    knot[0] += diffX > 0 ? 1 : -1;
                    knot[1] += diffY > 0 ? 1 : -1;
                }
            }
        }

        visit(rope[9][0], rope[9][1]);
    }

    private static void visit(long x_t, long y_t)
    {
        visited.add("" + x_t + "-" + y_t);
    }
}
