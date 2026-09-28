package payroll;

// linkTo, methodOn 같은 "링크 만들기 도구"를 클래스 이름 없이 바로 쓸 수 있게 가져옵니다(static import).
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

// Spring HATEOAS 관련 도구들 (아래 클래스 설명 참고)
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 직원 관련 웹 요청을 받아 처리하는 "컨트롤러(Controller)" 클래스입니다.
 *
 * <p>컨트롤러란? 식당의 "주문 받는 직원"과 같습니다. 손님(웹 브라우저나 다른 프로그램)이
 * "직원 목록 주세요", "새 직원 등록해 주세요" 같은 요청(주문)을 보내면, 알맞은 메서드가 받아서
 * 창고 관리인(Repository)에게 데이터를 가져오게 하고, 결과를 손님에게 돌려줍니다.</p>
 *
 * <p>요청은 HTTP 메서드(요청의 종류)와 주소(URL)로 구분됩니다.</p>
 * <ul>
 *   <li>GET /employees → 직원 전체 목록 조회</li>
 *   <li>POST /employees → 새 직원 등록</li>
 *   <li>GET /employees/{id} → 특정 직원 한 명 조회</li>
 *   <li>PUT /employees/{id} → 특정 직원 정보 수정(없으면 새로 등록)</li>
 *   <li>DELETE /employees/{id} → 특정 직원 삭제</li>
 * </ul>
 *
 * <p><b>HATEOAS(헤이티오스)란?</b> 응답 데이터 안에 "다음에 갈 수 있는 곳의 링크(주소)"를 함께 넣어 주는 방식입니다.
 * 웹사이트에서 페이지마다 다른 페이지로 가는 링크가 있듯이, 데이터에도 "이 직원 자신의 주소(self)",
 * "직원 전체 목록 주소(employees)" 같은 링크가 붙습니다. 덕분에 클라이언트는 주소를 외우지 않고
 * 응답 안의 링크를 따라가기만 하면 됩니다. 서버가 주소를 바꿔도 클라이언트가 덜 망가지므로
 * API를 발전(진화)시키기 쉬워집니다.</p>
 */
// tag::constructor[]
// @RestController: "이 클래스는 웹 요청을 처리하는 컨트롤러이고, 메서드가 돌려주는 값은
//                  화면(HTML)이 아니라 데이터(JSON 등) 그 자체로 응답에 담는다"는 표시입니다.
//                  스프링이 이 클래스를 자동으로 찾아 객체(빈)로 만들어 관리합니다.
//                  (빈(Bean): 스프링이 대신 만들어 보관하고 필요한 곳에 넣어 주는 객체)
@RestController
class EmployeeController {

	// repository: 데이터베이스에서 직원 정보를 저장/조회/삭제하는 창고 관리인
	// final: 한 번 정해지면 바꿀 수 없다는 뜻
	private final EmployeeRepository repository;

	// assembler: 직원 객체에 HATEOAS 링크를 붙여 "포장"해 주는 도우미
	private final EmployeeModelAssembler assembler;

	/**
	 * 생성자입니다. 스프링이 이 컨트롤러를 만들 때 필요한 부품(repository, assembler)을
	 * 자동으로 찾아서 넣어 줍니다. 이것을 "의존성 주입"이라고 합니다.
	 * (비유: 요리사가 직접 장을 보지 않아도, 가게 주인이 필요한 재료를 알아서 가져다 주는 것)
	 *
	 * @param repository 직원 데이터 저장소
	 * @param assembler  링크 포장 도우미
	 */
	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

