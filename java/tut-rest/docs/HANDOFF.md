# HANDOFF — 학습 세션 인수인계

> 다음 세션의 Claude가 이 파일을 먼저 읽고 이어서 진행한다.
> 마지막 세션: 2026-09-28

---

## 1. Claude의 역할

사용자의 **Java/Spring 학습 튜터**. 대상 프로젝트는 `java/tut-rest` (Spring 공식 가이드 "Building REST services with Spring").

- 코드를 대신 짜 주는 게 아니라, 사용자가 **흐름을 자기 말로 설명할 수 있게** 돕는다.
- 사용자가 이해한 항목은 `docs/FEATURE_CHECKLIST.md`에 `[x]`로 체크해서 진도를 기록한다.
- 실험을 위해 소스 코드를 바꾸는 건 **사용자가 직접** 하고, 끝나면 원래대로 되돌린다.
- 문서(`docs/*.md`)는 Claude가 수정해도 된다. 소스 코드는 사용자 요청 없이는 수정하지 않는다.

## 2. 수업 방식 (사용자가 합의한 방식)

처음에 질문 4개와 표를 한꺼번에 줬더니 사용자가 "감이 안 온다"고 했다. 그 뒤로 아래 방식으로 바꿨고 잘 맞았다.

1. **한 번에 한 주제**: 코드 몇 줄 + 짧은 설명 (주석 번호 ①②③)
2. **직접 해보기**: 브라우저, `Invoke-RestMethod`, 또는 코드 한 줄 주석 처리 → 재시작 → 확인
3. **질문 1~3개**: 빈칸 양식으로 제시한다 (사용자가 양식을 달라고 요청했음)
   ```
   1. ...:
   2. ...:
   ```
4. **피드백**: 맞은 점을 먼저 말하고, 틀린 부분은 "적은 답 / 정답 / 이유" 형태로 고친다. 그다음 체크리스트에 체크한다.

추가 원칙:
- 각 개념이 **Java 문법인지 Spring 기능인지** 구분해서 알려 준다 (사용자가 물어봤음).
- 추측보다 **직접 실행한 결과**를 우선한다. Claude도 `curl`로 로컬 서버(8080)에 요청해서 확인할 수 있다.
- 모듈을 넘어갈 때는 처음부터 다시 읽지 않고 **"뭐가 바뀌었지?"만** 본다.
- 한국어로 설명하고, 비유(상자, 표지판, 컨베이어 벨트)를 쓰면 잘 이해한다.

## 3. 사용자 특성 (피드백할 때 주의할 점)

> 자세한 오답 기록과 약점 패턴은 **`docs/WRONG_ANSWERS.md`(오답노트)**에 있다. 아래는 요약이다.

- **자주 헷갈리는 것**
  - 어노테이션 vs 메서드: `@GetMapping`을 "메서드"라고 답함 → "`@`로 시작하면 표지판, `이름(){}`이면 메서드"로 교정
  - URL vs 코드 호출: "`/employees/99`"를 DB 조회 코드라고 답함
  - `@GetMapping`과 `@PostMapping`을 혼동한 적 있음
  - get/set 방향: 요청으로 들어올 때 `setName`, 응답으로 나갈 때 `getName`
  - 흐름의 **순서**: 예외 메시지 생성(Exception) → Advice가 받음 (순서를 반대로 말한 적 있음)
  - 수량 표현: "한 개" / "여러 개" (CollectionModel)
  - 질문과 다른 것을 답할 때가 있음 ("바뀐 게 있나?"에 "추가된 것"으로 답함)
- **잘하는 것**: 실험을 스스로 확장한다 (`/employees/qwer` 직접 시도). 코드의 숨은 문제를 찾는다 (PUT에서 id가 무시되는 문제, `rest`의 중복 코드).
- 모르면 솔직하게 "모르겠다"고 말한다. 이때는 더 쉬운 비유로 다시 설명한다.

## 4. 환경 메모

- OS: Windows, **PowerShell** 사용
- 로컬에 `java`가 PATH에 없다. 서버는 Maven Wrapper로 실행한다 (정상 동작함).
  ```powershell
  .\mvnw.cmd -pl <모듈명> spring-boot:run     # 모듈명: nonrest | rest | evolution | links
  ```
- 모든 모듈이 **8080 포트**를 쓴다. 모듈을 바꿀 때는 이전 서버를 `Ctrl + C`로 끈다.
- 코드를 수정하면 **서버 재시작이 필요하다** (devtools 없음). 사용자가 한 번 재시작 없이 테스트해서 결과가 안 바뀐 적 있음.
- 요청 보내기 (PowerShell):
  ```powershell
  Invoke-RestMethod -Method Post -Uri http://localhost:8080/employees -ContentType 'application/json' -Body '{"name":"Samwise Gamgee","role":"gardener"}'
  (Invoke-WebRequest -Method Delete -Uri http://localhost:8080/employees/4 -UseBasicParsing).StatusCode
  ```
- 소스 파일에 한국어 주석(Javadoc)이 달려 있다 (예: `nonrest/.../EmployeeNotFoundAdvice.java`). 사용자 쪽에서 추가한 것이니 지우지 않는다.
- 실험용 변경(`@RestControllerAdvice` 주석 처리, `LoadDatabase`에 Samwise 추가)은 모두 원래대로 돌아가 있다 (2026-09-28 확인).

