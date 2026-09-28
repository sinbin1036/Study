package payroll;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 직원(Employee) 데이터를 데이터베이스에 저장하고 꺼내 오는 "저장소(Repository)"입니다.
 *
 * <p>저장소란? 창고 관리인에 비유할 수 있습니다. "직원 저장해 줘", "1번 직원 찾아 줘",
 * "전체 직원 목록 줘", "3번 직원 지워 줘" 같은 부탁을 하면 데이터베이스에서 알아서 처리해 줍니다.</p>
 *
 * <p>놀랍게도 안쪽이 비어 있습니다! JpaRepository를 상속(extends)하기만 하면,
 * 스프링 데이터 JPA가 프로그램 실행 시 실제 동작하는 코드를 자동으로 만들어 줍니다.
 * 그래서 save(저장), findById(번호로 찾기), findAll(전체 조회), deleteById(번호로 삭제) 같은
 * 기본 기능을 코드 한 줄 없이 바로 쓸 수 있습니다.
 * (JPA: 자바 객체를 데이터베이스 표에 자동으로 저장/조회해 주는 표준 기술)</p>
 *
 * <p>JpaRepository&lt;Employee, Long&gt;의 의미</p>
 * <ul>
 *   <li>Employee: 이 저장소가 다루는 데이터 종류(엔티티)</li>
 *   <li>Long: 그 데이터의 고유 번호(id)의 자료형</li>
 * </ul>
 *
 * <p>interface(인터페이스): "어떤 기능이 있어야 하는지"만 정해 둔 설계도. 실제 구현은 스프링이 채워 줍니다.</p>
 */
interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
