package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문(Order)에 관한 웹 요청을 받아 처리하는 "주문 접수 창구(컨트롤러, Controller)"입니다.
 *
 * <p>
 * 음식점 주문 카운터에 비유할 수 있습니다.
 * 손님은 주문을 넣고(POST), 주문 내역을 확인하고(GET), 조리 중인 주문을 취소하거나(DELETE .../cancel)
 * 완료 처리할 수 있습니다(PUT .../complete).
 * 단, 이미 끝난(완료/취소된) 주문은 다시 취소하거나 완료할 수 없으며, 그런 요청에는
 * HTTP 405 Method Not Allowed(지금 상태에서는 허용되지 않는 동작) 응답을 돌려줍니다.
 * 이렇게 "현재 상태에 따라 가능한 동작이 정해지는 구조"를 상태 기계(state machine)라고 합니다.
 * (상태 전이 규칙은 {@link Status} 설명 참고)
 * </p>
 *
 * <p>처리하는 요청 목록 (HTTP 메서드: GET=조회, POST=생성, PUT=수정/변경, DELETE=삭제/취소)</p>
 * <ul>
 * <li>GET    /orders               → 주문 전체 목록 조회</li>
 * <li>GET    /orders/{id}          → 주문 하나 조회</li>
 * <li>POST   /orders               → 새 주문 생성 (상태는 항상 IN_PROGRESS로 시작)</li>
 * <li>DELETE /orders/{id}/cancel   → 주문 취소 (IN_PROGRESS일 때만 가능)</li>
 * <li>PUT    /orders/{id}/complete → 주문 완료 (IN_PROGRESS일 때만 가능)</li>
 * </ul>
 *
 * <p>
 * {@code @RestController}: "이 클래스는 웹 요청을 처리하는 컨트롤러이고,
 * 메서드가 돌려주는 값을 JSON(데이터를 주고받는 "이름: 값" 형태의 텍스트) 등으로 변환해 응답 본문에 담아 보낸다"는 표시입니다.
 * </p>
 */
// tag::main[]
@RestController
class OrderController {

	// 주문 데이터를 저장/조회하는 저장소(리포지토리)
	private final OrderRepository orderRepository;
	// 주문 객체에 상태별 링크(self, orders, cancel, complete)를 붙여 주는 조립기
	private final OrderModelAssembler assembler;

	/**
	 * 생성자입니다. 필요한 부품들을 스프링이 자동으로 찾아서 넣어 줍니다.
	 * (의존성 주입, DI: 필요한 객체를 직접 new로 만들지 않고 스프링에게 받아 쓰는 방식)
	 */
	OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {

		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}

	/**
	 * GET /orders → 주문 전체 목록 조회
	 *
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>orderRepository.findAll()로 모든 주문을 가져옵니다.</li>
	 * <li>.stream(): 목록을 "컨베이어 벨트"처럼 하나씩 흘려보내며 가공할 수 있게 합니다.</li>
	 * <li>.map(assembler::toModel): 각 주문에 상태에 맞는 링크를 붙입니다. (메서드 참조: "assembler의 toModel을 써라"의 줄임 표기)</li>
	 * <li>.collect(Collectors.toList()): 결과를 다시 목록(List)으로 모읍니다.</li>
	 * <li>CollectionModel.of(...): 목록 전체에 자기 자신의 주소(self → /orders) 링크를 붙여 돌려줍니다.</li>
	 * </ol>
	 *
	 * @return 링크가 붙은 주문 목록
	 */
	@GetMapping("/orders")
	CollectionModel<EntityModel<Order>> all() {

		List<EntityModel<Order>> orders = orderRepository.findAll().stream() //
				.map(assembler::toModel) //
				.collect(Collectors.toList());

		return CollectionModel.of(orders, //
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}

	/**
	 * GET /orders/{id} → 주문 하나 조회 (예: GET /orders/4)
	 *
	 * <p>
	 * {@code @PathVariable}: 주소 안의 {id} 부분(예: /orders/4 의 "4")을 꺼내 id 변수에 넣어 줍니다.
	 * </p>
	 * <p>
	 * findById(id)는 Optional(값이 "있을 수도 없을 수도 있는 상자")을 돌려주고,
	 * orElseThrow(...)는 주문이 있으면 꺼내고 없으면 OrderNotFoundException을 발생시킵니다.
	 * ("() -> new ..." 는 람다(이름 없는 짧은 함수)로, "비어 있을 때만 이 예외를 만들어라"라는 뜻입니다.)
	 * </p>
	 *
	 * @param id 조회할 주문 번호
	 * @return 상태에 맞는 링크가 붙은 주문 정보
	 */
	@GetMapping("/orders/{id}")
	EntityModel<Order> one(@PathVariable Long id) {

		Order order = orderRepository.findById(id) //
				.orElseThrow(() -> new OrderNotFoundException(id));

		return assembler.toModel(order);
	}

	/**
	 * POST /orders → 새 주문 생성
	 *
	 * <p>
	 * {@code @RequestBody}: 요청 본문에 담겨 온 JSON 데이터를 Order 객체로 자동 변환해 받습니다.
	 * </p>
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>손님이 상태를 뭐라고 보냈든 상관없이, 새 주문의 상태를 무조건 IN_PROGRESS(진행 중)로 정합니다.
	 * (음식 주문이 들어오면 항상 "조리 중"부터 시작하는 것과 같습니다.)</li>
	 * <li>orderRepository.save(order)로 저장합니다. (이때 주문 번호 id가 자동으로 매겨짐)</li>
	 * <li>ResponseEntity.created(주소): HTTP 201 Created(새로 만들어졌음) 상태 코드로 응답하고,
	 * 응답 헤더 Location에 새 주문의 주소(/orders/{새 id})를 넣습니다.</li>
	 * <li>.body(...): 응답 본문에 링크가 붙은 새 주문 정보를 담습니다. 진행 중 상태이므로 cancel/complete 링크도 포함됩니다.</li>
	 * </ol>
	 *
	 * @param order 요청 본문에서 변환된 새 주문 정보
	 * @return 201 상태 코드 + 새 주문 정보
	 */
	@PostMapping("/orders")
	ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {

		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepository.save(order);

		return ResponseEntity //
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri()) //
				.body(assembler.toModel(newOrder));
	}
	// end::main[]

