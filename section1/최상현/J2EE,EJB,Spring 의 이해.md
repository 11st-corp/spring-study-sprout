# 섹션1



## 용어 

### J2EE (자바 to 플랫폼 엔터프라이즈 에디션)

- client 단에 J2EE 는 순수 HTML 지원. HTML, 다른 포맷 data 를 만들어 제공하기 위해서는 JSP, servlet code 가 필요
- EJB 는 플랫폼의 로직이 저장되는 별도 레이어를 제공. EJB 서버는 스레딩, 동시성제어, 보안, 메모리 관리 등을 지원하는 함수들 제공
- JDBC(자바 데이터베이스 연결성) 을 제공. 자바 데이터베이스를 위한 표준 인터페이스다.
- GUI (그래픽 사용자 인터페이스) 를 요구하는 일 없이 개발자들에게 일관성을 향상시켜주는 자바 서블릿 API 제공

### 그럼 스프링이랑 J2EE는 다른거야?

스프링 프레임워크 또한 J2EE application 이다.

> 스프링은 J2EE를 대체할 수 없다. 다만 스프링은 J2EE를 만족한다.

우리가 작성한 코드는 스프링 컨테이너에게 뭘해야할지를 알린다.

그러면 스프링은 J2EE 클래스들에게 이야기를 전달한다.

말하자면, **J2EE 없이는 스프링 프레임워크 또한 없다.**

https://www.quora.com/What-is-the-difference-between-J2EE-and-Spring

### EJB

EJB 는 엔터프라이즈 자바빈의 약어. Sun 사가 만들어낸, 확장성 있고 안정성있는 분산형 어플리케이션을 개발하기 위해 만든 명세다.

Weblogic 등으로 대표되는 어플리케이션 서버가 있어야만 EJB 어플리케이션을 돌릴 수 있다. 

- 생명주기 관리
- 보안
- 트랜잭션 관리
- 오브젝트 풀링 

등 기능을 지원한다. Server-side component 이다.

https://www.javatpoint.com/what-is-ejb

However, developing an enterprise application with EJB was not easy, as the developer needed to perform various tasks, such as creating Home and Remote interfaces and implementing lifecycle callback methods which lead to the complexity of providing code for EJBs Due to this complication, developers started looking for an easier way to develop enterprise applications.



## 스프링의 탄생 배경 이해

스프링 이전엔 EJB가 자바를 사용한 기업 소프트웨어 개발 세계를 지배했었다. 

그러나 현업개발자들이 기업형 애플리케이션을 EJB 로 개발하는건 어려웠다. 너무 다양한 작업을 수행하기를 개발자에게 요구했다.  예를 들어, Home, Remote 인터페이스를 변도로 만들어야 했고, EJB 에는 복잡함을 증가시키는 lifecycle callback 메서드들이 도입되어있었다. 그래서 개발자들은 더 쉽게 엔터프라이즈 애플리케이션을 개발할 방법을 찾아나섰다.

이 시기를 스프링의 창안자는 자바의 겨울에 비유했고, 스프링이 등장함으로써 그 겨울이(EJB를 사용하던 시기) 끝나고 봄이 찾아오게 될 것이라는 의미로 이 J2EE framework를 스프링이라 이름지었다.



EJB 내의 Entity Bean 을 대체하는 JPA 표준 정의 **하이버네이트**

Entity Bean -> Hibernate -> JPA

JPA 는 단순히 인터페이스에 불과하다. JPA 인터페이스를. 활용한다는 것은

즉 그 구현체들을 어떻게든 어디선가 본인이 구현해서 쓴다는 것

### 정리하자면

스프링의 의미는 자바 언어 생태계의 긴 겨울을 끝내고 봄을 맞이한다는 것

EJB 를 대체하는 새로운 자바 기반 프레임워크

### 스프링의 진짜 존재 의의

**좋은 객체 지향 애플리케이션 개발을 위한 도구**



## 스프링 구성 이해

스프링 프레임워크, 스프링 부트 둘 중 하나를 필수적으로 택한다.

그 외에 본인이 개발하고자 하는 내용에 따라 필요한 의존성을 추가해야한다.

데이터에 접근하고 관리,변경 등 진행하려면 spring JDBC, ORM 프레임워크등

#### 스프링 부트는 뭐냐?

스프링 프레임워크에는 해야하는 설정들이 많다. 그 설정들을 자동으로 해주는 프레임워크이기에 초심자가 접하기 좋다. 

suffix 에 starter 가 붙는 종속성들은 많이 접했을 것

#### 스프링 vs. 스프링 부트



## 객체 지향 프로그래밍

산업형 소프트웨어 개발에 용이한 개발 방식

**유연한 변경** -> 요구사항에 따라 부품을 대체하기 쉽다

### 4가지 객체 지향 속성

캡상추다

- 캠슐화
- 추상화
- 상속
- 다형성
  - 오버라이딩
  - 클라이언트 변경 없이 서버의 구현 기능 유연하게 변경
  - 제일 중요

### 객체 지향 설계 5원칙 SOLID

결국 DI로 흘러가게 됨





