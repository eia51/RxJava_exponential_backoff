## RxJava 지수 백오프
- 오류 응답이 연속 될 때마다 재시도 간 대기 시간을 지수 간격(2의 n승)으로 늘려나가는 기법<br/>
- 백그라운드에서 반드시 처리 되어야 하는 작업이 요청 시점의 불안정한 네트워크 환경으로 인하여 정상적으로 처리되지 못하는 경우를 방지하기 위한 알고리즘

<br/>

## How to use
- 안드로이드 기준, 아래 RxJava 의존성 필요

```xml
dependencies {
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    implementation 'io.reactivex.rxjava2:rxjava:2.2.10'
}
```

<br/>

- 본 경로에 있는 `RetryWithExponentialDelay.java` 파일을 프로젝트 경로에 다운로드 받는다<br/>
  `(TODO 안드로이드 gradle에서 불러올 수 있게 라이브러리 깃에 릴리즈 해보기)`

<br/>

- 다음과 같이 코드 작성해서 사용
```java
CompositeDisposable disposable = new CompositeDisposable();
disposable.add(myApi.getDataWithFlowable(_url) 
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) //Schedulers.newThread() | AndroidSchedulers.mainThread()
                .retryWhen(new RetryWithExponentialDelay(retryCount))
                .subscribe(resp -> {
                    // 성공 처리
                }, throwable -> {
                    // 지수 백오프 요청이 완전히 실패했을 때 처리
                    throwable.printStackTrace();
                }));
```

- `myApi.getDataWithFlowable(_url)`은 `Flowable`을 반환하는 `Retrofit` 인스턴스

- `new RetryWithExponentialDelay(retryCount)`의 `retryCount`는 내가 최대로 수행하고 싶은 재시도 횟수.
   재시도 횟수를 거듭 할수록 2의 n승 단위로 대기 시간이 길어지기 때문에 적정한 `retryCount` 정의 필요.
   
<br/>

## 적용 결과   
![](https://images.velog.io/images/eia51/post/316dc0b0-d140-4ba5-9383-74d886a7b4f6/retry.png)
