package payroll;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 직원 관련 웹 요청을 받아서 처리하는 "접수 창구" 역할의 컨트롤러(Controller) 클래스입니다.
 *
 * <p>컨트롤러란 사용자가 보낸 HTTP 요청(웹 브라우저나 프로그램이 서버에 보내는 요청)을
 * 가장 먼저 받아, 어떤 일을 할지 결정하고 결과를 돌려주는 곳입니다.
 * 은행 창구 직원처럼 손님(요청)을 받아 금고(데이터베이스, 여기서는 {@link EmployeeRepository})에서
 * 필요한 것을 꺼내 주거나 넣어 줍니다.</p>
 *
 * <p>{@code @RestController}는 "이 클래스는 웹 요청을 처리하는 컨트롤러이며,
 * 메서드가 반환하는 값을 화면(HTML)이 아닌 데이터(주로 JSON 형식) 그대로 응답에 담아 보낸다"는 표식입니다.
 * (JSON: {@code {"id":1,"name":"Bilbo Baggins","role":"burglar"}} 처럼 데이터를 글자로 표현하는 형식)</p>
 *
 * <p>HTTP 메서드(요청의 종류)별로 다음 기능을 제공합니다.</p>
 * <ul>
 *   <li>GET    /employees      → 직원 전체 목록 조회</li>
 *   <li>POST   /employees      → 새 직원 등록</li>
 *   <li>GET    /employees/{id} → 특정 직원 한 명 조회</li>
 *   <li>PUT    /employees/{id} → 특정 직원 정보 수정(없으면 새로 저장)</li>
 *   <li>DELETE /employees/{id} → 특정 직원 삭제</li>
 * </ul>
 */
@RestController
class EmployeeController {

	// 직원 데이터를 저장/조회/삭제할 때 사용하는 저장소입니다.
	// final 이므로 생성자에서 한 번 정해진 뒤에는 바뀌지 않습니다.
	private final EmployeeRepository repository;

	/**
	 * 컨트롤러를 만들 때 스프링이 자동으로 호출하는 생성자입니다.
	 * 필요한 {@link EmployeeRepository}를 스프링이 알아서 찾아 넣어 줍니다
	 * (이를 "생성자 주입" 또는 의존성 주입(DI)이라고 합니다. 필요한 부품을 외부에서 끼워 주는 방식).
	 *
	 * @param repository 스프링이 넘겨주는 직원 저장소
	 */
	EmployeeController(EmployeeRepository repository) {
		this.repository = repository;
	}


	// 집합 루트(Aggregate root): 직원 "전체 모음"을 다루는 주소(/employees)에 대한 처리
	// (아래 tag/end 주석은 README.adoc 문서에서 코드 일부를 잘라 보여주기 위한 표시이므로 수정하면 안 됩니다)
	/**
	 * GET /employees → 직원 전체 목록 조회
	 *
	 * <p>{@code @GetMapping("/employees")}: HTTP GET 방식(데이터를 "읽기"만 하는 요청)으로
	 * {@code /employees} 주소에 요청이 오면 이 메서드를 실행하라는 표식입니다.</p>
	 * <p>저장소의 {@code findAll()}로 모든 직원을 꺼내 리스트(List, 여러 개를 순서대로 담는 목록)로 반환하면,
	 * 스프링이 이를 JSON 배열로 바꿔 응답합니다.</p>
	 *
	 * @return 모든 직원 목록
	 */
	// tag::get-aggregate-root[]
	@GetMapping("/employees")
	List<Employee> all() {
		return repository.findAll();
	}
	// end::get-aggregate-root[]

