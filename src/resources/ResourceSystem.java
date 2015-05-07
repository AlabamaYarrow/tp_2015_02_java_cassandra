package resources;

import base.Resource;
import base.VFS;
import com.sun.istack.internal.Nullable;
import utils.VFSImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResourceSystem {

    private static ResourceSystem instance;

    public final Map<String, Resource> RESOURCES = new HashMap<>();

    private ResourceSystem() {
        VFS vfs = new VFSImpl("");
        for (Iterator<String> iterator = vfs.getIterator("resources"); iterator.hasNext(); ) {
            String path = iterator.next();
            @Nullable
            Resource resource = ResourceFactory.getInstance().get(path);
            if (resource != null) {
                this.RESOURCES.put(vfs.getName(path), resource);
            }
        }
    }

    public static ResourceSystem getInstance() {
        if (null == ResourceSystem.instance) {
            ResourceSystem.instance = new ResourceSystem();
        }
        return ResourceSystem.instance;
    }

    public Resource getResource(String path) {
        return this.RESOURCES.get(path);
    }
}
