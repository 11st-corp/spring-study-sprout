# 싱글톤 컨테이너
## 웹 애플리케이션과 싱글톤

- 대부분의 스프링 애플리케이션은 웹 애플리케이션이다.
물론 웹이 아닌 애플리케이션 개발도 얼마든지 개발가능.
- 웹 애플리케이션은 보통 여러 클라이언트가 동시에 요청을 함.

![Untitled (2)](https://user-images.githubusercontent.com/25525648/122172508-c557bd80-cebb-11eb-9c9d-2c54bad839e8.png)

요청이 올때마다 객체를 만들어냄 → 낭비가 생김

- 우리가 만들었던 스프링 없는 순수한 DI 컨테이너인 AppConfig는 요청할 때마다 객체를 새로 생성.
- 고객 트래픽이 초당 100이 나오면 초당 100개 객체가 생성되고 소멸됨 → 메모리 낭비가 심함.
- 따라서 딱 하나만 생성해두고 공유하도록 설계하면 된다 → 싱글톤 패턴

## 싱글톤 패턴

- 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴
- 객체 인스턴스를 2개 이상 생성하지 못하도록 막아야함.
- private 생성자를 사용해서 외부에서 임의로 new 키워드를 사용하지 못하도록 막아야함.

### 싱글톤 패턴 문제점

- 싱글톤 패턴을 구현하는 코드 자체가 많이 들어감
- 의존 관계상 클라이언트가 구체 클래스에 의존함 → DIP 위반
- 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높음
- 테스트 하기 어려움
- 내부 속성을 변경하거나 초기화 하기 어려움
- private 생성자로 자식 클래스를 만들기 어려움
- 결론적으로 유연성이 떨어짐
- 안티패턴이라고도 불림

→ 스프링 컨테이너는 싱글톤 패턴의 단점을 제거하면서 객체를 싱글톤으로 관리한다.

## 싱글톤 컨테이너

- 스프링 컨테이너는 싱글턴 패턴을 적용하지 않더라도 객체 인스턴스를 싱글톤으로 관리
- 스프링 컨테이너는 싱글톤 컨테이너 역할을 한다. 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스트리라 한다.
- 스프링 컨테이너의 이런 기능 덕분에 싱글턴 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다.
- DIP, OCP, 테스트, private 생성자로부터 자유롭게 싱글톤을 사용할 수 있다.

![Untitled (3)](https://user-images.githubusercontent.com/25525648/122172518-ca1c7180-cebb-11eb-8acd-43ae1b346847.png)

- 스프링 컨테이너 덕분에 client 요청이 올 때마다 객체를 생성하는 것이 아닌 이미 만들어진 객체를 사용해서 효율적으로 재사용 가능.

> 스프링의 기본 빈 등록 방식은 싱글톤이나, 싱글톤 방식만 지원하는 것은 아님. 요청할 때마다 새로운 객체를 생성해서 반환하는 기능도 제공.

## 싱글톤 방식의 주의점

- 싱글톤 패턴이든, 싱글톤 컨테이너를 사용하든, 
객체 인스턴스를 하나만 생성해서 공유하는 싱글톤 방식은 
여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에
 싱글톤 객체는 상태를 유지하게 설계하면 안됨.
- 무상태로 설계해야 함.
    - 가급적 읽기만 가능해야함.
    - 특정 클라이언트에 의존적인 필드가 있으면 안됨.
    - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안됨.
    - 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal을 사용해야 함.

## @Configuration과 싱글톤

```java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
```

결과적으로 각각 다른 2개의 `MemoryMemberRepository` 가 생성되면서 싱글톤이 깨지는 것 처럼 보인다.

스프링 컨테이너는 이를 어떻게 해결할까? 

```java
@Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        System.out.println("memberService -> memberRepository1 = " + memberRepository1);
        System.out.println("orderService -> memberRepository = " + memberRepository2);
        System.out.println("memberRepository = " + memberRepository);

        Assertions.assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        Assertions.assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);
    }
```

- 확인해보면 memberRepository 인스턴스는 모두 같은 인스턴스가 공유되어 사용된다.
- 스프링 컨테이너가 각각 @Bean을 호출해서 스프링 빈을 생성한다. 그래서 memberRepository는 다음과 같이 3번 호출될 것으로 예상했으나 결과는 1번만 호출되었다.

```java
// 예상
//call AppConfig.memberService
//call AppConfig.memberRepository
//call AppConfig.memberRepository
//call AppConfig.orderService
//call AppConfig.memberRepository

//결과
//call AppConfig.memberService
//call AppConfig.memberRepository
//call AppConfig.orderService
```

## @Configuration과 바이트코드 조작의 마법

```java
@Test
    void configurationDeep() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);

        System.out.println("bean = " + bean.getClass());
    }
// bean = class hello.core.AppConfig$$EnhancerBySpringCGLIB$$3aac460c
```

AppConfig 스프링 빈을 조회했을 때 클래스 정보는 이런 식으로 나온다.

스프링이 CGLIB라는 바이트 코드 조작 라이브러리를 이용해서 AppConfig클래스를 상속받은 임의의 다른 클래스를 만들고, 그 다른 클래스를 스프링 빈으로 등록한 것이다.

→ 임의의 다른 클래스가 바로 싱글톤이 보장되도록 해준다.

### AppConfig@CGLIB 예상 코드

```java
@Bean
public MemberRepository memberRepository() {
		
		if(memoryMemberRepository가 이미 스프링 컨테이너에 등록 되어 있다면?) {
				return 스프링 컨테이너에서 찾아서 반환;
		} else {
			기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
			return 반환;
		}
}
```

> AppConfig@CGLIB는 AppConfig의 자식타입으로, AppConfig타입으로 조회할 수 있다.

(참조: https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8#)
