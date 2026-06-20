# Excerption Android App

독서 기록 앱 프로젝트 골격입니다. Android Studio에서 이 폴더를 열면 `app` 모듈 기준으로 작업할 수 있습니다.

## 앱 구조

```text
SplashActivity
  -> 자동 로그인 체크
  -> LoginActivity 또는 MainActivity

LoginActivity
  -> LoginFragment
  -> SignUpFragment

MainActivity
  -> HomeFragment: 책 목록
  -> StatsFragment: 독서 통계
  -> CalendarFragment: 독서 달력
  -> ProfileFragment: 내 정보

BookDetailActivity
  -> 책 정보
  -> 최근 발췌
  -> 리뷰

AddBookActivity
  -> 책 등록
  -> 도서 검색 API 연결 예정

TextScanActivity
  -> CameraX 촬영
  -> ML Kit 한국어 OCR
  -> 발췌 문장 편집 및 저장

ExcerptListActivity
  -> 책별 발췌 전체 보기

ExcerptImageActivity
  -> 발췌 문장 이미지 미리보기
  -> 이미지 공유

ReviewActivity
  -> 리뷰 작성
  -> 리뷰 수정
```

## 패키지 역할

```text
com.example.excerption
  common/
    IntentKeys.kt          Activity 간 extra key
    SessionManager.kt      임시 로그인 세션

  data/
    AppContainer.kt        Repository 제공
    local/
      AppDatabase.kt       Room DB
      dao/                 Book, Excerpt, Review DAO
      entity/              Book, Excerpt, Review Entity
    repository/            데이터 접근 계층

  ui/
    splash/                스플래시
    auth/                  로그인/회원가입
    main/                  Bottom Navigation 화면
    book/                  책 목록 어댑터, 책 등록, 책 상세
    scan/                  OCR 카메라
    excerpt/               발췌 목록, 발췌 이미지
    review/                리뷰 작성/수정
```

## 현재 구현된 기능

- 임시 로그인/로그아웃
- 책 등록 및 책 목록 표시
- 책 상세 화면 이동
- 책별 발췌 저장 및 목록 표시
- CameraX + ML Kit 한국어 OCR 기본 연결
- 발췌 문장 이미지 미리보기 및 공유
- 리뷰 작성/수정
- 전체 통계 화면
- 날짜별 발췌 확인용 달력 화면

## 팀원별 작업 추천

| 담당 | 주요 파일 | 다음 작업 |
| --- | --- | --- |
| 로그인/회원가입 | `ui/auth`, `common/SessionManager.kt` | Firebase Auth 또는 서버 로그인 연결 |
| 책 검색/등록 | `ui/book/AddBookActivity.kt` | 네이버/카카오/Google Books API 연결 |
| 홈/상세 | `ui/main/HomeFragment.kt`, `ui/book/BookDetailActivity.kt` | 표지 이미지, 검색, 정렬 추가 |
| OCR/발췌 | `ui/scan`, `ui/excerpt` | OCR 결과 줄 정리, 발췌 수정/삭제 추가 |
| 리뷰 | `ui/review` | 리뷰 제목, 태그, 삭제 기능 추가 |
| 통계/달력 | `ui/main/StatsFragment.kt`, `CalendarFragment.kt` | 월별 독서량, 그래프 추가 |

## 실행 방법

1. Android Studio에서 `C:\Users\나영인\Documents\Excerption` 폴더를 엽니다.
2. Gradle Sync를 실행합니다.
3. Android Emulator 또는 실제 기기를 연결합니다.
4. `app` 실행 구성을 선택하고 Run 합니다.

## 주의할 점

- 현재 로그인은 협업용 임시 구현입니다. 실제 배포 전 Firebase Auth 또는 서버 인증으로 교체해야 합니다.
- 도서 검색 버튼은 API 연결 전용 자리만 만들어두었습니다.
- OCR은 카메라 권한이 필요합니다.
- 발췌 이미지 공유는 `FileProvider`를 사용합니다.
- 데이터는 현재 Room 로컬 DB에 저장됩니다. 동기화가 필요하면 Repository 안에서 Firebase 또는 서버 API를 붙이면 됩니다.
