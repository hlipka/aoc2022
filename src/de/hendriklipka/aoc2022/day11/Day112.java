package de.hendriklipka.aoc2022.day11;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigInteger;
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
public class Day112
{
    /**
     * Implementation note:
     * after each 'worry' operation, we MOD the result by the product of all the known divisors. That way we limit the
     * value range (so it does not grow indefinitely), but can still do all the MOD operations of each monkey.
     * 'modValue' here tracks this product (its updated in 'parseMonkeyData').
     * Most likely we don't even new the BigInteger operation below (long should be sufficient), but its still fast enough.
     */
    static int modValue=1;
    public static void main(String[] args)
    {
        try
        {
            List<List<String>> monkeyData = AocParseUtils.getStringBlocks("day11");
            List<Monkey> monkeys = parseMonkeyData(monkeyData);
            for (int i=0;i<10000;i++)
            {
                if (0==(i%100))
                {
                    System.out.println(i);
                    List<Integer> result = monkeys.stream().map(m -> m.inspected).collect(Collectors.toList());
                    System.out.println(result);
                }
                handleRound(monkeys);
            }
            List<Integer> result = monkeys.stream().map(m -> m.inspected).sorted().collect(Collectors.toList());
            System.out.println(result);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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
            BigInteger worry = monkey.popFirstItem();
            BigInteger newWorry = monkey.operation.apply(worry).remainder(BigInteger.valueOf(modValue));
            if (newWorry.remainder(monkey.divisor).equals(BigInteger.ZERO))
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
            monkey.items.addAll(Arrays.stream(StringUtils.split(aMonkey.get(1).substring(18), ',')).map(
                    s -> new BigInteger(s.trim())).collect(
                    Collectors.toList()));
            String operation = aMonkey.get(2);
            String operand = operation.substring(24).trim();
            if (operation.charAt(23)=='+')
            {
                if (operand.equals("old"))
                {
                    System.out.println("old + old");
                    monkey.operation = o -> o.add(o);
                }
                else
                {
                    final BigInteger operandValue = new BigInteger(operand);
                    System.out.println("old + "+operandValue);
                    monkey.operation = o -> o.add(operandValue);
                }
            }
            else if (operation.charAt(23) == '*')
            {
                if (operand.equals("old"))
                {
                    System.out.println("old * old");
                    monkey.operation = o -> o.multiply(o);
                }
                else
                {
                    final BigInteger operandValue = new BigInteger(operand);
                    System.out.println("old * " + operandValue);
                    monkey.operation = o -> o.multiply(operandValue);
                }

            }
            else
            {
                throw new IllegalArgumentException("unknown operation ["+ operation.charAt(23)+"] in "+operation);
            }
            monkey.divisor=new BigInteger(aMonkey.get(3).substring(21));
            modValue *=monkey.divisor.intValue();
            monkey.toDivisible=Integer.parseInt(aMonkey.get(4).substring(29));
            monkey.toNotDivisible=Integer.parseInt(aMonkey.get(5).substring(30));
            monkeys.add(monkey);
        }
        return monkeys;
    }

    private static class Monkey
    {
        LinkedList<BigInteger> items = new LinkedList<>();
        UnaryOperator<BigInteger> operation;
        BigInteger divisor;
        int toDivisible;
        int toNotDivisible;

        int inspected = 0;

        BigInteger popFirstItem()
        {
            BigInteger i=items.getFirst();
            items.removeFirst();
            return i;
        }

        void pushItem(BigInteger item)
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
