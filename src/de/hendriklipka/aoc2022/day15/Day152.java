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
public class Day152
{
    private static final int MAX_COORD = 4000000;

    public static void main(String[] args)
    {
        try
        {
            List<Scanner> scanners = AocParseUtils.getLines("day15")
                                                  .stream()
                                                  .map(Day152::parseScanner)
                                                  .collect(Collectors.toList());
            int beaconX = -1;
            int beaconY = -1;
            // brute-force all Y coordinates in the search space
            // might profit from multiple threads
            // another faster version: for each row, handle ranges of potential places, and just change the ranges for each scanner
            // when the ranges are empty afterwards there is no distress signal
            boolean[] testRow = new boolean[MAX_COORD + 1];
            for (int rowNum = 0; rowNum <= MAX_COORD; rowNum++)
            {
                if (0==(rowNum%1000))
                {
                    System.out.println(rowNum);
                }
                setKnownLocations(testRow, scanners, rowNum);
                for (int i = 0; i <= MAX_COORD; i++)
                {
                    boolean b = testRow[i];
                    if (b)
                    {
                        beaconY = rowNum;
                        beaconX = i;
                        break;
                    }
                }
                if (-1 != beaconY)
                {
                    break;
                }
            }
            System.out.println(beaconX);
            System.out.println(beaconY);
            System.out.println((long)beaconX* 4000000L+(long)beaconY);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void setKnownLocations(boolean[] testRow, List<Scanner> scanners, final int line)
    {
        Arrays.fill(testRow, true); // set row having the distress beacon, potentially
        for (Scanner scanner : scanners)
        {
            // and now fill all the covered locations
            if (setBeacon(scanner, testRow, line))
            {
                break;
            }
        }
    }

    private static boolean setBeacon(Scanner scanner, boolean[] row, int line)
    {
        // when the scanner is too far away, ignore it
        final int rowDist = Math.abs(line - scanner.getY());
        if (rowDist > scanner.getDistance())
        {
            return false;
        }
        int remDist = scanner.getDistance() - rowDist;
        // limit to search space
        final int xFrom = Math.max(scanner.getX() - remDist, 0);
        final int xTo = Math.min(scanner.getX() + remDist, MAX_COORD);
        Arrays.fill(row, xFrom, xTo+1, false);
        // signal early exit when we know the row is fully filled by this scanner
        return xFrom == 0 && xTo == MAX_COORD;
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
