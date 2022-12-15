package de.hendriklipka.aoc2022.day15;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 15.12.22
 * Time: 07:58
 */
public class Day151
{
    private static final int LINE = 2000000;

    public static void main(String[] args)
    {
        try
        {
            List<Scanner> scanners = AocParseUtils.getLines("day15")
                                                  .stream()
                                                  .map(Day151::parseScanner)
                                                  .collect(Collectors.toList());
            // find out what's the most left and right X position we will ever look at (no matter which row)
            int xMin = 0;
            int xMax = 0;
            for (Scanner scanner : scanners)
            {
                int min = scanner.getX() - scanner.getDistance();
                int max = scanner.getX() + scanner.getDistance();
                if (xMin > min)
                {
                    xMin = min;
                }
                if (xMax < max)
                {
                    xMax = max;
                }
            }
            boolean[] testRow = new boolean[xMax - xMin];
            Arrays.fill(testRow, true); // set row having the distress beacon, potentially
            for (Scanner scanner : scanners)
            {
                // and fill in all covered locations
                setBeacon(scanner, testRow, xMin, LINE);
            }

            int count=0;
            for (boolean b : testRow)
            {
                if (!b)
                {
                    count++;
                }
            }
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void setBeacon(Scanner scanner, boolean[] row, int xOffset, int line)
    {
        // when the scanner is too far away, ignore it
        final int rowDist = Math.abs(line - scanner.getY());
        if (rowDist >scanner.getDistance())
        {
            System.out.println("ignore "+scanner);
            return;
        }
        System.out.println("looking at "+scanner);
        int remDist = scanner.getDistance() - rowDist;
        for (int x=scanner.getX()-remDist; x<scanner.getX()+remDist;x++)
        {
            row[x-xOffset]=false;
        }
    }

    private static Scanner parseScanner(String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line,
                "Sensor at x=(\\-?\\d+), y=(\\-?\\d+): closest beacon is at x=(\\-?\\d+), y=(\\-?\\d+)");
        return new Scanner(Integer.parseInt(parts.get(0)), Integer.parseInt(parts.get(1)),
                Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)));
    }

    private static class Scanner
    {

        private final int x;
        private final int y;
        private final int beaconX;
        private final int beaconY;

        public Scanner(int x, int y, int beaconX, int beaconY)
        {

            this.x = x;
            this.y = y;
            this.beaconX = beaconX;
            this.beaconY = beaconY;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getBeaconX()
        {
            return beaconX;
        }

        public int getBeaconY()
        {
            return beaconY;
        }

        public int getDistance()
        {
            return Math.abs(x - beaconX) + Math.abs(y - beaconY);
        }

        @Override
        public String toString()
        {
            return "Scanner{" +
                    "x=" + x +
                    ", y=" + y +
                    ", beaconX=" + beaconX +
                    ", beaconY=" + beaconY +
                    ", dist=" + getDistance() +
                    '}';
        }
    }
}
