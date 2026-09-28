package payroll;

// Logger: 프로그램 실행 중 일어난 일을 콘솔(화면)에 기록(로그)으로 남기는 도구
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프로그램이 시작될 때 데이터베이스에 예제 직원 데이터를 미리 넣어 두는 설정 클래스입니다.
 *
 * <p>비유: 가게 문을 열기 전에 진열대에 샘플 상품을 미리 올려 두는 것과 같습니다.
 * 이 예제는 메모리 데이터베이스(프로그램이 꺼지면 내용이 사라지는 임시 DB)를 쓰므로,
 * 실행할 때마다 테스트용 직원 두 명(Bilbo, Frodo)을 새로 넣어 줍니다.</p>
 */
// @Configuration: "이 클래스는 스프링 설정을 담고 있다"는 표시입니다.
//                 스프링이 시작할 때 이 클래스 안의 @Bean 메서드들을 실행해 객체(빈)를 만들어 둡니다.
@Configuration
class LoadDatabase {

	// log: 이 클래스 이름표가 붙은 기록 도구. static final이라 프로그램 전체에서 딱 하나만 만들어 공유합니다.
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * 프로그램 시작 직후 한 번 실행될 작업(CommandLineRunner)을 만들어 스프링에 등록합니다.
	 *
	 * <p>@Bean: 이 메서드가 돌려주는 객체를 스프링이 보관·관리하는 "빈(Bean)"으로 등록하라는 표시입니다.<br>
	 * CommandLineRunner: 스프링 부트 앱이 완전히 켜진 뒤 자동으로 run 메서드를 한 번 실행해 주는 약속(인터페이스)입니다.</p>
	 *
	 * <p>매개변수 repository는 스프링이 자동으로 넣어 줍니다(의존성 주입).</p>
	 *
	 * @param repository 직원 데이터 저장소
	 * @return 시작 시 실행될 작업
	 */
	@Bean
	CommandLineRunner initDatabase(EmployeeRepository repository) {

		// args -> { ... } 는 람다(이름 없는 짧은 함수)입니다.
		// CommandLineRunner의 run(args) 메서드 내용을 간단히 적은 것으로, 앱 시작 시 중괄호 안의 코드가 실행됩니다.
		return args -> {
			// 새 생성자(이름, 성, 역할)를 사용해 직원을 만들고 저장한 뒤, 저장 결과를 로그로 출력합니다.
			// 예) Preloading Employee{id=1, firstName='Bilbo', lastName='Baggins', role='burglar'}
			// tag::new_constructor[]
			log.info("Preloading " + repository.save(new Employee("Bilbo", "Baggins", "burglar")));
			log.info("Preloading " + repository.save(new Employee("Frodo", "Baggins", "thief")));
			// end::new_constructor[]
		};
	}
}
