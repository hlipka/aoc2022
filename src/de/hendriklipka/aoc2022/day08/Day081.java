package de.hendriklipka.aoc2022.day08;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

public class Day081
{
    public static void main(String[] args)
    {
        try
        {
            List<List<Integer>> forrest = AocParseUtils.getLinesAsDigits("day08");
            int visible=0;
            for (int i=0;i<forrest.size();i++)
            {
                List<Integer> row = forrest.get(i);
                for (int j=0;j<row.size();j++)
                {
                    if (isVisible(forrest, i, j, row.get(j)))
                    {
                        visible++;
                    }
                }
            }
            System.out.println(visible);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isVisible(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (
                isVisibleLeft(forrest, row, col, height)
                || isVisibleRight(forrest, row, col, height)
                || isVisibleUp(forrest, row, col, height)
                || isVisibleDown(forrest, row, col, height)
        )
        {
            return true;
        }
        return false;
    }

    private static boolean isVisibleRight(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (col==0)
        {
            return true;
        }
        List<Integer> treeRow = forrest.get(row);
        for (int i=col+1;i<treeRow.size();i++)
        {
            if (treeRow.get(i)>=height)
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isVisibleLeft(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        List<Integer> treeRow = forrest.get(row);
        if (col==treeRow.size()-1)
        {
            return true;
        }
        for (int i=0;i<col;i++)
        {
            if (treeRow.get(i)>=height)
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isVisibleDown(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (row==0)
        {
            return true;
        }
        for (int i=row+1;i<forrest.size();i++)
        {
            if (forrest.get(i).get(col)>=height)
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isVisibleUp(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (row==forrest.size()-1)
        {
            return true;
        }
        for (int i=0;i<row;i++)
        {
            if (forrest.get(i).get(col)>=height)
            {
                return false;
            }
        }
        return true;
    }
}
