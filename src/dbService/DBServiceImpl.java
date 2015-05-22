package dbService;

import base.DBService;
import base.dataSets.UserDataSet;
import dbService.dao.UserDataSetDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import resources.DBResource;

import java.util.List;

public class DBServiceImpl implements DBService {
    private SessionFactory sessionFactory;

    public DBServiceImpl(DBResource resource) {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", resource.url);
        configuration.setProperty("hibernate.connection.username", resource.username);
        configuration.setProperty("hibernate.connection.password", resource.password);
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");

        sessionFactory = createSessionFactory(configuration);
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public String getLocalStatus() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String status = transaction.getLocalStatus().toString();
        session.close();
        return status;
    }

    public void save(UserDataSet dataSet) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        dao.save(dataSet);
        transaction.commit();
    }

    public UserDataSet read(long id) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.read(id);
    }

    @Override
    public boolean has(String name) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.readByName(name) != null;
    }

    public UserDataSet readByName(String name) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.readByName(name);
    }

    public List<UserDataSet> readAll() {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.readAll();
    }

    public void shutdown() {
        sessionFactory.close();
    }

    @Override
    public long getUsersCount() {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.getCount();
    }
}
