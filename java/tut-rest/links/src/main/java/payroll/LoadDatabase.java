package payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션이 시작될 때 데이터베이스에 "샘플(예시) 데이터"를 미리 넣어 두는 설정 클래스입니다.
 *
 * <p>
 * 가게 문을 열기 전에 진열대에 견본 상품을 미리 올려 두는 것과 비슷합니다.
 * 덕분에 서버를 켜자마자 바로 직원 목록과 주문 목록을 조회해 볼 수 있습니다.
 * (이 프로젝트는 H2라는 메모리 기반 데이터베이스를 쓰므로 서버를 끄면 데이터가 사라지고, 켤 때마다 다시 채워집니다.)
 * </p>
 *
 * <p>
 * {@code @Configuration}: "이 클래스는 스프링 설정을 담고 있다"는 표시입니다.
 * 스프링은 시작할 때 이 클래스 안의 {@code @Bean} 메서드들을 실행해서 그 결과물을 등록합니다.
 * </p>
 */
@Configuration
class LoadDatabase {

	// 로그(프로그램 실행 기록)를 콘솔에 출력하기 위한 도구입니다.
	// static final: 클래스 전체에서 하나만 만들어 공유하고, 이후 바꾸지 않는다는 뜻입니다.
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * 서버 시작 직후 한 번 실행될 "초기 데이터 입력 작업"을 만들어 스프링에 등록합니다.
	 *
	 * <ul>
	 * <li>{@code @Bean}: 이 메서드가 돌려주는 객체를 스프링이 관리하는 부품(빈, Bean)으로 등록하라는 뜻입니다.</li>
	 * <li>{@code CommandLineRunner}: "애플리케이션 준비가 끝나면 자동으로 실행할 작업"을 나타내는 인터페이스입니다.
	 * 스프링 부트는 이런 빈을 찾아서 시작 직후에 실행해 줍니다.</li>
	 * <li>매개변수 employeeRepository, orderRepository: 스프링이 알아서 찾아 넣어 줍니다(의존성 주입, DI:
	 * 필요한 부품을 직접 만들지 않고 스프링에게 받아 쓰는 방식).</li>
	 * </ul>
	 *
	 * @param employeeRepository 직원 데이터 저장소
	 * @param orderRepository 주문 데이터 저장소
	 * @return 시작 시 실행될 작업
	 */
	@Bean
	CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

		// "args -> { ... }"는 람다(lambda) 표현식입니다.
		// 람다: 이름 없는 짧은 함수를 즉석에서 만들어 전달하는 문법입니다.
		// 여기서는 "CommandLineRunner가 실행할 내용"을 바로 적어 넣은 것입니다. (args는 실행 인자)
		return args -> {
			// 샘플 직원 두 명을 데이터베이스에 저장합니다.
			employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
			employeeRepository.save(new Employee("Frodo", "Baggins", "thief"));

			// 저장된 모든 직원을 꺼내서 하나씩 로그에 "Preloaded(미리 넣음) ..." 형태로 출력합니다.
			employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));

			
			// 샘플 주문 두 개를 저장합니다.
			// - "MacBook Pro"는 이미 완료(COMPLETED)된 주문 → 응답에 cancel/complete 링크가 붙지 않습니다.
			// - "iPhone"은 진행 중(IN_PROGRESS)인 주문 → 응답에 cancel/complete 링크가 붙습니다.
			orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
			orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));

			// 저장된 모든 주문을 하나씩 로그에 출력합니다.
			orderRepository.findAll().forEach(order -> {
				log.info("Preloaded " + order);
			});
			
		};
	}
}
