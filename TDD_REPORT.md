# Bowling Kata TDD 구현 리포트

## 개요

| 항목 | 내용 |
|------|------|
| 프로젝트 | bowling-tdd |
| 언어 / 빌드 | Java 17 / Gradle + JUnit Jupiter |
| 방법론 | Test-Driven Development (Red → Green → Refactor) |
| 작성일 | 2026-06-11 |

---

## 사전 준비

### 환경 설정

- `JAVA_HOME` 미설정 상태 → `C:\Users\User\.jdks\temurin-17.0.19` 확인 후 사용
- `build.gradle`에 UTF-8 인코딩 옵션 추가 (한글 `@DisplayName` 컴파일 오류 해결)

```groovy
compileJava.options.encoding = 'UTF-8'
compileTestJava.options.encoding = 'UTF-8'
```

---

## TDD 사이클 기록

### Cycle 1 — 거터 게임 (Gutter Game)

**목표:** 모든 투구가 0이면 총점은 0이다

#### RED

```java
@Test
@DisplayName("거터 게임: 모든 투구가 0이면 총점은 0이다")
void gutterGameScoresZero() {
    rollMany(20, 0);
    assertEquals(0, game.score());
}
```

실패 원인: `Game` 클래스 미존재 → 컴파일 오류

```
error: cannot find symbol
    private Game game;
```

#### GREEN

`Game` 클래스 최초 생성. 투구를 배열에 저장하고 단순 합산.

```java
public int score() {
    int score = 0;
    for (int i = 0; i < 20; i++) {
        score += rolls[i];
    }
    return score;
}
```

결과: `BUILD SUCCESSFUL`

---

### Cycle 2 — 올원 게임 (All Ones)

**목표:** 모든 프레임에서 핀 1개씩 쓰러뜨리면 총점은 20이다

#### RED

```java
@Test
@DisplayName("모든 프레임에서 1개씩 쓰러뜨리면 총점은 20이다")
void allOnesScoresTwenty() {
    rollMany(20, 1);
    assertEquals(20, game.score());
}
```

#### GREEN

Cycle 1의 단순 합산 로직으로 즉시 통과. 추가 구현 불필요.

결과: `BUILD SUCCESSFUL`

---

### Cycle 3 — 스페어 (Spare)

**목표:** 스페어 후 다음 투구 점수가 보너스로 추가된다 (5+5+3 → 16점)

#### RED

```java
@Test
@DisplayName("스페어 후 다음 투구가 보너스로 추가된다")
void oneSpareScoresSixteen() {
    game.roll(5);
    game.roll(5); // spare
    game.roll(3);
    rollMany(17, 0);
    assertEquals(16, game.score());
}
```

실패 원인: 보너스 로직 없음 → 13점으로 계산됨

```
expected: <16> but was: <13>
```

#### GREEN

`score()` 메서드를 프레임 단위 루프로 재작성. 스페어 판별 후 보너스 적용.

```java
public int score() {
    int score = 0;
    int rollIndex = 0;
    for (int frame = 0; frame < 10; frame++) {
        if (isSpare(rollIndex)) {
            score += 10 + rolls[rollIndex + 2];
            rollIndex += 2;
        } else {
            score += rolls[rollIndex] + rolls[rollIndex + 1];
            rollIndex += 2;
        }
    }
    return score;
}

private boolean isSpare(int rollIndex) {
    return rolls[rollIndex] + rolls[rollIndex + 1] == 10;
}
```

결과: 전체 테스트 `BUILD SUCCESSFUL`

---

### Cycle 4 — 스트라이크 (Strike)

**목표:** 스트라이크 후 다음 두 투구 점수가 보너스로 추가된다 (10+3+4 → 24점)

#### RED

```java
@Test
@DisplayName("스트라이크 후 다음 두 투구가 보너스로 추가된다")
void oneStrikeScoresTwentyFour() {
    game.roll(10); // strike
    game.roll(3);
    game.roll(4);
    rollMany(16, 0);
    assertEquals(24, game.score());
}
```

실패 원인: 스트라이크 판별 없음 → 스페어로 오판 또는 보너스 미적용

#### GREEN

스트라이크 조건을 스페어보다 먼저 검사. 스트라이크 프레임은 1구로 종료되므로 `rollIndex += 1`.

```java
if (isStrike(rollIndex)) {
    score += 10 + rolls[rollIndex + 1] + rolls[rollIndex + 2];
    rollIndex += 1;
} else if (isSpare(rollIndex)) {
    score += 10 + rolls[rollIndex + 2];
    rollIndex += 2;
} else {
    score += rolls[rollIndex] + rolls[rollIndex + 1];
    rollIndex += 2;
}

private boolean isStrike(int rollIndex) {
    return rolls[rollIndex] == 10;
}
```

