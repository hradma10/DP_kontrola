package cz.upol.logicgo.model.games.dao;

import cz.upol.logicgo.list.history.GameHistoryEntry;
import cz.upol.logicgo.model.games.Game;
import cz.upol.logicgo.model.games.cqUpdate.SudokuUpdate;
import cz.upol.logicgo.model.games.entity.sudoku.Sudoku;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SudokuDAO extends GameDAO {

    public SudokuDAO(EntityManager em) {
        super(em);
    }

    public SudokuDAO() {
        super();
    }

    public void save(Sudoku sudoku) {
        em.getTransaction().begin();
        em.persist(sudoku);
        em.getTransaction().commit();
    }

    public void update(Sudoku sudoku) {
        em.getTransaction().begin();
        em.merge(sudoku);
        em.getTransaction().commit();
    }

    public Optional<Sudoku> findById(long id) {
        return Optional.ofNullable(em.find(Sudoku.class, id));
    }

    public List<Sudoku> findAll() {
        Query query = em.createNamedQuery("Sudoku.findAll");
        return (List<Sudoku>) query.getResultList();
    }

    public void delete(Sudoku sudoku) {
        em.getTransaction().begin();
        em.remove(sudoku);
        em.getTransaction().commit();
    }

    public List<Sudoku> getAllUserSudokuById(long id) {
        Query query = em.createNamedQuery("Sudoku.getAllUserSudokuById");
        query.setParameter("id", id);
        return (List<Sudoku>) query.getResultList();
    }

    public List<Sudoku> getAllUserSudokuByUsername(String username) {
        Query query = em.createNamedQuery("Sudoku.getAllUserSudokuByUsername");
        query.setParameter("username", username);
        return (List<Sudoku>) query.getResultList();
    }

    public List<Sudoku> getLastNumberOfGames(long id, int numberOfGames) {
        Query query = em.createNamedQuery("Sudoku.getLastNumberOfGames");
        query.setParameter("id", id);
        query.setMaxResults(numberOfGames);
        return (List<Sudoku>) query.getResultList();
    }

    public void updateBoard(SudokuUpdate sudokuUpdate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Sudoku> update = cb.createCriteriaUpdate(Sudoku.class);
        Root<Sudoku> root = update.from(Sudoku.class);

        if (sudokuUpdate.hasElapsedTime()){
            update.set("elapsedTime", sudokuUpdate.getElapsedTime());
        }

        if (sudokuUpdate.hasStatus()){
            update.set("status", sudokuUpdate.getStatus());
        }

        if (sudokuUpdate.hasBoard()){
            update.set("board", sudokuUpdate.getBoard());
        }

        if (sudokuUpdate.hasUndoStack()){
            update.set("undoStack", sudokuUpdate.getUndoStack());
        }

        if (sudokuUpdate.hasRedoStack()){
            update.set("redoStack", sudokuUpdate.getRedoStack());
        }

        if (sudokuUpdate.hasRespectRules()){
            update.set("respectRules", sudokuUpdate.isRespectRules());
        }

        update.set("lastPlayed", LocalDateTime.now());

        update.where(cb.and(
                cb.equal(root.get("player"), sudokuUpdate.getUser()),
                cb.equal(root.get("id"), sudokuUpdate.getGameId())
        ));

        em.getTransaction().begin();
        em.createQuery(update).executeUpdate();
        em.flush();
        em.getTransaction().commit();
    }


}
