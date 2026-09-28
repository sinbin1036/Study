package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API의 "첫 화면(대문)" 역할을 하는 컨트롤러입니다.
 *
 * <p>
 * 건물 입구의 안내판처럼, 서버의 가장 기본 주소("/")로 들어오면
 * "직원 목록은 여기, 주문 목록은 여기"라는 링크 목록을 알려 줍니다.
 * 클라이언트는 이 주소 하나만 알고 시작해도 링크를 따라 모든 기능을 찾아갈 수 있습니다.
 * </p>
 *
 * <p>
 * {@code @RestController}: "이 클래스는 웹 요청을 받아 처리하는 창구(컨트롤러)이고,
 * 메서드가 돌려주는 값을 JSON 같은 데이터 형태로 응답 본문에 바로 담아 보낸다"는 표시입니다.
 * </p>
 */
@RestController
class RootController {

	/**
	 * GET / → API 시작점. 직원 목록과 주문 목록으로 가는 링크를 돌려줍니다.
	 *
	 * <p>
	 * {@code @GetMapping}에 주소를 적지 않았으므로 루트 경로("/")에 대한 GET 요청(데이터 조회 요청)을 처리합니다.
	 * </p>
	 *
	 * @return 데이터 없이 링크만 담긴 모델 (employees → /employees, orders → /orders)
	 */
	@GetMapping
	RepresentationModel<?> index() {

		// 링크만 담을 빈 모델을 만듭니다. (RepresentationModel: 링크 목록을 담을 수 있는 기본 상자)
		RepresentationModel<?> rootModel = new RepresentationModel<>();
		// "employees"라는 이름으로 직원 전체 목록 주소(/employees) 링크를 추가합니다.
		rootModel.add(linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
		// "orders"라는 이름으로 주문 전체 목록 주소(/orders) 링크를 추가합니다.
		rootModel.add(linkTo(methodOn(OrderController.class).all()).withRel("orders"));
		return rootModel;
	}

}
