package base;

import java.util.Iterator;

public interface VFS {
    Iterator<String> getIterator(String startDir);

    String getName(String path);
}
