package de.hendriklipka.aoc2022.day12;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 11.12.22
 * Time: 22:27
 */
public class Day121
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
            int startX=0;
            int startY=0;
            int[][] field = new int[width][height];
            int[][] path = new int[width][height];
            for (int x=0;x<width;x++)
            {
                for (int y=0;y<height;y++)
                {
                    path[x][y]=Integer.MAX_VALUE;
                    char c=fieldData.get(y).get(x).charAt(0);
                    if (c=='S')
                    {
                        startX=x;
                        startY=y;
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
            findPath(startX, startY, field, path, 0);
            dump(path);
            System.out.println(path[endX][endY]);
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
