# FEATURE_CHECKLIST — 기능 흐름 기반 학습 체크리스트

> 대상 프로젝트: `tut-rest` (Spring 공식 가이드 "Building REST services with Spring")
> 목표: 각 기능이 **요청 → 코드 → DB → 응답**으로 어떻게 흘러가는지 코드를 보지 않고 설명할 수 있는 상태

---

## 0. 먼저 알아둘 프로젝트 구조

이 프로젝트는 하나의 애플리케이션이 아니라, **같은 급여(payroll) 앱을 4단계로 발전시킨 4개의 Maven 모듈**이다.
각 모듈은 독립 실행되는 Spring Boot 앱이고, 모두 같은 패키지 `payroll`을 쓴다.

| 순서 | 모듈 | 핵심 변화 | 응답 형태 |
|---|---|---|---|
| 1 | `nonrest` | 기본 CRUD API (Employee) | 순수 JSON |
| 2 | `rest` | 조회 API에 HATEOAS 링크 추가 | HAL JSON (`_links`, `_embedded`) |
| 3 | `evolution` | 링크 생성 로직을 `EmployeeModelAssembler`로 분리, `ResponseEntity`로 상태코드 제어, `name` → `firstName`/`lastName` 스키마 변경 | HAL JSON + 201/204 |
| 4 | `links` | `Order` 도메인 추가, 상태(`Status`)에 따라 링크가 바뀌는 API, 루트(`/`) API | HAL JSON + Problem Details |

실제 호출 구조 (모든 모듈 공통):

```
HTTP 요청
  → DispatcherServlet (Spring MVC가 제공, 코드에 없음)
  → @RestController 메서드 (EmployeeController / OrderController / RootController)
  → [evolution, links] ModelAssembler (Entity → EntityModel 변환, 링크 추가)
  → JpaRepository 인터페이스 (EmployeeRepository / OrderRepository)
  → Spring Data JPA 구현체 → Hibernate → H2 인메모리 DB
  → 반환 객체를 Jackson이 JSON으로 직렬화 → HTTP 응답
```

### 이 프로젝트에 **없는** 것 (학습 범위 밖)

- **Service 계층 없음**: Controller가 Repository를 직접 호출한다.
- **DTO 없음**: JPA Entity(`Employee`, `Order`)가 요청 본문과 응답 본문에 그대로 쓰인다.
- **Validation 없음**: `@Valid`, `@NotNull` 등이 없다.
- **인증/인가 없음**: Spring Security를 쓰지 않는다.
- **설정 파일 없음**: `application.properties`/`application.yml`이 없고, 모두 Spring Boot 자동 설정(auto-configuration)의 기본값으로 동작한다.
- **테스트 코드 없음**: `src/test`가 없다.

- [ ] 4개 모듈이 각각 무엇을 추가했는지 순서대로 설명할 수 있다.
- [ ] 이 프로젝트의 호출 계층이 Controller → Repository로 끝나는 이유(Service가 없다)를 설명할 수 있다.
- [ ] Entity를 요청/응답에 그대로 쓸 때의 장점과 문제점을 말할 수 있다.

### 관련 코드
- `pom.xml` (루트, `<modules>`)
- `nonrest/pom.xml`, `rest/pom.xml`, `evolution/pom.xml`, `links/pom.xml`

---

## 1. 애플리케이션 시작과 Spring 컨테이너 구성

`main()` 실행 → Spring Boot가 Bean을 만들고 서로 연결(DI)한 뒤 → 내장 Tomcat(8080)과 H2 DB를 띄우는 흐름.

