# CODE_COVERAGE — 코드 학습 커버리지

> 목적: 프로젝트 안에서 "아직 설명하지 못하는 코드"가 남지 않게 관리한다.
> 체크 기준은 "읽었다"가 아니라 **"코드 없이 설명할 수 있다"**이다.

## 읽는 방법

- 4개 모듈(`nonrest` → `rest` → `evolution` → `links`)은 같은 클래스 이름이 반복된다.
- 중복을 피하려고 **클래스를 처음 등장하는 모듈에서 전체 분석**하고, 이후 모듈에서는 **변경점만** 체크한다.
- 모든 파일은 `<모듈>/src/main/java/payroll/` 아래에 있다.
- 단순 getter/setter, `equals`/`hashCode`/`toString`은 개별 체크하지 않고 클래스 체크리스트에서 한 번만 다룬다.
  단, **로직이 있는 getter/setter**(`Employee#getName`, `#setName` — evolution)는 따로 다룬다.

## 커버리지 현황

| 구분 | 대상 | 완료 |
|---|---|---|
| 빌드 설정 | 루트 `pom.xml`, 모듈 `pom.xml` | [ ] |
| Entry Point | `PayrollApplication` | [ ] |
| Entity | `Employee`(nonrest / evolution 변경), `Order`, `Status` | [ ] |
| Repository | `EmployeeRepository`, `OrderRepository` | [ ] |
| Controller | `EmployeeController`(4개 버전), `OrderController`, `RootController` | [ ] |
| Model Assembler | `EmployeeModelAssembler`, `OrderModelAssembler` | [ ] |
| Exception | `EmployeeNotFoundException`, `OrderNotFoundException` | [ ] |
| Exception Handler | `EmployeeNotFoundAdvice` | [ ] |
| 설정/초기화 | `LoadDatabase`(3개 버전) | [ ] |

### 학습 제외 대상 (이유)
- `.github/`, `.travis.yml`: CI 설정 — 애플리케이션 로직과 무관
- `mvnw`, `mvnw.cmd`, `.mvn/`: Maven Wrapper 자동 생성 스크립트
- `README.adoc`: 튜토리얼 본문 (코드 이해 후 참고 자료로 읽으면 좋음)
- `LICENSE.*`: 라이선스

---

# Part 0. 빌드 설정

## `pom.xml` (루트)

### 역할
4개 모듈을 묶는 부모 POM. Spring Boot 버전(`spring-boot-starter-parent 3.5.x`)과 Java 버전(17)을 한 곳에서 정하고, 모든 모듈에 테스트 의존성을 준다.

### 체크리스트
- [ ] `<packaging>pom</packaging>`이 "실행 코드 없이 하위 모듈을 묶는 용도"임을 설명할 수 있다.
- [ ] `<parent>spring-boot-starter-parent</parent>`가 의존성 버전을 관리해주기 때문에 하위 모듈에서 `<version>`을 안 적는다는 것을 설명할 수 있다.
- [ ] `<modules>`에 나열된 순서가 튜토리얼 진행 순서임을 설명할 수 있다.

## `nonrest/pom.xml`, `rest/pom.xml`, `evolution/pom.xml`, `links/pom.xml`

### 역할
각 모듈이 어떤 Spring 기능을 쓰는지 결정한다. 의존성이 곧 "자동 설정으로 켜지는 기능 목록"이다.

### 체크리스트
- [ ] `spring-boot-starter-web` → Spring MVC + 내장 Tomcat + Jackson이 들어온다는 것을 설명할 수 있다.
- [ ] `spring-boot-starter-data-jpa` → Spring Data JPA + Hibernate + 트랜잭션이 들어온다는 것을 설명할 수 있다.
- [ ] `h2`(`runtime` scope) → 인메모리 DB. 설정 파일이 없어도 Boot가 자동으로 `DataSource`를 만들고 테이블을 생성/삭제(create-drop)한다는 것을 설명할 수 있다.
- [ ] `spring-boot-starter-hateoas`는 `rest`부터 추가된다는 것 → `nonrest`에는 `EntityModel` 등이 없음을 설명할 수 있다.
- [ ] `spring-boot-maven-plugin`이 실행 가능한 jar를 만들고 `mvn spring-boot:run`을 가능하게 한다는 것을 설명할 수 있다.

---

# Part 1. `nonrest` 모듈 — 기본 CRUD

## `PayrollApplication` (Entry Point)

### 역할
애플리케이션 진입점. `main()`에서 Spring 컨테이너를 띄우고, 이 클래스가 있는 `payroll` 패키지를 기준으로 컴포넌트 스캔을 한다. (4개 모듈 모두 동일)

### 체크리스트
- [ ] 이 클래스가 왜 필요한지 설명할 수 있다.
- [ ] 이 클래스만 `public`이고 나머지 클래스는 package-private인 이유(외부에서 실행해야 하는 건 진입점뿐)를 설명할 수 있다.
- [ ] `@SpringBootApplication`이 합쳐진 3개 어노테이션(`@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`)을 설명할 수 있다.
- [ ] 컴포넌트 스캔 범위가 이 클래스의 패키지(`payroll`)와 하위 패키지라는 것을 설명할 수 있다.
- [ ] 이 코드에서 사용된 Java 개념: `public static void main`, 가변 인자 `String... args`

