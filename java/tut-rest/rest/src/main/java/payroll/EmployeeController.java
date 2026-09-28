package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 아래 두 줄은 README 문서가 코드 일부를 잘라 보여 줄 때 쓰는 표시(마커)이므로 수정하면 안 됩니다.
// tag::hateoas-imports[]
// end::hateoas-imports[]

/**
 * 직원(Employee)과 관련된 웹 요청을 받아서 처리하는 "접수 창구" 클래스(컨트롤러)입니다.
 * <p>
 * 컨트롤러(Controller)는 손님(웹 브라우저, 앱, 다른 프로그램)의 요청을 가장 먼저 받는 곳입니다.
 * 비유하면 은행 창구 직원처럼 "어떤 업무로 오셨나요?"를 확인한 뒤,
 * 실제 데이터 작업은 창고 관리인({@link EmployeeRepository})에게 맡기고 결과를 손님에게 돌려줍니다.
 * <p>
 * {@code @RestController}는 "이 클래스는 REST API 요청을 처리하며,
 * 메서드가 돌려주는 값을 웹 페이지(HTML)가 아니라 데이터(JSON 등) 그대로 응답 본문에 담아 보낸다"는 표시입니다.
 * <p>
 * 이 컨트롤러가 처리하는 요청 목록 (HTTP 메서드 = 요청의 종류):
 * <ul>
 *   <li>GET    /employees      → 직원 전체 목록 조회</li>
 *   <li>POST   /employees      → 새 직원 등록</li>
 *   <li>GET    /employees/{id} → 특정 번호(id) 직원 한 명 조회</li>
 *   <li>PUT    /employees/{id} → 특정 번호 직원 정보 수정(없으면 새로 등록)</li>
 *   <li>DELETE /employees/{id} → 특정 번호 직원 삭제</li>
 * </ul>
 * <p>
 * <b>HATEOAS(하이퍼미디어 링크)란?</b><br>
 * 이 모듈은 Spring HATEOAS를 사용해 조회 결과에 "링크"를 함께 담아 보냅니다.
 * (HATEOAS: Hypermedia As The Engine Of Application State의 약자. 쉽게 말해
 *  "응답 안에 다음에 갈 수 있는 주소(링크)를 함께 넣어 주자"는 REST 설계 원칙)
 * 웹 페이지에서 링크를 눌러 다른 페이지로 이동하듯, API를 사용하는 프로그램도
 * 응답에 들어 있는 링크를 따라가면 다음 작업을 할 수 있습니다.
 * 덕분에 사용하는 쪽이 주소를 외우거나 직접 조립할 필요가 줄고,
 * 나중에 서버의 주소 구조가 바뀌어도 링크만 따라가면 되므로 변화에 강해집니다.
 * <p>
 * 예) GET /employees/1 의 응답 모양 (대략):
 * <pre>
 * {
 *   "id": 1, "name": "Bilbo Baggins", "role": "burglar",
 *   "_links": {
 *     "self":      { "href": "http://localhost:8080/employees/1" },  ← 나 자신의 주소
 *     "employees": { "href": "http://localhost:8080/employees" }     ← 전체 목록 주소
 *   }
 * }
 * </pre>
 */
@RestController
class EmployeeController {

	// 직원 데이터를 저장/조회할 때 사용하는 저장소(리포지토리)입니다.
	// final : 한 번 정해지면 다른 것으로 바꿀 수 없다는 뜻입니다.
	private final EmployeeRepository repository;

