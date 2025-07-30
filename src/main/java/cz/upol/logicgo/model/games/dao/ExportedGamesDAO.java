package cz.upol.logicgo.model.games.dao;

import cz.upol.logicgo.model.games.entity.export.ExportedGames;
import cz.upol.logicgo.model.games.entity.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class ExportedGamesDAO extends AbstractDAO {

    public ExportedGamesDAO() {
        super();
    }

    public ExportedGamesDAO(EntityManager em) {
        super(em);
    }

    public void save(ExportedGames game) {
        em.getTransaction().begin();
        em.persist(game);
        em.getTransaction().commit();
    }

    public void saveAll(List<ExportedGames> games) {
        if (games.isEmpty()) return;
        em.getTransaction().begin();
        games.forEach(em::persist);
        em.getTransaction().commit();
    }

    public ExportedGames findById(Long id) {
        return em.find(ExportedGames.class, id);
    }

    public List<ExportedGames> findByUser(User user) {
        Query query = em.createNamedQuery(
            "ExportedGames.findByUsername", ExportedGames.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<ExportedGames> findAll() {
        Query query = em.createNamedQuery("ExportedGames.findAll", ExportedGames.class);
        return query.getResultList();
    }

    public void delete(ExportedGames game) {
        em.getTransaction().begin();
        ExportedGames managed = em.merge(game);
        em.remove(managed);
        em.getTransaction().commit();
    }

    public void delete(long id) {
        em.getTransaction().begin();
        Query query = em.createNamedQuery("ExportedGames.deleteById");
        query.setParameter("id", id);
        query.executeUpdate();
        em.getTransaction().commit();
    }
}
