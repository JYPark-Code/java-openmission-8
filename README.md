# 우테코 8기 4주차 오픈미션
작성자 : 박지용

## 🧭 개요
이 프로젝트는 E북으로 무료 제공되는 "점프 투 스프링부트"를 기반으로 한 
게시판 구현 리팩토링 학습용 프로젝트입니다.
[점프 투 스프링부트](https://wikidocs.net/book/7601)

## 💡 미션 선택 이유
3년 전에 동일 저자의 책인 점프투파이썬 - flask 편으로 게시판 코드를 만들어본 바 있습니다.
당시에 Python, Flask + Jinja Template + SQLAlchemy 조합으로 진행해보았습니다.
[Flask 게시판](https://github.com/JYPark-Code/flask-board)

그 때와 달리, 현재는 Java/Spring에 대한 이해도가 있는 상태에서 Code Along하고,
책의 완성된 코드 구조를 개선하는 것을 목표로 진행했습니다.

## 🧩 구현 목록

Part 1 - 게시판
* Entity 설계 및 연관관계 매핑
* Repository 별 DB 관리
* 게시 글 상세 보기 (질문 목록 보기)
* Root URL 적용하기
* Controller - Repository - Service 계층 분리
* 상세 페이지 만들기 (Thymeleaf를 이용한 View Template 구성)
* URL prefix 적용하기
* 답변 만들기
* Bootstrap css 적용하기
* 표준 HTML 구조 적용하기
* 예외 처리 및 유효성 검증 (Validation)
* form 활용하기

---

Part 2 - 게시판
* Navigation 바 추가
* 페이징 처리
  * 테스트 데이터 넣기 (300개)
  * 최신순으로 Sorting하기.
* 게시물 번호 부여하기
* 답변 개수 표시하기
* Spring Security를 이용한 회원 가입 기능 개발
  * 비회원 게시판 구경 허용 (Spring Security 로그인 페이지 우선 접속 해제)
  * 회원 엔티티 구현 및 Service, Controller 구현
  * 중복 로그인 방지
  * 책의 Spring Security v6 -> v7로 코드 개선 (Deprecated된 AntPathRequestMatcher 처리) 
* 로그인/로그아웃 구현
  * Spring Security 로그인 기준 UserSecurityService 구현하기 (UserSecurityService implement UserDetailsService)
  * 유저 등급 나누기 (Enum)
  * 로그인, 로그아웃 상태 템플릿에 적용하기
* 게시판에 작성자 컬럼 추가
  * 게시글에 작성자 추가
  * 답글에 작성자 추가
* 수정/삭제 기능 추가하기
  * 질문 수정/삭제
  * 댓글 수정/삭제
  * 수정 일시 표시하기
* 추천 기능 추가하기
  * 질문 추천 기능 추가
  * 답변 추천 기능 추가
* 앵커 기능 추가하기
  * 답변 앵커 추가하기 - why? : 답변 작성, 수정시 페이지 상단으로 스크롤 이동하는 것을 답변이 있는 location에 고정.
  * 리다이렉트 수정하기
  * 답변 Service, Controller 수정하기
* 마크다운 적용하기
  * 작성에 마크다운 적용(노션, Github, velog 등)
* 검색 기능 추가하기
  * 검색 기능 구현하기 1 - JPA Specification 사용. - QuestionService에서 Predicate로 구현
  * 검색 기능 구현하기 2 - @Query 사용하기. - Repository에서 JPQL로 구현
  * 검색 화면 만들기
  
---
Part 3 - Out of Book 
#### 추가기능 개발하기
* 답변 페이징, 정렬 기능 추가 - 답변은 5개 디스플레이가 기본이고, 넘어 갈 경우 페이지가 생성된다. 
* 답변 - 댓글 기능 추가 + 대댓글 기능 추가 - 난이도 ★★★★☆
  * 비로그인시 댓글 보기
  * 로그인시 추천, 댓글, 수정, 삭제 가능
  * 부모 댓글 삭제 시, 연쇄 삭제 기능
  * 특수 케이스 처리 : CSRF 오류 -> 구글 독스 해결 방법 정리
* My Page 구현하기
  * 가입일 추가
  * 가입일 경과일 추가
  * 활동 요약 작성
  * 추천 Count
  * 최근 작성한 질문 / 답변 / 댓글
    * 링크 구현
  * 프로필 편집
    * 이메일 편집
    * 자기소개
  * 비밀번호 변경 & 임시 비밀번호 발급 
    * 로그인 후 마이페이지 새 비밀번호/새 비밀번호 확인
    * 비밀번호 찾기 - 가입 시 넣은 이메일로 본인 인증 -> 임시 비밀번호 8자리 발급
* 최근 답변 / 추천 답변 순으로 노출하기
* 조회 수 표시하기
  * 메인 페이지 Column 추가.
  * 상세 페이지 내 추가.
  * Transaction 적용

Part 4 - Docker
* 


## ⚙️ 개발 환경
* IDE: IntelliJ IDEA (Eclipse 미사용)
* DB: MySQL (예제의 H2 Database 대체)
* Framework: Spring Boot 3.5.7
* Template Engine: Thymeleaf
* Language: Java 21
* Build Tool: Gradle
* Docker

## :camera: 스크린샷
1. 게시판 목록 (로그인 전, 로그인 후)
<img width="1918" height="1064" alt="screencapture-localhost-8080-question-list-2025-11-19-17_34_19" src="https://github.com/user-attachments/assets/05abb0bd-b6b8-480a-a43f-431b444ebf1c" />
<img width="1918" height="1064" alt="screencapture-localhost-8080-question-list-2025-11-19-17_35_43" src="https://github.com/user-attachments/assets/c51d40dc-e487-4f66-9322-b733907bd93f" />

2. 로그인 창, 비밀번호 찾기
<img width="1918" height="1064" alt="screencapture-localhost-8080-user-login-2025-11-19-17_34_49" src="https://github.com/user-attachments/assets/e639d3d2-4edd-4f6e-9ee7-f0384fe2005c" />
<img width="1918" height="1064" alt="screencapture-localhost-8080-user-forgot-password-2025-11-19-17_39_06" src="https://github.com/user-attachments/assets/4d57a697-9a3b-42c0-a449-efb2e5a2dd50" />
<img width="1918" height="1064" alt="screencapture-localhost-8080-user-forgot-password-2025-11-19-17_39_23" src="https://github.com/user-attachments/assets/a1860d92-f879-484c-b61f-46101cc0d695" />

3. 마이페이지
<img width="1918" height="1617" alt="screencapture-localhost-8080-user-mypage-2025-11-19-17_35_55" src="https://github.com/user-attachments/assets/207c664f-931e-4996-b195-272f1c1ba228" />

4. 게시판 상세페이지
<img width="1918" height="1314" alt="screencapture-localhost-8080-question-detail-307-2025-11-19-17_36_46" src="https://github.com/user-attachments/assets/c0d05da8-0302-4d1c-80ec-09cd55107717" />
<img width="1918" height="2487" alt="screencapture-localhost-8080-question-detail-306-2025-11-19-17_37_13" src="https://github.com/user-attachments/assets/040570e1-c042-4e06-a9d3-27c75eb6cf97" />

5. 검색 결과
<img width="1918" height="1064" alt="screencapture-localhost-8080-question-list-2025-11-19-17_38_00" src="https://github.com/user-attachments/assets/c9b0846b-11f8-4741-b3ac-a203edb4688d" />

6. 질문 등록
<img width="1918" height="1064" alt="screencapture-localhost-8080-question-create-2025-11-19-17_38_29" src="https://github.com/user-attachments/assets/56da3c5e-8305-463d-a16d-0a7d3b3d9b5f" />

