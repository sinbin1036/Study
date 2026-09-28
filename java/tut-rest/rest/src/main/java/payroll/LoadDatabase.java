package payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프로그램이 시작될 때 데이터베이스에 예시(샘플) 직원 데이터를 미리 넣어 두는 설정 클래스입니다.
 * <p>
 * 서버를 막 켰을 때 데이터베이스가 텅 비어 있으면 조회해 봐도 아무것도 안 나오니,
 * 테스트하기 편하도록 직원 두 명을 자동으로 등록해 둡니다.
 * 비유하면, 가게 문을 열기 전에 진열대에 견본 상품을 미리 올려 두는 것과 같습니다.
 * <p>
 * {@code @Configuration}은 "이 클래스는 스프링 설정을 담고 있다"는 표시입니다.
 * 스프링은 이 클래스 안에서 {@code @Bean}이 붙은 메서드를 찾아 실행하고,
 * 그 결과로 나온 객체를 빈(Bean: 스프링이 대신 만들어서 보관·관리해 주는 객체)으로 등록합니다.
 */
@Configuration
class LoadDatabase {

	/**
	 * 로그(작업 기록)를 남기기 위한 도구입니다.
	 * 로그는 프로그램이 "지금 무슨 일을 했는지" 콘솔(실행 화면)에 적어 두는 일기장 같은 것입니다.
	 * {@code static final}은 "클래스 전체가 하나만 공유하고, 한 번 정하면 바꾸지 않는다"는 뜻입니다.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * 애플리케이션이 완전히 켜진 직후 한 번 실행될 작업(CommandLineRunner)을 만들어 등록합니다.
	 * <p>
	 * {@code CommandLineRunner}는 "스프링 부트가 준비를 마치면 자동으로 실행해 줄 작업"을 뜻하는 인터페이스입니다.
	 * 스프링이 이 빈을 발견하면, 서버가 켜진 뒤 안에 들어 있는 코드를 실행해 줍니다.
	 * <p>
	 * 매개변수 {@code repository}는 우리가 직접 만들지 않아도
	 * 스프링이 알아서 찾아서 넣어 줍니다. (이를 "의존성 주입"이라고 부릅니다)
	 *
	 * @param repository 직원 데이터를 저장할 때 사용할 저장소(리포지토리)
	 * @return 시작 시 실행될 작업
	 */
	@Bean
	CommandLineRunner initDatabase(EmployeeRepository repository) {

		// "args -> { ... }"는 람다(lambda) 표현식입니다.
		// 람다란 이름 없이 간단히 적는 "즉석 함수"로, 여기서는 CommandLineRunner가 해야 할 일을 짧게 적은 것입니다.
		// args는 실행 시 전달된 명령줄 인자인데, 여기서는 사용하지 않습니다.
		return args -> {
			// 직원 "Bilbo Baggins"(역할: burglar, 좀도둑)를 데이터베이스에 저장하고,
			// 저장된 결과(자동으로 번호가 매겨진 직원 정보)를 로그로 출력합니다.
			log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
			// 직원 "Frodo Baggins"(역할: thief, 도둑)를 저장하고 로그로 출력합니다.
			log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
		};
	}
}