### 주요 메서드

- [ ] `main(String... args)`
  - 역할: `SpringApplication.run(PayrollApplication.class, args)`로 Spring Boot 앱 시작
  - 호출되는 곳: JVM(`java -jar` / IDE 실행 / `mvn spring-boot:run`)
  - 호출하는 코드: `SpringApplication.run()` → 컨텍스트 생성, Bean 등록, 내장 Tomcat 시작, `CommandLineRunner` 실행
  - 사용 개념: Spring Boot 부트스트랩, 자동 설정, IoC 컨테이너

---

## `Employee` (Entity)

### 역할
직원 정보를 담는 JPA 엔티티. DB의 `EMPLOYEE` 테이블 한 행과 1:1로 대응하며, **DTO 없이** 요청/응답 JSON으로도 그대로 사용된다.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(DB 테이블 ↔ 자바 객체 ↔ JSON의 공통 모델) 설명할 수 있다.
- [ ] 주요 필드의 역할을 설명할 수 있다: `id`(PK, 자동 생성), `name`, `role`
- [ ] `@Entity`가 JPA 관리 대상임을 표시한다는 것을 설명할 수 있다.
- [ ] `private @Id @GeneratedValue Long id;`에서 어노테이션이 필드 선언 중간에 있어도 필드에 붙는다는 것을 설명할 수 있다.
- [ ] `id`의 타입이 `long`이 아니라 `Long`인 이유(저장 전에는 `null` = "아직 id 없음"을 표현)를 설명할 수 있다.
- [ ] 생성자 2개가 왜 필요한지 설명할 수 있다:
  - `Employee() {}` — JPA와 Jackson이 리플렉션으로 객체를 만들 때 필요한 기본 생성자
  - `Employee(String name, String role)` — 개발자가 코드에서(`LoadDatabase`) 편하게 만들기 위한 생성자 (id는 DB가 채우므로 받지 않음)
- [ ] getter/setter가 Jackson 직렬화(getter)·역직렬화(setter)에 쓰인다는 것을 설명할 수 있다.
- [ ] `equals`/`hashCode`를 `Objects.equals`/`Objects.hash`로 재정의한 이유와, 둘을 **항상 함께** 재정의해야 하는 이유를 설명할 수 있다.
- [ ] `toString()`이 `LoadDatabase`의 로그 출력(`"Preloading " + employee`)에 쓰인다는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeRepository`의 관리 대상, `EmployeeController`의 입력/출력, `LoadDatabase`가 생성
- [ ] 사용 개념: JPA 엔티티 매핑, 캡슐화(private 필드 + public 접근자), `Object` 메서드 오버라이드, `@Override`, `instanceof` + 캐스팅

---

## `EmployeeRepository` (Repository, Interface)

### 역할
`Employee`의 DB 접근 담당. 코드 한 줄 없이 `JpaRepository`를 상속하는 것만으로 CRUD 메서드를 얻는다. 구현 클래스는 Spring Data JPA가 런타임에 만든다.

### 체크리스트
- [ ] 이 인터페이스가 왜 필요한지 설명할 수 있다.
- [ ] 구현 클래스가 없는데 동작하는 이유(Spring Data가 `SimpleJpaRepository` 기반 프록시를 Bean으로 등록)를 설명할 수 있다.
- [ ] 제네릭 `JpaRepository<Employee, Long>`의 두 타입 인자 의미를 설명할 수 있다.
- [ ] 인터페이스 상속(`extends`)으로 부모 인터페이스의 메서드를 물려받는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeController`, `LoadDatabase`에 주입됨
- [ ] 사용 개념: 인터페이스, 제네릭, Spring Data JPA, 프록시, 리포지토리 패턴

### 이 프로젝트에서 실제로 쓰는 상속 메서드

- [ ] `findAll()` — 전체 조회 (`List<Employee>`). 호출: `EmployeeController#all()`, `LoadDatabase`(links)
- [ ] `findById(Long)` — 단건 조회 (`Optional<Employee>`). 호출: `#one()`, `#replaceEmployee()`
- [ ] `save(Employee)` — id 없으면 INSERT, 있으면 UPDATE 후 저장된 엔티티 반환. 호출: `#newEmployee()`, `#replaceEmployee()`, `LoadDatabase`
- [ ] `deleteById(Long)` — 삭제 (없으면 무시). 호출: `#deleteEmployee()`

---

## `EmployeeController` (nonrest 버전)

