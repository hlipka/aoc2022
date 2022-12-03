package de.hendriklipka.aoc2022.day03;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 03.12.22
 * Time: 11:12
 */
public class Day031
{
    public static void main(String[] args)
    {
        try
        {
            List<String> packs = AocParseUtils.getLines("day03");
            List<String> dupes = packs.stream().map(Day031::findDupes).flatMap(List::stream).collect(Collectors.toList());
            int sum = dupes.stream().mapToInt(Day031::score).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int score(final String s)
    {
        char c=s.charAt(0);
        if (c>='a')
        {
            return c-'a'+1;
        }
        return c-'A'+27;
    }

    private static List<String> findDupes(final String line)
    {
        List<String> items = line.chars().mapToObj(c->String.valueOf((char)c)).collect(Collectors.toList());
        final int middle = items.size() / 2;
        List<String> left = items.subList(0, middle);
        List<String> right = items.subList(middle, items.size());
        return ListUtils.intersection(left, right);
    }
}
