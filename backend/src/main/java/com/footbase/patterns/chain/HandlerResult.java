package com.footbase.patterns.chain;

public class HandlerResult {

    private final boolean success;
    private final String message;
    private final String handlerName;

    private HandlerResult(boolean success, String message, String handlerName) {
        this.success = success;
        this.message = message;
        this.handlerName = handlerName;
    }

    public static HandlerResult success(String message) {
        return new HandlerResult(true, message, null);
    }

    public static HandlerResult success() {
        return new HandlerResult(true, "İşlem başarılı", null);
    }

    public static HandlerResult failure(String message) {
        return new HandlerResult(false, message, null);
    }

    public static HandlerResult failure(String message, String handlerName) {
        return new HandlerResult(false, message, handlerName);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getHandlerName() {
        return handlerName;
    }

    @Override
    public String toString() {
        return String.format("HandlerResult{success=%s, message='%s', handler='%s'}",
                success, message, handlerName);
    }
}