### 역할
`/employees` 경로의 HTTP 요청을 받아 Repository를 호출하고 결과를 JSON으로 반환한다. Service 계층이 없으므로 컨트롤러가 곧바로 Repository를 사용한다.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지 설명할 수 있다.
- [ ] `@RestController` = `@Controller` + `@ResponseBody`(반환값을 뷰 이름이 아니라 HTTP 본문으로)를 설명할 수 있다.
- [ ] 필드 `private final EmployeeRepository repository`가 `final`인 이유(생성 후 바뀌지 않음, 반드시 생성자에서 초기화)를 설명할 수 있다.
- [ ] 생성자 `EmployeeController(EmployeeRepository repository)`가 왜 필요한지(생성자 주입, 생성자가 하나면 `@Autowired` 생략 가능) 설명할 수 있다.
- [ ] `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`이 HTTP 메서드 + URL을 메서드에 연결한다는 것을 설명할 수 있다.
- [ ] `@PathVariable`(URL 일부 → 파라미터)과 `@RequestBody`(요청 JSON → 객체)의 차이를 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeRepository` 사용, `Employee` 입출력, `EmployeeNotFoundException` 발생
- [ ] 사용 개념: 의존성 주입(DI), 요청 매핑, `Optional`, 람다

### 주요 메서드

- [ ] `all()`
  - 역할: 전체 직원 목록 반환
  - 호출되는 곳: `GET /employees` (DispatcherServlet)
  - 호출하는 코드: `repository.findAll()`
  - 사용 개념: `@GetMapping`, `List<Employee>` → JSON 배열 직렬화

- [ ] `newEmployee(@RequestBody Employee newEmployee)`
  - 역할: 요청 JSON으로 직원 생성
  - 호출되는 곳: `POST /employees`
  - 호출하는 코드: `repository.save(newEmployee)`
  - 사용 개념: `@RequestBody`, Jackson 역직렬화, 200 OK + 저장된 객체 반환

- [ ] `one(@PathVariable Long id)`
  - 역할: id로 직원 1명 조회, 없으면 예외
  - 호출되는 곳: `GET /employees/{id}`
  - 호출하는 코드: `repository.findById(id)`, `new EmployeeNotFoundException(id)`
  - 사용 개념: `Optional#orElseThrow(Supplier)`, 람다, `@PathVariable` 타입 변환

- [ ] `replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id)`
  - 역할: 있으면 이름/역할 수정, 없으면 새로 저장 (upsert)
  - 호출되는 곳: `PUT /employees/{id}`
  - 호출하는 코드: `repository.findById(id)`, `employee.setName/setRole`, `repository.save(...)`
  - 사용 개념: `Optional#map`, `Optional#orElseGet`, 람다 블록 `{ ...; return ...; }`
  - 주의: 없는 경우 경로의 `id`를 쓰지 않으므로 새 id가 발급됨

- [ ] `deleteEmployee(@PathVariable Long id)`
  - 역할: 직원 삭제
  - 호출되는 곳: `DELETE /employees/{id}`
  - 호출하는 코드: `repository.deleteById(id)`
  - 사용 개념: `void` 반환 → 200 OK 빈 본문

---

## `EmployeeNotFoundException` (Exception)

### 역할
요청한 id의 직원이 없을 때 던지는 커스텀 예외. 에러 메시지를 만들어 두고, `EmployeeNotFoundAdvice`가 이를 404 응답으로 바꾼다.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(“없음”이라는 상황에 이름을 붙여 특정 핸들러가 잡을 수 있게 함) 설명할 수 있다.
- [ ] `extends RuntimeException`(unchecked) vs `Exception`(checked)의 차이, 여기서 unchecked를 고른 이유를 설명할 수 있다.
- [ ] 생성자 `EmployeeNotFoundException(Long id)`가 `super(message)`로 부모에 메시지를 넘기는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeController#one()`이 생성/던짐 → `EmployeeNotFoundAdvice`가 받음
- [ ] 사용 개념: 상속, `super()` 생성자 호출, 예외 계층 구조

---

## `EmployeeNotFoundAdvice` (Exception Handler)

### 역할
모든 컨트롤러에서 발생한 `EmployeeNotFoundException`을 가로채 **HTTP 404 + 메시지 문자열**로 변환하는 전역 예외 처리기.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(컨트롤러마다 try-catch를 쓰지 않고 예외→응답 변환을 한 곳에 모음) 설명할 수 있다.
- [ ] `@RestControllerAdvice`가 "모든 컨트롤러에 적용되는 공통 처리 + 반환값을 응답 본문으로"라는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeNotFoundException`만 처리. **`OrderNotFoundException`은 처리하지 않음**(links 모듈에서 500 발생 원인)
- [ ] 사용 개념: AOP 스타일의 횡단 관심사 분리, `@ExceptionHandler`, `@ResponseStatus`

### 주요 메서드

- [ ] `employeeNotFoundHandler(EmployeeNotFoundException ex)`
  - 역할: 예외 메시지를 응답 본문으로 반환, 상태코드 404 설정
  - 호출되는 곳: Spring MVC 예외 처리 메커니즘(`ExceptionHandlerExceptionResolver`)이 컨트롤러에서 해당 예외가 던져졌을 때 자동 호출
  - 호출하는 코드: `ex.getMessage()`
  - 사용 개념: `@ExceptionHandler(EmployeeNotFoundException.class)`, `@ResponseStatus(HttpStatus.NOT_FOUND)`, `String` 반환 → text 본문

---

## `LoadDatabase` (Configuration / 초기화)

### 역할
애플리케이션 시작 시 샘플 직원 데이터를 DB에 넣는 초기화 설정 클래스.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(인메모리 DB는 시작할 때마다 비어 있음) 설명할 수 있다.
- [ ] `@Configuration`이 "Bean 정의를 담는 설정 클래스"임을 설명할 수 있다.
- [ ] 필드 `private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class)`의 역할(SLF4J 로거, 클래스당 하나)을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeRepository`를 주입받아 `Employee`를 저장
- [ ] 사용 개념: `@Configuration`, `@Bean`, `CommandLineRunner`, 함수형 인터페이스, 람다, SLF4J 로깅

