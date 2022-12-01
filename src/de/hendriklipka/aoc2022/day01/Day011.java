package de.hendriklipka.aoc2022.day01;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Day011
{

    public static void main(String[] args) {
        try {
            List<String> lines = FileUtils.readLines(new File("data/day01.txt"), StandardCharsets.UTF_8);

            List<List<Integer>> food = new ArrayList<>();
            List<Integer> current = new ArrayList<>();
            food.add(current);
            for (String line : lines) {
                if (StringUtils.isBlank(line)) {
                    current = new ArrayList<>();
                    food.add(current);
                    continue;
                }
                current.add(Integer.parseInt(line));
            }
            int max = 0;
            for (List<Integer> calories : food) {
                int cals = calories.stream().reduce(0, Integer::sum);
                if (cals > max) {
                    max = cals;
                }
            }
            System.out.println(max);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
