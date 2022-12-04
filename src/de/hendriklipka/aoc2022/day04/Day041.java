package de.hendriklipka.aoc2022.day04;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Range;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 04.12.22
 * Time: 20:03
 */
public class Day041
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocParseUtils.getLines("day04");
            long count = lines.stream().map(Day041::getRanges).filter(Day041::isOverlap).count();
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isOverlap(final Pair<Range, Range> r)
    {
        final Range left = r.getLeft();
        final Range right = r.getRight();
        return left.isInside(right) || right.isInside(left);
    }

    private static Pair<Range, Range> getRanges(final String s)
    {
        String[] ranges = s.split(",");
        final ImmutablePair<Range, Range> pair = new ImmutablePair<>(new Range(ranges[0]), new Range(ranges[1]));
        System.out.println(pair);
        return pair;
    }
}
