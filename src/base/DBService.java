package base;

import base.dataSets.UserDataSet;

import java.util.List;

public interface DBService {
    String getLocalStatus();

    void save(UserDataSet dataSet);

    UserDataSet read(long id);

    boolean has(String name);

    UserDataSet readByName(String name);

    List<UserDataSet> readAll();

    void shutdown();

    long getUsersCount();
}
