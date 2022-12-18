package de.hendriklipka.aoc2022.day18;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * idea:
 * - we determine min+max x,y,z for the cubes
 * - this gives us the surrounding box for the formation
 * - we mark this box as 'known outside'
 * - and then flood-fill inwards to find anything connected
 * (start with the cubes just inside of the surrounding box, and check recursively)
 */
public class Day182
{
    private static Set<String> cubeCache = new HashSet<>();
    private static Set<String> knownOutside = new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            List<List<Integer>> cubes = AocParseUtils.getLineIntegers("day18");
            int minX = 1000, minY = 1000, minZ = 1000;
            int maxX = -1000, maxY = -1000, maxZ = -1000;
            for (List<Integer> cube : cubes)
            {
                addOutside(cubeCache, StringUtils.join(cube, "-"));
                int x = cube.get(0);
                int y = cube.get(1);
                int z = cube.get(2);
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
            }
            Set<List<Integer>> toVisit = new HashSet<>();
            // mark the surrounding box as 'outside', so we don't visit anything there and venture into the universe
            for (int y = minY; y <= maxY; y++)
            {
                for (int z = minZ; z <= maxZ; z++)
                {
                    addOutside(knownOutside, getKey(minX - 1, y, z));
                    toVisit.add(getCube(minX, y, z));
                    addOutside(knownOutside, getKey(maxX + 1, y, z));
                    toVisit.add(getCube(maxX, y, z));
                }
            }
            for (int y = minY; y <= maxY; y++)
            {
                for (int x = minX; x <= maxX; x++)
                {
                    addOutside(knownOutside, getKey(x, y, minZ - 1));
                    toVisit.add(getCube(x, y, minZ));
                    addOutside(knownOutside, getKey(x, y, maxZ + 1));
                    toVisit.add(getCube(x, y, maxZ));
                }
            }
            for (int x = minX; x <= maxX; x++)
            {
                for (int z = minZ; z <= maxZ; z++)
                {
                    addOutside(knownOutside, getKey(x, minY - 1, z));
                    toVisit.add(getCube(x, minY, z));
                    addOutside(knownOutside, getKey(x, maxY + 1, z));
                    toVisit.add(getCube(x, maxY, z));
                }
            }
            // flood-fill from the outside
            while (!toVisit.isEmpty())
            {
                List<Integer> cube = toVisit.iterator().next();
                toVisit.remove(cube);
                int x = cube.get(0);
                int y = cube.get(1);
                int z = cube.get(2);
                String key = getKey(x, y, z);
                // skip anything we know to be either a cube or which is knows to be outside
                if (knownOutside.contains(key))
                {
                    continue;
                }
                if (cubeCache.contains(key))
                {
                    continue;
                }
                // if its not known already, it belongs to the outside
                knownOutside.add(key);
                // add the neighbours to be visited (the hash set makes sure we don't add anything twice)
                toVisit.add(getCube(x - 1, y, z));
                toVisit.add(getCube(x + 1, y, z));
                toVisit.add(getCube(x, y - 1, z));
                toVisit.add(getCube(x, y + 1, z));
                toVisit.add(getCube(x, y, z - 1));
                toVisit.add(getCube(x, y, z + 1));
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

    private static void addOutside(Set<String> knownOutside, String key)
    {
        if (!cubeCache.contains(key))
        {
            knownOutside.add(key);
        }
    }

    private static List<Integer> getCube(int x, int y, int z)
    {
        List<Integer> cube = new ArrayList<>(3);
        cube.add(x);
        cube.add(y);
        cube.add(z);
        return cube;
    }

    private static int countFaces(List<Integer> cube)
    {
        int faces = 0;
        int x = cube.get(0);
        int y = cube.get(1);
        int z = cube.get(2);
        if (hasNoCubeAtAndIsOutside(x - 1, y, z))
            faces++;
        if (hasNoCubeAtAndIsOutside(x + 1, y, z))
            faces++;
        if (hasNoCubeAtAndIsOutside(x, y - 1, z))
            faces++;
        if (hasNoCubeAtAndIsOutside(x, y + 1, z))
            faces++;
        if (hasNoCubeAtAndIsOutside(x, y, z - 1))
            faces++;
        if (hasNoCubeAtAndIsOutside(x, y, z + 1))
            faces++;
        return faces;
    }

    private static boolean hasNoCubeAtAndIsOutside(int x, int y, int z)
    {
        String key = getKey(x, y, z);
        return !cubeCache.contains(key) && knownOutside.contains(key);
    }

    private static String getKey(int x, int y, int z)
    {
        return "" + x + "-" + y + "-" + z;
    }
}
