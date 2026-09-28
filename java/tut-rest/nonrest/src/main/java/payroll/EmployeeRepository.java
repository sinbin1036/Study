package payroll;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 직원(Employee) 데이터를 데이터베이스에 저장하고 꺼내 오는 "창고 관리인" 역할의 인터페이스입니다.
 *
 * <p>리포지토리(Repository)는 "저장소"라는 뜻으로, 데이터베이스와 직접 대화하는 일을 맡습니다.
 * 도서관 사서에 비유하면, 우리가 "이 책 보관해 주세요", "3번 책 찾아 주세요"라고 말만 하면
 * 사서가 알아서 서가에 넣고 꺼내 주는 것과 같습니다.</p>
 *
 * <p>놀랍게도 이 인터페이스 안에는 코드가 한 줄도 없습니다. {@code JpaRepository}를
 * 상속(extends, 이미 만들어진 기능을 물려받음)했기 때문에, 스프링 데이터 JPA
 * (JPA: 자바 객체를 데이터베이스 표(테이블)의 한 줄로 자동 변환해 주는 표준 기술)가
 * 실행 시점에 실제 동작하는 구현체를 자동으로 만들어 줍니다. 그래서 별도 코드 없이도 다음과 같은 기능을 바로 쓸 수 있습니다.</p>
 * <ul>
 *   <li>{@code save(...)} : 새 직원 저장 또는 기존 직원 정보 수정</li>
 *   <li>{@code findAll()} : 모든 직원 목록 조회</li>
 *   <li>{@code findById(id)} : id(고유 번호)로 직원 한 명 조회</li>
 *   <li>{@code deleteById(id)} : id로 직원 삭제</li>
 * </ul>
 *
 * <p>{@code JpaRepository<Employee, Long>}의 꺾쇠 괄호 안 의미:
 * 첫 번째 {@code Employee}는 "다룰 데이터의 종류(엔티티)", 두 번째 {@code Long}은
 * "그 데이터의 고유 번호(id)의 자료형(큰 정수)"입니다.</p>
 */
interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
