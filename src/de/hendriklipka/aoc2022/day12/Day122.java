package de.hendriklipka.aoc2022.day12;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 11.12.22
 * Time: 22:27
 */
public class Day122
{
    static int endX=0;
    static int endY=0;

    public static void main(String[] args)
    {
        try
        {
            List<List<String>> fieldData = AocParseUtils.getLinesAsChars("day12");
            int width=fieldData.get(0).size();
            int height=fieldData.size();
            int[][] field = new int[width][height];
            int[][] path = new int[width][height];
            List<int[]> startPoints=new ArrayList<>();

            for (int x=0;x<width;x++)
            {
                for (int y=0;y<height;y++)
                {
                    char c=fieldData.get(y).get(x).charAt(0);
                    if (c=='S' || c=='a')
                    {
                        field[x][y]=0;
                        int[] start={x,y};
                        startPoints.add(start);
                    }
                    else if (c=='E')
                    {
                        field[x][y]= 25;
                        endX=x;
                        endY=y;
                    }
                    else
                    {
                        field[x][y]=c-'a';
                    }
                }
            }
            dump(field);
            int minPath=Integer.MAX_VALUE;
            // brute force with all potential start points
            for (int[] start: startPoints)
            {
                // clear path
                for (int x=0;x<width;x++)
                {
                    for (int y=0;y<height;y++)
                    {
                        path[x][y]=Integer.MAX_VALUE;
                    }
                }
                findPath(start[0], start[1], field, path, 0);
                int length = path[endX][endY];
                if (length<minPath)
                {
                    minPath=length;
                }
            }
            System.out.println(minPath);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void findPath(int x, int y, int[][] field, int[][] path, int length)
    {
        int oldLength = path[x][y];
        if (length<oldLength)
        {
            path[x][y]=length;
        }
        else
        {
            return;
        }
        if (x==endX && y==endY)
        {
            return;
        }

        int elevation=field[x][y];
        if (canGo(x-1,y, elevation, field))
        {
            findPath(x-1,y,field,path, length+1);
        }
        if (canGo(x+1,y, elevation, field))
        {
            findPath(x+1,y,field,path, length+1);
        }
        if (canGo(x,y-1, elevation, field))
        {
            findPath(x,y-1,field,path, length+1);
        }
        if (canGo(x,y+1, elevation, field))
        {
            findPath(x,y+1,field,path, length+1);
        }
    }

    private static boolean canGo(int x, int y, int elevation, int[][] field)
    {
        if (x<0)
            return false;
        if (y<0)
            return false;
        if (x>=field.length)
            return false;
        if (y>=field[0].length)
            return false;
        if (elevation+1<field[x][y])
            return false;
        return true;
    }

    private static void dump(int[][] field)
    {
        System.out.println("------------");
        for (int y=0;y<field[0].length;y++)
        {
            for (int x=0;x<field.length;x++)
            {
                System.out.print((field[x][y])+",");
            }
            System.out.println();
        }
    }
}
