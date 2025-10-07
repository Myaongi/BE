package Myaong.Gangajikimi.chatmessage.web.controller;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import Myaong.Gangajikimi.auth.jwt.JwtTokenProvider;
import Myaong.Gangajikimi.chatmessage.service.ChatMessageService;
import Myaong.Gangajikimi.chatmessage.web.dto.ChatSendRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Chat WebSocket", description = "실시간 채팅 WebSocket API")
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

	private final ChatMessageService messageService;
	private final JwtTokenProvider jwtTokenProvider;

	/** 세션ID -> memberId 매핑 테이블 */
	private static final ConcurrentHashMap<String, Long> SESSION_USER_MAP = new ConcurrentHashMap<>();

	@MessageMapping("/chat")
	public void handle(@Payload ChatSendRequest req,
		Principal principal,
		SimpMessageHeaderAccessor accessor) {

		final String sessionId = accessor.getSessionId();

		if ("AUTH".equals(req.getType())) {
			// ✅ AUTH는 반드시 "AccessToken" 사용 (refresh 토큰 금지)
			final String accessToken = req.getToken();
			jwtTokenProvider.validateJwtToken(accessToken); // 유효성 검사
			Long memberId = Long.valueOf(jwtTokenProvider.parseClaimsFromToken(accessToken).getSubject());

			SESSION_USER_MAP.put(sessionId, memberId);
			log.info("[AUTH OK] sessionId={}, memberId={}", sessionId, memberId);
			return;
		}

		if ("MESSAGE".equals(req.getType())) {
			// 1순위: 세션 매핑 (AUTH로 등록된 사용자)
			Long senderId = SESSION_USER_MAP.get(sessionId);

			// (보조) CONNECT에서 principal을 세팅해둔 경우 폴백
			if (senderId == null && principal != null) {
				try {
					senderId = Long.valueOf(principal.getName());
					log.warn("[AUTH missing → Fallback principal] sessionId={}, principal={}", sessionId, principal.getName());
				} catch (NumberFormatException ignore) { /* principal name이 숫자가 아닐 수도 있음 */ }
			}

			if (senderId == null) {
				log.warn("[MESSAGE REJECT] senderId unresolved. sessionId={}, req={}", sessionId, req);
				return; // 혹은 커스텀 예외 throw
			}

			messageService.handleMessage(req, senderId);
			log.info("[MSG] sessionId={}, resolvedSenderId={}, chatroomId={}, content={}",
				sessionId, senderId, req.getChatroomId(), req.getContent());
		}
	}

	@EventListener
	public void onConnect(SessionConnectedEvent event) {
		String sid = (String) event.getMessage().getHeaders().get("simpSessionId");
		String user = (event.getUser() != null) ? event.getUser().getName() : null;
		log.info("[WS CONNECT] sessionId={}, principal={}", sid, user);
	}

	@EventListener
	public void onDisconnect(SessionDisconnectEvent event) {
		String sessionId = event.getSessionId();
		Long removed = SESSION_USER_MAP.remove(sessionId);
		log.info("[WS DISCONNECT] sessionId={}, removedMemberId={}", sessionId, removed);
	}
}