결과: 전체 테스트 `BUILD SUCCESSFUL`

---

### Cycle 5 — 퍼펙트 게임 (Perfect Game)

**목표:** 12번 연속 스트라이크 → 총점 300

#### RED

```java
@Test
@DisplayName("퍼펙트 게임: 12번 연속 스트라이크면 총점은 300이다")
void perfectGameScoresThreeHundred() {
    rollMany(12, 10);
    assertEquals(300, game.score());
}
```

#### GREEN

Cycle 4의 스트라이크 로직으로 즉시 통과. 10프레임 루프 + `rolls[21]` 배열 크기가 12연속 스트라이크를 올바르게 처리.

결과: 전체 테스트 `BUILD SUCCESSFUL`

---

## 최종 코드

### `Game.java`

```java
package org.example;

public class Game {

    private final int[] rolls = new int[21];
    private int currentRoll = 0;

    public void roll(int pins) {
        rolls[currentRoll++] = pins;
    }

    public int score() {
        int score = 0;
        int rollIndex = 0;
        for (int frame = 0; frame < 10; frame++) {
            if (isStrike(rollIndex)) {
                score += 10 + rolls[rollIndex + 1] + rolls[rollIndex + 2];
                rollIndex += 1;
            } else if (isSpare(rollIndex)) {
                score += 10 + rolls[rollIndex + 2];
                rollIndex += 2;
            } else {
                score += rolls[rollIndex] + rolls[rollIndex + 1];
                rollIndex += 2;
            }
        }
        return score;
    }

    private boolean isStrike(int rollIndex) {
        return rolls[rollIndex] == 10;
    }

    private boolean isSpare(int rollIndex) {
        return rolls[rollIndex] + rolls[rollIndex + 1] == 10;
    }
}
```

### `GameTest.java`

```java
package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    @DisplayName("거터 게임: 모든 투구가 0이면 총점은 0이다")
    void gutterGameScoresZero() {
        rollMany(20, 0);
        assertEquals(0, game.score());
    }

    @Test
    @DisplayName("모든 프레임에서 1개씩 쓰러뜨리면 총점은 20이다")
    void allOnesScoresTwenty() {
        rollMany(20, 1);
        assertEquals(20, game.score());
    }

    @Test
    @DisplayName("스페어 후 다음 투구가 보너스로 추가된다")
    void oneSpareScoresSixteen() {
        game.roll(5);
        game.roll(5);
        game.roll(3);
        rollMany(17, 0);
        assertEquals(16, game.score());
    }

    @Test
    @DisplayName("스트라이크 후 다음 두 투구가 보너스로 추가된다")
    void oneStrikeScoresTwentyFour() {
        game.roll(10);
        game.roll(3);
        game.roll(4);
        rollMany(16, 0);
        assertEquals(24, game.score());
    }

    @Test
    @DisplayName("퍼펙트 게임: 12번 연속 스트라이크면 총점은 300이다")
    void perfectGameScoresThreeHundred() {
        rollMany(12, 10);
        assertEquals(300, game.score());
    }

    private void rollMany(int times, int pins) {
        for (int i = 0; i < times; i++) {
            game.roll(pins);
        }
    }
}
```

---

## 사이클 요약

| 사이클 | 테스트 | RED 원인 | GREEN 핵심 변경 |
|--------|--------|----------|-----------------|
| 1 | 거터 게임 → 0점 | `Game` 클래스 없음 | `Game` 최초 생성, 단순 합산 |
| 2 | 올원 게임 → 20점 | — | 기존 로직으로 즉시 통과 |
| 3 | 스페어 → 16점 | 보너스 미적용 (13점) | 프레임 루프 + 스페어 판별 |
| 4 | 스트라이크 → 24점 | 보너스 미적용 | 스트라이크 판별, rollIndex +1 |
| 5 | 퍼펙트 게임 → 300점 | — | 기존 로직으로 즉시 통과 |

**실제 RED → GREEN 전환이 필요했던 사이클: 3건 (Cycle 1, 3, 4)**

---

## 준수한 TDD 원칙

- 모든 프로덕션 코드는 실패하는 테스트 작성 후 작성
- 각 RED 단계에서 실패를 직접 실행으로 확인
- GREEN 단계에서 테스트를 통과할 최소한의 코드만 작성
- 각 사이클 후 전체 테스트(`./gradlew test`) 실행으로 회귀 확인
- mock 미사용 — 실제 `Game` 객체로 동작 검증
