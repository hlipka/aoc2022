package de.hendriklipka.aoc2022.day09;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day091
{
    static long xHead = 0;
    static long yHead = 0;
    static long xTail = 0;
    static long yTail = 0;

    static Set<String> visited = new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            List<String> moves = AocParseUtils.getLines("day09");
            visit(xTail, yTail);
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
            System.out.println("xH="+xHead+", yH="+yHead+", xT="+xTail+", yT="+yTail);
        }
    }

    private static void moveRight()
    {
        xHead += 1;
        followTail();
    }

    private static void moveLeft()
    {
        xHead -= 1;
        followTail();
    }

    private static void moveUp()
    {
        yHead += 1; // origin is at bottom left - it's a real grid, not an image
        followTail();
    }

    private static void moveDown()
    {
        yHead -= 1;
        followTail();
    }

    private static void followTail()
    {
        long diffX = xHead - xTail;
        long diffY = yHead - yTail;
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
            yTail+=diffY>0?1:-1;
        }
        else if (diffY==0)
        {
            if (Math.abs(diffX)==1)
            {
                return;
            }
            xTail+=diffX>0?1:-1;
        }
        else
        {
            if (Math.abs(diffX)>1 || Math.abs(diffY)>1)
            {
                xTail += diffX > 0 ? 1 : -1;
                yTail += diffY > 0 ? 1 : -1;
            }
        }
        visit(xTail, yTail);
    }

    private static void visit(long x_t, long y_t)
    {
        visited.add("" + x_t + "-" + y_t);
    }
}
