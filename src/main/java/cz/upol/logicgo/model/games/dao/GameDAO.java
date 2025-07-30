package cz.upol.logicgo.model.games.dao;

import cz.upol.logicgo.list.history.FilterProperties;
import cz.upol.logicgo.misc.enums.SortOption;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.misc.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GameDAO extends AbstractDAO {

    public GameDAO() {
        super();
    }

    public GameDAO(EntityManager em) {
        super(em);
    }

    public boolean gameWithSeedExistsFinished(long seed, User player) {
        try {
            Query query = em.createNamedQuery("Game.gamesWithSeedExistsForPlayer");
            query.setParameter("seed", seed);
            query.setParameter("player", player);
            return ((Game) query.getSingleResult()).getStatus() == Status.FINISHED;
        } catch (NoResultException _) {
            return false;
        }

    }

    public Game getExistingGameWithSeed(long seed, User player) {
        try {
            Query query = em.createNamedQuery("Game.gamesWithSeedExistsForPlayer");
            query.setParameter("seed", seed);
            query.setParameter("player", player);
            return (Game) query.getResultList().getFirst();
        } catch (NoSuchElementException _ ) {
            return null;
        }

    }


    public long getCountPlayedGames(User user) {
        try {
            Query query = em.createNamedQuery("Game.gamesPlayed");
            query.setParameter("user", user);
            return (long) query.getSingleResult();
        } catch (NoResultException _) {
            return 0;
        }

    }

    public long getCountGamesByStatus(User user, Status status) {
        try {
            Query query = em.createNamedQuery("Game.gamesByStatusCount");
            query.setParameter("user", user);
            query.setParameter("status", status);
            return (long) query.getSingleResult();
        } catch (NoResultException _) {
            return 0;
        }
    }

    public Game getLastPlayedNonFinishedGame(User user) {
        try {
            Query query = em.createNamedQuery("Game.lastPlayedNonFinishedGame");
            query.setParameter("player", user);
            query.setParameter("status", Status.IN_PROGRESS);
            return (Game) query.getResultList().getFirst();
        } catch (NoSuchElementException _) {
            return null;
        }
    }

    public List<Game> getGamesByFilter(FilterProperties properties) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> query = cb.createQuery(Game.class);
        Root<Game> root = query.from(Game.class);

        List<Predicate> predicates = new ArrayList<>();

        if (properties.getTypeGame() != null) {
            predicates.add(cb.equal(root.get("typeGame"), properties.getTypeGame()));
        }

        if (properties.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), properties.getStatus()));
        }

        if (properties.getUser() != null) {
            predicates.add(cb.equal(root.get("player"), properties.getUser()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        SortOption sortOption = properties.getSortOption();

        if (properties.getSortOption() != null) {
            if (properties.isAscending()) {
                query.orderBy(cb.asc(root.get(sortOption.getDescription())));
            }else {
                query.orderBy(cb.desc(root.get(sortOption.getDescription())));
            }
        }

        return em.createQuery(query).getResultList();
    }

    public void updateGameNotes(long id, String text) {
        em.getTransaction().begin();
        Query query = em.createNamedQuery("Game.updateGameNotes");
        query.setParameter("note", text);
        query.setParameter("id", id);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public String getGameNotes(long id) {
        Query query = em.createNamedQuery("Game.getGameNotes");
        query.setParameter("id", id);
        return query.getSingleResult().toString();
    }

    public void updateGameThumbnail(long id, String name) {
        em.getTransaction().begin();
        Query query = em.createNamedQuery("Game.updateThumbnailName");
        query.setParameter("name", name);
        query.setParameter("id", id);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public String getGameThumbnail(long id) {
        Query query = em.createNamedQuery("Game.getThumbnailName");
        query.setParameter("id", id);
        return query.getSingleResult().toString();
    }

    public void save(Game game) {
        em.getTransaction().begin();
        em.persist(game);
        em.getTransaction().commit();
    }

    public <T extends Game> void saveAll(List<T> games) {
        em.getTransaction().begin();
        games.forEach(em::persist);
        em.getTransaction().commit();
    }

    public void delete(long id) {
        em.getTransaction().begin();
        Query query = em.createNamedQuery("Game.deleteById");
        query.setParameter("id", id);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public Game refresh(Game game) {
        em.getTransaction().begin();
        em.refresh(game);
        em.getTransaction().commit();
        return game;
    }


}