- [ ] 이 기능의 목적(애플리케이션을 띄우고 모든 객체를 준비하는 것)을 설명할 수 있다.
- [ ] 진입점이 `PayrollApplication#main()`임을 찾을 수 있다.
- [ ] `SpringApplication.run()` → 컴포넌트 스캔 → Bean 생성 → 의존성 주입 → 내장 웹서버 시작 순서를 설명할 수 있다.
- [ ] `@SpringBootApplication`이 `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`을 합친 것임을 설명할 수 있다.
- [ ] `payroll` 패키지 안의 `@RestController`, `@Component`, `@Configuration`, `@RestControllerAdvice` 클래스가 스캔되어 Bean이 되는 것을 설명할 수 있다.
- [ ] `EmployeeRepository`는 클래스가 아닌 인터페이스인데도 Bean으로 주입되는 이유(Spring Data JPA가 구현 프록시를 만든다)를 설명할 수 있다.
- [ ] `EmployeeController(EmployeeRepository repository)` 같은 **생성자 주입**이 `@Autowired` 없이 동작하는 이유(생성자가 1개면 자동 주입)를 설명할 수 있다.
- [ ] 설정 파일이 없어도 H2 인메모리 DB, 테이블 자동 생성(DDL), Jackson, Tomcat이 준비되는 이유(의존성 + 자동 설정)를 설명할 수 있다.
- [ ] 코드를 보지 않고 "main 실행부터 요청을 받을 준비가 될 때까지"를 내 말로 설명할 수 있다.

### 관련 코드
- `PayrollApplication#main(String...)`
- `@SpringBootApplication`
- 각 모듈 `pom.xml`의 `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-hateoas`, `h2`
- 생성자 주입: `EmployeeController(EmployeeRepository ...)`, `OrderController(OrderRepository, OrderModelAssembler)`

---

## 2. 초기 데이터 로딩

앱이 뜬 직후 DB에 샘플 데이터를 넣고 로그를 찍는 흐름.

- [ ] 이 기능의 목적(빈 인메모리 DB에 테스트용 데이터를 채움)을 설명할 수 있다.
- [ ] 시작 지점이 HTTP 요청이 아니라 `LoadDatabase#initDatabase()`가 등록한 `CommandLineRunner`임을 찾을 수 있다.
- [ ] 호출 흐름을 설명할 수 있다:
  `SpringApplication.run()` 완료 직전 → Spring이 `CommandLineRunner` Bean의 `run(args)` 호출 → 람다 본문 실행 → `repository.save(new Employee(...))` → INSERT
- [ ] `@Configuration` + `@Bean` 메서드가 "메서드의 반환값을 Bean으로 등록"한다는 것을 설명할 수 있다.
- [ ] `@Bean` 메서드의 파라미터(`EmployeeRepository`, `OrderRepository`)가 Spring에 의해 주입된다는 것을 설명할 수 있다.
- [ ] `return args -> { ... };`가 함수형 인터페이스 `CommandLineRunner`를 **람다로 구현**한 것임을 설명할 수 있다.
- [ ] `save()`가 id가 채워진 엔티티를 반환하므로 로그에 `id=1` 같은 값이 찍힌다는 것을 설명할 수 있다.
- [ ] SLF4J `Logger`를 `static final`로 두는 이유를 설명할 수 있다.
- [ ] 모듈별 차이를 설명할 수 있다:
  - `nonrest`/`rest`: `new Employee("Bilbo Baggins", "burglar")` (이름 하나)
  - `evolution`: `new Employee("Bilbo", "Baggins", "burglar")` (성/이름 분리)
  - `links`: Employee 2건 + `Order` 2건(`COMPLETED`, `IN_PROGRESS`), `findAll().forEach(...)`로 로그
- [ ] 인메모리 DB라서 재시작하면 데이터가 초기화되고 다시 로딩된다는 것을 설명할 수 있다.
- [ ] 코드를 보지 않고 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `LoadDatabase#initDatabase(EmployeeRepository)` (nonrest, rest, evolution)
- `LoadDatabase#initDatabase(EmployeeRepository, OrderRepository)` (links)
- `CommandLineRunner#run(String...)` (Spring Boot 인터페이스)
- `EmployeeRepository#save()`, `OrderRepository#save()`, `#findAll()`
- `Employee(String, String)`, `Employee(String, String, String)`, `Order(String, Status)`

---

## 3. DB 저장 및 조회 흐름 (JPA 계층 공통)

모든 API 기능이 공통으로 거치는 "Repository → DB" 구간.

