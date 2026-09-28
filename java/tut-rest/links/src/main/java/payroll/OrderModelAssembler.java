package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * 주문(Order) 객체를 "링크가 붙은 응답용 모델(EntityModel)"로 포장해 주는 조립기(Assembler)입니다.
 * 이 모듈(links)의 핵심 아이디어인 "상태에 따라 달라지는 링크"가 바로 여기서 만들어집니다.
 *
 * <p>
 * 음식 주문 앱에 비유해 보겠습니다. 주문 화면에서
 * </p>
 * <ul>
 * <li>음식이 아직 조리 중(IN_PROGRESS)이면 [주문 취소] 버튼과 [수령 완료] 버튼이 보입니다.</li>
 * <li>이미 받았거나(COMPLETED) 취소된(CANCELLED) 주문이면 그 버튼들이 사라집니다.</li>
 * </ul>
 * <p>
 * 이 클래스는 응답(JSON: 데이터를 주고받는 "이름: 값" 형태의 텍스트)에 그런 "버튼" 역할을 하는
 * 하이퍼미디어 링크(cancel, complete)를 주문 상태에 따라 붙이거나 빼는 일을 합니다.
 * 클라이언트(앱/웹 화면)는 규칙을 따로 외울 필요 없이 "링크가 있으면 그 동작을 할 수 있다"고만 판단하면 됩니다.
 * </p>
 *
 * <ul>
 * <li>{@code @Component}: 스프링이 이 클래스를 자동으로 찾아 부품(빈, Bean)으로 등록하라는 표시입니다.</li>
 * <li>{@code RepresentationModelAssembler<Order, EntityModel<Order>>}: "Order를 EntityModel&lt;Order&gt;로 바꿔 주는
 * toModel 메서드를 제공하겠다"는 스프링 HATEOAS의 약속(인터페이스)입니다.</li>
 * </ul>
 */
@Component
class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

	/**
	 * 주문 하나를 링크가 붙은 모델로 변환합니다.
	 *
	 * <p>항상 붙는 링크:</p>
	 * <ul>
	 * <li>self: 이 주문 자신의 주소 (예: /orders/4)</li>
	 * <li>orders: 주문 전체 목록 주소 (/orders)</li>
	 * </ul>
	 * <p>상태가 IN_PROGRESS(진행 중)일 때만 추가로 붙는 링크:</p>
	 * <ul>
	 * <li>cancel: 주문 취소 주소 (DELETE /orders/{id}/cancel)</li>
	 * <li>complete: 주문 완료 주소 (PUT /orders/{id}/complete)</li>
	 * </ul>
	 *
	 * @param order 변환할 주문
	 * @return 주문 데이터 + 상태에 맞는 링크들이 담긴 EntityModel
	 */
	@Override
	public EntityModel<Order> toModel(Order order) {

		// 조건 없이 항상 붙는 링크: 주문 하나(자기 자신)의 주소와 전체 주문 목록(집합 루트)의 주소
		// linkTo(methodOn(...).메서드(...)): 해당 컨트롤러 메서드가 담당하는 URL을 자동으로 계산합니다.
		// withSelfRel(): 링크 이름을 "self"로, withRel("orders"): 링크 이름을 "orders"로 정합니다.

		EntityModel<Order> orderModel = EntityModel.of(order,
				linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
				linkTo(methodOn(OrderController.class).all()).withRel("orders"));

		// 주문 상태에 따라 조건부로 붙는 링크
		// 진행 중(IN_PROGRESS)일 때만 "취소(cancel)"와 "완료(complete)" 링크를 추가합니다.
		// 완료(COMPLETED)나 취소(CANCELLED) 상태라면 이 if 문을 건너뛰므로 두 링크가 붙지 않습니다.

		if (order.getStatus() == Status.IN_PROGRESS) {
			orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
			orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
		}

		return orderModel;
	}
}
