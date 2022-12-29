package de.hendriklipka.aoc2022.day15;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * use a BitSet instead of a boolean array, while still brute-forcing the solution.
 * This is about 15 times faster than the 'boolean array' version
 */
public class Day152b
{
    private static final int MAX_COORD = 4000000;

    public static void main(String[] args)
    {
        try
        {
            List<Scanner> scanners = AocParseUtils.getLines("day15")
                                                  .stream()
                                                  .map(Day152b::parseScanner)
                                                  .collect(Collectors.toList());
            int beaconX = -1;
            int beaconY = -1;
            // brute-force all Y coordinates in the search space
            // might profit from multiple threads
            // another faster version: for each row, handle ranges of potential places, and just change the ranges for each scanner
            // when the ranges are empty afterwards there is no distress signal
            long start=System.currentTimeMillis();
            BitSet testRow = new BitSet(MAX_COORD+1);
            for (int rowNum = 0; rowNum <= MAX_COORD; rowNum++)
            {
                testRow.clear();
                if (0==(rowNum%1000))
                {
                    System.out.println(rowNum);
                }
                setKnownLocations(testRow, scanners, rowNum);
                int clearBit=testRow.nextClearBit(0);
                if (MAX_COORD+1 > clearBit)
                {
                    beaconY = rowNum;
                    beaconX = clearBit;
                    break;
                }
            }
            System.out.println(beaconX);
            System.out.println(beaconY);
            final long result = (long) beaconX * 4000000L + (long) beaconY;
            System.out.println(result);
            // results must be X=3257428, Y=2573243
            if (result == 13029714573243L)
            {
                System.out.println("OK");
            }
            else
            {
                System.out.println("Wrong");
            }
            System.out.println("time needed: "+(System.currentTimeMillis()-start)/1000+"s");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void setKnownLocations(BitSet testRow, List<Scanner> scanners, final int line)
    {
        // fill all the covered locations
        for (Scanner scanner : scanners)
        {
            setBeacon(scanner, testRow, line);
        }
    }

    private static void setBeacon(Scanner scanner, BitSet row, int line)
    {
        // when the scanner is too far away, ignore it
        final int rowDist = Math.abs(line - scanner.getY());
        if (rowDist > scanner.getDistance())
        {
            return;
        }
        int remDist = scanner.getDistance() - rowDist;
        // limit to search space
        final int xFrom = Math.max(scanner.getX() - remDist, 0);
        final int xTo = Math.min(scanner.getX() + remDist, MAX_COORD);
        row.set(xFrom, xTo+1, true);
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