		this.repository = repository;
		this.assembler = assembler;
	}
	// end::constructor[]

	// 집합 루트(Aggregate root): 직원 "전체 모음"을 다루는 부분 (/employees)

	// tag::get-aggregate-root[]
	/**
	 * GET /employees → 직원 전체 목록 조회
	 *
	 * <p>처리 순서</p>
	 * <ol>
	 *   <li>repository.findAll()로 데이터베이스의 모든 직원을 가져옵니다.</li>
	 *   <li>stream()으로 직원들을 컨베이어 벨트에 올리듯 하나씩 흘려보냅니다.</li>
	 *   <li>map(assembler::toModel)로 직원마다 링크를 붙여 EntityModel로 포장합니다.</li>
	 *   <li>collect(Collectors.toList())로 다시 하나의 리스트로 모읍니다.</li>
	 *   <li>CollectionModel.of(...)로 목록 전체를 감싸고, 목록 자체의 주소(self 링크)도 붙여 돌려줍니다.</li>
	 * </ol>
	 *
	 * <p>@GetMapping("/employees"): 이 주소로 GET 요청(조회 요청)이 오면 이 메서드를 실행하라는 표시입니다.</p>
	 *
	 * @return 링크가 붙은 직원 목록 (CollectionModel: 여러 개의 데이터 + 목록 전체에 대한 링크를 담는 상자)
	 */
	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

		// EntityModel<Employee>: 직원 한 명의 데이터 + 그 직원에 대한 링크들을 함께 담는 상자
		// assembler::toModel 은 "메서드 참조"로, employee -> assembler.toModel(employee) 라는 람다(이름 없는 짧은 함수)를 줄여 쓴 것입니다.
		// 줄 끝의 // 는 코드 자동 정렬 시 줄바꿈이 유지되도록 붙인 빈 주석입니다.
		List<EntityModel<Employee>> employees = repository.findAll().stream() //
				.map(assembler::toModel) //
				.collect(Collectors.toList());

		// linkTo(methodOn(EmployeeController.class).all()): "all() 메서드에 연결된 주소"(= /employees)를 자동으로 계산합니다.
		// 주소를 문자열로 직접 적지 않아서, 나중에 주소가 바뀌어도 링크가 자동으로 따라 바뀝니다.
		// withSelfRel(): 이 링크의 이름(관계)을 "self"(자기 자신의 주소)로 정합니다.
		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
	// end::get-aggregate-root[]

	// tag::post[]
	/**
	 * POST /employees → 새 직원 등록
	 *
	 * <p>처리 순서</p>
	 * <ol>
	 *   <li>요청 본문의 JSON(예: {"name": "Samwise Gamgee", "role": "gardener"} 또는
	 *       {"firstName": "Samwise", "lastName": "Gamgee", "role": "gardener"})을 Employee 객체로 바꿔 받습니다.
	 *       name으로 보내면 setName()이 호출되어 이름/성으로 나뉘어 들어갑니다(하위 호환).</li>
	 *   <li>repository.save()로 데이터베이스에 저장합니다. 이때 id가 자동으로 부여됩니다.</li>
	 *   <li>assembler.toModel()로 저장된 직원에 링크를 붙입니다.</li>
	 *   <li>HTTP 상태 코드 201 Created(새로 만들어졌음)와 함께, 새 직원의 주소를 Location 헤더에 담아 응답합니다.</li>
	 * </ol>
	 *
	 * <p>@PostMapping: POST 요청(새 데이터 생성 요청)을 이 메서드와 연결합니다.<br>
	 * {@code @RequestBody}: 요청 본문(body)에 담겨 온 JSON 데이터를 자바 객체(Employee)로 자동 변환해 받겠다는 표시입니다.</p>
	 *
	 * @param newEmployee 클라이언트가 보낸 새 직원 정보
	 * @return 201 상태 코드 + Location 헤더 + 링크가 붙은 직원 데이터
	 */
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

		// 저장(save)한 결과(= id가 채워진 직원)를 바로 링크가 붙은 모델로 포장
		EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

		// ResponseEntity: 응답 내용(body)뿐 아니라 상태 코드와 헤더까지 직접 정해서 돌려줄 수 있는 "응답 봉투"
		// created(URI): 상태 코드를 201 Created로 정하고, Location 헤더에 새로 생긴 자원의 주소를 넣습니다.
		// getRequiredLink(IanaLinkRelations.SELF): 모델에 붙은 링크 중 "self"(자기 주소) 링크를 꺼냅니다(없으면 오류).
		//   IanaLinkRelations: 인터넷 표준 기관(IANA)이 정한 공식 링크 이름 모음 (self, next 등)
		// toUri(): 링크를 주소(URI) 형태로 변환
		// body(entityModel): 응답 본문에 직원 데이터(+링크)를 담습니다.
		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}
	// end::post[]

	// 단일 항목(Single item): 직원 "한 명"을 다루는 부분 (/employees/{id})

	// tag::get-single-item[]
	/**
	 * GET /employees/{id} → 특정 직원 한 명 조회 (예: GET /employees/1)
	 *
	 * <p>처리 순서</p>
	 * <ol>
	 *   <li>주소의 {id} 부분을 숫자로 받아 repository.findById(id)로 찾습니다.</li>
	 *   <li>findById는 Optional(값이 "있을 수도, 없을 수도 있는" 상자)을 돌려줍니다.</li>
	 *   <li>orElseThrow: 상자가 비어 있으면(직원이 없으면) EmployeeNotFoundException 예외를 던집니다.
	 *       이 예외는 EmployeeNotFoundAdvice가 받아서 404 Not Found(찾을 수 없음) 응답으로 바꿔 줍니다.</li>
	 *   <li>찾았다면 링크를 붙여 돌려줍니다.</li>
	 * </ol>
	 *
	 * <p>@PathVariable: 주소(URL) 경로 안의 {id} 자리에 들어온 값을 매개변수 id로 받아 오겠다는 표시입니다.</p>
	 *
	 * @param id 조회할 직원 번호
	 * @return 링크가 붙은 직원 데이터
	 */
	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {

		// () -> new EmployeeNotFoundException(id) 는 람다(이름 없는 짧은 함수)로,
		// "직원이 없을 때만" 실행되어 예외 객체를 만듭니다.
		Employee employee = repository.findById(id) //
				.orElseThrow(() -> new EmployeeNotFoundException(id));

		return assembler.toModel(employee);
	}
	// end::get-single-item[]

	// tag::put[]
	/**
	 * PUT /employees/{id} → 특정 직원 정보 수정 (해당 id의 직원이 없으면 새로 저장)
	 *
	 * <p>처리 순서</p>
	 * <ol>
	 *   <li>repository.findById(id)로 기존 직원을 찾습니다.</li>
	 *   <li>있으면(map 부분): 기존 직원의 이름과 역할을 새 값으로 바꾸고 저장합니다.
	 *       이름은 setName(newEmployee.getName())으로 옮기는데, getName()이 "이름 성"을 합쳐 주고
	 *       setName()이 다시 나눠 주므로 결과적으로 firstName/lastName이 모두 갱신됩니다.</li>
	 *   <li>없으면(orElseGet 부분): 요청으로 받은 새 직원을 그대로 저장합니다.</li>
	 *   <li>결과에 링크를 붙이고, 201 Created 상태 코드와 Location 헤더(해당 직원 주소)를 담아 응답합니다.</li>
	 * </ol>
	 *
	 * <p>@PutMapping: PUT 요청(기존 데이터를 통째로 교체/수정하는 요청)을 이 메서드와 연결합니다.</p>
	 *
	 * @param newEmployee 요청 본문으로 받은 새 직원 정보
	 * @param id          수정할 직원 번호 (주소에서 추출)
	 * @return 201 상태 코드 + Location 헤더 + 링크가 붙은 직원 데이터
	 */
	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		// Optional.map: 상자 안에 값(기존 직원)이 있을 때만 괄호 안의 람다를 실행합니다.
		// Optional.orElseGet: 상자가 비어 있을 때만 괄호 안의 람다를 실행해 대체 값을 만듭니다.
		Employee updatedEmployee = repository.findById(id) //
				.map(employee -> {
					// 기존 직원 발견 → 이름과 역할을 새 값으로 교체한 뒤 저장
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}) //
				.orElseGet(() -> {
					// 기존 직원 없음 → 받은 정보를 새 직원으로 저장
					return repository.save(newEmployee);
				});

		// 수정(또는 새로 저장)된 직원에 링크를 붙여 포장
		EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

		// 201 Created + Location 헤더(self 링크 주소) + 본문(직원 데이터와 링크)으로 응답
		return ResponseEntity //
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(entityModel);
	}
	// end::put[]

	// tag::delete[]
	/**
	 * DELETE /employees/{id} → 특정 직원 삭제
	 *
	 * <p>처리 순서</p>
	 * <ol>
	 *   <li>repository.deleteById(id)로 해당 번호의 직원을 데이터베이스에서 지웁니다.</li>
	 *   <li>HTTP 상태 코드 204 No Content(성공했지만 돌려줄 내용은 없음)로 응답합니다.</li>
	 * </ol>
	 *
	 * <p>@DeleteMapping: DELETE 요청(데이터 삭제 요청)을 이 메서드와 연결합니다.</p>
	 *
	 * @param id 삭제할 직원 번호
	 * @return 본문 없는 204 응답
	 */
	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

		repository.deleteById(id);

		// noContent(): 상태 코드 204, build(): 본문 없이 응답 봉투를 완성
		return ResponseEntity.noContent().build();
	}
	// end::delete[]
}
