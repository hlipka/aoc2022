package de.hendriklipka.aoc2022.day11;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 11.12.22
 * Time: 11:39
 */
public class Day111a
{
    public static void main(String[] args)
    {
        try
        {
            List<List<String>> monkeyData = AocParseUtils.getStringBlocks("day11");
            List<Monkey> monkeys = parseMonkeyData(monkeyData);
            dumpItems(monkeys);
            for (int i=0;i<20;i++)
            {
                handleRound(monkeys);
                dumpItems(monkeys);
            }
            List<Integer> result = monkeys.stream().map(m -> m.inspected).sorted().collect(Collectors.toList());
            System.out.println(result);
            System.out.println(result.get(result.size()-1) * result.get(result.size() - 2));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void dumpItems(List<Monkey> monkeys)
    {
        for (Monkey monkey: monkeys)
        {
            System.out.println(monkey.items);
        }
        System.out.println("----------");
    }

    private static void handleRound(List<Monkey> monkeys)
    {
        for (Monkey monkey: monkeys)
        {
            handleTurn(monkey, monkeys);
        }
    }

    private static void handleTurn(Monkey monkey, List<Monkey> monkeys)
    {
        while (!monkey.items.isEmpty())
        {
            monkey.inspected++;
            int worry = monkey.popFirstItem();
            int newWorry = monkey.operation.apply(worry) / 3;
            if ((newWorry % monkey.divisor) == 0)
            {
                monkeys.get(monkey.toDivisible).pushItem(newWorry);
            }
            else
            {
                monkeys.get(monkey.toNotDivisible).pushItem(newWorry);
            }
        }
    }

    private static List<Monkey> parseMonkeyData(List<List<String>> monkeyData)
    {
        List<Monkey> monkeys = new ArrayList<>();
        for (List<String> aMonkey: monkeyData)
        {
            Monkey monkey = new Monkey();
            final String[] items = StringUtils.split(aMonkey.get(1).substring(18), ',');
            monkey.items.addAll(Arrays.stream(items).map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList()));
            List<String> operation = AocParseUtils.parsePartsFromString(aMonkey.get(2), "  Operation: new = old (.) (.+)");
            String operator = operation.get(0);
            String operand = operation.get(1);
            if (operator.equals("+"))
            {
                if (operand.equals("old"))
                {
                    monkey.operation = o -> o+o;
                }
                else
                {
                    final int operandValue = Integer.parseInt(operand);
                    monkey.operation = o -> o + operandValue;
                }
            }
            else if (operator.equals("*"))
            {
                if (operand.equals("old"))
                {
                    monkey.operation = o -> o * o;
                }
                else
                {
                    final int operandValue = Integer.parseInt(operand);
                    monkey.operation = o -> o * operandValue;
                }
            }
            else
            {
                throw new IllegalArgumentException("unknown operation ["+ operator+"] in "+operation);
            }
            monkey.divisor= AocParseUtils.parseIntFromString(aMonkey.get(3), "  Test: divisible by (\\d+)");
            monkey.toDivisible= AocParseUtils.parseIntFromString(aMonkey.get(4), "    If true: throw to monkey (\\d+)");
            monkey.toNotDivisible= AocParseUtils.parseIntFromString(aMonkey.get(5), "    If false: throw to monkey (\\d+)");
            monkeys.add(monkey);
        }
        return monkeys;
    }

    private static class Monkey
    {
        LinkedList<Integer> items = new LinkedList<>();
        UnaryOperator<Integer> operation;
        int divisor;
        int toDivisible;
        int toNotDivisible;

        int inspected = 0;

        int popFirstItem()
        {
            int i=items.getFirst();
            items.removeFirst();
            return i;
        }

        void pushItem(int item)
        {
            items.add(item);
        }

        @Override
        public String toString()
        {
            return "Monkey{" +
                    "items=" + items +
                    ", operation=" + operation +
                    ", divisor=" + divisor +
                    ", toDivisible=" + toDivisible +
                    ", toNotDivisible=" + toNotDivisible +
                    '}';
        }
    }
}