	/**
	 * DELETE /orders/{id}/cancel → 주문 취소
	 *
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>번호로 주문을 찾습니다. 없으면 OrderNotFoundException이 발생합니다.</li>
	 * <li>주문이 진행 중(IN_PROGRESS)이면 → 상태를 CANCELLED(취소됨)로 바꾸고 저장한 뒤,
	 * HTTP 200 OK(성공)와 함께 바뀐 주문 정보를 돌려줍니다.
	 * 이제 취소 상태이므로 응답에는 cancel/complete 링크가 더 이상 붙지 않습니다.</li>
	 * <li>이미 완료되었거나 취소된 주문이면 → 상태를 바꾸지 않고
	 * HTTP 405 Method Not Allowed(지금 상태에서는 허용되지 않는 동작) 오류를 돌려줍니다.
	 * (이미 손님이 받아 간 음식은 취소할 수 없는 것과 같습니다.)</li>
	 * </ol>
	 *
	 * <p>
	 * 오류 응답 형식: {@code MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE}("application/problem+json")는
	 * 오류 내용을 정해진 표준 형식(RFC 7807 "Problem Details")의 JSON으로 보낸다는 뜻입니다.
	 * {@code Problem.create()}로 그 오류 객체를 만들고, withTitle(제목), withDetail(자세한 설명)을 채웁니다.
	 * </p>
	 *
	 * @param id 취소할 주문 번호
	 * @return 성공 시 200 + 취소된 주문, 실패 시 405 + 오류 설명
	 */
	// tag::delete[]
	@DeleteMapping("/orders/{id}/cancel")
	ResponseEntity<?> cancel(@PathVariable Long id) {

		// 1) 주문 찾기 (없으면 예외 발생)
		Order order = orderRepository.findById(id) //
				.orElseThrow(() -> new OrderNotFoundException(id));

		// 2) 진행 중인 주문만 취소할 수 있음 → 상태를 CANCELLED로 바꾸고 저장 후 200 OK 응답
		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}

		// 3) 그 외(완료/취소된 주문): 405 상태 코드 + Content-Type 헤더(응답 형식: problem+json) + 오류 설명 본문
		//    오류 설명 예: "You can't cancel an order that is in the COMPLETED status"
		//    (COMPLETED 상태인 주문은 취소할 수 없습니다)
		return ResponseEntity //
				.status(HttpStatus.METHOD_NOT_ALLOWED) //
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
				.body(Problem.create() //
						.withTitle("Method not allowed") //
						.withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
	}
	// end::delete[]

	/**
	 * PUT /orders/{id}/complete → 주문 완료 처리
	 *
	 * <p>단계별 동작:</p>
	 * <ol>
	 * <li>번호로 주문을 찾습니다. 없으면 OrderNotFoundException이 발생합니다.</li>
	 * <li>주문이 진행 중(IN_PROGRESS)이면 → 상태를 COMPLETED(완료)로 바꾸고 저장한 뒤,
	 * HTTP 200 OK(성공)와 함께 바뀐 주문 정보를 돌려줍니다.
	 * 이제 완료 상태이므로 응답에는 cancel/complete 링크가 더 이상 붙지 않습니다.</li>
	 * <li>이미 완료되었거나 취소된 주문이면 → 상태를 바꾸지 않고
	 * HTTP 405 Method Not Allowed(지금 상태에서는 허용되지 않는 동작) 오류를
	 * 표준 오류 형식(application/problem+json)으로 돌려줍니다.
	 * (이미 취소된 주문을 "배달 완료"로 바꿀 수는 없는 것과 같습니다.)</li>
	 * </ol>
	 *
	 * @param id 완료 처리할 주문 번호
	 * @return 성공 시 200 + 완료된 주문, 실패 시 405 + 오류 설명
	 */
	// tag::complete[]
	@PutMapping("/orders/{id}/complete")
	ResponseEntity<?> complete(@PathVariable Long id) {

		// 1) 주문 찾기 (없으면 예외 발생)
		Order order = orderRepository.findById(id) //
				.orElseThrow(() -> new OrderNotFoundException(id));

		// 2) 진행 중인 주문만 완료할 수 있음 → 상태를 COMPLETED로 바꾸고 저장 후 200 OK 응답
		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}

		// 3) 그 외(완료/취소된 주문): 405 상태 코드 + Content-Type 헤더(응답 형식: problem+json) + 오류 설명 본문
		//    오류 설명 예: "You can't complete an order that is in the CANCELLED status"
		//    (CANCELLED 상태인 주문은 완료할 수 없습니다)
		return ResponseEntity //
				.status(HttpStatus.METHOD_NOT_ALLOWED) //
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
				.body(Problem.create() //
						.withTitle("Method not allowed") //
						.withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
	}
	// end::complete[]
}
