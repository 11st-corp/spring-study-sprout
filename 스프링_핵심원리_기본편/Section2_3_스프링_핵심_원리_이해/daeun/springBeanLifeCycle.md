# Spring Bean Life Cycle
## 빈 인스턴스화 및 DI

![](https://i.imgur.com/E6QxDX1.png)

IoC 컨테이너는 개발자가 설정해둔 빈 정의, 빈 주입 관계를 토대로 생성하고 의존성 주입을 한다.

1. XML파일

    ```java
    <?xml version="1.0" encoding="UTF-8"?>
    <beans ...

    <bean id="hello" class="pojo.Hello">
    <property name="name" value="Spring"/>
    <property name="printer" ref="printer"/>
    </bean>

    <bean id="printer" class="pojo.StringPrinter"/>

    </beans>
    @Test
    public void 빈_생성_주입_테스트_XML() {
    GenericApplicationContext ac = new GenericApplicationContext();

    //register bean
    XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
    reader.loadBeanDefinitions("hello-config.xml");
    ac.refresh();

    //call bean

    Hello hello = ac.getBean("hello", Hello.class);
    hello.print();

    assertThat(ac.getBean("printer").toString(), is("Hello Spring"));

    출처: https://feco.tistory.com/11 [wmJun]
    ```

2. 자바 설정 클래스

    ```java
    public class AppConfig {

        public MemberService memberService() {
            return new MemberServiceImpl(new MemoryMemberRepository());
        }

        public OrderService orderService() {
            return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
        }
    }
    public class OrderServiceImpl implements OrderService {

        private final MemberRepository memberRepository;
        private final DiscountPolicy discountPolicy;

        public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
            this.memberRepository = memberRepository;
            this.discountPolicy = discountPolicy;
        }

        @Override
        public Order createOrder(Long memberId, String itemName, int itemPrice) {
            Member member = memberRepository.findById(memberId);
            int discountPrice = discountPolicy.discount(member, itemPrice); // 단일 책임의 원칙을 잘 지킴

            return new Order(memberId, itemName, itemPrice, discountPrice);
        }
    }

    @Test
    @BeforeEach
        public void beforeEach() {
            AppConfig appConfig = new AppConfig();
            memberService = appConfig.memberService();
        }
        @Test
        void join() {
            //given
            Member member = new Member(1L, "memberA", Grade.VIP);
            //when
            memberService.join(member);
            Member findMember = memberService.findMember(1L);
            //then
            Assertions.assertThat(member).isEqualTo(findMember);
        }

    ```

3. 어노테이션

    ```java
    @Configuration
    public class OtherHelloConfig {

        @Bean
        public OtherHello theOtherHello() {
            OtherHello otherHello = new OtherHello();
            otherHello.setPrinter(printer());

            return otherHello;
        }

        @Bean
        public Printer printer() {
            return new StringPrinter();
        }
    }
    @Test
    public void 빈_생성_주입_테스트_자바코드에의한설정() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(OtherHelloConfig.class);

        OtherHello hello = ac.getBean("theOtherHello", OtherHello.class);
        OtherHello hello2 = ac.getBean("theOtherHello", OtherHello.class);
        assertNotNull(hello);

        OtherHelloConfig config = ac.getBean("otherHelloConfig", OtherHelloConfig.class);
        assertNotNull(config);

        assertEquals(hello, config.theOtherHello());
        assertEquals(hello, hello2);
        assertEquals(hello2, config.theOtherHello());
        System.out.println(hello.sayHello());
        assertEquals(hello.sayHello(), hello2.sayHello());

    }

    출처: https://feco.tistory.com/11 [wmJun]
    ```

    ## 스프링 인지 여부 검사

    ### *AbstractAutowireCapableBeanFactory 클래스에서 아래와 같이 셋팅*

    ### BeanNameAware 인터페이스

    해당 인터페이스는 빈 객체가 자기 자신의 이름을 알아야 하는 경우에 사용된다.

    예를 들면, `log.debug( "beanName: " + beanName);` 과 같이 어떠한 빈 객체가 생성되는지에 대한 로그를 찍고자 할 때 사용할 수 있다.

    만일, 빈 클래스가 `BeanNameAware` 인터페이스를 구현한 경우 컨테이너는 setBeanName()메서드를 호출하여 빈 객체의 이름을 전달한다.

    ### BeanClassLoaderAware 인터페이스

    현재 빈 팩토리에서 빈 클래스를 로드하기 위해 사용하는 클래스 로더인데,  해당 클래스 로더를 커스텀했을 경우 이 인터페이스를 구현하여 참조를 설정할 수 있다.

    ### BeanFactoryAware 인터페이스

    빈 팩토리를 커스텀했을 경우, 해당 인터페이스를 구현함으로써 빈 팩토리의 참조를 설정할 수 있다.

    ```java

    private void invokeAwareMethods(final String beanName, final Object bean) {
            if (bean instanceof Aware) {
                if (bean instanceof BeanNameAware) {
                    ((BeanNameAware) bean).setBeanName(beanName);
                }
                if (bean instanceof BeanClassLoaderAware) {
                    ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
                }
                if (bean instanceof BeanFactoryAware) {
                    ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
                }
            }
        }
    ```

    ### ApplicationContextAware 인터페이스

    bean 에서 ApplicationContext에 직접 접근 시 사용

    주로 ApplicationContext세부 설정을 빈 설정 정보 파일 로딩 시점에 특정 bean에 위임하는 경우에 사용됨.

    빈 생성 생명주기 & 소멸 생명주기

    [https://ooeunz.tistory.com/107](https://ooeunz.tistory.com/107)