	/**
	 * 컨트롤러를 만드는 생성자입니다.
	 * 스프링이 이 컨트롤러를 만들 때, 필요한 리포지토리 객체를 자동으로 찾아서 넣어 줍니다.
	 * (이것을 "생성자 주입" 또는 "의존성 주입"이라고 합니다. 필요한 부품을 외부에서 끼워 주는 방식)
	 *
	 * @param repository 스프링이 넣어 주는 직원 저장소
	 */
	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}

	// 집합 루트(Aggregate root): 직원 "전체 모음"을 다루는 주소(/employees)에 대한 처리들

	/**
	 * [GET /employees] 직원 전체 목록을 조회합니다. (링크 포함)
	 * <p>
	 * {@code @GetMapping("/employees")} : HTTP GET 방식으로 "/employees" 주소에 요청이 오면
	 * 이 메서드를 실행하라는 표시입니다. (GET: 데이터를 "읽어 오는" 요청)
	 * <p>
	 * 처리 순서:
	 * <ol>
	 *   <li>데이터베이스에서 모든 직원을 꺼냅니다. ({@code repository.findAll()})</li>
	 *   <li>직원 한 명 한 명을 {@code EntityModel}(데이터 + 링크를 함께 담는 상자)로 감쌉니다.
	 *       이때 각 직원에게 "자기 자신 주소(self)" 링크와 "전체 목록 주소(employees)" 링크를 붙입니다.</li>
	 *   <li>감싼 직원들을 리스트로 모읍니다.</li>
	 *   <li>그 리스트 전체를 다시 {@code CollectionModel}(여러 개의 데이터 + 링크를 담는 상자)로 감싸고,
	 *       "이 목록 자신의 주소(self)" 링크를 붙여서 돌려줍니다.</li>
	 * </ol>
	 *
	 * @return 링크가 붙은 직원 목록 (JSON으로 변환되어 응답됨)
	 */
	// tag::get-aggregate-root[]
	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

		// findAll()로 전체 직원을 가져와 stream()(데이터를 하나씩 흘려보내며 가공하는 도구)으로 처리합니다.
		// map(employee -> ...) : 각 직원(employee)을 "링크가 붙은 상자(EntityModel)"로 바꿉니다. (람다: 이름 없는 즉석 함수)
		//   - linkTo(methodOn(EmployeeController.class).one(id)) : "one(id) 메서드가 담당하는 주소"를 자동으로 계산합니다.
		//     (예: /employees/1) 주소를 문자열로 직접 적지 않아서, 주소가 바뀌어도 링크가 자동으로 따라 바뀝니다.
		//   - withSelfRel() : 그 링크에 "self(나 자신)"라는 이름을 붙입니다.
		//   - withRel("employees") : 전체 목록 주소 링크에 "employees"라는 이름을 붙입니다.
		// collect(Collectors.toList()) : 가공된 결과들을 다시 하나의 리스트로 모읍니다.
		List<EntityModel<Employee>> employees = repository.findAll().stream()
				.map(employee -> EntityModel.of(employee,
						linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
						linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
				.collect(Collectors.toList());

		// 직원 목록 전체를 CollectionModel로 감싸고, 목록 자신의 주소(/employees)를 self 링크로 붙여 반환합니다.
		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
	// end::get-aggregate-root[]

	/**
	 * [POST /employees] 새 직원을 등록합니다.
	 * <p>
	 * {@code @PostMapping("/employees")} : HTTP POST 방식(새 데이터를 "만들어 달라"는 요청)으로
	 * "/employees"에 요청이 오면 이 메서드를 실행합니다.
	 * <p>
	 * {@code @RequestBody} : 요청 본문(body)에 담겨 온 JSON 데이터
	 * (예: {"name":"Samwise Gamgee", "role":"gardener"})를 자동으로 Employee 객체로 바꿔 줍니다.
	 *
	 * @param newEmployee 요청 본문에서 변환된 새 직원 정보
	 * @return 저장이 끝난 직원 정보 (데이터베이스가 매겨 준 id 포함)
	 */
	@PostMapping("/employees")
	Employee newEmployee(@RequestBody Employee newEmployee) {
		// 전달받은 직원을 데이터베이스에 저장하고, 저장된 결과를 그대로 돌려줍니다.
		return repository.save(newEmployee);
	}

	// 단일 항목(Single item): 직원 "한 명"을 다루는 주소(/employees/{id})에 대한 처리들

	/**
	 * [GET /employees/{id}] 번호(id)로 직원 한 명을 조회합니다. (링크 포함)
	 * <p>
	 * 주소의 {id} 부분은 "빈칸"으로, 실제 요청에서는 /employees/1 처럼 숫자가 들어옵니다.
	 * {@code @PathVariable} : 주소 속 빈칸({id})에 들어온 값을 꺼내서 매개변수 id에 넣어 줍니다.
	 * <p>
	 * 처리 순서:
	 * <ol>
	 *   <li>{@code findById(id)}로 직원을 찾습니다. 결과는 Optional(값이 "있을 수도, 없을 수도 있는" 상자)로 옵니다.</li>
	 *   <li>{@code orElseThrow(...)} : 상자가 비어 있으면(직원이 없으면) EmployeeNotFoundException을 던집니다.
	 *       이 예외는 {@link EmployeeNotFoundAdvice}가 받아서 404(찾을 수 없음) 응답으로 바꿔 줍니다.</li>
	 *   <li>직원을 찾았다면 EntityModel로 감싸고 self 링크와 employees(전체 목록) 링크를 붙여 돌려줍니다.</li>
	 * </ol>
	 *
	 * @param id 조회할 직원 번호 (주소에서 꺼낸 값)
	 * @return 링크가 붙은 직원 정보
	 */
	// tag::get-single-item[]
	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {

		// 직원을 찾고, 없으면 "() -> new EmployeeNotFoundException(id)" 람다가 만든 예외를 던집니다.
		Employee employee = repository.findById(id) //
				.orElseThrow(() -> new EmployeeNotFoundException(id));

		// 찾은 직원 데이터에 두 개의 링크를 붙여서 반환합니다.
		//   - self      : 이 직원 자신의 주소 (예: /employees/1)
		//   - employees : 직원 전체 목록 주소 (/employees)
		return EntityModel.of(employee, //
				linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
	// end::get-single-item[]

	/**
	 * [PUT /employees/{id}] 번호(id)에 해당하는 직원 정보를 수정합니다. 없으면 새로 등록합니다.
	 * <p>
	 * {@code @PutMapping} : HTTP PUT 방식(기존 데이터를 "새 내용으로 교체해 달라"는 요청)을 처리합니다.
	 * {@code @RequestBody} : 요청 본문의 JSON을 Employee 객체(newEmployee)로 바꿔 줍니다.
	 * {@code @PathVariable} : 주소의 {id} 값을 꺼내 줍니다.
	 * <p>
	 * 처리 순서:
	 * <ol>
	 *   <li>{@code findById(id)}로 기존 직원을 찾습니다.</li>
	 *   <li>{@code map(...)} : 직원이 "있으면" 이름과 역할을 새 값으로 바꾼 뒤 저장합니다.</li>
	 *   <li>{@code orElseGet(...)} : 직원이 "없으면" 요청으로 받은 newEmployee를 그대로 새로 저장합니다.</li>
	 * </ol>
	 *
	 * @param newEmployee 요청 본문에서 변환된 새 직원 정보
	 * @param id 수정할 직원 번호 (주소에서 꺼낸 값)
	 * @return 저장이 끝난 직원 정보
	 */
	@PutMapping("/employees/{id}")
	Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		return repository.findById(id) //
				// 기존 직원이 있을 때: 이름·역할을 새 값으로 덮어쓰고 저장
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}) //
				// 기존 직원이 없을 때: 요청으로 받은 직원을 새로 저장
				.orElseGet(() -> {
					return repository.save(newEmployee);
				});
	}

	/**
	 * [DELETE /employees/{id}] 번호(id)에 해당하는 직원을 삭제합니다.
	 * <p>
	 * {@code @DeleteMapping} : HTTP DELETE 방식(데이터를 "지워 달라"는 요청)을 처리합니다.
	 * 돌려주는 값이 없으므로(void) 응답 본문은 비어 있습니다.
	 *
	 * @param id 삭제할 직원 번호 (주소에서 꺼낸 값)
	 */
	@DeleteMapping("/employees/{id}")
	void deleteEmployee(@PathVariable Long id) {
		// 해당 번호의 직원을 데이터베이스에서 삭제합니다.
		repository.deleteById(id);
	}
}