### 주요 메서드

- [ ] `initDatabase(EmployeeRepository repository)`
  - 역할: `CommandLineRunner` Bean을 생성해 반환. 실제 데이터 저장은 반환된 람다가 나중에 실행될 때 일어남
  - 호출되는 곳: Spring 컨테이너가 Bean 생성 시 1회 호출 (`@Bean`)
  - 호출하는 코드: (람다 내부) `repository.save(new Employee("Bilbo Baggins", "burglar"))`, `repository.save(new Employee("Frodo Baggins", "thief"))`, `log.info(...)`
  - 사용 개념: **Bean 생성 시점**과 **람다 실행 시점**(앱 시작 완료 직전 `run()` 호출)이 다르다는 점, `@Bean` 메서드 파라미터 주입

---

# Part 2. `rest` 모듈 — HATEOAS 도입

> `PayrollApplication`, `Employee`, `EmployeeRepository`, `EmployeeNotFoundException`, `EmployeeNotFoundAdvice`, `LoadDatabase`는 **nonrest와 동일**하다.
> - [ ] 위 6개 클래스가 nonrest와 동일하다는 것을 확인했다.

## `EmployeeController` (rest 버전) — 변경점

### 역할
조회 API(`all`, `one`)의 응답에 링크를 붙여 HAL 형식으로 반환한다. 생성/수정/삭제는 nonrest와 동일하다.

### 체크리스트
- [ ] `import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;` static import를 설명할 수 있다.
- [ ] `EntityModel<T>`(단일 리소스 + 링크)와 `CollectionModel<T>`(컬렉션 + 링크)의 차이를 설명할 수 있다.
- [ ] `all()`과 `one()`에 **같은 링크 생성 코드가 중복**된다는 문제를 지적할 수 있다 (→ evolution에서 해결).
- [ ] POST/PUT은 여전히 `Employee`를 반환해 링크가 없다는 불일치를 찾을 수 있다.

### 주요 메서드

- [ ] `all()` (변경)
  - 역할: 전체 직원을 각각 `EntityModel`로 감싸고, 전체를 `CollectionModel`로 감싸 반환
  - 호출되는 곳: `GET /employees`
  - 호출하는 코드: `repository.findAll().stream().map(employee -> EntityModel.of(employee, linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(), linkTo(methodOn(EmployeeController.class).all()).withRel("employees"))).collect(Collectors.toList())`, `CollectionModel.of(...)`
  - 사용 개념: Stream API, 람다, `linkTo`/`methodOn`(프록시로 URL 생성), `withSelfRel`/`withRel`, HAL(`_embedded.employeeList`)

- [ ] `one(Long id)` (변경)
  - 역할: 조회한 직원을 `self`, `employees` 링크와 함께 `EntityModel`로 반환
  - 호출되는 곳: `GET /employees/{id}`
  - 호출하는 코드: `repository.findById(id).orElseThrow(...)`, `EntityModel.of(...)`, `linkTo(methodOn(...).one(id))`, `linkTo(methodOn(...).all())`
  - 사용 개념: `methodOn`이 메서드를 실제로 실행하지 않고 호출 정보만 기록한다는 점

---

# Part 3. `evolution` 모듈 — Assembler 분리 + 상태코드 + 스키마 진화

> `PayrollApplication`, `EmployeeRepository`, `EmployeeNotFoundException`, `EmployeeNotFoundAdvice`는 **이전과 동일**하다.
> - [ ] 위 4개 클래스가 이전 모듈과 동일하다는 것을 확인했다.

## `Employee` (evolution 버전) — 변경점

### 역할
`name` 필드를 `firstName`, `lastName`으로 나눴다. 대신 `getName()`/`setName()`을 **가상 속성**으로 남겨 옛 API 클라이언트와 호환된다.

### 체크리스트
- [ ] 필드 변경: `name` 삭제 → `firstName`, `lastName` 추가 (DB 컬럼도 바뀜)
- [ ] 생성자 변경: `Employee(String firstName, String lastName, String role)`
- [ ] `equals`/`hashCode`/`toString`도 새 필드 기준으로 바뀌었음을 확인할 수 있다.
- [ ] Jackson은 getter/setter 기준, JPA는 필드 기준(`@Id`가 필드에 있으므로 필드 접근)이라서 `name`이 JSON에는 있지만 DB에는 없다는 것을 설명할 수 있다.
- [ ] 사용 개념: 하위 호환성(backward compatibility), 계산된 속성, JPA 접근 방식(field access)

### 주요 메서드

