package payroll;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * 직원 한 명의 정보를 담는 "데이터 틀(설계도)" 클래스입니다.
 * <p>
 * {@code @Entity}는 이 클래스가 JPA 엔티티라는 표시입니다.
 * (JPA: 자바 객체를 데이터베이스 표(테이블)와 자동으로 연결해 주는 기술,
 *  엔티티: 데이터베이스 표의 한 줄(행)과 1:1로 대응되는 자바 객체)
 * 즉, 이 클래스로 만든 객체 하나가 데이터베이스 "직원 표"의 한 줄이 됩니다.
 * 비유하면 엑셀 표에서 "번호 | 이름 | 역할" 칸이 있고, 직원 한 명이 한 줄을 차지하는 것과 같습니다.
 * <p>
 * 이 객체는 웹 요청/응답 시 JSON 형태로 자동 변환되어 주고받게 됩니다.
 * (JSON: {"id":1, "name":"Bilbo Baggins", "role":"burglar"}처럼
 *  데이터를 글자로 표현하는 가볍고 널리 쓰이는 형식)
 */
@Entity
class Employee {

	// 직원 고유 번호(id)입니다. 데이터베이스에서 각 직원을 구별하는 "주민번호" 같은 값입니다.
	// @Id : 이 필드가 기본 키(Primary Key, 표에서 각 줄을 유일하게 구별하는 값)임을 표시합니다.
	// @GeneratedValue : 번호를 사람이 직접 정하지 않고, 저장할 때 데이터베이스(JPA)가 자동으로 매겨 줍니다.
	// Long : 아주 큰 범위의 정수를 담을 수 있는 자료형입니다.
	private @Id @GeneratedValue Long id;
	// 직원 이름 (예: "Bilbo Baggins")
	private String name;
	// 직원의 역할/직무 (예: "burglar")
	private String role;

	/**
	 * 아무 값도 받지 않는 기본 생성자입니다.
	 * JPA가 데이터베이스에서 데이터를 읽어 와 객체를 만들 때 이 빈 생성자가 필요하기 때문에 존재합니다.
	 * (생성자: 객체를 새로 만들 때 호출되는 특별한 메서드)
	 */
	Employee() {}

	/**
	 * 이름과 역할을 받아서 새 직원 객체를 만드는 생성자입니다.
	 * id는 넣지 않습니다. 저장할 때 데이터베이스가 자동으로 번호를 매겨 주기 때문입니다.
	 *
	 * @param name 직원 이름
	 * @param role 직원 역할
	 */
	Employee(String name, String role) {

		// "this.name"은 이 객체 자신의 name 칸을, 오른쪽 "name"은 전달받은 값을 뜻합니다.
		this.name = name;
		this.role = role;
	}

	// ----- 아래는 getter(값 꺼내기) / setter(값 바꾸기) 메서드입니다. -----
	// 필드가 private(외부에서 직접 접근 금지)로 숨겨져 있으므로, 이 메서드들을 통해서만 값을 읽고 씁니다.
	// JSON 변환 도구도 이 getter/setter를 이용해 객체 <-> JSON 변환을 합니다.

	/** 직원 번호(id)를 돌려줍니다. */
	public Long getId() {
		return this.id;
	}

	/** 직원 이름을 돌려줍니다. */
	public String getName() {
		return this.name;
	}

	/** 직원 역할을 돌려줍니다. */
	public String getRole() {
		return this.role;
	}

	/** 직원 번호(id)를 새 값으로 바꿉니다. */
	public void setId(Long id) {
		this.id = id;
	}

	/** 직원 이름을 새 값으로 바꿉니다. */
	public void setName(String name) {
		this.name = name;
	}

	/** 직원 역할을 새 값으로 바꿉니다. */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 두 직원 객체가 "내용상 같은지" 비교하는 메서드입니다.
	 * <p>
	 * 자바는 기본적으로 "메모리상 같은 물건인지"만 비교하므로,
	 * id·이름·역할이 모두 같으면 같은 직원으로 보도록 규칙을 직접 정의(재정의)합니다.
	 * {@code @Override}는 "부모(Object 클래스)에 있던 메서드를 새로 고쳐 쓴다"는 표시입니다.
	 *
	 * @param o 비교할 대상 객체
	 * @return 내용이 같으면 true, 다르면 false
	 */
	@Override
	public boolean equals(Object o) {

		// 1) 완전히 같은 객체(자기 자신)라면 당연히 같으므로 true
		if (this == o)
			return true;
		// 2) 비교 대상이 Employee가 아니면(다른 종류의 객체라면) 다르므로 false
		if (!(o instanceof Employee))
			return false;
		// 3) Employee로 형 변환(타입을 바꿔서 봄)한 뒤, id·이름·역할을 하나씩 비교합니다.
		//    Objects.equals는 값이 null(비어 있음)이어도 오류 없이 안전하게 비교해 줍니다.
		Employee employee = (Employee) o;
		return Objects.equals(this.id, employee.id) && Objects.equals(this.name, employee.name)
				&& Objects.equals(this.role, employee.role);
	}

	/**
	 * 객체를 대표하는 숫자(해시코드)를 계산합니다.
	 * <p>
	 * 해시코드는 객체를 빠르게 찾기 위한 "요약 번호"입니다.
	 * equals로 같다고 판단되는 두 객체는 반드시 같은 해시코드를 가져야 한다는 자바의 규칙이 있어서,
	 * equals에서 비교한 것과 같은 필드(id, 이름, 역할)로 계산합니다.
	 *
	 * @return 계산된 해시코드 값
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.role);
	}

	/**
	 * 객체를 사람이 읽기 쉬운 문자열로 바꿔 줍니다.
	 * 로그를 출력할 때 등에 사용됩니다.
	 * 예: Employee{id=1, name='Bilbo Baggins', role='burglar'}
	 *
	 * @return 직원 정보를 담은 문자열
	 */
	@Override
	public String toString() {
		return "Employee{" + "id=" + this.id + ", name='" + this.name + '\'' + ", role='" + this.role + '\'' + '}';
	}
}
