package com.footbase.patterns.observer;

import com.footbase.entity.Kullanici;
import com.footbase.entity.Mac;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminObserver implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(AdminObserver.class);

    private final Long adminId;
    private final String adminEmail;

    public AdminObserver(Kullanici admin) {
        this.adminId = admin.getId();
        this.adminEmail = admin.getEmail();
    }

    public AdminObserver(Long adminId, String adminEmail) {
        this.adminId = adminId;
        this.adminEmail = adminEmail;
    }

    @Override
    public void update(String eventType, Object data) {
        if (data instanceof Mac) {
            Mac mac = (Mac) data;
            handleMacEvent(eventType, mac);
        }
    }

    private void handleMacEvent(String eventType, Mac mac) {
        switch (eventType) {
            case "MAC_EKLENDI":
                logger.info("Admin {} ({}) icin yeni mac bildirimi: Mac ID={}",
                        adminId, adminEmail, mac.getId());
                // Burada gercek bildirim sistemi entegre edilebilir (email, push notification vb.)
                break;
            case "MAC_ONAYLANDI":
                logger.info("Admin {} ({}) icin mac onay bildirimi: Mac ID={}",
                        adminId, adminEmail, mac.getId());
                break;
            case "MAC_REDDEDILDI":
                logger.info("Admin {} ({}) icin mac red bildirimi: Mac ID={}",
                        adminId, adminEmail, mac.getId());
                break;
            default:
                logger.warn("Bilinmeyen olay tipi: {}", eventType);
        }
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AdminObserver that = (AdminObserver) obj;
        return adminId != null && adminId.equals(that.adminId);
    }

    @Override
    public int hashCode() {
        return adminId != null ? adminId.hashCode() : 0;
    }
}
