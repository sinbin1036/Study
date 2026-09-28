package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

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
 * 직원(Employee)에 관한 웹 요청을 받아 처리하는 "접수 창구(컨트롤러, Controller)"입니다.
 *
 * <p>
 * 은행 창구에 비유하면, 손님(클라이언트)이 "조회해 주세요/등록해 주세요/수정해 주세요/삭제해 주세요"라고
 * 요청하면 창구 직원(이 컨트롤러)이 금고 관리인(리포지토리)에게 일을 시키고,
 * 결과를 포장 담당(Assembler)이 링크를 붙여 예쁘게 포장한 뒤 손님에게 돌려줍니다.
 * </p>
 *
 * <p>처리하는 요청 목록 (HTTP 메서드: 요청의 종류. GET=조회, POST=생성, PUT=수정/교체, DELETE=삭제)</p>
 * <ul>
 * <li>GET    /employees      → 직원 전체 목록 조회</li>
 * <li>POST   /employees      → 새 직원 등록</li>
 * <li>GET    /employees/{id} → 직원 한 명 조회</li>
 * <li>PUT    /employees/{id} → 직원 정보 수정(없으면 새로 저장)</li>
 * <li>DELETE /employees/{id} → 직원 삭제</li>
 * </ul>
 *
 * <p>
 * {@code @RestController}: "이 클래스는 웹 요청을 처리하는 컨트롤러이고,
 * 메서드가 돌려주는 값을 JSON(데이터를 주고받는 "이름: 값" 형태의 텍스트) 등으로 변환해 응답 본문에 담아 보낸다"는 표시입니다.
 * </p>
 */
// tag::constructor[]
@RestController
class EmployeeController {

	// 직원 데이터를 저장/조회하는 저장소(리포지토리)
	// final: 한 번 정해지면 바꿀 수 없다는 뜻입니다.
	private final EmployeeRepository repository;

	// 직원 객체에 링크(self, employees)를 붙여 응답용 모델로 포장하는 조립기
	private final EmployeeModelAssembler assembler;
	// 주문 객체용 조립기 (이 클래스의 현재 코드에서는 저장만 하고 사용하지는 않습니다)
	private final OrderModelAssembler orderAssembler;

