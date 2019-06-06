package pl.kognitywistyka.database;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by pwilkin on 28-Mar-19.
 */
public class DatabaseConnection {

    public static interface ORMRunnable {
        public void execute(EntityManager manager);
    }

    private static DatabaseConnection singleton;

    private DatabaseConnection() {}

    public synchronized static DatabaseConnection getInstance() {
        if (singleton == null) {
            singleton = new DatabaseConnection();
        }
        return singleton;
    }

    private EntityManager manager;

    public void runInORM(ORMRunnable runnable) {
        initializeManagerIfNeeded();
        runnable.execute(manager);
    }

    private synchronized void initializeManagerIfNeeded() {
        if (manager == null) {
            Map<String, Object> params = new HashMap<>();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("songbook", params);
            manager = emf.createEntityManager();
        }
    }

}
