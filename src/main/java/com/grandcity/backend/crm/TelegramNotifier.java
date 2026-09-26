package com.grandcity.backend.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Posts CRM events to a Telegram chat via the Bot API. Does nothing until both
 * TELEGRAM_BOT_TOKEN and TELEGRAM_CHAT_ID are set, so the app runs fine without it.
 */
@Component
public class TelegramNotifier {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotifier.class);

    private final String token;
    private final String chatId;
    private final RestClient client;
    private final TaskExecutor executor;

    public TelegramNotifier(
            @Value("${app.telegram.bot-token:}") String token,
            @Value("${app.telegram.chat-id:}") String chatId,
            @Value("${app.telegram.api-url:https://api.telegram.org}") String apiUrl,
            @Qualifier("applicationTaskExecutor") TaskExecutor executor) {
        this.token = token;
        this.chatId = chatId;
        this.client = RestClient.create(apiUrl);
        this.executor = executor;
    }

    public boolean isEnabled() {
        return !token.isBlank() && !chatId.isBlank();
    }

    /**
     * Fire-and-forget on a background thread, so a slow or failing Telegram never holds up
     * the request. Inside a transaction the message waits for the commit — a rolled-back
     * lead is never announced.
     */
    public void send(String html) {
        if (!isEnabled()) return;
        Runnable task = () -> sendNow(html);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    executor.execute(task);
                }
            });
        } else {
            executor.execute(task);
        }
    }

    /** Synchronous send; returns false when disabled or when Telegram rejects the message. */
    public boolean sendNow(String html) {
        if (!isEnabled()) return false;
        try {
            client.post()
                    .uri("/bot{token}/sendMessage", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "chat_id", chatId,
                            "text", html,
                            "parse_mode", "HTML",
                            "disable_web_page_preview", true))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception ex) {
            log.warn("Telegram notification failed: {}", ex.getMessage());
            return false;
        }
    }

    /** Escape user-supplied text for Telegram's HTML parse mode. */
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
