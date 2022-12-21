package de.hendriklipka.aoc2022.day21;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: hli
 * Date: 21.12.22
 * Time: 07:51
 */
public class Day211
{
    public static void main(String[] args)
    {
        try
        {
            List<String> monkeys = AocParseUtils.getLines("day21");
            Map<String, Long> numbers = new HashMap<>();
            Set<MonkeyOp> unsolved = new HashSet<>();
            Pattern numMonkey = Pattern.compile("([a-z]+): (\\d+)");
            // read all monkey tasks and parse them
            for (String line : monkeys)
            {
                final Matcher matcher = numMonkey.matcher(line);
                if (matcher.matches())
                {
                    String monkey = matcher.group(1);
                    Long num = Long.parseLong(matcher.group(2));
                    numbers.put(monkey, num);
                }
                else
                {
                    List<String> parts = AocParseUtils.parsePartsFromString(line,
                            "([a-z]+): ([a-z]+) ([\\+\\-\\*\\/]) ([a-z]+)");
                    unsolved.add(new MonkeyOp(parts.get(0), parts.get(2), parts.get(1), parts.get(3)));
                }
            }
            while (!numbers.containsKey("root"))
            {
                boolean solvedOne = false;
                for (MonkeyOp monkey : unsolved)
                {
                    if (monkey.isSolvable(numbers.keySet()))
                    {
                        solvedOne = true;
                        numbers.put(monkey.monkey, monkey.getNumber(numbers));
                        unsolved.remove(monkey);
                        break;
                    }
                }
                if (!solvedOne)
                {
                    System.out.println("did not find any solvable monkey in " + unsolved + "\n with "+numbers);
                }
            }
            System.out.println(numbers.get("root"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static class MonkeyOp
    {
        String monkey;
        String left;
        String right;
        String op;

        public MonkeyOp(String monkey, String op, String left, String right)
        {

            this.monkey = monkey;
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public boolean isSolvable(Set<String> numbers)
        {
            return numbers.contains(left) && numbers.contains(right);
        }

        public long getNumber(Map<String, Long> numbers)
        {
            long lNum = numbers.get(left);
            long rNum = numbers.get(right);
            switch (op)
            {
                case "+":
                    return lNum + rNum;
                case "-":
                    return lNum - rNum;
                case "*":
                    return lNum * rNum;
                case "/":
                    return lNum / rNum;
            }
            throw new IllegalStateException("unknown op " + op);
        }

        @Override
        public String toString()
        {
            return "MonkeyOp{" +
                    "monkey='" + monkey + '\'' +
                    ", " + left + " " + op + " " + right +
                    '}';
        }
    }
}