- [ ] `getName()`
  - 역할: `firstName + " " + lastName`을 합쳐 반환 (저장된 필드 아님)
  - 호출되는 곳: Jackson 직렬화(응답 JSON의 `"name"`), `EmployeeController#replaceEmployee`(`newEmployee.getName()`)
  - 호출하는 코드: 없음 (문자열 연결)
  - 사용 개념: 계산된 getter, JavaBeans 프로퍼티 규약

- [ ] `setName(String name)`
  - 역할: `"First Last"`를 공백으로 나눠 `firstName`, `lastName`에 저장
  - 호출되는 곳: Jackson 역직렬화(요청 JSON에 `"name"`이 있을 때), `EmployeeController#replaceEmployee`
  - 호출하는 코드: `String#split(" ")`
  - 사용 개념: 배열, 문자열 분리
  - 주의: 공백이 없으면 `parts[1]`에서 `ArrayIndexOutOfBoundsException` → JSON 역직렬화 중 예외라 **400 Bad Request** (POST/PUT 모두, 직접 확인함), 3단어 이상이면 나머지 손실

---

## `EmployeeModelAssembler` (신규)

### 역할
`Employee` → `EntityModel<Employee>` 변환(링크 추가)을 전담하는 컴포넌트. rest 모듈의 중복된 링크 생성 코드를 한 곳으로 모았다.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(중복 제거, 단일 책임, 링크 규칙 변경 시 한 곳만 수정) 설명할 수 있다.
- [ ] `@Component`로 Bean 등록 → `EmployeeController` 생성자에 주입된다는 것을 설명할 수 있다.
- [ ] `implements RepresentationModelAssembler<Employee, EntityModel<Employee>>`가 "Employee를 받아 EntityModel<Employee>를 만든다"는 **계약**임을 설명할 수 있다.
- [ ] 이 인터페이스에 `toCollectionModel()` 기본 메서드도 있지만 이 프로젝트는 쓰지 않고 컨트롤러에서 stream으로 직접 모은다는 것을 알고 있다.
- [ ] 다른 클래스와의 관계: `EmployeeController`가 사용, 링크 생성을 위해 `EmployeeController`의 메서드(`one`, `all`)를 `methodOn`으로 참조 (서로 참조하는 관계)
- [ ] 사용 개념: 인터페이스 구현, 제네릭 인터페이스, `@Override`, `@Component`

### 주요 메서드

- [ ] `toModel(Employee employee)`
  - 역할: 직원을 `self`(`/employees/{id}`), `employees`(`/employees`) 링크와 함께 감싸 반환
  - 호출되는 곳: `EmployeeController#all()`(`assembler::toModel` 메서드 참조), `#one()`, `#newEmployee()`, `#replaceEmployee()`
  - 호출하는 코드: `EntityModel.of(...)`, `linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel()`, `linkTo(methodOn(EmployeeController.class).all()).withRel("employees")`
  - 사용 개념: HATEOAS, 어셈블러 패턴

---

## `EmployeeController` (evolution 버전) — 변경점

### 역할
모든 응답을 `EmployeeModelAssembler`로 만들고, `ResponseEntity`로 HTTP 상태코드(201, 204)와 `Location` 헤더를 명시한다.

### 체크리스트
- [ ] 새 필드 `private final EmployeeModelAssembler assembler`와 생성자 파라미터가 추가된 것을 설명할 수 있다.
- [ ] `ResponseEntity`가 "상태코드 + 헤더 + 본문"을 직접 제어하는 응답 객체임을 설명할 수 있다.
- [ ] `ResponseEntity<?>`에서 `?`(와일드카드)를 쓴 이유(본문 타입을 특정하지 않음)를 설명할 수 있다.
- [ ] 사용 개념: 메서드 참조, 빌더 패턴, HTTP 상태코드 의미(201 Created, 204 No Content)

### 주요 메서드

- [ ] `all()` (변경)
  - 역할: rest와 같은 결과, 단 `map(assembler::toModel)` 사용
  - 호출되는 곳: `GET /employees`
  - 호출하는 코드: `repository.findAll()`, `assembler.toModel`, `CollectionModel.of(...)`
  - 사용 개념: **메서드 참조** `assembler::toModel` = `e -> assembler.toModel(e)`

- [ ] `newEmployee(Employee newEmployee)` (변경)
  - 역할: 저장 후 201 Created + `Location` 헤더 + HAL 본문 반환
  - 호출되는 곳: `POST /employees`
  - 호출하는 코드: `repository.save()`, `assembler.toModel()`, `entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()`, `ResponseEntity.created(uri).body(entityModel)`
  - 사용 개념: `IanaLinkRelations`(표준 링크 관계 이름), `Link → URI` 변환

- [ ] `one(Long id)` (변경)
  - 역할: rest와 같은 결과, 링크 생성은 `assembler.toModel(employee)`
  - 호출되는 곳: `GET /employees/{id}`
  - 호출하는 코드: `repository.findById(id).orElseThrow(...)`, `assembler.toModel()`
  - 사용 개념: 위임(delegation)

