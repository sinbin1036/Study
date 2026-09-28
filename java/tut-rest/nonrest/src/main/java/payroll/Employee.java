package payroll;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


/**
 * 직원 한 명의 정보를 담는 "데이터 그릇" 클래스입니다.
 *
 * <p>{@code @Entity}는 이 클래스가 JPA 엔티티라는 표식입니다.
 * (JPA: 자바 객체와 데이터베이스 표(테이블)를 자동으로 연결해 주는 표준 기술,
 * 엔티티: 데이터베이스에 저장되는 대상이 되는 객체)
 * 쉽게 말해, 엑셀 표에 비유하면 이 클래스는 "직원 표"의 열(컬럼) 구성이고,
 * 이 클래스로 만든 객체 하나하나가 표의 한 줄(행)이 됩니다.</p>
 *
 * <p>직원 정보는 세 가지로 이루어집니다.</p>
 * <ul>
 *   <li>{@code id} : 직원마다 붙는 고유 번호 (주민등록번호처럼 중복되지 않음)</li>
 *   <li>{@code name} : 직원 이름</li>
 *   <li>{@code role} : 직원의 역할/직무</li>
 * </ul>
 *
 * <p>이 객체는 웹 응답으로 보낼 때 JSON(데이터를 {@code {"id":1,"name":"..."}} 처럼
 * 글자로 표현하는 형식)으로 자동 변환되며, 이때 아래의 getter 메서드들이 사용됩니다.</p>
 */
@Entity
class Employee {

	// id : 직원의 고유 번호(기본 키, Primary Key).
	// @Id            → 이 필드가 각 행을 구별하는 "기본 키"라는 표식입니다.
	// @GeneratedValue → 번호를 직접 넣지 않아도 저장할 때 데이터베이스/JPA가 자동으로 1, 2, 3... 처럼 만들어 줍니다.
	// Long           → 아주 큰 정수까지 담을 수 있는 자료형입니다. (값이 없을 때는 null 가능)
	private @Id
	@GeneratedValue Long id;
	// name : 직원 이름 (예: "Bilbo Baggins")
	private String name;
	// role : 직원의 역할/직무 (예: "burglar")
	private String role;

	/**
	 * 매개변수가 없는 기본 생성자입니다.
	 * 생성자란 객체를 새로 만들 때 호출되는 특별한 메서드입니다.
	 * JPA가 데이터베이스에서 데이터를 읽어 와 객체를 만들 때 이 빈 생성자가 필요하기 때문에 존재합니다.
	 */
	Employee() {}

	/**
	 * 이름과 역할을 받아 새 직원 객체를 만드는 생성자입니다.
	 * id는 넣지 않습니다. 저장할 때 자동으로 만들어지기 때문입니다({@code @GeneratedValue}).
	 *
	 * @param name 직원 이름
	 * @param role 직원 역할
	 */
	Employee(String name, String role) {

		// this.name 은 "이 객체의 name 필드", 오른쪽 name 은 "매개변수로 받은 값"을 뜻합니다.
		this.name = name;
		this.role = role;
	}

	// ---------------------------------------------------------------
	// 아래는 getter/setter 메서드입니다.
	// getter(getXxx) : 필드 값을 꺼내 읽는 메서드
	// setter(setXxx) : 필드 값을 새로 바꾸는 메서드
	// 필드를 private(외부에서 직접 접근 불가)으로 숨기고, 이 메서드들을 통해서만 읽고 쓰게 하는 방식입니다.
	// ---------------------------------------------------------------

	/** 직원의 고유 번호(id)를 돌려줍니다. */
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

	/** 직원의 고유 번호(id)를 바꿉니다. */
	public void setId(Long id) {
		this.id = id;
	}

	/** 직원 이름을 바꿉니다. (직원 정보 수정 시 사용) */
	public void setName(String name) {
		this.name = name;
	}

	/** 직원 역할을 바꿉니다. (직원 정보 수정 시 사용) */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 두 직원 객체가 "같은 내용"인지 비교합니다.
	 *
	 * <p>자바에서 {@code ==}는 "완전히 같은 물건(같은 메모리 위치)인가"를 비교하지만,
	 * {@code equals}는 "내용이 같은가"를 비교하도록 직접 정의할 수 있습니다.
	 * 여기서는 id, 이름, 역할이 모두 같으면 같은 직원으로 봅니다.</p>
	 * <p>{@code @Override}는 "부모 클래스(Object)에 이미 있는 메서드를 새로 정의한다"는 표식입니다.</p>
	 *
	 * @param o 비교할 대상 객체
	 * @return 내용이 같으면 true, 다르면 false
	 */
	@Override
	public boolean equals(Object o) {

		// 1단계: 자기 자신과 비교하는 경우라면 당연히 같으므로 true
		if (this == o)
			return true;
		// 2단계: 비교 대상이 Employee 종류가 아니라면(또는 null이면) 다르므로 false
		if (!(o instanceof Employee))
			return false;
		// 3단계: Employee 타입으로 바꾼(형변환) 뒤, id·이름·역할을 하나씩 비교
		// Objects.equals 는 값이 null이어도 오류 없이 안전하게 비교해 줍니다.
		Employee employee = (Employee) o;
		return Objects.equals(this.id, employee.id) && Objects.equals(this.name, employee.name)
				&& Objects.equals(this.role, employee.role);
	}

	/**
	 * 객체의 해시코드(hash code)를 계산합니다.
	 *
	 * <p>해시코드란 객체 내용을 바탕으로 만든 "요약 번호"로, HashMap·HashSet 같은 자료구조가
	 * 데이터를 빠르게 찾을 때 사용합니다. {@code equals}로 같다고 판단되는 객체는
	 * 반드시 같은 해시코드를 가져야 하므로, equals에서 비교한 것과 같은 필드(id, 이름, 역할)로 계산합니다.</p>
	 *
	 * @return id, 이름, 역할로 계산한 해시코드 값
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.role);
	}

	/**
	 * 객체를 사람이 읽기 쉬운 문자열로 바꿔 줍니다.
	 * 로그를 출력하거나 디버깅할 때 유용합니다.
	 * 예: {@code Employee{id=1, name='Bilbo Baggins', role='burglar'}}
	 *
	 * @return 직원 정보를 담은 문자열
	 */
	@Override
	public String toString() {
		return "Employee{" + "id=" + this.id + ", name='" + this.name + '\'' + ", role='" + this.role + '\'' + '}';
	}
}
