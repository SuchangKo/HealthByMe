# HealthByMe
Software Maestro 1st Homework

구성
--
Server
- Node.js
- Express.js
- SQLite3
- Microsoft Azure PaaS (Web-App, Node/Express)

Android
- Retrofit 2.0
- Facebook Login

과제 제출 요건
--
### 필수사항
#### 1. 앱(게임)과 서버 통신하는 로직이 포함되어야함.
Android(Retrofit) -> Azure(Node.js/Express)

#### 2. 클라이언트 게임은 게임첫 설치 시간. 접속시간. 접속후 게임종료시간. 마지막 플레이시간등이 DB에 저장되어야 함.
Android App // 해당없음

#### 3. 페이스북, 트위터, 이메일등 타 서비스 연계가 1개이상 되어야 함.
Facebook Login 사용

#### 4. 앱스토어에 업로드 되어야함.
 [Google Play Link](https://play.google.com/store/apps/details?id=kr.kodev.healthbyme)

#### 5. 서버 프로그램이 클라우드나 호스팅이 되는 서버에 배포가 되어야함.
Microsoft Azure [Server URL](http://healthbyme-node-express.azurewebsites.net/)

#### 6. 데이터는 DB에 저장되어 SQL을 통해서 조회 및 추가가 가능해야함.
SQLITE 3 (healthbyme.db)

#### 7. 백엔드는 BaaS나 클라우드 제한 없으나 웹서비스 형태이어야 함.
Microsoft Azure [Server URL](http://healthbyme-node-express.azurewebsites.net/)

### 가점사항
#### 1. 클라우드 이용시 가점.
Microsoft Azure [Server URL](http://healthbyme-node-express.azurewebsites.net/)

#### 2. 친구추가, 랭킹, 선물하기등의 기능 추가시 가점.
해당없음

#### 3. Restful API 형태 설계 적용시 가점.
/users
- GET: 회원정보 요청 
- POST: 회원가입 (Facebook Login 자동가입)
- PUT: 닉네임 수정
- DELETE: 회원탈퇴

/foods
- GET: Food List 요청
- POST: Food 작성
- PUT: Food 수정
- DELETE: Food 삭제

#### 4. push 구현 되었으면 가점.
Android GCM

#### 5. 안드로이드와 아이폰 둘다 대응시 가점.
Only Android

#### 6. DB와 연동되는 별도의 CMS(관리시스템)을 구축시 가점.