- [ ] 이 계층의 목적(자바 객체 ↔ DB 테이블 행 변환을 대신해 줌)을 설명할 수 있다.
- [ ] `interface EmployeeRepository extends JpaRepository<Employee, Long>` 한 줄로 `findAll`, `findById`, `save`, `deleteById`가 생기는 이유를 설명할 수 있다.
- [ ] 제네릭 `<Employee, Long>`이 각각 "엔티티 타입"과 "PK 타입"임을 설명할 수 있다.
- [ ] `@Entity` 클래스가 테이블에, 필드가 컬럼에 매핑되는 것을 설명할 수 있다.
- [ ] `@Id` + `@GeneratedValue`로 PK를 DB/JPA가 자동 발급하는 것을 설명할 수 있다.
- [ ] JPA가 **기본 생성자**(`Employee() {}`)를 요구하는 이유(리플렉션으로 객체를 먼저 만든 뒤 값을 채움)를 설명할 수 있다.
- [ ] `save()`가 id가 없으면 INSERT(persist), id가 있으면 UPDATE(merge)로 동작함을 설명할 수 있다.
- [ ] `findById()`가 `Optional<T>`을 반환하는 이유(없을 수도 있음)를 설명할 수 있다.
- [ ] `Order` 엔티티에 `@Table(name = "CUSTOMER_ORDER")`를 붙인 이유(`ORDER`는 SQL 예약어)를 설명할 수 있다.
- [ ] `Status` enum 필드가 `@Enumerated` 없이 저장되면 기본값인 **ORDINAL(숫자 0,1,2)** 로 저장된다는 것을 설명할 수 있다.
- [ ] 코드 없이 "Controller가 `repository.findById(1L)`을 호출하면 DB까지 무슨 일이 일어나는지" 설명할 수 있다.

### 관련 코드
- `EmployeeRepository` (`JpaRepository<Employee, Long>`)
- `OrderRepository` (`JpaRepository<Order, Long>`) — links
- `Employee` (`@Entity`, `@Id`, `@GeneratedValue`)
- `Order` (`@Entity`, `@Table`) — links
- `Status` (enum) — links

---

## 4. 직원 전체 조회 — `GET /employees`

- [ ] 이 기능의 목적(모든 직원 목록 반환)을 설명할 수 있다.
- [ ] 요청이 처음 들어오는 지점 `@GetMapping("/employees")` → `EmployeeController#all()`을 찾을 수 있다.
- [ ] 호출 흐름을 모듈별로 설명할 수 있다:
  - `nonrest`: `all()` → `repository.findAll()` → `List<Employee>` 그대로 반환
  - `rest`: `all()` → `findAll()` → `stream().map(employee -> EntityModel.of(employee, 링크들))` → `collect(toList())` → `CollectionModel.of(목록, self 링크)`
  - `evolution`/`links`: 위와 같지만 `map(assembler::toModel)`로 링크 생성을 `EmployeeModelAssembler`에 위임
- [ ] 사용된 Java 문법을 설명할 수 있다: Stream API(`stream`, `map`, `collect`), 람다, **메서드 참조**(`assembler::toModel`), 제네릭 중첩 타입(`CollectionModel<EntityModel<Employee>>`)
- [ ] 사용된 Spring 개념을 설명할 수 있다: `@RestController`, `@GetMapping`, HATEOAS `EntityModel`, `CollectionModel`, `linkTo(methodOn(...))`
- [ ] 데이터 변환을 설명할 수 있다: DB 행 → `Employee` → `EntityModel<Employee>` → `List<EntityModel<Employee>>` → `CollectionModel<...>` → HAL JSON
- [ ] 최종 응답 모양을 설명할 수 있다:
  - `nonrest`: `[{"id":1,"name":"Bilbo Baggins","role":"burglar"}, ...]`
  - `rest` 이후: `{"_embedded":{"employeeList":[{... "_links":{"self":..., "employees":...}}]}, "_links":{"self":{"href":"http://localhost:8080/employees"}}}`
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeController#all()`
- `EmployeeRepository#findAll()`
- `EmployeeModelAssembler#toModel(Employee)` (evolution, links)
- `Employee`
- `CollectionModel.of(...)`, `EntityModel.of(...)`, `WebMvcLinkBuilder#linkTo`, `#methodOn`

---

## 5. 직원 단일 조회 — `GET /employees/{id}`

