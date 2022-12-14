package de.hendriklipka.aoc2022.day14;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 14.12.22
 * Time: 07:52
 */
public class Day141
{
    static int minX = Integer.MAX_VALUE;
    static int maxX = Integer.MIN_VALUE;
    static int maxY = 0;

    public static void main(String[] args)
    {
        try
        {
            List<List<String>> lines = AocParseUtils.getLines("day14")
                                                    .stream()
                                                    .map(l -> Arrays.asList(
                                                            StringUtils.splitByWholeSeparator(l, " -> "))).collect(
                            Collectors.toList());
            List<List<Pair<Integer, Integer>>> linesOfPoints = lines.stream()
                                                                    .map(Day141::mapLinesOfPoints)
                                                                    .collect(Collectors.toList());
            maxY++; // cave starts at 0
            maxX += 2; // need one row left and right, so the sand can fall down there if it reaches the sides
            char[][] cave = new char[maxX][maxY];
            System.out.println(maxX);
            System.out.println(minX);
            System.out.println(maxY);

            for (int x = 0; x < maxX; x++)
            {
                for (int y = 0; y < maxY; y++)
                {
                    cave[x][y] = '.';
                }
            }

            for (List<Pair<Integer, Integer>> line : linesOfPoints)
            {
                drawLine(line, cave);
            }

            int units = 0;
            boolean fallsDown = false;
            while (!fallsDown)
            {
                units++;
                fallsDown = dropSand(cave);
            }
            dumpCave(cave);
            System.out.println(units-1);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean dropSand(char[][] cave)
    {
        int sandX = 500;
        int sandY = 0;
        while (true)
        {
            if (sandY >= maxY-1)
            {
                return true;
            }
            if (cave[sandX][sandY + 1] == '.')
            {
                sandY++;
            } else if (cave[sandX - 1][sandY + 1] == '.')
            {
                sandY++;
                sandX--;
            } else if (cave[sandX + 1][sandY + 1] == '.')
            {
                sandX++;
                sandY++;
            } else
            {
                cave[sandX][sandY] = '*';
                return false;
            }
        }
    }

    private static void dumpCave(char[][] cave)
    {
        for (int y = 0; y < maxY; y++)
        {
            for (int x = 0; x < maxX; x++)
            {
                System.out.print(cave[x][y]);
            }
            System.out.println();
        }
        System.out.println("--------");
    }

    private static void drawLine(List<Pair<Integer, Integer>> line, char[][] cave)
    {
        Pair<Integer, Integer> from = line.get(0);
        for (int i = 1; i < line.size(); i++)
        {
            Pair<Integer, Integer> to = line.get(i);
            drawLine(from, to, cave);
            from = to;
        }
    }

    private static void drawLine(Pair<Integer, Integer> from, Pair<Integer, Integer> to, char[][] cave)
    {
        int fromX = from.getLeft();
        int toX = to.getLeft();
        int fromY = from.getRight();
        int toY = to.getRight();
        if (fromX == toX)
        {
            drawVertical(fromX, fromY, toY, cave);
        } else if (fromY == toY)
        {
            drawHorizontal(fromY, fromX, toX, cave);
        } else
        {
            throw new IllegalArgumentException("not a straight line: [" + from + "]->[" + to + "]");
        }
    }

    private static void drawHorizontal(int y, int fromX, int toX, char[][] cave)
    {
        if (fromX > toX)
        {
            int h = fromX;
            fromX = toX;
            toX = h;
        }
        for (int x = fromX; x <= toX; x++)
        {
            cave[x][y] = '#';
        }
    }

    private static void drawVertical(int x, int fromY, int toY, char[][] cave)
    {
        if (fromY > toY)
        {
            int h = fromY;
            fromY = toY;
            toY = h;
        }
        for (int y = fromY; y <= toY; y++)
        {
            cave[x][y] = '#';
        }

    }

    private static List<Pair<Integer, Integer>> mapLinesOfPoints(List<String> strings)
    {
        return strings.stream().map(c -> {
            final String[] point = StringUtils.split(c, " ,");
            int x = Integer.parseInt(point[0]);
            if (x > maxX)
            {
                maxX = x;
            }
            if (x < minX)
            {
                minX = x;
            }
            int y = Integer.parseInt(point[1]);
            if (y > maxY)
            {
                maxY = y;
            }

            //noinspection SuspiciousNameCombination
            final ImmutablePair<Integer, Integer> coords = new ImmutablePair<>(x, y);
            System.out.println(coords);
            return coords;
        }).collect(Collectors.toList());
    }

}
