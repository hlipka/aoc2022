package de.hendriklipka.aoc2022.day08;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

public class Day082
{
    public static void main(String[] args)
    {
        try
        {
            List<List<Integer>> forrest = AocParseUtils.getLinesAsDigits("day08");
            int maxScore=0;
            for (int i=0;i<forrest.size();i++)
            {
                List<Integer> row = forrest.get(i);
                for (int j=0;j<row.size();j++)
                {
                    int score = getScore(forrest, i, j, row.get(j));
                    if (score>maxScore)
                    {
                        maxScore=score;
                    }
                }
            }
            System.out.println(maxScore);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int getScore(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        int scoreLeft = getScoreLeft(forrest, row, col, height);
        int scoreRight = getScoreRight(forrest, row, col, height);
        int scoreUp = getScoreUp(forrest, row, col, height);
        int scoreDown = getScoreDown(forrest, row, col, height);
        return scoreLeft * scoreRight * scoreUp * scoreDown;
    }

    private static int getScoreRight(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (col==0)
        {
            return 0;
        }
        List<Integer> treeRow = forrest.get(row);
        for (int i=col+1;i<treeRow.size();i++)
        {
            if (treeRow.get(i)>=height)
            {
                return i-col;
            }
        }
        return treeRow.size()-col-1;
    }

    private static int getScoreLeft(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        List<Integer> treeRow = forrest.get(row);
        if (col==treeRow.size()-1)
        {
            return 0;
        }
        for (int i=col-1;i>=0;i--)
        {
            if (treeRow.get(i)>=height)
            {
                return col-i;
            }
        }
        return col;
    }

    private static int getScoreDown(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (row==0)
        {
            return 0;
        }
        for (int i=row+1;i<forrest.size();i++)
        {
            if (forrest.get(i).get(col)>=height)
            {
                return i-row;
            }
        }
        return forrest.size()-row-1;
    }

    private static int getScoreUp(List<List<Integer>> forrest, int row, int col, Integer height)
    {
        if (row==forrest.size()-1)
        {
            return 0;
        }
        for (int i=row-1;i>=0;i--)
        {
            if (forrest.get(i).get(col)>=height)
            {
                return row-i;
            }
        }
        return row;
    }
}
