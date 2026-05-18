package de.hsbi.lockgame.logic;


import de.hsbi.lockgame.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private Level createEmptyLevel() {
        CellType[][] cells = new CellType[3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        return new Level(
            3,
            3,
            cells,
            List.of(),
            new Position(1, 1)
        );
    }

    @Test
    void initialState_shouldBeRunning() {
        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(),
            GameState.Status.RUNNING,
            Direction.NONE
        );

        assertEquals(GameState.Status.RUNNING, state.status());
        assertEquals(Direction.NONE, state.pendingDirection());
    }

    @Test
    void whenMovingRight_thenSnakeMoves() {
        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertNotEquals(state.snake().head(), next.snake().head());
    }

    @Test
    void whenHittingWall_thenLose() {
        CellType[][] cells = new CellType[3][3];

        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                cells[x][y] = CellType.EMPTY;

        cells[2][1] = CellType.WALL;

        Level level = new Level(3, 3, cells, List.of(), new Position(1, 1));

        GameState state = new GameState(
            level,
            new Snake(List.of(new Position(1, 1))),
            List.of(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertTrue(
            next.status() == GameState.Status.LOST_SELF_COLLISION
                || next.status() == GameState.Status.LOST_OUT_OF_BOUNDS
        );
    }

    @Test
    void whenSnakeRunsIntoItself_thenLose() {
        Snake snake = new Snake(List.of(
            new Position(2, 2),
            new Position(1, 2),
            new Position(1, 1)
        ));

        GameState state = new GameState(
            createEmptyLevel(),
            snake,
            List.of(),
            GameState.Status.RUNNING,
            Direction.LEFT
        );

        GameState next = state.tick();

        assertTrue(
            next.status() == GameState.Status.LOST_SELF_COLLISION
                || next.status() == GameState.Status.LOST_OUT_OF_BOUNDS
        );
    }

    @Test
    void whenPinWrongDirection_thenBlocked() {
        Pin pin = new Pin(new Position(2, 1), Pin.State.LOW, Direction.UP);

        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(state.snake().head(), next.snake().head());
    }

    @Test
    void whenPinCorrectDirection_thenActivated() {
        Pin pin = new Pin(new Position(2, 1), Pin.State.LOW, Direction.RIGHT);

        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertTrue(
            next.pins().stream().anyMatch(p -> p.state().isSet())
        );
    }

    @Test
    void whenAllPinsSet_thenWin() {
        Pin pin = new Pin(new Position(2, 1), Pin.State.HIGH, Direction.RIGHT);

        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.WON, next.status());
    }

    @Test
    void whenNoDirection_thenNothingHappens() {
        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(),
            GameState.Status.RUNNING,
            Direction.NONE
        );

        GameState next = state.tick();

        assertEquals(state.snake().head(), next.snake().head());
    }

    @Test
    void whenGameAlreadyLost_thenNothingChanges() {

        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(),
            GameState.Status.LOST_OUT_OF_BOUNDS,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, next.status());
    }

    @Test
    void whenMovingUp_thenSnakeChangesPosition() {

        GameState state = new GameState(
            createEmptyLevel(),
            new Snake(List.of(new Position(1, 1))),
            List.of(),
            GameState.Status.RUNNING,
            Direction.UP
        );

        GameState next = state.tick();

        assertNotEquals(state.snake().head(), next.snake().head());
    }
}

