# 빈 생명주기 콜백
## 빈 생명주기 콜백 시작

- 데이터베이스 커넥션 풀, 네트워크 소켓처럼 애플리케이션 시작 시점에 필요한 연결을 미리 해두고, 애플리케이션 종료 시점에 연결을 모두 종료하는 작업을 진행하려면, 객체의 초기화와 종료 작업이 필요.

```java
public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
    }

    @Configuration
    static class LifeCycleConfig {

        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
}
```

```java
생성자 호출, url = null
connect: null
call: null message = 초기화 연결 메시지
```

실행 후 결과는 이러하다.

생성자 부분을 보면 url 정보 없이 connect가 호출된다. 생성된 후 외부에서 수정자 주입을 통해

`setUrl()`이 호출되어야 url이 존재함.

- 초기화 작업은 의존관계 주입이 모두 완료되고 난 다음 호출해야 한다.

> 스프링은 의존관계 주입이 완료되면 스프링 빈에게 콜백 메서드를 통해서 초기화 시점을 알려주는 다양한 기능을 제공, 스프링은 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 줌

### **스프링 빈의 이벤트 라이프사이클**

스프링 컨테이너 생성 → 스프링 빈 생성 → 의존관계 주입 → 초기화 콜백 → 사용 → 소멸전 콜백 → 스프링 종료

- **초기화 콜백**: 빈이 생성되고, 빈의 의존관계 주입이 완료된 후 호출
- **소멸전 콜백**: 빈이 소멸되기 직전에 호출

### **객체의 생성과 초기화를 분리하자**

- 생성자: 필수 정보(파라미터)를 받고, 메모리를 할당해서 객체를 생성하는 책임을 가짐.
- 초기화: 생성된 값을 활용해 외부 커넥션을 연결하는 등 무거운 동작을 수행함.

유지보수 관점에서 좋다. 물론 초기화 작업이 내부 값들만 약간 변경하는 정도로 단순한 경우 생성자에서 한번에 다 처리하는 게 더 나을 수 있다.

### **스프링의 빈 생명 주기 콜백**

- 인터페이스(InitializingBean, DisposableBean)
- 설정 정보에 초기화 메서드, 종료 메서드 지정
- @PostConstruct, @PreDestory 애노테이션 지원

## 인터페이스 InitializingBean, DisposableBean

### 초기화, 소멸 인터페이스 단점

- 이 인터페이스는 스프링 전용 인터페이스임. 해당 코드가 스프링 전용 인터페이스에 의존함.
- 초기화, 소멸 메서드의 이름을 변경할 수 없음.
- 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없음.

사실 인터페이스를 사용한 초기화, 종료 방법은 스프링 초창기에 나온 방법들이고, 더 나은 방법들이 있어 거의 사용 X

## 빈 등록 초기화, 소멸 메서드

설정 정보에 `@Bean(initMethod = "init", destroyMethod = "close")` 처럼 초기화, 소멸 메서드를 지정할 수 있음.

### 특징

- 메서드 이름을 자유롭게 줄 수 있음.
- 스프링 빈이 스프링 코드에 의존하지 않음.
- 코드가 아니라 설정 정보를 사용하므로 코드를 고칠 수 없는 외부 라이브러리에도 초기화, 종료 메서드를 적용 가능.

### 종료 메서드 추론

- @Bean destroyMethod엔 특별한 기능이 있음.
- destroyMethod는 기본 값이 (inferred)로 등록됨.
- 이 추론 기능은 close, shutdown이라는 이름의 메서드를 자동으로 호출함.
- 추론 기능 사용하기 싫으면 destroyMethod="" 처럼 빈 공백을 지정하면 됨.

## 애노테이션 @PostConstrouct, @PreDestroy

### 특징

- 최신 스프링에서 가장 권장함
- 스프링에 종속적인 기술이 아닌 자바 표준(JSR-250)임. 다른 컨테이너에서도 동작함.
- 컴포넌트 스캔과 잘 어울림.
- 외부 라이브러리에는 적용하지 못함. 외부라이브러리 초기화, 종료하려면 @Bean 설정을 이용하자.