- [ ] `replaceEmployee(Employee newEmployee, Long id)` (변경)
  - 역할: upsert 후 201 Created + `Location` + HAL 본문
  - 호출되는 곳: `PUT /employees/{id}`
  - 호출하는 코드: `findById().map(...).orElseGet(...)`, `employee.setName(newEmployee.getName())`(→ evolution의 split 로직), `assembler.toModel()`, `ResponseEntity.created(...)`
  - 사용 개념: `Optional` 체이닝, 수정에도 201을 쓰는 튜토리얼의 설계 선택

- [ ] `deleteEmployee(Long id)` (변경)
  - 역할: 삭제 후 204 No Content
  - 호출되는 곳: `DELETE /employees/{id}`
  - 호출하는 코드: `repository.deleteById(id)`, `ResponseEntity.noContent().build()`
  - 사용 개념: 본문 없는 응답

---

## `LoadDatabase` (evolution 버전) — 변경점

- [ ] `new Employee("Bilbo", "Baggins", "burglar")`처럼 새 3-인자 생성자를 사용한다는 것을 설명할 수 있다.

---

# Part 4. `links` 모듈 — Order 도메인 + 상태 기반 링크

> `PayrollApplication`, `Employee`(evolution 버전), `EmployeeRepository`, `EmployeeModelAssembler`, `EmployeeNotFoundException`, `EmployeeNotFoundAdvice`는 **evolution과 동일**하다.
> - [ ] 위 6개 클래스가 evolution과 동일하다는 것을 확인했다.

## `Status` (Enum)

### 역할
주문 상태를 나타내는 열거형: `IN_PROGRESS`, `COMPLETED`, `CANCELLED`. 주문 흐름의 상태 기계를 표현한다.

### 체크리스트
- [ ] 이 enum이 왜 필요한지(문자열 대신 허용된 값만 쓰도록 타입으로 제한) 설명할 수 있다.
- [ ] enum 비교에 `==`를 쓸 수 있는 이유(각 상수는 JVM에 하나만 존재)를 설명할 수 있다.
- [ ] JPA가 `@Enumerated` 없이 저장하면 **ORDINAL**(0, 1, 2)로 저장되어 상수 순서를 바꾸면 기존 데이터 의미가 바뀐다는 위험을 설명할 수 있다.
- [ ] JSON에서는 이름 문자열(`"IN_PROGRESS"`)로 직렬화된다는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `Order#status` 필드 타입, `OrderController`/`OrderModelAssembler`의 분기 조건, `LoadDatabase`의 초기값
- [ ] 사용 개념: `enum`, 상태 기계

---

## `Order` (Entity)

### 역할
주문 정보를 담는 JPA 엔티티(`description`, `status`). `CUSTOMER_ORDER` 테이블에 매핑된다.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지 설명할 수 있다.
- [ ] 주요 필드: `id`(PK, 자동 생성), `description`(주문 내용), `status`(`Status` enum)
- [ ] `@Table(name = "CUSTOMER_ORDER")`가 필요한 이유(`ORDER`는 SQL 예약어라 테이블명으로 쓰면 SQL 오류)를 설명할 수 있다.
- [ ] 생성자: 기본 생성자(JPA/Jackson용), `Order(String description, Status status)`(초기 데이터용)
- [ ] `equals`에서 `status`는 `==`, 나머지는 `Objects.equals`로 비교하는 이유(enum은 `==`로 충분, 객체는 null-safe 비교)를 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `OrderRepository` 관리 대상, `OrderController` 입출력, `OrderModelAssembler` 변환 대상, `LoadDatabase` 생성
- [ ] 사용 개념: `@Entity`, `@Table`, enum 필드 매핑

---

## `OrderRepository` (Repository, Interface)

### 역할
`Order`의 DB 접근 담당. `EmployeeRepository`와 같은 구조.

### 체크리스트
- [ ] `JpaRepository<Order, Long>`의 의미를 설명할 수 있다.
- [ ] 사용되는 메서드: `findAll()`(`OrderController#all`, `LoadDatabase`), `findById()`(`#one`, `#cancel`, `#complete`), `save()`(`#newOrder`, `#cancel`, `#complete`, `LoadDatabase`)
- [ ] 다른 클래스와의 관계: `OrderController`, `LoadDatabase`에 주입

---

## `OrderNotFoundException` (Exception)

### 역할
주문 id가 없을 때 던지는 예외. 메시지 `"Could not find order " + id`.

### 체크리스트
- [ ] `EmployeeNotFoundException`과 같은 구조임을 설명할 수 있다.
- [ ] **대응하는 `@ExceptionHandler`가 없다**는 것을 찾을 수 있고, 그래서 클라이언트가 404가 아닌 **500 Internal Server Error**를 받는다는 것을 설명할 수 있다.
- [ ] 이를 고치려면 `OrderNotFoundAdvice`를 만들거나 기존 Advice에 핸들러를 추가하면 된다는 것을 설명할 수 있다. (학습용 개선 과제)
- [ ] 다른 클래스와의 관계: `OrderController#one`, `#cancel`, `#complete`가 던짐

---

## `OrderModelAssembler` (Component)