	/**
	 * POST /employees → 새 직원 등록
	 *
	 * <p>{@code @PostMapping}: HTTP POST 방식(새 데이터를 "만들어 달라"는 요청)을 처리합니다.</p>
	 * <p>{@code @RequestBody}: 요청 본문(body)에 담겨 온 JSON 데이터를 자동으로
	 * {@link Employee} 객체로 바꿔 매개변수에 넣어 줍니다.
	 * 예: {@code {"name":"Samwise Gamgee","role":"gardener"}} → Employee 객체</p>
	 * <p>이 객체를 {@code save()}로 저장하면 id가 자동으로 붙고, 저장된 결과를 그대로 돌려줍니다.</p>
	 *
	 * @param newEmployee 요청 본문에서 변환된 새 직원 정보
	 * @return 저장된 직원(자동 생성된 id 포함)
	 */
	@PostMapping("/employees")
	Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}

	// 단일 항목: 직원 "한 명"을 다루는 주소(/employees/{id})에 대한 처리
	
	/**
	 * GET /employees/{id} → 특정 직원 한 명 조회
	 *
	 * <p>{@code {id}}는 주소 안의 "빈칸"으로, 예를 들어 {@code /employees/1}로 요청하면 id 자리에 1이 들어갑니다.
	 * {@code @PathVariable}은 이 주소 속 값을 꺼내서 메서드 매개변수 {@code id}에 넣어 주는 표식입니다.</p>
	 *
	 * <p>처리 순서:</p>
	 * <ol>
	 *   <li>{@code findById(id)}로 해당 id의 직원을 찾습니다. 결과는 {@code Optional}로 돌아오는데,
	 *       Optional은 "값이 들어 있을 수도, 비어 있을 수도 있는 상자"입니다.</li>
	 *   <li>{@code orElseThrow(...)}: 상자에 직원이 있으면 그 직원을 꺼내 반환하고,
	 *       비어 있으면(=직원이 없으면) {@link EmployeeNotFoundException} 예외를 던집니다.</li>
	 *   <li>던져진 예외는 {@link EmployeeNotFoundAdvice}가 받아서 404(찾을 수 없음) 응답으로 바꿔 줍니다.</li>
	 * </ol>
	 *
	 * @param id 주소에서 꺼낸 직원 고유 번호
	 * @return 찾은 직원
	 */
	@GetMapping("/employees/{id}") //어노테이션
	Employee one(@PathVariable Long id) { //실제로 실행되는 메서드
		
		// "() -> new EmployeeNotFoundException(id)" 는 람다(이름 없는 짧은 함수)로,
		// 직원이 없을 때에만 실행되어 예외 객체를 만들어 줍니다.
		return repository.findById(id)
			.orElseThrow(() -> new EmployeeNotFoundException(id));
	}

	/**
	 * PUT /employees/{id} → 특정 직원 정보 수정 (해당 id의 직원이 없으면 새로 저장)
	 *
	 * <p>{@code @PutMapping}: HTTP PUT 방식(기존 데이터를 "통째로 바꿔 달라"는 요청)을 처리합니다.</p>
	 * <p>{@code @RequestBody}로 요청 본문의 JSON을 {@link Employee} 객체로 받고,
	 * {@code @PathVariable}로 주소 속 id를 받습니다.</p>
	 *
	 * <p>처리 순서:</p>
	 * <ol>
	 *   <li>{@code findById(id)}로 기존 직원을 찾습니다 (결과는 Optional 상자).</li>
	 *   <li>{@code map(...)}: 상자에 직원이 들어 있으면 그 직원의 이름과 역할을 새 값으로 바꾼 뒤 저장하고,
	 *       저장된 결과를 반환합니다.</li>
	 *   <li>{@code orElseGet(...)}: 상자가 비어 있으면(=그 id의 직원이 없으면) 요청으로 받은 새 직원 정보를
	 *       그대로 저장하고 반환합니다.</li>
	 * </ol>
	 *
	 * @param newEmployee 요청 본문에서 변환된, 바꿀 직원 정보
	 * @param id          주소에서 꺼낸, 수정할 직원의 고유 번호
	 * @return 수정되었거나 새로 저장된 직원
	 */
	@PutMapping("/employees/{id}")
	Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
		
		return repository.findById(id)
			// 직원이 존재하는 경우: "employee -> { ... }" 람다에서 employee는 찾아낸 기존 직원입니다.
			.map(employee -> {
				// 기존 직원의 이름과 역할을 요청으로 받은 값으로 덮어씁니다.
				employee.setName(newEmployee.getName());
				employee.setRole(newEmployee.getRole());
				// 바뀐 내용을 데이터베이스에 저장하고, 저장된 결과를 반환합니다.
				return repository.save(employee);
			})
			// 직원이 존재하지 않는 경우: 요청으로 받은 직원 정보를 그대로 새로 저장합니다.
			.orElseGet(() -> {
				return repository.save(newEmployee);
			});
	}

	/**
	 * DELETE /employees/{id} → 특정 직원 삭제
	 *
	 * <p>{@code @DeleteMapping}: HTTP DELETE 방식(데이터를 "지워 달라"는 요청)을 처리합니다.
	 * 주소 속 id({@code @PathVariable})에 해당하는 직원을 {@code deleteById}로 삭제합니다.</p>
	 * <p>반환형이 {@code void}(돌려줄 값 없음)이므로 응답 본문 없이 처리가 끝납니다.</p>
	 *
	 * @param id 삭제할 직원의 고유 번호
	 */
	@DeleteMapping("/employees/{id}")
	void deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
	}
}
