--------------------
# Development Document
--------------------

# 목차

- [1. GoogleLoginOAuth2](#GoogleLoginOAuth2)
    - [1. GoogleLogin scope 설정](#GoogleLogin-scope-설정)
    - [2. Person 객체 생성하기](#Person-객체-생성하기)

# GoogleLoginOAuth2

이전에는 org.springframework.social.oauth2 라이브 러리를 사용해서 OAuth2 인증을 시도했지만 `2019년 3월 7일` 부로 서비스를 Google API 가 종료되어 
[Google API 서비스 종료](#https://developers.google.com/people/legacy)

> Legacy People API has not been used in project before or it is disabled 

같은 에러 메세지가 출력됩니다.

이를 대체하기 위해서 `Google People API` 를 사용 하였습니다.

> gradle 의존성

~~~
implementation 'com.google.apis:google-api-services-people:v1-rev528-1.25.0'
~~~

> 구글 API 등록

https://console.developers.google.com/apis/dashboard?authuser=1&project=jjunpro-project

사용자 인증 정보 > 사용자 인증정보 만들기 + > 웹 에플리케이션 > 승인된 리디렉션 URI > 원하는 로그인 콜백 링크 작성 'http://localhost:8081/google' > ID, 클라이언트 보안 비밀 프로젝트 프로퍼티스에 추가

## GoogleLogin scope 설정

> GoogleServiceImpl.class

이전 방법 Google API

~~~
@Override
public String googleLogin() {
    String scope = "https://www.googleapis.com/auth/userinfo.profile";

    OAuth2Parameters parameters = new OAuth2Parameters();

    parameters.setRedirectUri(this.uri);
    parameters.setScope(scope);

    return googleConnectionFactory()
            .getOAuthOperations()
            .buildAuthenticateUrl(parameters);
}            
~~~

이후 방법 Google People API

~~~
@Override
public String googleLogin() {
    List<String> SCOPES = Arrays.asList(
        PeopleServiceScopes.USERINFO_EMAIL, 
        PeopleServiceScopes.USERINFO_PROFILE
    );

    String authorizationUrl =
            new GoogleBrowserClientRequestUrl(
                    this.googleId,
                    this.uri,
                    SCOPES
            ).setResponseTypes(Collections.singleton("code")).build();

    return authorizationUrl;
}
~~~

이전 방법(Google API) 으로 URL 을 출력하면 

> https://accounts.google.com/o/oauth2/auth?client_id=127049673428-qq2nov7cfgi51m0pbho6o1b81legk39s.apps.googleusercontent.com&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fgoogle&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile

redirect_uri 와 `response_type 의 code` 둘다 가져와서 추가적인 인증을 할 수 있도록 데이터를 제공하였습니다.
하지만 이후 방법(Google People API) 으로 `setResponseTypes 설정 없이` URL 을 출력하는 경우

> https://accounts.google.com/o/oauth2/auth?client_id=127049673428-qq2nov7cfgi51m0pbho6o1b81legk39s.apps.googleusercontent.com&redirect_uri=http://localhost:8081/google&response_type=token&scope=https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile

redirect_uri 와 `response_type 의 token` 으로 불러와서 GoogleAuthorizationCodeTokenRequest tokenResponse 객체를 생성하는데 필요한 code 값이 존재하지 않아 추가적인 인증이 불가능 했습니다.
둘의 URL 차이를 비교해서 `response_type 의 차이`가 있다는 것을 알고 URL 를 생성하는 객체에 추가적인 기능이 있을거라는 생각에 `GoogleBrowserClientRequestUrl` 객체를 탐색해 보았습니다.
객체의 메서드중에서 `Collection<String>  setResponseTypes` 이라는 메소드를 발견하여 `code` 를 명시하여 객체를 build 해보았습니다.
결과는 성공적으로 code 값을 리턴하였습니다.

추가적으로 SCOPES 를 추가하여 사용자의 정보를 다양하게 받아오려면 아래 주소를 참고합니다.

[SCOPES 설정](#https://developers.google.com/people/api/rest/v1/people/get)

authorizationUrl 값을 Contoller class 로 보내서 redirect 시킵니다.

> GoogleController.class

~~~
@GetMapping("/googleLogin")
public RedirectView googleLogin() {

    RedirectView redirectView = new RedirectView();
    String       url          = googleService.googleLogin();
    redirectView.setUrl(url);

    return redirectView;
}
~~~

redirect 된 후 google api 등록된 `승인된 리디렉션 URI` 주소로 이동되게 됩니다.
ex) localhost:8080/google

~~~
/* Google Login Call Back 턴 메소드 */
@GetMapping("/google")
public String google(@RequestParam("code") String code) throws IOException {
    googleService.getGoogleUserProfile(code);

    ...
}
~~~

getGoogleUserProfile 메소드를 실행하여 Person 객체를 생성합니다.

## Person 객체 생성하기

googleLogin 메소드로 부터 전달받은 code 값을 getGoogleUserProfile 메소드에서 전달받아 Person 객체를 생성하는데 사용합니다.

~~~
/* Google Login Call Back 턴 메소드 */
@GetMapping("/google")
public String google(
        @RequestParam("code") String code,
        HttpServletRequest request,
        Model model
) throws IOException {
    Person       googleUserProfile = googleService.getGoogleUserProfile(code);
    Name         userName          = googleUserProfile.getNames().iterator().next();
    EmailAddress emailAddress      = googleUserProfile.getEmailAddresses().iterator().next();

    Optional<Account> accountDB = accountService
            .findByEmail(emailAddress.getValue());

    UserRole userrole;

    if (accountDB.isPresent()) {
        accountDB.get().setFirstName(userName.getGivenName());
        accountDB.get().setLastName(userName.getFamilyName());
        userrole = accountDB.get().getUserRole();

        accountService.updateAccount(accountDB.get());

        model.addAttribute("user", accountDB.get());
    } else {
        Account account = Account.builder()
                .firstName(userName.getGivenName())
                .lastName(userName.getFamilyName())
                .enabled(true)
                .userRole(UserRole.USER)
                .build();
        userrole = account.getUserRole();

        accountService.insertAccount(account);

        model.addAttribute("user", account);
    }

    securityService.autologin(
            emailAddress.getValue(),
            null,
            userrole,
            request
    );

    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
            .getAuthentication().getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    while (iterator.hasNext()) {
        System.out.println(iterator.next());
    }

    System.out.println(name);

    return "account/userProfile";
}
~~~

# 참고 사이트

[social-login-oauth2 구버전 전체적인 코드 틀만 참고함](#https://github.com/talk2amareswaran/social-login-oauth2-spring-boot)