## 5. 진행 상황

`docs/FEATURE_CHECKLIST.md` 기준 **33개 항목 체크 완료**.

| 단계 | 모듈 | 상태 | 다룬 내용 |
|---|---|---|---|
| 1 | `nonrest` | ✅ 완료 | 앱 시작(`main` → `SpringApplication.run` → Bean → DI), Repository 구현 객체(Spring Data), `LoadDatabase`(`@Bean`, `CommandLineRunner`, 람다, `run()` 두 개 구분), CRUD 전체, 예외 처리(Advice 제거 실험 → 500), 상태코드 200/400/404/500, `long` vs `Long`, 같은 패키지는 import 불필요 |
| 2 | `rest` | ✅ 완료 | HAL 응답 구조(`_embedded`, `_links`), HATEOAS 목적, `EntityModel` vs `CollectionModel`, `linkTo(methodOn(...))`, Stream(`stream/map/collect`), `all()`과 `one()`의 중복 코드 문제 |
| 3 | `evolution` | 🔶 진행 중 | ✅ `name` → `firstName`/`lastName` 하위 호환(Jackson은 getter/setter 기준, `"Gandalf"` → 400) / ⏳ `EmployeeModelAssembler` 분리 **설명만 하고 질문 대기 중** |
| 4 | `links` | ⬜ 미시작 | |

### 지금 멈춘 곳 (다음 세션에서 여기부터)

`EmployeeModelAssembler`를 설명하고 아래 질문을 냈지만 **아직 답을 받지 못했다.**

```
1. EmployeeController는 assembler를 new로 만들지 않았다. 그럼 누가, 어떻게 넣어 주나?
   (nonrest에서 repository를 넣어 준 방식을 떠올려 보세요):
2. 링크를 하나 더 추가하고 싶다면, rest와 evolution에서 각각 몇 군데를 고쳐야 하나?:
```

답을 받은 뒤 설명하기로 약속한 것: **메서드 참조 `assembler::toModel`** (= `e -> assembler.toModel(e)`)

### 다음 순서

1. 위 질문 답 받기 → 피드백 → `assembler::toModel` 설명
2. `evolution`의 `ResponseEntity` 변경
   - POST/PUT → **201 Created** + `Location` 헤더 (`ResponseEntity.created(...)`, `getRequiredLink(IanaLinkRelations.SELF)`)
   - DELETE → **204 No Content** (`ResponseEntity.noContent()`)
   - 실험: `Invoke-WebRequest`로 상태코드와 `Location` 헤더 확인
3. `links` 모듈 (FEATURE_CHECKLIST 12~15번)
   - `RootController` (`GET /`, 링크만 있는 `RepresentationModel`)
   - `Order`, `Status` enum, `@Table(name="CUSTOMER_ORDER")` (ORDER는 SQL 예약어)
   - `OrderModelAssembler`의 **상태에 따른 조건부 링크** (`IN_PROGRESS`일 때만 cancel/complete)
   - `cancel`/`complete`의 405 + `Problem` 응답
   - `/orders/99` → Advice가 없어서 **500** (nonrest에서 했던 Advice 제거 실험과 연결)
   - `EmployeeController`에 주입되지만 사용하지 않는 `orderAssembler`
4. 마무리: FEATURE_CHECKLIST 맨 아래 "전체 API 표" 설명하기, CODE_COVERAGE로 빠진 부분 점검

### 아직 체크하지 않은 주요 항목 (복습 때 확인할 것)

- 5번: 진입 지점을 **메서드 이름(`one`)**으로 정확히 말하기 (어노테이션과 혼동했음)
- 1번: `@SpringBootApplication`의 구성, 설정 파일 없이 H2/Tomcat이 준비되는 이유(자동 설정)
- 2번: `CommandLineRunner`가 함수형 인터페이스라서 람다로 구현한다는 것, `static final Logger`
- 10번: `withSelfRel()` vs `withRel()`, static import
- 16번: 요청/응답 직렬화 전체 흐름 (Jackson). getter/setter 부분은 11번에서 이미 다룸

## 6. 세션 시작 / 종료 규칙

- **시작** ("공부 이어서 하자"): 이 파일과 `WRONG_ANSWERS.md`를 읽는다 → 오답노트에서 `재확인`이 안 된 항목 1~2개를 복습 질문으로 먼저 낸다 (맞히면 `[x]`) → "지금 멈춘 곳"부터 이어서 진행한다.
- **종료** ("오늘은 여기까지"): 두 문서를 함께 업데이트한다.
  - `HANDOFF.md`: 진행 상황 표, 지금 멈춘 곳, 다음 순서, 체크 개수
  - `WRONG_ANSWERS.md`: 그날의 오답을 A~E 분류로 추가하고, 약점 패턴 표의 횟수를 갱신하고, 다시 맞힌 항목에 `재확인`을 체크한다.
- 수업 방식 전체는 `java/README.md`에 정리되어 있다.

## 7. 문서 수정 이력

- 2026-09-28: `Employee#setName`에 공백 없는 이름이 들어오면 500이라고 적었던 것을 **400**으로 정정했다 (FEATURE_CHECKLIST 11번, CODE_COVERAGE evolution `setName`). POST와 PUT 모두 실제로 400이 나오는 것을 확인했다. 예외가 JSON 역직렬화 단계에서 나기 때문이다.
