package de.hendriklipka.aoc2022.day03;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 03.12.22
 * Time: 11:12
 */
public class Day032
{
    public static void main(String[] args)
    {
        try
        {
            List<List<String>> packs = AocParseUtils.getLinesAsChars("day03");
            final List<List<List<String>>> groups = ListUtils.partition(packs, 3);
            int sum = groups.stream().map(Day032::findBadge).mapToInt(Day032::score).sum();

            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String findBadge(final List<List<String>> group)
    {
        List<String> candidates = ListUtils.intersection(group.get(0), group.get(1));
        candidates = ListUtils.intersection(candidates, group.get(2));
        return candidates.get(0);
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
}
