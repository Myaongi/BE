# 코드 컨벤션 설정

### 📘시스템 아키텍처

<img width="1351" height="540" alt="image" src="https://github.com/user-attachments/assets/05280bf4-0428-456c-8211-79cf5fbc1369" />



### 📦 기본 응답 통일

- `BaseResponse<T>` 사용 (code, message, data 포함)
- 응답 코드 관리는 `SuccessStatus`, `ErrorStatus` Enum 활용

### ⚠️ 공통 예외 처리

- `ExceptionAdvice` 클래스: 전역 예외 처리
- `GeneralException` 기반 계층적 예외 처리

---
# Git branch 전략 수립

### 📌 브랜치 전략: **Git Flow**

| 브랜치 이름 | 용도 |
| --- | --- |
| `main` | 프로덕션 배포용 브랜치 |
| `develop` | 개발 브랜치 |
| `feature/#이슈번호-기능이름` | 기능 개발 브랜치 (ex. `feature/#12-email`) |

### ✅ 운영 규칙

- **모든 기능 개발**은 `develop`에서 분기
- 완료 후 `develop`으로 PR 생성 → **코드 리뷰 후 머지**
- 일정 주기마다 `develop` → `main`으로 머지 및 배포

---
### 🔖 타입별 규칙

| 이모지 | 타입         | 설명                                |
| --- |------------| --------------------------------- |
| ✨   | `feat`     | 새로운 기능 추가                         |
| 🐛  | `bug`      | 버그 수정                             |
| 💄  | `hotfix`   | 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없음) |
| ♻️  | `refactor` | 코드 리팩토링                           |   |

---
