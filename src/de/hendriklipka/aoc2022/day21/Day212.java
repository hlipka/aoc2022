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
public class Day212
{
    public static void main(String[] args)
    {
        try
        {
            List<String> monkeys = AocParseUtils.getLines("day21");
            Map<String, Long> numbers1 = new HashMap<>();
            Set<MonkeyOp> unsolved1 = new HashSet<>();
            Map<String, MonkeyOp> tasks = new HashMap<>();
            Pattern numMonkey = Pattern.compile("([a-z]+): (\\d+)");
            String rootLeft = null;
            String rootRight = null;
            // read all monkey tasks and parse them
            for (String line : monkeys)
            {
                final Matcher matcher = numMonkey.matcher(line);
                if (matcher.matches())
                {
                    String monkey = matcher.group(1);
                    Long num = Long.parseLong(matcher.group(2));
                    numbers1.put(monkey, num);
                }
                else
                {
                    List<String> parts = AocParseUtils.parsePartsFromString(line,
                            "([a-z]+): ([a-z]+) ([\\+\\-\\*\\/]) ([a-z]+)");
                    final String monkey = parts.get(0);
                    final String left = parts.get(1);
                    final String right = parts.get(3);
                    if (monkey.equals("root"))
                    {
                        rootLeft=left;
                        rootRight=right;
                        continue;
                    }
                    MonkeyOp task = new MonkeyOp(monkey, parts.get(2), left, right);
                    unsolved1.add(task);
                    tasks.put(monkey, task);
                }
            }
            // we don't have our own number
            numbers1.remove("humn");
            // resolve anything we know
            Map<String, Long> numbers = new HashMap<>(numbers1);
            Set<MonkeyOp> unsolved = new HashSet<>(unsolved1);
            while (true)
            {
                boolean solvedOne = false;
                for (Day212.MonkeyOp monkey : unsolved)
                {
                    if (monkey.isSolvable(numbers.keySet()))
                    {
                        solvedOne = true;
                        numbers.put(monkey.monkey, monkey.getNumber(numbers));
                        unsolved.remove(monkey);
                        break;
                    }
                }
                // stop when we have resolved anything we can
                if (!solvedOne)
                {
                    break;
                }
            }
            // we assume that one of the numbers is known
            long known;
            String unknown;
            if (numbers.containsKey(rootLeft))
            {
                // need to resolve right
                known = numbers.get(rootLeft);
                unknown=rootRight;
            }
            else if (numbers.containsKey(rootRight))
            {
                // need to resolve left
                known = numbers.get(rootRight);
                unknown=rootLeft;
            }
            else
            {
                System.err.println("both numbers are not known");
                return;
            }
            System.out.println(unsolved);
            // so we resolve the other one, recursively, backwards
            System.out.println(known);
            System.out.println(unknown);
            solveNumber(unknown, known, numbers, tasks);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void solveNumber(String unknownNumber, long knownResult, Map<String, Long> numbers, Map<String, MonkeyOp> tasks)
    {
        MonkeyOp monkey=tasks.get(unknownNumber);
        // which of the two numbers is not known?
        String nextUnknown = monkey.getUnknownNumber(numbers);
        // reverse the operation to know what the missing number needs to have as a value
        long nextResult = monkey.getNeededValue(numbers, knownResult);
        String knownNumberName=monkey.getKnownNumber(numbers);
        long knownValue=numbers.get(knownNumberName);
        System.out.println("result ["+monkey+"] to need "+nextResult+" from "+nextUnknown+", known value of "+knownNumberName+" is "+knownValue);

        // is the missing number our own own?
        if (nextUnknown.equals("humn"))
        {
            System.out.println("human number is "+nextResult);
            return;
        }
        // let's determine the next number in the chain
        solveNumber(nextUnknown, nextResult, numbers, tasks);

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

        public String getUnknownNumber(Map<String, Long> numbers)
        {
            if (!numbers.containsKey(left))
            {
                return left;
            }
            if (!numbers.containsKey(right))
            {
                return right;
            }
            throw new IllegalStateException("already know both numbers");
        }

        public String getKnownNumber(Map<String, Long> numbers)
        {
            if (numbers.containsKey(left))
            {
                return left;
            }
            if (numbers.containsKey(right))
            {
                return right;
            }
            return null;
        }

        // the reverse operations
        public long getNeededValue(Map<String, Long> numbers, long knownResult)
        {
            if (!numbers.containsKey(left))
            {
                long right=numbers.get(this.right);
                switch (op)
                {
                    case "+":
                        return knownResult - right;
                    case "-":
                        return knownResult + right;
                    case "*":
                        return knownResult / right;
                    case "/":
                        return knownResult * right;
                }
            }
            if (!numbers.containsKey(right))
            {
                long left = numbers.get(this.left);
                switch (op)
                {
                    case "+":
                        return knownResult - left;
                    case "-":
                        return left - knownResult;
                    case "*":
                        return knownResult / left;
                    case "/":
                        return left / knownResult;
                }
            }
            throw new IllegalStateException("already know both numbers");
        }
    }
}
