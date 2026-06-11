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
        game.roll(5); // spare
        game.roll(3);
        rollMany(17, 0);

        assertEquals(16, game.score());
    }

    @Test
    @DisplayName("스트라이크 후 다음 두 투구가 보너스로 추가된다")
    void oneStrikeScoresTwentyFour() {
        game.roll(10); // strike
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
