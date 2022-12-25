package de.hendriklipka.aoc2022.day25;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 25.12.22
 * Time: 18:46
 */
public class Day251
{
    public static void main(String[] args) throws IOException
    {
        // test calls
        System.out.println("snafu for 4890 = " + createSnafu(4890));
        System.out.println("snafu for 2022=" + createSnafu(2022));
        System.out.println("snafu for 12345=" + createSnafu(12345));
        System.out.println("snafu for 314159265=" + createSnafu(314159265L));

        long sum = AocParseUtils.getLines("day25").stream().mapToLong(Day251::parseSnafu).sum();
        System.out.println("sum is " + sum);
        System.out.println("entry is "+ createSnafu(sum));
    }

    private static long parseSnafu(String line)
    {
        List<String> digits = line.chars()
                                  .mapToObj(c -> String.valueOf((char) c))
                                  .collect(Collectors.toList());
        Collections.reverse(digits);
        long result = 0;
        long dMult = 1;
        for (String digit : digits)
        {
            switch (digit)
            {
                case "2":
                    result += (2 * dMult);
                    break;
                case "1":
                    result += dMult;
                    break;
                case "0":
                    break;
                case "-":
                    result -= dMult;
                    break;
                case "=":
                    result -= (2 * dMult);
                    break;
            }
            dMult *= 5;
        }
        return result;
    }

    private static String createSnafu(long l)
    {
        // find the largest digit (power of 5) we need
        // yes, we could calculate the 5th root
        long dMult=1;
        int len=1;
        while (dMult*5<=l)
        {
            dMult*=5;
            len++;
        }
        // got through the number and find the correct value for each digit (when doing regular base 5)
        int[] intSnafu=new int[len+1];
        int pos=0;
        while (true)
        {
            long digit = l/dMult;
            long rest = l%dMult;
            intSnafu[1+pos++]=(int)digit;
            l=rest;
            if (dMult==1)
            {
                break;
            }
            dMult /=5;
        }
        // now normalize, and create the result string
        StringBuilder snafu = new StringBuilder();
        for (int i=len;i>=0;i--)
        {
            // 3 and 4 need to increase the next higher digit, so we can subtract
            if (intSnafu[i] == 3 || intSnafu[i] == 4)
            {
                intSnafu[i - 1]++;
            }
            // 5 means we also increase the next digit, but we don't beed to subtract
            if (intSnafu[i] ==5)
            {
                intSnafu[i - 1]++;
                intSnafu[i]=0;
            }
            switch (intSnafu[i])
            {
                case 0:
                    snafu.append("0");
                    break;
                case 1:
                    snafu.append("1");
                    break;
                case 2:
                    snafu.append("2");
                    break;
                case 3:
                    // we need to subtract 2 from the next higher digit
                    snafu.append("=");
                    break;
                case 4:
                    snafu.append("-");
                    break;
            }
        }
        return snafu.reverse().toString();
    }
}
