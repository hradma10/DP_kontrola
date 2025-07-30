package cz.upol.logicgo.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JpaUtil {
    private static final EntityManagerFactory emf;

    private static final String sharedDataPath = System.getenv("ProgramData");
    private static final String appFolderName = "LogicGo";
    private static final String appFolderPath = sharedDataPath + File.separator + appFolderName;

    private static final String databaseFile = appFolderPath + File.separator + "data.db";

    static {
        ensureDirectoryExists();

        String url = "jdbc:sqlite:" + databaseFile;

        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", url);
        props.put("jakarta.persistence.jdbc.driver", "org.sqlite.JDBC");
        props.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "true");

        emf = Persistence.createEntityManagerFactory("logic-games-pu", props);
    }

    private static void ensureDirectoryExists() {
        File dir = new File(appFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
