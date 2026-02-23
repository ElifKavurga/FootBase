package com.footbase.patterns.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandInvoker {

    private static final Logger logger = LoggerFactory.getLogger(CommandInvoker.class);

    @Autowired
    private CommandHistory commandHistory;

    public boolean executeCommand(Command command) {
        logger.info("Komut çalıştırılıyor: {}", command.getDescription());

        boolean result = command.execute();

        if (result) {
            commandHistory.push(command);
            logger.info("Komut başarıyla çalıştırıldı ve geçmişe eklendi");
        } else {
            logger.error("Komut çalıştırılamadı: {}", command.getDescription());
        }

        return result;
    }

    public boolean undo() {
        logger.info("🔄 Son komut geri alınıyor...");
        return commandHistory.undo();
    }

    public boolean undoByKullaniciId(Long kullaniciId) {
        logger.info("🔄 Kullanıcı #{} için son komut geri alınıyor...", kullaniciId);
        return commandHistory.undoByKullaniciId(kullaniciId);
    }

    public boolean redo() {
        logger.info("🔁 Komut tekrar yapılıyor...");
        return commandHistory.redo();
    }

    public void clearHistory() {
        logger.info("🧹 Komut geçmişi temizleniyor...");
        commandHistory.clear();
    }

    public int getHistorySize() {
        return commandHistory.size();
    }

    public int getRedoSize() {
        return commandHistory.redoSize();
    }

    public void printHistory() {
        commandHistory.printHistory();
    }

    public Command getLastCommand() {
        return commandHistory.peek();
    }

    public CommandHistory getCommandHistory() {
        return commandHistory;
    }
}
