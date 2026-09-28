package payroll;

// linkTo, methodOn 같은 "링크 만들기 도구"를 클래스 이름 없이 바로 쓸 수 있게 가져옵니다(static import).
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * 직원(Employee) 객체에 HATEOAS 링크를 붙여 "포장"해 주는 조립기(Assembler) 클래스입니다.
 *
 * <p>조립기란? 택배 포장 담당자에 비유할 수 있습니다. 상품(직원 데이터)을 받으면 상자(EntityModel)에 넣고,
 * 상자 겉면에 "이 상품의 주소(self)", "같은 종류 상품 목록의 주소(employees)" 같은 안내 스티커(링크)를 붙여 줍니다.
 * 컨트롤러의 여러 메서드가 같은 포장 작업을 반복하지 않도록 이 한 곳에 모아 둔 것입니다.</p>
 *
 * <p>포장 결과를 JSON으로 보면 대략 다음과 같습니다.</p>
 * <pre>
 * {
 *   "id": 1, "firstName": "Bilbo", "lastName": "Baggins", "role": "burglar", "name": "Bilbo Baggins",
 *   "_links": {
 *     "self":      { "href": "http://localhost:8080/employees/1" },
 *     "employees": { "href": "http://localhost:8080/employees" }
 *   }
 * }
 * </pre>
 *
 * <p>RepresentationModelAssembler&lt;Employee, EntityModel&lt;Employee&gt;&gt;: 스프링 HATEOAS가 제공하는 약속(인터페이스)으로,
 * "Employee를 받아서 EntityModel&lt;Employee&gt;로 바꿔 주는 toModel 메서드를 반드시 만든다"는 뜻입니다.</p>
 */
// @Component: 스프링에게 "이 클래스로 객체(빈)를 하나 만들어 관리해 달라"고 알리는 표시입니다.
//             그래서 EmployeeController의 생성자에 이 조립기가 자동으로 주입될 수 있습니다.
@Component
class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

	/**
	 * 직원 한 명을 받아 링크 두 개가 붙은 EntityModel로 바꿔 돌려줍니다.
	 *
	 * <ul>
	 *   <li>self 링크: 이 직원 자신의 주소 (예: /employees/1) - EmployeeController.one(id)에 해당하는 주소</li>
	 *   <li>employees 링크: 직원 전체 목록 주소 (/employees) - EmployeeController.all()에 해당하는 주소</li>
	 * </ul>
	 *
	 * @param employee 포장할 직원
	 * @return 직원 데이터 + 링크를 담은 EntityModel
	 */
	@Override
	public EntityModel<Employee> toModel(Employee employee) {

		// EntityModel.of(데이터, 링크1, 링크2): 데이터와 링크들을 하나의 상자로 묶습니다.
		// methodOn(EmployeeController.class).one(...): 실제로 메서드를 실행하는 것이 아니라,
		//   "그 메서드가 연결된 주소가 무엇인지" 알아내기 위해 흉내만 내는 것입니다.
		// linkTo(...): 알아낸 주소로 링크를 만듭니다.
		// withSelfRel(): 링크 이름을 "self"로, withRel("employees"): 링크 이름을 "employees"로 정합니다.
		return EntityModel.of(employee, //
				linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
}
