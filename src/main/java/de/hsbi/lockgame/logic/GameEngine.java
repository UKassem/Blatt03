package de.hsbi.lockgame.logic;
import de.hsbi.lockgame.logic.GameStateObserver;

import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.ui.GamePanel;
import de.hsbi.lockgame.model.Snake;
import java.util.List;

// TODO: Die GameEngine verwaltet den GameState.


// TODO: Die GameEngine wird durch den Timer im main() getriggert ("tick") und lässt den GameState
// daraufhin einen Schritt ausführen. Dann müssen alle für den GameState registrierten Observer
// benachrichtigt werden, damit das Spielfeld neu gezeichnet werden kann o.ä.

// TODO: Die GameEngine beobachtet die Tastatureingaben (gesetzt in GamePanel.setupKeyBindings()),
// die in Direction übersetzt und an GameEngine.update() übergeben werden. Wenn es eine neue Eingabe
// gibt, wird die "update"-Methode von GameEngine aufgerufen, und die GameEngine muss die
// Blickrichtung der Schlange aktualisieren und diese GameState-Änderung den für den GameState
// registrierten Observer mitteilen.

// TODO: Die GameEngine ist ein Observer für Direction: GameEngine.update(Direction)
// TODO: Die GameEngine ist ein Observable für GameState: GamePanel.update(GameState)
public final class GameEngine {
    //Die GameEngine benötigt 2 Dinge
    //speichert den Spielzustand / Observer speichert die Anzeige
    private GameState state;
    private GameStateObserver observer;

    private void notifyObserver(){
        if (observer != null) {
            observer.update(state);
        }
    }

    //Aufgabe: Spiel starten
  public GameEngine(Level level) {
    // TODO: lege eine neue GameEngine mit den übergebenen Informationen an
    //Ich speichere den neuen Spielzustand in der GameEngine
    this.state = new GameState(
        level,
        new Snake(List.of(level.snakeStart())),
        level.pins(),
        GameState.Status.RUNNING,
        Direction.NONE
    );

  }

  public GameState state() {
    // TODO: gebe den aktuellen Spielzustand zurück
      return state;
  }

  public void setGamePanel(GameStateObserver observer) {
    // TODO: Setter
      //Für Updates, denn ohne panel.update(state) würde es crashen
      this.observer = observer;
  }

  //Wird aufgerufen wenn der SPieler eine Taste drückt
    //Ziel: Die Richtung im GameState ändern
  public void update(Direction d) {
      state = new GameState(
          state.level(),
          state.snake(),
          state.pins(),
          state.status(),
          d
      );

      notifyObserver();
  }

  public void tick() {
    // TODO: lass das Spiel (den GameState) einen Schritt ("tick") machen
      //GameState macht einen Schritt
      state = state.tick();
    // TODO: benachrichtige alle Observer und gibt den neuen Spielzustand mit (Neuzeichnen der
    // Spielfläche)
      notifyObserver();
}
}