- [x] 이 기능의 목적(id로 직원 1명 반환, 없으면 404)을 설명할 수 있다.
- [ ] 진입 지점 `@GetMapping("/employees/{id}")` → `EmployeeController#one(Long id)`를 찾을 수 있다.
- [x] URL의 `{id}` 문자열이 `@PathVariable Long id`로 **타입 변환**되어 들어오는 것을 설명할 수 있다.
- [ ] 호출 흐름을 설명할 수 있다:
  `one(id)` → `repository.findById(id)` → `Optional<Employee>`
  → 값 있음: `Employee` (nonrest는 그대로 반환 / rest는 `EntityModel.of(...)` / evolution·links는 `assembler.toModel(...)`)
  → 값 없음: `orElseThrow(() -> new EmployeeNotFoundException(id))` → **6번 예외 처리 흐름**으로 이동
- [ ] `Optional#orElseThrow(Supplier)`와 람다 `() -> new ...`를 설명할 수 있다.
- [ ] 최종 응답(성공 200 + JSON/HAL, 실패 404 + 문자열 메시지)을 설명할 수 있다.
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeController#one(Long)`
- `EmployeeRepository#findById(Long)`
- `EmployeeNotFoundException(Long)`
- `EmployeeModelAssembler#toModel(Employee)` (evolution, links)

---

## 6. 예외 처리 — 존재하지 않는 리소스 → 404

- [x] 이 기능의 목적(예외를 HTTP 404 응답으로 바꿔 클라이언트에게 알림)을 설명할 수 있다.
- [ ] 예외가 처음 발생하는 지점(`one()`의 `orElseThrow`)과 처리되는 지점(`EmployeeNotFoundAdvice`)을 찾을 수 있다.
- [x] 호출 흐름을 설명할 수 있다:
  `EmployeeController#one()` → `throw EmployeeNotFoundException` → 컨트롤러 밖으로 전파 → Spring MVC가 `@RestControllerAdvice`에서 `@ExceptionHandler(EmployeeNotFoundException.class)`인 메서드를 찾음 → `employeeNotFoundHandler(ex)` 실행 → `@ResponseStatus(NOT_FOUND)`로 404 설정 → `ex.getMessage()` 문자열이 응답 본문
- [ ] `EmployeeNotFoundException extends RuntimeException`인 이유(unchecked 예외라 `throws` 선언이 필요 없음)를 설명할 수 있다.
- [x] `super("Could not find employee " + id)`로 넘긴 메시지가 `getMessage()`로 꺼내지는 것을 설명할 수 있다.
- [ ] `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`(반환값을 뷰가 아닌 응답 본문으로)를 설명할 수 있다.
- [x] **links 모듈의 빈틈**을 설명할 수 있다: `OrderNotFoundException`에는 대응하는 Advice가 **없다** → `GET /orders/99`는 404가 아니라 Spring Boot 기본 에러 처리로 **500**이 된다.
- [x] 최종 응답: `404 Not Found`, 본문 `Could not find employee 99`
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeNotFoundException`
- `EmployeeNotFoundAdvice#employeeNotFoundHandler(EmployeeNotFoundException)`
- `EmployeeController#one(Long)` (예외 발생 지점)
- `OrderNotFoundException` (links — 핸들러 없음)

---

## 7. 직원 생성 — `POST /employees`

- [ ] 이 기능의 목적(요청 JSON으로 새 직원 저장)을 설명할 수 있다.
- [ ] 진입 지점 `@PostMapping("/employees")` → `EmployeeController#newEmployee(@RequestBody Employee)`를 찾을 수 있다.
- [ ] `@RequestBody`가 요청 JSON을 Jackson으로 `Employee` 객체로 **역직렬화**(기본 생성자 호출 → setter로 값 채움)하는 것을 설명할 수 있다.
- [ ] 호출 흐름을 모듈별로 설명할 수 있다:
  - `nonrest`/`rest`: `repository.save(newEmployee)` → 저장된 `Employee`를 그대로 반환 (200 OK)
  - `evolution`/`links`: `save()` → `assembler.toModel(saved)` → `ResponseEntity.created(self 링크 URI).body(entityModel)` (201 Created + `Location` 헤더)
- [ ] `entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()`로 새로 만들어진 리소스 주소를 얻는 과정을 설명할 수 있다.
- [ ] `ResponseEntity<?>`의 와일드카드 제네릭 `?`의 의미를 설명할 수 있다.
- [ ] 데이터 흐름: 요청 JSON → `Employee`(id 없음) → `save()` → `Employee`(id 있음) → `EntityModel<Employee>` → 응답 JSON
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeController#newEmployee(Employee)`
- `EmployeeRepository#save(Employee)`
- `EmployeeModelAssembler#toModel(Employee)` (evolution, links)
- `ResponseEntity#created(URI)`, `EntityModel#getRequiredLink(LinkRelation)`, `IanaLinkRelations.SELF`
- `Employee()` 기본 생성자 + setter

