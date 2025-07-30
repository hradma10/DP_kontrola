package cz.upol.logicgo.commands;

import java.time.ZonedDateTime;

public abstract class Command implements ICommand {
    final private ZonedDateTime timestamp;


    public Command() {
        this.timestamp = ZonedDateTime.now();
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public abstract byte getType();
}
