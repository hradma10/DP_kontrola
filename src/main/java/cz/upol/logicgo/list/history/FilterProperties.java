package cz.upol.logicgo.list.history;

import cz.upol.logicgo.misc.enums.SortOption;
import cz.upol.logicgo.misc.enums.Status;
import cz.upol.logicgo.misc.enums.TypeGame;
import cz.upol.logicgo.model.games.entity.user.User;

public class FilterProperties {
    User user;
    TypeGame typeGame = null;
    Status status = null;
    Boolean ascending = false;
    SortOption sortOption = null;

    public FilterProperties(User user) {
        this.user = user;
    }

    public TypeGame getTypeGame() {
        return typeGame;
    }

    public FilterProperties setTypeGame(TypeGame typeGame) {
        this.typeGame = typeGame;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public FilterProperties setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Boolean isAscending() {
        return ascending;
    }

    public FilterProperties setAscending(Boolean ascending) {
        this.ascending = ascending;
        return this;
    }

    public FilterProperties setSortOption(SortOption sortOption) {
        this.sortOption = sortOption;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getAscending() {
        return ascending;
    }

    public SortOption getSortOption() {
        return sortOption;
    }

}
