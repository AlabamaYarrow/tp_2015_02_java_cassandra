package resources;

import base.Resource;
import com.sun.istack.internal.Nullable;
import utils.xml.XMLReader;

public class ResourceFactory {

    private static ResourceFactory instance;

    private ResourceFactory() {

    }

    public static ResourceFactory getInstance() {
        if (null == ResourceFactory.instance) {
            ResourceFactory.instance = new ResourceFactory();
        }
        return ResourceFactory.instance;
    }

    @Nullable
    public Resource get(String path) {
        return (Resource) XMLReader.read(path);
    }
}
