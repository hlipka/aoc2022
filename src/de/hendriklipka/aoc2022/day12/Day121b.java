package de.hendriklipka.aoc2022.day12;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.ArrayWorld;

import java.io.IOException;
import java.util.List;

/**
 * Same as Day121, but using a generic A* search
 */
public class Day121b
{
    static int endX=0;
    static int endY=0;

    public static void main(String[] args)
    {
        try
        {
            List<List<String>> fieldData = AocParseUtils.getLinesAsChars("day12");
            HillWorld world = new HillWorld(fieldData);

            AStarSearch search = new AStarSearch(world);
            int length = search.findPath();
            System.out.println(length);
            if (length==534)
            {
                System.out.println("OK");
            }
            else
            {
                System.out.println("Wrong");
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    static class HillWorld implements ArrayWorld
    {
        private final int width;
        private final int height;
        private int startX;
        private int startY;
        private final int[][] field;

        public HillWorld(List<List<String>> fieldData)
        {
            width = fieldData.get(0).size();
            height = fieldData.size();
            startX = 0;
            startY = 0;
            field = new int[width][height];
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    char c = fieldData.get(y).get(x).charAt(0);
                    if (c == 'S')
                    {
                        startX = x;
                        startY = y;
                    }
                    else if (c == 'E')
                    {
                        field[x][y] = 25;
                        endX = x;
                        endY = y;
                    }
                    else
                    {
                        field[x][y] = c - 'a';
                    }
                }
            }

        }

        @Override
        public int getWidth()
        {
            return width;
        }

        @Override
        public int getHeight()
        {
            return height;
        }

        @Override
        public boolean canMoveTo(int oldX, int oldY, int x, int y)
        {
            if (x < 0)
                return false;
            if (y < 0)
                return false;
            if (x >= field.length)
                return false;
            if (y >= field[0].length)
                return false;

            int elevation = field[oldX][oldY];
            return elevation + 1 >= field[x][y];
        }

        @Override
        public int getEndX()
        {
            return endX;
        }

        @Override
        public int getEndY()
        {
            return endY;
        }

        @Override
        public int getStartX()
        {
            return startX;
        }

        @Override
        public int getStartY()
        {
            return startY;
        }
    }
}
