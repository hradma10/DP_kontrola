package cz.upol.logicgo.model.games.entity.export;

import cz.upol.logicgo.algorithms.sudoku.layout.RegionLayout;
import cz.upol.logicgo.misc.enums.Difficulty;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.misc.enums.settings.gameTypes.GameType;
import cz.upol.logicgo.misc.enums.settings.gameTypes.SudokuType;
import cz.upol.logicgo.model.games.entity.user.User;
import cz.upol.logicgo.util.converters.DifficultyConverter;
import cz.upol.logicgo.util.converters.GameTypeConverter;
import cz.upol.logicgo.util.converters.SudokuRegionConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "ExportedGames.findByUsername",
                query = "SELECT g FROM ExportedGames g WHERE g.user = :user"
        ),
        @NamedQuery(
                name = "ExportedGames.findAll",
                query = "SELECT g FROM ExportedGames g"
        ),
        @NamedQuery(
                name = "ExportedGames.deleteById",
                query = "DELETE ExportedGames eg where eg.id = :id"
        ),

})
@Table(name = "exported_games")
public class ExportedGames {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ElementCollection
    @CollectionTable(name = "exported_games_seeds",
            joinColumns = @JoinColumn(name = "exported_games_id"))
    @Column(name = "seed")
    private List<Long> seeds;

    @Column(nullable = false, name = "difficulty")
    @Convert(converter = DifficultyConverter.class)
    private Difficulty difficulty;

    @Column(nullable = false, name = "game_type")
    @Convert(converter = GameTypeConverter.class)
    private TypeGame typeGame;

    @Column(nullable = false, name = "sudoku_type")
    private SudokuType sudokuType; // TODO podpora více typů

    @Column(nullable = false, name = "region_type")
    @Convert(converter = SudokuRegionConverter.class)
    private RegionLayout regionLayout; // TODO podpora více typů

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "generation_time", nullable = false)
    private LocalDateTime generationTime;

    public ExportedGames() {

    }

    @PrePersist
    public void prePersist() {
        if (generationTime == null) {
            generationTime = LocalDateTime.now();
        }
    }

    public ExportedGames(List<Long> seeds, Difficulty difficulty, TypeGame typeGame, RegionLayout regionLayout, SudokuType sudokuType, User user) {
        this.seeds = seeds;
        this.difficulty = difficulty;
        this.typeGame = typeGame;
        this.regionLayout = regionLayout;
        this.sudokuType = sudokuType;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Long> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<Long> seeds) {
        this.seeds = seeds;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public TypeGame getTypeGame() {
        return typeGame;
    }

    public void setTypeGame(TypeGame typeGame) {
        this.typeGame = typeGame;
    }

    public SudokuType getSudokuType() {
        return sudokuType;
    }

    public void setSudokuType(SudokuType sudokuType) {
        this.sudokuType = sudokuType;
    }

    public LocalDateTime getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(LocalDateTime generationTime) {
        this.generationTime = generationTime;
    }

    public RegionLayout getRegionLayout() {
        return regionLayout;
    }

    public void setRegionLayout(RegionLayout regionLayout) {
        this.regionLayout = regionLayout;
    }
}
