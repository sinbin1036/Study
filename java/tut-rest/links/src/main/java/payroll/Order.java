package payroll;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * "주문(Order)" 하나의 정보를 담는 클래스(엔티티, Entity)입니다.
 *
 * <p>
 * 엔티티란 "데이터베이스 표(table)의 한 줄(행)과 1:1로 짝지어지는 자바 객체"입니다.
 * 식당 주문서에 비유하면, 이 클래스는 "주문서 양식"이고
 * 이 클래스로 만든 객체 하나하나가 "실제로 작성된 주문서 한 장"입니다.
 * 주문서에는 주문 번호(id), 주문 내용(description), 진행 상태(status)가 적힙니다.
 * </p>
 *
 * <ul>
 * <li>{@code @Entity}: JPA(자바 객체 ↔ 데이터베이스 표를 자동으로 연결해 주는 기술)에게
 * "이 클래스를 데이터베이스 표로 저장해 줘"라고 알리는 표시입니다.</li>
 * <li>{@code @Table(name = "CUSTOMER_ORDER")}: 데이터베이스 표 이름을 "CUSTOMER_ORDER"로 직접 지정합니다.
 * 원래대로라면 클래스 이름을 따라 "ORDER"라는 표가 만들어지는데,
 * ORDER는 SQL(데이터베이스 명령어)에서 정렬을 뜻하는 "ORDER BY"에 쓰이는 예약어(이미 특별한 뜻으로 쓰이는 단어)라서
 * 표 이름으로 쓰면 오류가 날 수 있습니다. 그래서 겹치지 않는 이름으로 바꿔 준 것입니다.</li>
 * </ul>
 */
@Entity
@Table(name = "CUSTOMER_ORDER")
class Order {

	// 주문 고유 번호(주문서 번호)
	// @Id: 이 필드가 표의 "기본 키(Primary Key, 각 줄을 구별하는 고유 번호)"라는 뜻입니다.
	// @GeneratedValue: 저장할 때 번호를 직접 넣지 않아도 데이터베이스가 자동으로 매겨 줍니다.
	private @Id @GeneratedValue Long id;

	// 주문 내용(예: "MacBook Pro", "iPhone")
	private String description;
	// 주문의 현재 상태(IN_PROGRESS=진행 중, COMPLETED=완료, CANCELLED=취소) → Status 열거형 참고
	private Status status;

	/**
	 * 매개변수가 없는 기본 생성자입니다.
	 * JPA가 데이터베이스에서 값을 읽어 와 객체를 만들 때,
	 * 그리고 요청 본문의 JSON을 Order 객체로 바꿀 때 이 생성자가 필요합니다.
	 */
	Order() {}

	/**
	 * 주문 내용과 상태를 받아 새 주문 객체를 만드는 생성자입니다.
	 * id는 데이터베이스에 저장될 때 자동으로 매겨지므로 여기서 받지 않습니다.
	 *
	 * @param description 주문 내용
	 * @param status 주문 상태
	 */
	Order(String description, Status status) {

		this.description = description;
		this.status = status;
	}

	// ----- 아래는 getter(값 꺼내기) / setter(값 바꾸기) 메서드들입니다. -----
	// 필드를 private(외부에서 직접 접근 불가)로 숨기고, 이 메서드들을 통해서만 읽고 쓰도록 합니다.
	// JSON 변환 도구(Jackson)와 JPA도 이 메서드들을 이용해 값을 읽고 씁니다.

	/** 주문 번호(id)를 돌려줍니다. */
	public Long getId() {
		return this.id;
	}

	/** 주문 내용(description)을 돌려줍니다. */
	public String getDescription() {
		return this.description;
	}

	/** 주문 상태(status)를 돌려줍니다. 이 값에 따라 응답에 붙는 링크(cancel/complete)가 달라집니다. */
	public Status getStatus() {
		return this.status;
	}

	/** 주문 번호(id)를 바꿉니다. */
	public void setId(Long id) {
		this.id = id;
	}

	/** 주문 내용(description)을 바꿉니다. */
	public void setDescription(String description) {
		this.description = description;
	}

	/** 주문 상태(status)를 바꿉니다. (예: 진행 중 → 완료, 진행 중 → 취소) */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * 두 주문 객체가 "내용상 같은 주문인지" 비교합니다.
	 *
	 * <p>
	 * 자바에서 기본 비교는 "메모리상 같은 물건인지"만 봅니다.
	 * 이 메서드를 재정의(@Override: 물려받은 기능을 내 방식으로 다시 만듦)해서
	 * id, 주문 내용, 상태가 모두 같으면 같은 주문으로 판단하도록 바꿉니다.
	 * </p>
	 *
	 * @param o 비교할 다른 객체
	 * @return 모든 값이 같으면 true, 아니면 false
	 */
	@Override
	public boolean equals(Object o) {

		// 완전히 같은 객체(같은 메모리 주소)라면 당연히 같습니다.
		if (this == o)
			return true;
		// 비교 대상이 Order가 아니면 다릅니다.
		if (!(o instanceof Order))
			return false;
		// Order 타입으로 형변환한 뒤 각 필드를 비교합니다.
		// status는 열거형(enum)이라 값마다 객체가 딱 하나씩만 존재하므로 == 로 비교해도 안전합니다.
		Order order = (Order) o;
		return Objects.equals(this.id, order.id) && Objects.equals(this.description, order.description)
				&& this.status == order.status;
	}

	/**
	 * 객체의 "해시 코드(요약 번호)"를 계산합니다.
	 * HashMap/HashSet 같은 자료구조가 객체를 빠르게 찾을 때 쓰며,
	 * equals에서 비교한 것과 같은 필드(id, description, status)로 계산합니다.
	 *
	 * @return 필드 값들로 계산한 정수
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.description, this.status);
	}

	/**
	 * 주문 객체를 사람이 읽기 쉬운 문자열로 바꿔 줍니다. 로그(실행 기록)를 찍을 때 주로 쓰입니다.
	 * 예: Order{id=3, description='MacBook Pro', status=COMPLETED}
	 *
	 * @return 주문 정보를 담은 문자열
	 */
	@Override
	public String toString() {
		return "Order{" + "id=" + this.id + ", description='" + this.description + '\'' + ", status=" + this.status + '}';
	}
}
