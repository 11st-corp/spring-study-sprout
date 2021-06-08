# AnnotationConfigApplicationContext 살펴보기
## 목적

- 내부를 살펴보면서 빈이 어떻게 관리되는지 알 수 있음.
- 환경 변수가 어떻게 처리되는지 알 수 있음.

## 구조

![Untitled (22)](https://user-images.githubusercontent.com/25525648/121211649-9c319e80-c8b7-11eb-90e1-b593a08dd4c2.png)
## 빈 등록

![Untitled (23)](https://user-images.githubusercontent.com/25525648/121212188-14985f80-c8b8-11eb-9295-d3e6f1b6bab3.png)

해당 AnnotatedBeanDefinitionReader를 설정한다.

![Untitled (15)](https://user-images.githubusercontent.com/25525648/121211476-786e5880-c8b7-11eb-9740-89f899413331.png)

빈을 등록한다.


![Untitled (17)](https://user-images.githubusercontent.com/25525648/121211449-72787780-c8b7-11eb-9be6-d5583f2ffdd7.png)

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
		if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) { 
			return;
		} // 1
		abd.setInstanceSupplier(supplier); 
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
		abd.setScope(scopeMetadata.getScopeName());
		String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

		AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
		if (qualifiers != null) {
			for (Class<? extends Annotation> qualifier : qualifiers) {
				if (Primary.class == qualifier) {
					abd.setPrimary(true);
				}
				else if (Lazy.class == qualifier) {
					abd.setLazyInit(true);
				}
				else {
					abd.addQualifier(new AutowireCandidateQualifier(qualifier));
				}
			}
		}
		if (customizers != null) {
			for (BeanDefinitionCustomizer customizer : customizers) {
				customizer.customize(abd);
			}
		}

		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
		definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry); // 3
	}
```

1. 유효한 메타데이터인지 체크
2. 빈 설정 정보에 따라 해당 값 설정.

![Untitled (24)](https://user-images.githubusercontent.com/25525648/121212193-1530f600-c8b8-11eb-859a-baaf6f6b796a.png)

3. 유효하지 않은 빈 설정인 경우 BeanDefinitionStoreException, BeanDefinitionOverrideException 등 과 같은 예외가 일어남.

## 환경변수

로컬, 개발, 운영등을 구분해서 처리

![Untitled (18)](https://user-images.githubusercontent.com/25525648/121211431-70aeb400-c8b7-11eb-90da-81465c8d2d09.png)

GenericApplicationContext는 AbstractApplicationContext를 상속받음.

![Untitled (19)](https://user-images.githubusercontent.com/25525648/121211419-6e4c5a00-c8b7-11eb-9d8d-76ea1040c93b.png)
해당 메소드를 살펴봤을 때 파라미터의 타입인 ConfigurableEnvironment 객체(인터페이스)를 살펴보면, 아래와 같이 activeProfiles를 셋팅하는 추상 메소드를 볼 수 있다.

![Untitled (20)](https://user-images.githubusercontent.com/25525648/121211404-6bea0000-c8b7-11eb-960a-98e4d9784522.png)


![Untitled (21)](https://user-images.githubusercontent.com/25525648/121211380-67254c00-c8b7-11eb-99ff-69962e350c71.png)

해당 IDE에서 active profiles를 설정하는데 이 IDE는 ConfigurableEnvironment의 구현체로 볼 수 있다.

이 곳에서 설정이 되면, 해당 profile의 application.yml 파일 정보로 처리될 것이다.