---

## 8. 직원 수정(없으면 생성) — `PUT /employees/{id}`

- [ ] 이 기능의 목적(id의 직원을 요청 내용으로 교체, 없으면 새로 생성 = upsert)을 설명할 수 있다.
- [ ] 진입 지점 `@PutMapping("/employees/{id}")` → `EmployeeController#replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id)`를 찾을 수 있다.
- [ ] 호출 흐름을 설명할 수 있다:
  `findById(id)` → `Optional<Employee>`
  → 있음: `.map(employee -> { setName, setRole; return save(employee); })` → UPDATE
  → 없음: `.orElseGet(() -> save(newEmployee))` → INSERT
- [ ] `Optional#map`과 `Optional#orElseGet`의 차이(`orElse`와 달리 없을 때만 람다 실행)를 설명할 수 있다.
- [ ] **주의점**을 설명할 수 있다: "없음" 분기에서는 URL의 `id`가 `newEmployee`에 설정되지 않으므로 **새 id가 자동 발급**된다 (즉, `PUT /employees/99`로 만든 직원의 id는 99가 아니다).
- [ ] `evolution`/`links`에서 `employee.setName(newEmployee.getName())`이 `"First Last"`를 공백 기준으로 쪼개 `firstName`/`lastName`에 넣는다는 것을 설명할 수 있다 (10번 참고).
- [ ] 최종 응답 모듈별 차이:
  - `nonrest`/`rest`: 저장된 `Employee` JSON (200)
  - `evolution`/`links`: `ResponseEntity.created(...)` → **201 Created** + `Location` + HAL 본문 (수정인데도 201을 쓰는 것은 튜토리얼 선택)
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeController#replaceEmployee(Employee, Long)`
- `EmployeeRepository#findById(Long)`, `#save(Employee)`
- `Employee#setName(String)`, `#setRole(String)`, `#getName()`, `#getRole()`
- `EmployeeModelAssembler#toModel(Employee)` (evolution, links)

---

## 9. 직원 삭제 — `DELETE /employees/{id}`

- [ ] 이 기능의 목적을 설명할 수 있다.
- [ ] 진입 지점 `@DeleteMapping("/employees/{id}")` → `EmployeeController#deleteEmployee(Long)`을 찾을 수 있다.
- [ ] 호출 흐름: `deleteEmployee(id)` → `repository.deleteById(id)` → DELETE SQL
- [ ] 없는 id를 지워도 예외가 나지 않는다는 것(Spring Data JPA 3.x의 `deleteById`는 없으면 조용히 무시)을 설명할 수 있다.
- [ ] 최종 응답 모듈별 차이:
  - `nonrest`/`rest`: 반환 타입 `void` → **200 OK**, 빈 본문
  - `evolution`/`links`: `ResponseEntity.noContent().build()` → **204 No Content**
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeController#deleteEmployee(Long)`
- `EmployeeRepository#deleteById(Long)`
- `ResponseEntity#noContent()`

---

## 10. HATEOAS 링크 생성 흐름 (rest → evolution → links)

응답 데이터에 "다음에 갈 수 있는 URL"을 함께 넣어주는 기능. 조회/생성/수정 응답 모두에 쓰인다.

- [ ] 이 기능의 목적(클라이언트가 URL을 하드코딩하지 않고 응답의 링크를 따라가게 함 = REST의 HATEOAS 원칙)을 설명할 수 있다.
- [ ] `EntityModel.of(데이터, 링크...)`가 엔티티를 링크와 함께 감싸는 **래퍼**라는 것을 설명할 수 있다.
- [ ] `linkTo(methodOn(EmployeeController.class).one(id))`의 동작을 설명할 수 있다:
  `methodOn`이 컨트롤러의 **프록시**를 만들고 → `.one(id)` 호출을 실제로 실행하지 않고 "기록"만 함 → `linkTo`가 그 메서드의 `@GetMapping` 경로와 인자로 `http://localhost:8080/employees/1` URL을 조립
