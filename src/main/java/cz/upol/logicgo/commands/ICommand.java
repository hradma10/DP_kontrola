package cz.upol.logicgo.commands;


interface ICommand {
    void execute();

    void undo();

    byte[] getCommandsAsBytes();
}