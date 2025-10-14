package Myaong.Gangajikimi.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
	PENDING("대기 중"),       // 처리 대기
	IN_PROGRESS("진행 중"),   // 처리 중
	COMPLETED("처리완료"); // 처리 완료

	private final String description;
}