- [ ] `.withSelfRel()`(rel=`self`)과 `.withRel("employees")`의 차이를 설명할 수 있다.
- [ ] `rest` 모듈에서 `all()`과 `one()`에 똑같은 링크 생성 코드가 **중복**되는 문제를 찾을 수 있다.
- [ ] `evolution`에서 중복을 `EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>>`로 추출하고, `@Component`로 Bean 등록 후 컨트롤러에 **생성자 주입**하는 리팩토링을 설명할 수 있다.
- [ ] `import static ...WebMvcLinkBuilder.*;`(static import)로 `linkTo`, `methodOn`을 클래스명 없이 쓰는 것을 설명할 수 있다.
- [ ] 데이터 변환: `Employee` → `EntityModel<Employee>`(+`self`, `employees` 링크) → HAL JSON의 `_links`
- [ ] 코드를 보지 않고 "링크가 어떻게 만들어지는지" 내 말로 설명할 수 있다.

### 관련 코드
- `EmployeeController#all()`, `#one(Long)` (rest — 인라인 링크 생성)
- `EmployeeModelAssembler#toModel(Employee)` (evolution, links)
- `OrderModelAssembler#toModel(Order)` (links)
- `EntityModel`, `CollectionModel`, `RepresentationModel`, `WebMvcLinkBuilder#linkTo`, `#methodOn`, `Link#withSelfRel`, `#withRel`

---

## 11. API 진화: `name` → `firstName` + `lastName` 하위 호환 (evolution, links)

DB 스키마는 바꾸면서 기존 클라이언트(`name` 필드 사용)는 깨지지 않게 하는 흐름.

- [ ] 이 기능의 목적(API를 바꿔도 옛 클라이언트가 계속 동작하도록 유지)을 설명할 수 있다.
- [ ] 테이블 컬럼은 `firstName`, `lastName`, `role`인데 JSON에는 `name`도 함께 나오는 이유를 설명할 수 있다:
  Jackson은 **필드가 아니라 getter**(`getName()`, `getFirstName()` ...)를 보고 JSON 속성을 만든다 → `getName()`이 `firstName + " " + lastName`을 계산해 반환 → `"name"` 속성이 생김
- [ ] 요청 쪽 흐름을 설명할 수 있다: 옛 클라이언트가 `{"name":"Samwise Gamgee"}`를 보내면 Jackson이 `setName()`을 호출 → `split(" ")`으로 나눠 `firstName`, `lastName`에 저장
- [ ] JPA는 필드(`@Id`가 필드에 붙음 → 필드 접근 방식)를 기준으로 컬럼을 만들기 때문에 `name` 컬럼은 생기지 않는다는 것을 설명할 수 있다.
- [ ] **한계**를 설명할 수 있다: `"name":"Gandalf"`처럼 공백이 없으면 `parts[1]`에서 `ArrayIndexOutOfBoundsException` → 500 에러. 세 단어 이상이면 뒷부분이 버려진다.
- [ ] 응답 예: `{"id":1,"firstName":"Bilbo","lastName":"Baggins","role":"burglar","name":"Bilbo Baggins","_links":{...}}`
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `Employee#getName()`, `Employee#setName(String)` (evolution, links)
- `Employee(String firstName, String lastName, String role)`
- `EmployeeController#replaceEmployee(...)` (내부에서 `setName(getName())` 사용)
- `LoadDatabase#initDatabase(...)` (새 생성자 사용)

---

## 12. API 루트 탐색 — `GET /` (links)

- [ ] 이 기능의 목적(클라이언트가 시작점 하나만 알면 나머지 API를 링크로 찾아가게 함)을 설명할 수 있다.
- [ ] 진입 지점: 경로 없는 `@GetMapping` → `/` 에 매핑된 `RootController#index()`를 찾을 수 있다.
- [ ] 흐름: `new RepresentationModel<>()` → `add(employees 링크)` → `add(orders 링크)` → 반환. **Repository/DB를 전혀 거치지 않는다.**
- [ ] `RepresentationModel<?>`이 데이터 없이 링크만 담는 가장 기본 모델(= `EntityModel`, `CollectionModel`의 부모)이라는 것을 설명할 수 있다.
- [ ] 응답 예: `{"_links":{"employees":{"href":"http://localhost:8080/employees"},"orders":{"href":"http://localhost:8080/orders"}}}`
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `RootController#index()`
- `EmployeeController#all()`, `OrderController#all()` (링크 대상)
- `RepresentationModel`