### 역할
`Order` → `EntityModel<Order>` 변환. **주문 상태에 따라 가능한 동작 링크만** 추가한다 (이 프로젝트에서 HATEOAS의 핵심 가치를 보여주는 클래스).

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(클라이언트가 상태 규칙을 몰라도 링크 유무로 가능한 동작을 판단) 설명할 수 있다.
- [ ] `EmployeeModelAssembler`와의 차이(조건부 링크)를 설명할 수 있다.
- [ ] `EntityModel#add(Link)`로 생성 후에 링크를 추가할 수 있다는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `OrderController`가 사용, `OrderController`의 `one`/`all`/`cancel`/`complete`를 `methodOn`으로 참조. `EmployeeController`에도 주입되지만 **사용되지 않음**.
- [ ] 사용 개념: 조건 분기, HATEOAS 상태 기반 링크(hypermedia as the engine of application state)

### 주요 메서드

- [ ] `toModel(Order order)`
  - 역할: 항상 `self`, `orders` 링크 추가 / `IN_PROGRESS`이면 `cancel`, `complete` 링크 추가
  - 호출되는 곳: `OrderController#all()`(`assembler::toModel`), `#one()`, `#newOrder()`, `#cancel()`, `#complete()`
  - 호출하는 코드: `EntityModel.of(...)`, `linkTo(methodOn(OrderController.class).one/all/cancel/complete(...))`, `orderModel.add(...)`, `order.getStatus()`
  - 사용 개념: `methodOn`으로 `cancel(id)` 같은 DELETE/PUT 메서드의 URL도 만들 수 있음 (링크는 URL만 담고 HTTP 메서드는 담지 않음)

---

## `OrderController` (Controller)

### 역할
`/orders` 관련 요청 처리: 목록/단건 조회, 생성, 취소, 완료. 상태 전이 규칙(`IN_PROGRESS`에서만 취소/완료 가능)을 검사한다. 이 프로젝트에서 **비즈니스 로직이 들어간 유일한 컨트롤러**다.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지 설명할 수 있다.
- [ ] 필드 `orderRepository`, `assembler`와 생성자 주입을 설명할 수 있다.
- [ ] Service 계층이 있었다면 상태 전이 로직(`if (status == IN_PROGRESS) ...`)이 어디로 옮겨졌을지 말할 수 있다.
- [ ] `Problem`(RFC 7807)으로 에러 본문을 만들고 `Content-Type: application/problem+json`을 지정하는 이유를 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `OrderRepository`, `OrderModelAssembler`, `Order`, `Status`, `OrderNotFoundException`
- [ ] 사용 개념: `ResponseEntity` 빌더, `HttpStatus`, `HttpHeaders`, `MediaTypes`, `Problem`

### 주요 메서드

- [ ] `all()`
  - 역할: 전체 주문을 `CollectionModel`로 반환
  - 호출되는 곳: `GET /orders`, 링크 생성용 `methodOn(OrderController.class).all()`(`RootController`, `OrderModelAssembler`)
  - 호출하는 코드: `orderRepository.findAll()`, `assembler::toModel`, `CollectionModel.of(...)`
  - 사용 개념: Stream, 메서드 참조

- [ ] `one(Long id)`
  - 역할: 주문 1건 조회, 없으면 `OrderNotFoundException`
  - 호출되는 곳: `GET /orders/{id}`, 링크 생성(`OrderModelAssembler`, `#newOrder`)
  - 호출하는 코드: `orderRepository.findById(id).orElseThrow(...)`, `assembler.toModel(order)`
  - 사용 개념: `Optional`

- [ ] `newOrder(@RequestBody Order order)`
  - 역할: 상태를 `IN_PROGRESS`로 강제 설정 후 저장, 201 Created 반환
  - 호출되는 곳: `POST /orders`
  - 호출하는 코드: `order.setStatus(Status.IN_PROGRESS)`, `orderRepository.save(order)`, `linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri()`, `ResponseEntity.created(...).body(assembler.toModel(newOrder))`
  - 사용 개념: 서버가 초기 상태를 통제, `ResponseEntity<EntityModel<Order>>` 구체 제네릭

- [ ] `cancel(@PathVariable Long id)`
  - 역할: `IN_PROGRESS` 주문을 `CANCELLED`로 변경(200), 아니면 405 Problem
  - 호출되는 곳: `DELETE /orders/{id}/cancel`, 링크 생성(`OrderModelAssembler`)
  - 호출하는 코드: `findById().orElseThrow()`, `order.getStatus()`, `order.setStatus(Status.CANCELLED)`, `orderRepository.save()`, `ResponseEntity.ok(...)`, `ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).header(...).body(Problem.create().withTitle(...).withDetail(...))`
  - 사용 개념: DELETE 매핑이지만 실제 삭제가 아닌 상태 변경(soft 처리), 조기 return 패턴

- [ ] `complete(@PathVariable Long id)`
  - 역할: `IN_PROGRESS` 주문을 `COMPLETED`로 변경(200), 아니면 405 Problem
  - 호출되는 곳: `PUT /orders/{id}/complete`, 링크 생성(`OrderModelAssembler`)
  - 호출하는 코드: `cancel`과 동일 구조, 상태만 `Status.COMPLETED`
  - 사용 개념: 같은 구조의 중복 코드 → 리팩토링 여지 생각해보기

