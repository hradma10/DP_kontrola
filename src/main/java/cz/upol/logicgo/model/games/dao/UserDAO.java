package cz.upol.logicgo.model.games.dao;

import cz.upol.logicgo.exceptions.database.UserErrorException;
import cz.upol.logicgo.model.games.entity.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserDAO extends AbstractDAO {

    public UserDAO() {
        super();
        //initAnon();
    }

    public UserDAO(EntityManager em) {
        super(em);
        //initAnon();
    }


    void initAnon(){
        if (findByUsername("anon").isEmpty()) {
            save(new User("anon", ""));
        }
    }

    public Optional<User> save(User user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return Optional.of(user);
    }

    public Optional<User> findByUsername(String username) {
        Query query = em.createNamedQuery("User.findByUsername");
        query.setParameter("username", username);
        return Optional.ofNullable((User) query.getSingleResultOrNull());
    }

    public List<User> findAllByUsernames(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return List.of();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        cq.select(user).where(user.get("username").in(usernames));

        return em.createQuery(cq).getResultList();
    }

    public String findHashByUsername(String username) {
        Query query = em.createNamedQuery("User.findPasswordHashByUsername");
        query.setParameter("username", username);
        Object hashedPassword = query.getSingleResultOrNull();
        return Objects.isNull(hashedPassword) ? "" : hashedPassword.toString();
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findAnon() {
        return Optional.of(new User("anon"));
    }

    public void update(User user) {
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
    }

    public void delete(User user) {
        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
    }

    public void deleteAll(List<User> users) {
        em.getTransaction().begin();
        users.forEach(em::remove);
        em.getTransaction().commit();
    }

    public List<User> findAll() {
        Query query = em.createNamedQuery("User.findAll");
        return (List<User>) query.getResultList();
    }

    public void saveAll(List<User> users) {
        em.getTransaction().begin();
        users.forEach(em::persist);
        em.getTransaction().commit();
    }
}
