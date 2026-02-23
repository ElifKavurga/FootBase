package com.footbase.patterns.command;

public interface Command {

    boolean execute();

    boolean undo();

    boolean redo();

    String getDescription();

    String getCommandType();

    Long getKullaniciId();

    java.time.LocalDateTime getExecutionTime();
}
