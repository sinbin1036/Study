package payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션이 시작될 때 데이터베이스에 예시 직원 데이터를 미리 넣어 두는 설정 클래스입니다.
 *
 * <p>가게를 열기 전에 진열대에 상품 몇 개를 미리 올려 두는 것과 비슷합니다.
 * 덕분에 서버를 켜자마자 {@code GET /employees}로 조회해 보면 바로 직원 2명이 보입니다.</p>
 *
 * <p>{@code @Configuration}은 "이 클래스는 스프링 설정을 담고 있다"는 표식입니다.
 * 스프링은 시작할 때 이 클래스를 읽고, 안에 있는 {@code @Bean} 메서드들을 실행해
 * 그 결과 객체를 빈(Bean: 스프링이 직접 만들어 보관·관리하는 객체)으로 등록합니다.</p>
 */
@Configuration
class LoadDatabase {

	/**
	 * 로그(log, 프로그램이 실행 중 남기는 기록/일지)를 출력하기 위한 도구입니다.
	 * {@code static final}이므로 클래스 전체가 하나만 공유하고, 한 번 정해지면 바뀌지 않습니다.
	 * {@code LoadDatabase.class}를 넘겨서 "이 로그는 LoadDatabase에서 남긴 것"임을 표시합니다.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * 애플리케이션 시작 직후 한 번 실행될 작업을 만들어 스프링에 등록합니다.
	 *
	 * <p>{@code @Bean}: 이 메서드가 반환하는 객체를 스프링이 빈으로 등록해 관리하게 합니다.</p>
	 * <p>{@code CommandLineRunner}: 스프링 부트가 준비를 모두 마친 뒤 자동으로 실행해 주는 "시작 작업" 규격입니다.
	 * 스프링 부트는 등록된 {@code CommandLineRunner} 빈을 찾아 실행합니다.</p>
	 * <p>매개변수 {@code repository}는 우리가 직접 만들어 넘기지 않아도, 스프링이 알아서
	 * {@link EmployeeRepository} 빈을 찾아 넣어 줍니다(이를 의존성 주입, DI라고 합니다).</p>
	 *
	 * @param repository 직원 데이터를 저장할 때 사용할 저장소(스프링이 자동으로 전달)
	 * @return 시작 시 실행될 작업(예시 직원 2명을 저장하고 로그로 출력)
	 */
	@Bean
	CommandLineRunner initDatabase(EmployeeRepository repository) {

		// "args -> { ... }"는 람다(lambda) 표현식입니다.
		// 람다란 이름 없이 짧게 적는 함수로, "args를 받으면 중괄호 안의 일을 해라"라는 뜻입니다.
		// 여기서는 CommandLineRunner가 해야 할 일(run 메서드의 내용)을 간단히 적은 것입니다.
		return args -> {
			// 새 직원(이름: Bilbo Baggins, 역할: burglar)을 만들어 DB에 저장하고,
			// 저장된 결과(자동으로 부여된 id 포함)를 "Preloading ..." 형태로 로그에 남깁니다.
			log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
			// 두 번째 직원(이름: Frodo Baggins, 역할: thief)도 같은 방식으로 저장하고 로그를 남깁니다.
			log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
		};
	}
}
