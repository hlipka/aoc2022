package de.hendriklipka.aoc2022.day18;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hli
 * Date: 17.12.22
 * Time: 21:23
 */
public class Day181
{
    private static Set<String> cubeCache = new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            List<List<Integer>> cubes = AocParseUtils.getLineIntegers("day18");
            for (List<Integer> cube: cubes)
            {
                cubeCache.add(StringUtils.join(cube,"-"));
            }
            int faces = 0;
            for (List<Integer> cube : cubes)
            {
                faces += countFaces(cube);
            }
            System.out.println(faces);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int countFaces(List<Integer> cube)
    {
        int faces=0;
        int x=cube.get(0);
        int y=cube.get(1);
        int z=cube.get(2);
        if (hasNoCubeAt(x - 1, y, z))
            faces++;
        if (hasNoCubeAt(x + 1, y, z))
            faces++;
        if (hasNoCubeAt(x, y - 1, z))
            faces++;
        if (hasNoCubeAt(x, y + 1, z))
            faces++;
        if (hasNoCubeAt(x, y, z - 1))
            faces++;
        if (hasNoCubeAt(x, y, z + 1))
            faces++;
        return faces;
    }

    private static boolean hasNoCubeAt(int x, int y, int z)
    {
        String key=""+x+"-"+y+"-"+z;
        return !cubeCache.contains(key);
    }
}