---

## 13. 주문 조회 — `GET /orders`, `GET /orders/{id}` (links)

- [ ] 이 기능의 목적을 설명할 수 있다.
- [ ] 진입 지점 `OrderController#all()`, `OrderController#one(Long)`을 찾을 수 있다.
- [ ] 흐름이 직원 조회(4, 5번)와 같은 구조임을 설명할 수 있다:
  `orderRepository.findAll()/findById()` → `OrderModelAssembler#toModel` → `CollectionModel`/`EntityModel`
- [ ] **직원과 다른 점**: `OrderModelAssembler`가 주문 상태에 따라 링크를 **조건부로** 추가한다
  - 항상: `self`, `orders`
  - `status == IN_PROGRESS`일 때만: `cancel`, `complete`
- [ ] 없는 id 조회 시 `OrderNotFoundException`이 발생하지만 처리하는 Advice가 없어 **500**이 된다는 것을 설명할 수 있다 (6번 참고).
- [ ] 응답 예(`IN_PROGRESS` 주문): `_links`에 `self`, `orders`, `cancel`, `complete`가 모두 있음 / (`COMPLETED` 주문): `self`, `orders`만 있음
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `OrderController#all()`, `OrderController#one(Long)`
- `OrderRepository#findAll()`, `#findById(Long)`
- `OrderModelAssembler#toModel(Order)`
- `OrderNotFoundException`
- `Order`, `Status`

---

## 14. 주문 생성 — `POST /orders` (links)

- [ ] 이 기능의 목적을 설명할 수 있다.
- [ ] 진입 지점 `OrderController#newOrder(@RequestBody Order order)`를 찾을 수 있다.
- [ ] 흐름: 요청 JSON → `Order` → **클라이언트가 보낸 status를 무시하고** `order.setStatus(Status.IN_PROGRESS)`로 강제 → `orderRepository.save()` → `ResponseEntity.created(linkTo(methodOn(OrderController.class).one(newId)).toUri()).body(assembler.toModel(newOrder))`
- [ ] 초기 상태를 서버가 정하는 이유(상태 전이 규칙을 서버가 통제)를 설명할 수 있다.
- [ ] 직원 생성과 달리 `Location` URI를 `getRequiredLink` 대신 `linkTo(...).toUri()`로 직접 만든다는 차이를 찾을 수 있다.
- [ ] 반환 타입이 `ResponseEntity<EntityModel<Order>>`로 구체적인 제네릭을 쓴다는 것을 설명할 수 있다.
- [ ] 최종 응답: 201 Created + `Location` + `cancel`/`complete` 링크가 포함된 HAL 본문
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `OrderController#newOrder(Order)`
- `Order#setStatus(Status)`, `OrderRepository#save(Order)`
- `OrderModelAssembler#toModel(Order)`

---

## 15. 주문 상태 전이 — 취소 `DELETE /orders/{id}/cancel`, 완료 `PUT /orders/{id}/complete` (links)

비즈니스 규칙(진행 중인 주문만 취소/완료 가능)이 들어간 유일한 기능.

- [ ] 이 기능의 목적(상태 기계: `IN_PROGRESS` → `CANCELLED` 또는 `COMPLETED`)을 설명할 수 있다.
- [ ] 진입 지점 `OrderController#cancel(Long)`, `OrderController#complete(Long)`를 찾을 수 있다.
- [ ] 흐름을 설명할 수 있다:
  `findById(id).orElseThrow(OrderNotFoundException)`
  → `status == IN_PROGRESS`이면: `setStatus(CANCELLED / COMPLETED)` → `save()` → `ResponseEntity.ok(assembler.toModel(saved))` → **200**
  → 아니면: `ResponseEntity.status(METHOD_NOT_ALLOWED).header(CONTENT_TYPE, application/problem+json).body(Problem.create().withTitle(...).withDetail(...))` → **405**
