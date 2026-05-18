package de.hsbi.lockgame.logic;



import de.hsbi.lockgame.model.*;
import java.util.List;

public final class GameState {

    private final Level level; //Spielfeld
    private final Snake snake;
    private final List<Pin> pins;
    private final Status status;
    private final Direction pendingDirection; //Richtung die gesetzt wurde

  public GameState(
      Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
    // TODO: lege einen neuen GameState mit den übergebenen Informationen an
      this.level = level;
      this.snake = snake;
      this.pins = pins;
      this.status = status;
      this.pendingDirection = pendingDirection;
  }

  public Level level() {
    // TODO: Getter
      return level;
  }

  public Snake snake() {
    // TODO: Getter
      return snake;
  }

  public List<Pin> pins() {
    // TODO: Getter
      return pins;
  }

  public Status status() {
    // TODO: Getter
      return status;
  }

  public Direction pendingDirection() {
    // TODO: Getter
      return pendingDirection;
  }

    public GameState tick() {

        // Spiel läuft nicht → nichts machen
        if (!status.isRunning()) {
            return this;
        }

        // Keine Richtung gesetzt
        if (pendingDirection == Direction.NONE) {
            return this;
        }

        Position nextHead = snake.nextHead(pendingDirection);

        // (a) Out of bounds
        if (!level.isInside(nextHead)) {
            return new GameState(
                level,
                snake,
                pins,
                Status.LOST_OUT_OF_BOUNDS,
                Direction.NONE
            );
        }

        // (b) Wand
        if (level.cellAt(nextHead) == CellType.WALL) {
            return new GameState(
                level,
                snake,
                pins,
                status,
                Direction.NONE
            );
        }

        // (c) Selbstkollision
        if (snake.occupies(nextHead)) {
            return new GameState(
                level,
                snake,
                pins,
                Status.LOST_SELF_COLLISION,
                Direction.NONE
            );
        }

        // (d) Pin prüfen
        for (Pin pin : pins) {

            if (pin.position().equals(nextHead)) {

                // Pin schon gesetzt
                if (pin.state().isSet()) {
                    return new GameState(
                        level,
                        snake,
                        pins,
                        status,
                        Direction.NONE
                    );
                }

                // falsche Richtung
                if (pin.activationDirection() != pendingDirection) {
                    return new GameState(
                        level,
                        snake,
                        pins,
                        status,
                        Direction.NONE
                    );
                }

                // Pin aktivieren
                List<Pin> newPins = pins.stream()
                    .map(p -> p == pin
                        ? p.withState(Pin.State.HIGH)
                        : p)
                    .toList();

                boolean allSet = newPins.stream()
                    .allMatch(p -> p.state().isSet());

                return new GameState(
                    level,
                    snake,
                    newPins,
                    allSet ? Status.WON : status,
                    Direction.NONE
                );
            }
        }

        // Normale Bewegung
        Snake newSnake = snake.grow(pendingDirection);

        return new GameState(
            level,
            newSnake,
            pins,
            status,
            pendingDirection
        );
    }

  public enum Status {
    RUNNING,
    WON,
    LOST_SELF_COLLISION,
    LOST_OUT_OF_BOUNDS;

    public boolean isRunning() {
      return this == RUNNING;
    }
  }
}
