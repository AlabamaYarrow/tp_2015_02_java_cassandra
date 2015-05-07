package utils;

import base.VFS;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class VFSImpl implements VFS {

    private String root;

    public VFSImpl(String root) {
        this.root = root;
    }

    public Iterator<String> getIterator(String startDir) {
        return new FileIterator(startDir);
    }

    @Override
    public String getName(String path) {
        return new File(path).getName();
    }

    private class FileIterator implements Iterator<String> {

        private Queue<File> files = new LinkedList<>();

        public FileIterator(String path) {
            files.add(new File(root + path));
        }

        public boolean hasNext() {
            return !files.isEmpty();
        }

        public String next() {
            File file = files.peek();
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    files.add(subFile);
                }
            }

            return files.poll().getAbsolutePath();
        }

        public void remove() {

        }
    }
}