- [ ] `cancel`이 `@DeleteMapping`이지만 실제로 행을 지우지 않고 **상태만 바꾼다**는 것을 설명할 수 있다.
- [ ] enum 비교에 `==`를 쓰는 이유(enum 상수는 싱글톤)를 설명할 수 있다.
- [ ] `Problem`(RFC 7807 Problem Details)이 표준화된 에러 응답 형식이라는 것을 설명할 수 있다.
- [ ] 이 규칙이 `OrderModelAssembler`의 조건부 링크와 **짝**을 이룬다는 것(가능한 동작만 링크로 노출 → 서버가 405로 막음)을 설명할 수 있다.
- [ ] 빌더 패턴(`ResponseEntity.status(...).header(...).body(...)`, `Problem.create().withTitle(...)`)을 설명할 수 있다.
- [ ] 에러 응답 예: `405`, `{"title":"Method not allowed","detail":"You can't cancel an order that is in the COMPLETED status"}`
- [ ] 코드를 보지 않고 전체 흐름을 내 말로 설명할 수 있다.

### 관련 코드
- `OrderController#cancel(Long)`, `OrderController#complete(Long)`
- `Status` (`IN_PROGRESS`, `COMPLETED`, `CANCELLED`)
- `OrderModelAssembler#toModel(Order)` (조건부 링크)
- `OrderRepository#findById`, `#save`
- `ResponseEntity#ok`, `#status`, `Problem#create`, `MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE`

---

## 16. 요청/응답 직렬화 흐름 (JSON ↔ 객체) — 공통

코드에 직접 나오지는 않지만 모든 API가 거치는 구간.

- [ ] 요청 본문 JSON → `@RequestBody` → Jackson `ObjectMapper`가 **기본 생성자 + setter**로 객체 생성하는 과정을 설명할 수 있다.
- [ ] 반환 객체 → `@RestController`(= `@Controller` + `@ResponseBody`) → Jackson이 **getter**를 읽어 JSON 생성하는 과정을 설명할 수 있다.
- [ ] HATEOAS 의존성이 있는 모듈에서는 `EntityModel`/`CollectionModel`이 HAL 형식(`_links`, `_embedded`)으로 직렬화된다는 것을 설명할 수 있다.
- [ ] 반환 타입별 응답 차이를 설명할 수 있다: 객체 → 200 + JSON / `void` → 200 빈 본문 / `ResponseEntity` → 상태코드·헤더 직접 지정 / `String`(Advice) → text 본문
- [ ] 코드를 보지 않고 "JSON 한 덩어리가 들어와서 DB에 저장되고 다시 JSON으로 나가기까지"를 설명할 수 있다.

### 관련 코드
- 모든 `@RestController`의 `@RequestBody` 파라미터와 반환 타입
- `Employee`, `Order`의 getter/setter와 기본 생성자

---

## 최종 점검: 전체 API 표를 보고 흐름을 설명할 수 있는가?

| 메서드 | URL | 모듈 | 처리 메서드 | 성공 응답 | 실패 응답 |
|---|---|---|---|---|---|
| GET | `/` | links | `RootController#index` | 200 링크만 | - |
| GET | `/employees` | 전체 | `EmployeeController#all` | 200 | - |
| GET | `/employees/{id}` | 전체 | `EmployeeController#one` | 200 | 404 (Advice) |
| POST | `/employees` | 전체 | `EmployeeController#newEmployee` | 200 → 201 (evolution~) | - |
| PUT | `/employees/{id}` | 전체 | `EmployeeController#replaceEmployee` | 200 → 201 (evolution~) | - |
| DELETE | `/employees/{id}` | 전체 | `EmployeeController#deleteEmployee` | 200 → 204 (evolution~) | - |
| GET | `/orders` | links | `OrderController#all` | 200 | - |
| GET | `/orders/{id}` | links | `OrderController#one` | 200 | 500 (핸들러 없음) |
| POST | `/orders` | links | `OrderController#newOrder` | 201 | - |
| DELETE | `/orders/{id}/cancel` | links | `OrderController#cancel` | 200 | 405 Problem / 500 |
| PUT | `/orders/{id}/complete` | links | `OrderController#complete` | 200 | 405 Problem / 500 |

- [ ] 표의 모든 행에 대해 "진입 → 호출 → 변환 → 응답"을 코드 없이 설명할 수 있다.
- [ ] 같은 URL이 모듈별로 응답이 다른 이유를 설명할 수 있다.
