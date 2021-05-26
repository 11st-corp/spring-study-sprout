# spring-boot-starter

Spring Boot에서는 `spring-boot-starter`라는 사전에 미리 정의해둔 편리한 의존성 조합을 제공한다. 

프로젝트에 설정해야하는 다수의 의존성들을 `starter`가 이미 포함하고 있기 때문에 우리는 `starter`에 대한 의존성 추가만으로 프로젝트를 시작하거나 새로운 기능을 추가할 수 있다.

그렇다면, `spring-boot-starter-web` 의존성을 걸어줬을 때 어떤 작업들이 있을까?

## 의존성 트리

1. cmd 창에 아래와 같이 명령어를 치면, dependency 트리를 확인할 수 있다.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled.png)

위와 같은 방법은 cmd창으로 나열되어있어 보기가 깔끔하지 않다는 단점을 가진다.

2. IntelliJ에서 gradle 모달창을 확인해보자.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%201.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%201.png)

spring-boot-starter-web은 여러 의존성으로 깊게 얽혀있는 라이브러리이다.

`org.springframework.boot.starter` 의`spring-boot-autoconfigure` 를 살펴보겠다.

```java
// org.springframework.boot.starter
plugins {
	id "org.springframework.boot.starter"
}

description = "Core starter, including auto-configuration support, logging and YAML"

dependencies {
	api(project(":spring-boot-project:spring-boot"))
	api(project(":spring-boot-project:spring-boot-autoconfigure"))
	api(project(":spring-boot-project:spring-boot-starters:spring-boot-starter-logging"))
	api("jakarta.annotation:jakarta.annotation-api")
	api("org.springframework:spring-core")
	api("org.yaml:snakeyaml")
}

```

### [spring-boot-project:spring-boot-autoconfigure](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-project/spring-boot-autoconfigure)

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/ezgif.com-gif-maker_(6).gif](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/ezgif.com-gif-maker_(6).gif)

대부분의 라이브러리가 optional로 설정되어있는 것을 확인할 수 있다.

autoconfiugre에서 참조한 의존성에는 optional을 걸어두는 것이 좋다. autoconfigure를 참조하는 모듈에서 필요한 의존성이 없을 때, Spring Boot는 자동 설정을 하지 않는다.

너무나도 방대하다.... 조그만 규모의 spring-boot-starter를 구현해보자!

# starter 구현체

해당 starter를 구현해봄으로써 어떻게 의존성을 걸어주고 어떤 설정을 걸어주는지 알 수 있다.

참조: [https://meetup.toast.com/posts/152](https://meetup.toast.com/posts/152)

## 구현 내용

request parameter를 logging하는 `spring-boot-starter`

## 구현

`sample-boot-starter` 내부에 **3개의 모듈**을 생성한다.

- `sample-spring-boot-autoconfigure` : `@Configuration`으로 특정 조건에 맞춰서 설정을 실행
- `sample-spring-boot-starter-request-parameter-logging-filter` : `autoconfigure`와 필요한 의존성을 가짐
- `sample-spring-boot-starter-web` : `starter`를 주입받음

`autoconfigure`와 `starter`를 굳이 나누지 않고,`starter`내에 `autoconfigure`를 정의해서 배포하는 경우도 있다.

### sample-spring-boot-autoconfigure

autoconfigure 모듈은 자동 설정에 필요한 요소와 library를 갖고 있다.

Spring Boot 의 AutoConfiguration 을 살펴보면, 모든 선택적 의존성에 Dependency Optional True 가 붙어 있는 것을 확인할 수 있음. Gradle 에서는 compileOnly 를 사용할 수 있음. 대부분의 선택적 의존성들은 Starter Pack 에 의하여 주입됨.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%202.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%202.png)

의존성 있는 파일을 추가한다. 선택적 의존성이 대부분이다.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%203.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%203.png)

로깅을 하기 위해 filter를 구현했다.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%204.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%204.png)

아래의 정보를 META-INF/additional-spring-configuration-metadata.json에 저장함으로써 application.yml에서 설정한 key에 대한 정보를 정의할 수 있다.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%205.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%205.png)

additional-spring-configuration-metadata.json

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%206.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%206.png)

**1. 자동으로 설정할 내용을 담을 @Configuration 클래스 작성**

모듈이 다 구현되었으니, 자동 설정 기능을 위한 코드를 담고 있는 모듈(jar 파일)은 @Configuration 애노테이션을 사용해서 설정 클래스를 작성한다.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%207.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%207.png)

**2. META-INF/spring.factories 파일에 설정 클래스 지정하기**

스프링 부트는 클래스패스에 위치한 모든 META-INF/spring.factories 파일의 org.springframework.boot.autoconfigure.EnableAutoConfiguration 프로퍼티 값을 읽어와 설정 클래스로 사용한다.

클래스패스 위치에(메이븐 같으면 src/main/resources 폴더에) META-INF/spring.factories 파일을 만들고, org.springframework.boot.autoconfigure.EnableAutoConfiguration 프로퍼티의 값으로 자동 설정으로 사용할 클래스를 값으로 준다.

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%208.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%208.png)

spring.factories

### sample-spring-boot-starter-request-parameter-logging-filter

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%209.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%209.png)

선택적으로 걸었던 것을 필수적으로 의존성을 걸어준다.

### sample-spring-boot-starter-web

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%2010.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%2010.png)

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%2011.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%2011.png)

![spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%2012.png](spring-boot-starter%2014fb3716b8be410592e0e81f7f11d085/Untitled%2012.png)

의존성이 걸어진 것을 확인할 수 있다.

### 아쉬웠던 점

1. gradle 프로젝트로 구현함으로써 의존성을 걸고 싶었는데, gradle 개발 지식 부족으로 못했다.. (gradle 공부를 통해 다시 개발해보고 싶다.)
2. 스프링부트는 어떻게 spring.factories에서 프로퍼티 값을 읽어와 설정 클래스로 사용하는지 알고 싶다.
3. /META-INF가 어떻게 매핑되는 건지 알고 싶다.
4. EnableAutoConfiguration에 대해 자세히 알고 싶다.

### 알게된 점

1. 라이브러리에 starter를 추가함으로써 어떤 식으로 의존성이 걸려서 개발을 할 수 있도록 만들어주는지 알 수 있었다.
2. gradle 모달창을 통해 연관된 라이브러리가 어떤 것들이 있는지 알 수 있었다.
3. github에 spring-boot 레포에서 해당 라이브러리에 대해 자세히 뜯어볼 수 있다는 것을 알게 되었다.
4. 내가 공부하고 싶어하는 것을 알게 되었다.