	/**
	 * 생성자입니다. 필요한 부품들을 스프링이 자동으로 찾아서 넣어 줍니다.
	 * (의존성 주입, DI: 필요한 객체를 직접 new로 만들지 않고 스프링에게 받아 쓰는 방식)
	 */
	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler,
			OrderModelAssembler orderAssembler) {

		this.repository = repository;
		this.assembler = assembler;
		this.orderAssembler = orderAssembler;
	}
	// end::constructor[]

	// 집합 루트(Aggregate root): 직원 "전체"를 다루는 주소(/employees)

	/**
	 * GET /employees → 직원 전체 목록 조회
	 *
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>repository.findAll()로 데이터베이스의 모든 직원을 가져옵니다.</li>
	 * <li>.stream(): 목록을 "컨베이어 벨트"처럼 하나씩 흘려보내며 가공할 수 있는 형태로 바꿉니다.</li>
	 * <li>.map(assembler::toModel): 벨트 위의 직원 하나하나를 링크가 붙은 모델로 바꿉니다.
	 * (assembler::toModel은 "assembler의 toModel 메서드를 사용하라"는 짧은 표기법, 메서드 참조입니다.)</li>
	 * <li>.collect(Collectors.toList()): 가공된 결과들을 다시 하나의 목록(List)으로 모읍니다.</li>
	 * <li>CollectionModel.of(...): 목록 전체를 담고, 목록 자신의 주소(self → /employees) 링크를 붙여 돌려줍니다.</li>
	 * </ol>
	 *
	 * @return 링크가 붙은 직원 목록
	 */
	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

		List<EntityModel<Employee>> employees = repository.findAll().stream() //
				.map(assembler::toModel) //
				.collect(Collectors.toList());

		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}

	/**
	 * POST /employees → 새 직원 등록
	 *
	 * <p>
	 * {@code @RequestBody}: 요청 본문(body)에 담겨 온 JSON 데이터를 Employee 객체로 자동 변환해 받는다는 뜻입니다.
	 * </p>
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>repository.save(newEmployee)로 데이터베이스에 저장합니다. (이때 id가 자동으로 매겨짐)</li>
	 * <li>assembler.toModel(...)로 저장된 직원에 링크를 붙입니다.</li>
	 * <li>ResponseEntity.created(주소): HTTP 201 Created(새로 만들어졌음) 상태 코드로 응답하고,
	 * 응답 헤더의 Location 항목에 새 직원의 주소(self 링크)를 넣습니다.</li>
	 * <li>.body(entityModel): 응답 본문에 새 직원 정보를 담습니다.</li>
	 * </ol>
	 * <p>
	 * ResponseEntity: 응답 본문뿐 아니라 상태 코드와 헤더(부가 정보)까지 직접 정할 수 있는 "응답 봉투"입니다.
	 * {@code <?>}는 "본문의 타입은 무엇이든 될 수 있다"는 뜻입니다.
	 * </p>
	 *
	 * @param newEmployee 요청 본문에서 변환된 새 직원 정보
	 * @return 201 상태 코드 + 새 직원 정보
	 */
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

		EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	// 단일 항목(Single item): 직원 "한 명"을 다루는 주소(/employees/{id})

	/**
	 * GET /employees/{id} → 직원 한 명 조회 (예: GET /employees/1)
	 *
	 * <p>
	 * {@code @PathVariable}: 주소 안의 {id} 부분(예: /employees/1 의 "1")을 꺼내서 id 변수에 넣어 줍니다.
	 * </p>
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>repository.findById(id)로 직원을 찾습니다. 결과는 Optional(값이 "있을 수도 없을 수도 있는 상자")입니다.</li>
	 * <li>.orElseThrow(...): 상자에 직원이 있으면 꺼내고, 비어 있으면 EmployeeNotFoundException을 발생시킵니다.
	 * 이 예외는 {@link EmployeeNotFoundAdvice}가 받아 HTTP 404(Not Found: 없음) 응답으로 바꿉니다.
	 * ("() -> new ..." 는 람다(이름 없는 짧은 함수)로, "비어 있을 때만 이 예외를 만들어라"라는 뜻입니다.)</li>
	 * <li>찾은 직원에 링크를 붙여 돌려줍니다.</li>
	 * </ol>
	 *
	 * @param id 조회할 직원 번호
	 * @return 링크가 붙은 직원 정보
	 */
	@GetMapping("/employees/{id}")
	EntityModel<Employee> one(@PathVariable Long id) {

		Employee employee = repository.findById(id) //
				.orElseThrow(() -> new EmployeeNotFoundException(id));

		return assembler.toModel(employee);
	}

	/**
	 * PUT /employees/{id} → 직원 정보 수정 (해당 번호의 직원이 없으면 요청 내용으로 새로 저장)
	 *
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>repository.findById(id)로 기존 직원을 찾습니다.</li>
	 * <li>.map(employee -> {...}): 직원이 있으면 이름과 역할을 요청 내용으로 바꾼 뒤 저장합니다.</li>
	 * <li>.orElseGet(() -> {...}): 직원이 없으면 요청으로 받은 newEmployee를 그대로 저장합니다.</li>
	 * <li>저장 결과에 링크를 붙이고, HTTP 201 Created 상태 코드와 Location 헤더(해당 직원 주소)를 담아 응답합니다.</li>
	 * </ol>
	 *
	 * @param newEmployee 요청 본문에서 변환된 수정할 직원 정보
	 * @param id 수정할 직원 번호
	 * @return 201 상태 코드 + 수정(또는 새로 저장)된 직원 정보
	 */
	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		Employee updatedEmployee = repository.findById(id) //
				.map(employee -> {
					// 기존 직원이 있을 때: 이름(이름+성)과 역할을 새 값으로 바꾸고 저장
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}) //
				.orElseGet(() -> {
					// 기존 직원이 없을 때: 요청으로 받은 직원 정보를 새로 저장
					return repository.save(newEmployee);
				});

		EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	/**
	 * DELETE /employees/{id} → 직원 삭제
	 *
	 * <p>
	 * repository.deleteById(id)로 해당 직원을 삭제한 뒤,
	 * ResponseEntity.noContent().build()로 HTTP 204 No Content(성공했지만 돌려줄 내용은 없음) 응답을 보냅니다.
	 * </p>
	 *
	 * @param id 삭제할 직원 번호
	 * @return 본문 없는 204 응답
	 */
	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

		repository.deleteById(id);

		return ResponseEntity.noContent().build();
	}
}
