package de.hendriklipka.aoc2022.day01;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Day012
{

    public static void main(String[] args) {
        try {
            List<List<Integer>> food = AocParseUtils.getIntegerBlocks("day01");
            int calories = food.stream().map(l -> l.stream().reduce(0, Integer::sum)).sorted(Comparator.reverseOrder()).limit(3).reduce(0, Integer::sum);
            System.out.println(calories);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
