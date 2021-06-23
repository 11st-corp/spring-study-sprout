# @SpringBootApplication  를 뜯어보자

## @SpringBootApplication

![image](https://user-images.githubusercontent.com/23401317/123054622-ff870900-d43f-11eb-9f9a-6b2cc41c710b.png)


## 자바 어노테이션

### @Target : 어노테이션을 부착할 대상을 지정함

```java
@Target({ElementType.TYPE)

public enum ElementType {
	TYPE, // 클래스, 인터페이스, Enum
	FIELD,
	METHOD,
	PARAMETER,
	CONSTRUCTOR,
	LOCAL_VARIABLE,
	ANNOTATION_TYPE,
	PACKAGE,
	TYPE_PARAMETER,
	TYPE_USE,
	MODULE
}
```

### @Retention : 어느 시점에 어노테이션 메모리를 가져갈 지 설정

```java
public enum RetentionPolicy {
	/**
	 * Annotations are to be discarded by the compiler.
	 * 컴파일러가 컴파일하고 어노테이션 메모리를 버림
	 */
	SOURCE,
	/**
	  * Annotations are to be recorded in the class file by the compiler
	  * but need not be retained by the VM at run time.  This is the default
	  * behavior.
    * 컴파일시에는 어노테이션 메모리를 가져가지만 런타임시에는 사라짐, 리플렉션으로 선언된 어노테이션 데이터를 가져올 수 없음
	  */
	CLASS,
	/**
	  * Annotations are to be recorded in the class file by the compiler and
	  * retained by the VM at run time, so they may be read reflectively.
	  * JVM이 자바 바이트코드가 담긴 class 파일에서 런타임환경을 구성하고 런타임을 종료할 때까지 메모리 살아있음
	  * @seejava.lang.reflect.AnnotatedElement
	  */
	RUNTIME
}
```

### @Documented

지정된 대상의 JavaDoc 에 이 어노테이션의 존재를 표기

### @Inherited

이 어노테이션을 사용한 슈퍼클래스를 상속한 서브클래스에서도 해당 어노테이션을 갖도록 한다.

## 스프링 어노테이션

### @SpringBootConfiguration

스프링의 @Configuration을 대체하는 스프링 부트 전용 어노테이션

### @EnableAutoConfiguration

@component 어노테이션 및 @Service, @Repository, @Controller 등의 어노테이션을 스캔하여 Bean으로 등록해주는 어노테이션

![image](https://user-images.githubusercontent.com/23401317/123054761-28a79980-d440-11eb-88e6-b4c210538329.png)

![image](https://user-images.githubusercontent.com/23401317/123054859-437a0e00-d440-11eb-9bb4-719f45a989db.png)

### @ComponentScan

@component 어노테이션 및 @Service, @Repository, @Controller 등의 어노테이션을 스캔하여 Bean으로 등록해주는 어노테이션
