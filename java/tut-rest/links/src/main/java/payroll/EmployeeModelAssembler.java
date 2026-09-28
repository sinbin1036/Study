package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * 직원(Employee) 객체를 "링크가 붙은 응답용 모델(EntityModel)"로 포장해 주는 조립기(Assembler)입니다.
 *
 * <p>
 * 택배 포장에 비유할 수 있습니다. 알맹이(직원 데이터)를 상자(EntityModel)에 넣고,
 * 상자 겉면에 "이 물건의 주소(self)", "같은 종류 전체 목록의 주소(employees)" 같은
 * 송장(하이퍼미디어 링크)을 붙여 줍니다. 받는 쪽(클라이언트)은 송장만 보고도 다음에 어디로 가면 되는지 알 수 있습니다.
 * 이런 방식을 HATEOAS(응답 안에 다음 행동의 링크를 담아 주는 REST 설계 원칙)라고 합니다.
 * </p>
 *
 * <ul>
 * <li>{@code @Component}: 스프링이 이 클래스를 자동으로 찾아 객체로 만들고 관리하는 부품(빈, Bean)으로 등록하라는 표시입니다.
 * 그래서 컨트롤러에서 생성자로 받아 쓸 수 있습니다.</li>
 * <li>{@code RepresentationModelAssembler<Employee, EntityModel<Employee>>}: 스프링 HATEOAS가 제공하는 인터페이스로,
 * "Employee를 받아서 EntityModel&lt;Employee&gt;로 바꿔 주는 toModel 메서드를 만들어라"라는 약속입니다.</li>
 * </ul>
 */
@Component
class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

	/**
	 * 직원 한 명을 링크가 붙은 모델로 변환합니다.
	 *
	 * <p>붙는 링크:</p>
	 * <ul>
	 * <li>self: 이 직원 자신의 주소 (예: /employees/1)</li>
	 * <li>employees: 직원 전체 목록의 주소 (/employees)</li>
	 * </ul>
	 *
	 * <p>
	 * {@code linkTo(methodOn(EmployeeController.class).one(id))}는 주소 문자열을 직접 쓰지 않고
	 * "EmployeeController의 one 메서드가 담당하는 주소"를 자동으로 계산해 줍니다.
	 * 그래서 나중에 컨트롤러의 주소가 바뀌어도 링크가 함께 맞춰집니다.
	 * </p>
	 *
	 * @param employee 변환할 직원
	 * @return 직원 데이터 + 링크들이 담긴 EntityModel
	 */
	@Override
	public EntityModel<Employee> toModel(Employee employee) {

		// EntityModel.of(데이터, 링크1, 링크2): 데이터와 링크들을 한 상자에 담습니다.
		// withSelfRel(): 링크 이름을 "self"(자기 자신)로 정합니다.
		// withRel("employees"): 링크 이름을 "employees"로 정합니다.
		return EntityModel.of(employee, //
				linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}
}
