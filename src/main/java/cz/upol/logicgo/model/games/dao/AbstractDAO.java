package cz.upol.logicgo.model.games.dao;

import cz.upol.logicgo.util.JpaUtil;
import jakarta.persistence.EntityManager;

public abstract class AbstractDAO {

    protected final EntityManager em;

    public AbstractDAO() {
        this.em = JpaUtil.getEntityManager();
    }

    public AbstractDAO(EntityManager em) {
        this.em = em;
    }
}
