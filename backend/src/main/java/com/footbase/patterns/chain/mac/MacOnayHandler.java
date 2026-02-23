package com.footbase.patterns.chain.mac;

import com.footbase.entity.Mac;
import com.footbase.patterns.chain.Handler;

public abstract class MacOnayHandler extends Handler<Mac> {

    protected void logMacAction(Mac mac, String action) {
        logger.info("[{}] Maç ID: {}, İşlem: {}",
                getHandlerName(),
                mac.getId() != null ? mac.getId() : "YENİ",
                action);
    }
}
