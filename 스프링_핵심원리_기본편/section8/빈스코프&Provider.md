# 빈스코프

스프링 빈은 기본적으로 싱글톤 스코프로 생성.

### 스코프란?

번역 그대로 빈이 존재할 수 있는 범위를 의미. 스프링은 다양한 스코프(빈의 존재범위)를 지원한다.

- 싱글톤: 기본 스코프. 스프링 컨테이너의 시작과 끝
- 프로토타입: 빈의 생성, 의존관계 주입까지만 관여하고 더는 관여안하는 매우 짧은 범위
- 웹 관련 스코프
  - 리퀘스트: 웹 요청이 들어오고 나갈때까지의 스코프
  - 세션: 웹 세션이 생성되고 종료될 때까지의 유지되는 스코프
  - 애플리케이션: 웹의 서블릭 컨텍스트와 같은 범위로 유지되는 스코프

```java
// componentScan 등록
@Scope("prototype")
@Component
public class HelloBean {}
// 수동 등록
@Scope("prototype")
@Bean
PrototypeBean HelloBean {}
```

## prototype scope v. singleton scope

singleton scope : 항상 같은 인스턴스 스프링 빈을 반환

prototype scope: 항상 다른 인스턴스 스프링 빈을 반환, 생성→DI→초기화 이후 과정은 스프링이 관여안함.

## test

### 싱글톤 빈 스코프

```java
public class SingletonTest{

@Test
    void singletonBeanFind() {
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SingletonBean.class);

        final SingletonBean bean1 = applicationContext.getBean(SingletonBean.class);
        final SingletonBean bean2 = applicationContext.getBean(SingletonBean.class);

        System.out.println("bean1 = " + bean1);
        System.out.println("bean2 = " + bean2);
assertThat(bean1).isSameAs(bean2);
        applicationContext.close();
}

@Scope("singleton")
static class SingletonBean{
@PostConstruct
        public void init() {
System.out.println("SingletonBean.init");
}

@PreDestroy
        public void destroy() {
System.out.println("SingletonBean.destroy");
}
    }
}
```

```java
SingletonBean.init
bean1 = shop.core.scope.SingletonTest$SingletonBean@432038ec
bean2 = shop.core.scope.SingletonTest$SingletonBean@432038ec
23:03:11.104 [main] DEBUG org.springframework.context.annotation.AnnotationConfigApplicationContext - Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@5403f35f, started on Tue Jul 06 23:03:10 KST 2021
SingletonBean.destroy
```

### 프로토타입 빈 스코프

```java
public class PrototypeTest {

    @Test
    void prototypeTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(PrototypeBean.class);
        System.out.println("find pBean1");
        final PrototypeBean bean1 = applicationContext.getBean(PrototypeBean.class);
        System.out.println("find pBean2");
        final PrototypeBean bean2 = applicationContext.getBean(PrototypeBean.class);

        System.out.println("bean1 = " + bean1);
        System.out.println("bean2 = " + bean2);
        assertThat(bean1).isNotSameAs(bean2);
        applicationContext.close();
    }

    @Scope("prototype")
    static class PrototypeBean {
        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
```

```java
find pBean1 
PrototypeBean.init // bean 생성이 컨테이너 생성 시점이 아니다. 조회 시점이다.
find pBean2
PrototypeBean.init
bean1 = shop.core.scope.PrototypeTest$PrototypeBean@432038ec
bean2 = shop.core.scope.PrototypeTest$PrototypeBean@7daa0fbd
// 종료 메서드 또한 실행되지 않는다. (스프링컨테이너가 빈 초기화 이후 관여 안함)
```

### 프로토타입 특징 정리

- 스프링 컨테이너 요청 시 새로 생성
- 스프링 컨테이너는 생성,의존관계주입, 초기화까지만 관여
- 종료 메서드 호출 안됨
- 프로토타입 빈의 소멸은 클라이언트가 관리해야한다

## 싱글톤 빈과 프로토타입을 같이 쓰면 문제가 발생

```java
public class SingletonWithPrototypeTest1 {
    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new
            AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);
        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(2);
    }

    static class ClientBean {
        private final PrototypeBean prototypeBean;

        @Autowired
        public ClientBean(PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
```

결론부터 말하면, 싱글톤 빈 내부에 프로토타입 빈을 주입해 사용하게 되면, 프로토타입 빈의 관리를 하지 않더라도, 싱글톤 빈때문에 프로토타입 빈 또한 함께 계속 유지가 된다.

이 것은, 프로토타입을 쓰려는 의도에 반하는 일이다. 왜냐하면 우린 프로토타입을 쓰는 이유가 `사용할 때마다 새로 생성해서 사용하기를 원하기 때문`

## Provider

`Dependency Lookup` 

의존관계를 외부에서 주입(DI) 받는게 아니라 이렇게 직접 필요한 의존관계를 찾는 것. 의존관계 조회(탐색)

필요한 기능은 지정한 프로토타입 빈을 컨테이너에서 대신 찾아주는 DL 기능의 무언가가 필요

### ObjectProvider

지정한 빈을 컨테이너에서 대신 찾아주는 DL 서비스를 제공하는 것이 바로 ObjectProvider 이다. 

```java
static class ClientBean {

        @Autowired
        private ObjectProvider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }
```

### javax.inject.Provider

- 별도 라이브러리 필요
- get() 메서드 하나만으로 가능해 기능이 단순
- 자바 표준이라서 스프링이 아니더라도 활용 가능
