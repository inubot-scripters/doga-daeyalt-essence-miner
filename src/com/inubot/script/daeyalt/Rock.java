package com.inubot.script.daeyalt;

import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.SceneObjects;

public enum Rock {

  NORTH(
      new Position(3674, 9765, 2),
      new Position(3674, 9764, 2),
      new Position(3675, 9764, 2)
  ),

  EAST(
      new Position(3687, 9755, 2),
      new Position(3686, 9757, 2),
      new Position(3686, 9756, 2)
  ),

  SOUTH_1(
      new Position(3671, 9750, 2),
      new Position(3671, 9753, 2),
      new Position(3672, 9753, 2)
  ),

  SOUTH_2(
      new Position(3671, 9750, 2),
      new Position(3674, 9750, 2),
      new Position(3674, 9751, 2)
  );


  private final Position position;
  private final Position m1;
  private final Position m2;

  Rock(Position position, Position m1, Position m2) {
    this.position = position;
    this.m1 = m1;
    this.m2 = m2;
  }

  public Position getM1() {
    return m1;
  }

  public Position getM2() {
    return m2;
  }

  public static SceneObject getActive() {
    return SceneObjects.query()
        .ids(Constant.ACTIVE_DAEYALT_ROCK)
        .results()
        .nearest();
  }

  public static Rock getBest(SceneObject object, SceneNode relative) {
    Rock best = null;
    for (Rock rock : Rock.values()) {
      if (!rock.position.equals(object.getPosition())) {
        continue;
      }

      if (best == null) {
        best = rock;
        continue;
      }

      if (relative.distance(best.m1) > relative.distance(rock.m1)) {
        best = rock;
      }
    }

    return best;
  }
}
