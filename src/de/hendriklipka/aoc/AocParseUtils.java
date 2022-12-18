package de.hendriklipka.aoc;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 02.12.22
 * Time: 08:04
 */
public class AocParseUtils
{
    public static List<List<Integer>> getIntegerBlocks(final String dayName) throws IOException
    {
        List<String> lines = FileUtils.readLines(new File("data/"+ dayName +".txt"), StandardCharsets.UTF_8);

        List<List<Integer>> blocks = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        blocks.add(current);
        for (String line : lines)
        {
            if (StringUtils.isBlank(line))
            {
                current = new ArrayList<>();
                blocks.add(current);
                continue;
            }
            current.add(Integer.parseInt(line));
        }
        return blocks;
    }

    public static List<List<String>> getStringBlocks(final String dayName) throws IOException
    {
        List<String> lines = FileUtils.readLines(new File("data/" + dayName + ".txt"), StandardCharsets.UTF_8);

        List<List<String>> blocks = new ArrayList<>();
        List<String> current = new ArrayList<>();
        blocks.add(current);
        for (String line : lines)
        {
            if (StringUtils.isBlank(line))
            {
                current = new ArrayList<>();
                blocks.add(current);
                continue;
            }
            current.add(line);
        }
        return blocks;
    }


    public static List<List<String>> getLineWords(final String dayName) throws IOException
    {
        List<String> lines = FileUtils.readLines(new File("data/" + dayName + ".txt"), StandardCharsets.UTF_8);
        return lines.stream().filter(StringUtils::isNotBlank).map(l->Arrays.asList(StringUtils.split(l, " "))).collect(Collectors.toList());
    }

    public static List<List<Integer>> getLineIntegers(final String dayName) throws IOException
    {
        List<String> lines = FileUtils.readLines(new File("data/" + dayName + ".txt"), StandardCharsets.UTF_8);
        return lines.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(l -> Arrays.stream(StringUtils.split(l, ",")).map(Integer::parseInt).collect(
                            Collectors.toList()))
                    .collect(Collectors.toList());
    }


    public static List<String> getLines(final String dayName) throws IOException
    {
        return FileUtils.readLines(new File("data/" + dayName + ".txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public static List<List<String>> getLinesAsChars(final String dayName) throws IOException
    {
        return FileUtils.readLines(new File("data/" + dayName + ".txt"), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l->l.chars().mapToObj(c->String.valueOf((char)c)).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<List<Integer>> getLinesAsDigits(final String dayName) throws IOException
    {
        return FileUtils.readLines(new File("data/" + dayName + ".txt"), StandardCharsets.UTF_8).stream()
                .filter(StringUtils::isNotBlank)
                .map(l->l.chars().mapToObj(c->c-'0').collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<String> parsePartsFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '"+pattern+"' in ["+str+"]");
        }
        final int count = m.groupCount();
        List<String> result = new ArrayList<>(count);
        for (int i=0;i<count; i++)
        {
            result.add(m.group(i+1));
        }
        return result;
    }

    public static String parseStringFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '" + pattern + "' in [" + str + "]");
        }
        return m.group(1);
    }

    public static int parseIntFromString(String str, String pattern)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        if (!m.matches())
        {
            throw new IllegalArgumentException("No match found for '" + pattern + "' in [" + str + "]");
        }
        return Integer.parseInt(m.group(1));
    }
}