---

## `RootController` (Controller, 신규)

### 역할
API의 시작점 `/`에서 `employees`, `orders` 링크만 제공한다. DB 접근 없음.

### 체크리스트
- [ ] 이 클래스가 왜 필요한지(클라이언트가 URL 하나만 알면 나머지를 탐색) 설명할 수 있다.
- [ ] 필드와 생성자가 없는 이유(의존하는 Bean이 없음)를 설명할 수 있다.
- [ ] 경로 없는 `@GetMapping`이 루트(`/`)에 매핑된다는 것을 설명할 수 있다.
- [ ] 다른 클래스와의 관계: `EmployeeController`, `OrderController`를 `methodOn`으로 참조만 함
- [ ] 사용 개념: `RepresentationModel`(링크만 담는 기본 모델), 제네릭 와일드카드 `RepresentationModel<?>`

### 주요 메서드

- [ ] `index()`
  - 역할: 빈 `RepresentationModel`에 `employees`, `orders` 링크를 추가해 반환
  - 호출되는 곳: `GET /`
  - 호출하는 코드: `new RepresentationModel<>()`, `rootModel.add(linkTo(methodOn(EmployeeController.class).all()).withRel("employees"))`, `rootModel.add(linkTo(methodOn(OrderController.class).all()).withRel("orders"))`
  - 사용 개념: 다이아몬드 연산자 `<>`, HATEOAS 진입점

---

## `EmployeeController` (links 버전) — 변경점

- [ ] 필드 `private final OrderModelAssembler orderAssembler`와 생성자 파라미터가 추가되었지만 **어떤 메서드에서도 사용하지 않는다**는 것을 찾을 수 있다. (튜토리얼 흔적)
- [ ] 나머지 메서드(`all`, `newEmployee`, `one`, `replaceEmployee`, `deleteEmployee`)는 evolution과 동일하다는 것을 확인했다.

## `LoadDatabase` (links 버전) — 변경점

- [ ] `initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository)`로 `@Bean` 메서드가 **두 개의 Bean을 주입**받는다는 것을 설명할 수 있다.
- [ ] 저장 후 `findAll().forEach(employee -> log.info(...))`로 로그를 찍는 방식(`Iterable#forEach` + 람다)으로 바뀐 것을 설명할 수 있다.
- [ ] `Order("MacBook Pro", Status.COMPLETED)`, `Order("iPhone", Status.IN_PROGRESS)` 두 주문이 서로 다른 상태로 들어가서 `/orders` 응답의 링크 차이를 바로 확인할 수 있다는 것을 설명할 수 있다.

---

# Part 5. 코드에는 없지만 흐름 이해에 필요한 프레임워크 구성 요소

코드에 직접 등장하지 않지만, 위 클래스들이 동작하려면 반드시 이해해야 하는 부분.

- [ ] **DispatcherServlet**: 모든 HTTP 요청을 받아 `@XxxMapping`이 맞는 컨트롤러 메서드로 보내는 Spring MVC의 프런트 컨트롤러
- [ ] **HandlerMethodArgumentResolver**: `@PathVariable`, `@RequestBody` 값을 만들어 메서드 인자로 넣어줌
- [ ] **HttpMessageConverter / Jackson `ObjectMapper`**: 객체 ↔ JSON 변환 (HATEOAS 모듈은 HAL 형식으로)
- [ ] **Spring Data JPA 프록시(`SimpleJpaRepository`)**: Repository 인터페이스의 실제 구현. 각 메서드는 트랜잭션 안에서 실행됨
- [ ] **Hibernate(EntityManager)**: 엔티티를 SQL로 변환해 실행하는 JPA 구현체
- [ ] **H2 자동 설정**: 설정 파일 없이 인메모리 DB + 엔티티 기반 테이블 자동 생성
- [ ] **내장 Tomcat**: 기본 포트 8080에서 HTTP 요청 수신
- [ ] **Bean 생명주기**: 스캔 → 생성(생성자 주입) → 준비 완료 → `CommandLineRunner` 실행

---

## 최종 자기 점검

- [ ] 모든 `.java` 파일(4개 모듈, 총 37개: nonrest 7, rest 7, evolution 8, links 15)에 대해 "이 파일이 없으면 무엇이 안 되는가"를 말할 수 있다.
- [ ] 같은 이름의 클래스가 모듈별로 어떻게 달라졌는지 표로 정리할 수 있다.
- [ ] `EmployeeController` ↔ `EmployeeModelAssembler`가 서로를 참조하는 구조를 그림으로 그릴 수 있다.
- [ ] 이 프로젝트의 알려진 한계 4가지를 말할 수 있다:
  1. `OrderNotFoundException` 핸들러 없음 → 500
  2. `Employee#setName`의 공백 없는 이름 처리 불가
  3. `PUT`의 "없음" 분기에서 경로 id 무시
  4. `EmployeeController`(links)의 미사용 `orderAssembler`
