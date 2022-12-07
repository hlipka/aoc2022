package de.hendriklipka.aoc2022.day07;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day072
{
    public static void main(String[] args)
    {
        try
        {
            List<String> commands = AocParseUtils.getLines("day07");
            commands.remove(0); // we can skip cd'ing to the root
            DirNode root = new DirNode();
            DirNode current = root;
            Set<DirNode> dirs = new HashSet<>();
            for (String cmd: commands)
            {
                if (cmd.startsWith("$ cd"))
                {
                    current = changeDir(cmd, current);
                    dirs.add(current);
                }
                else if (cmd.startsWith("$ ls"))
                {
                    // do nothing
                }
                else
                {
                    // we can ignore dirs here, they get created upon visiting them
                    if (!cmd.startsWith("dir"))
                    {
                        addFile(cmd, current);
                    }
                }
            }
            long free = 70000000 - root.getSize();
            long needed = 30000000 - free;
            System.out.println(free);
            System.out.println(needed);
            List<DirNode> sorted = dirs.stream().sorted(Day072::bySize).collect(Collectors.toList());
            long toBeDeleted = sorted.stream().map(DirNode::getSize).filter(s->s>needed).findFirst().orElseThrow();
            System.out.println(toBeDeleted);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int bySize(DirNode dirNode, DirNode dirNode1)
    {
        return Long.compare(dirNode.getSize(), dirNode1.getSize());
    }

    private static DirNode changeDir(String cmd, DirNode current)
    {
        String dirName = cmd.substring(5);
        if (dirName.equals(".."))
        {
            DirNode parent = current.parent;
            return Objects.requireNonNullElse(parent, current);
        }
        else
        {
            DirNode dir = new DirNode();
            dir.parent=current;
            dir.name=dirName;
            current.children.add(dir);
            return dir;
        }
    }

    private static void addFile(String cmd, DirNode current)
    {
        String[] parts = cmd.split(" ");
        FileNode file = new FileNode();
        file.length = Long.parseLong(parts[0]);
        file.name = parts[1];
        current.files.add(file);
    }

    private static class DirNode
    {
        String name;
        List<DirNode> children = new ArrayList<>();
        List<FileNode> files = new ArrayList<>();
        DirNode parent;

        @Override
        public String toString()
        {
            return "DirNode{" +
                    "name='" + name + '\'' +
                    ", children=\n" + children +
                    "\n, files=\n" + files +
                    "\n}";
        }

        public long getSize()
        {
            long fileSize = files.stream().mapToLong(FileNode::getLength).sum();
            long dirSize = children.stream().mapToLong(DirNode::getSize).sum();
            return fileSize+dirSize;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DirNode dirNode = (DirNode) o;
            return name.equals(dirNode.name) && Objects.equals(parent, dirNode.parent);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(name, parent);
        }
    }

    private static class FileNode
    {
        String name;
        long length;

        @Override
        public String toString()
        {
            return "FileNode{" +
                    "name='" + name + '\'' +
                    ", length=" + length +
                    '}';
        }

        public long getLength()
        {
            return length;
        }
    }
}
