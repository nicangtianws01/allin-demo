package org.example.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@ServerEndpoint(value = "/channel/group/{sid}")
public class WebSocketServer {

    private static final AtomicInteger onlineCount = new AtomicInteger(0);
//    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    private Session session;

    private String sid;

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    // 收到消息
    @OnMessage
    public void onMessage(String message) {
        for (WebSocketServer item : webSocketMap.values()) {
            try {
                if (item.sid.equals(this.sid)) {
                    item.sendMessage("me: " + message);
                } else {
                    item.sendMessage(sid + ": " + message);
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

//        log.info("[websocket] 收到消息：id={}，message={}", this.session.getId(), message);
//
//        if (message.equalsIgnoreCase("bye")) {
//            // 由服务器主动关闭连接。状态码为 NORMAL_CLOSURE（正常关闭）。
//            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));;
//            return;
//        }
//
//
//        this.session.getAsyncRemote().sendText("["+ Instant.now().toEpochMilli() +"] Hello " + message);
    }

    // 连接打开
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        // 保存 session 到对象
        this.session = session;
        log.info("[websocket] 新的连接：id={}", this.session.getId());
        if (StringUtils.isEmpty(sid)) {
            throw new RuntimeException("请输入sid");
        }
        int i = onlineCount.incrementAndGet();
        if (i > 10) {
            onlineCount.decrementAndGet();
            throw new RuntimeException("已达最大连接数");
        }
        webSocketMap.put(sid, this);
        this.sid = sid;
    }

    // 连接关闭
    @OnClose
    public void onClose(CloseReason closeReason) {
        if (this.session != null) {
            log.info("[websocket] 连接断开：id={}，reason={}", this.session.getId(), closeReason);
            webSocketMap.remove(sid);
            onlineCount.decrementAndGet();
        } else {
            log.info("[websocket] 连接断开：reason={}", closeReason);
        }
    }

    // 连接异常
    @OnError
    public void onError(Throwable throwable) throws IOException {

        if (this.session != null) {
            log.error("[websocket] 连接异常：id={}，throwable={}", this.session.getId(), throwable.getMessage());

            // 关闭连接。状态码为 UNEXPECTED_CONDITION（意料之外的异常）
            this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        } else {
            log.error("[websocket] 连接异常：throwable={}", throwable.getMessage());
        }
    }
}
