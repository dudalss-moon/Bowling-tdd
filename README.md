# Bowling Kata — TDD Practice

미국식 텐핀 볼링 점수 계산기를 TDD(Test-Driven Development)로 구현한 연습 프로젝트입니다.

## 요구사항

- Java 17+
- Gradle 9.3+

## 빠른 시작

```bash
# 테스트 실행
./gradlew test

# 테스트 리포트 확인
# build/reports/tests/test/index.html
```

## 프로젝트 구조

```
src/
├── main/java/org/example/
│   └── Game.java          # 볼링 게임 핵심 로직
└── test/java/org/example/
    └── GameTest.java      # TDD 테스트 5개
```

## API

```java
Game game = new Game();

game.roll(pins);  // 투구 — 쓰러뜨린 핀 수 전달
game.score();     // 최종 총점 반환
```

## 볼링 점수 규칙

| 상황 | 점수 |
|------|------|
| 일반 | 쓰러뜨린 핀 수 합계 |
| 스페어 (2구로 10핀) | 10 + 다음 1구 핀 수 |
| 스트라이크 (1구로 10핀) | 10 + 다음 2구 핀 수 합계 |
| 10프레임 스페어/스트라이크 | 추가 투구 허용 (최대 3구) |

## TDD 사이클

| 사이클 | 테스트 | 결과 |
|--------|--------|------|
| 1 | 거터 게임 → 0점 | `Game` 클래스 최초 생성 |
| 2 | 올원 게임 → 20점 | 기존 로직으로 통과 |
| 3 | 스페어 → 16점 | 프레임 루프 + 스페어 보너스 |
| 4 | 스트라이크 → 24점 | 스트라이크 보너스 |
| 5 | 퍼펙트 게임 → 300점 | 기존 로직으로 통과 |

상세 내용은 [TDD_REPORT.md](./TDD_REPORT.md)를 참고하세요.
