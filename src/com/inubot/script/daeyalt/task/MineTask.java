package com.inubot.script.daeyalt.task;

import com.inubot.script.daeyalt.Constant;
import com.inubot.script.daeyalt.Rock;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.inventory.Backpack;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Players;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;

@TaskDescriptor(name = "Mining")
public class MineTask extends Task {

  private int tick = 0;

  @Override
  public boolean execute() {
    if (tick++ >= 2) {
      tick = 0;
    }

    Player self = Players.self();
    if (self == null || self.getPosition().getRegionId() != Constant.REGION) {
      Log.warn("Start in mine!");
      return reset();
    }

    if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 5) {
      Movement.toggleRun(true);
    }

    SceneObject obj = Rock.getActive();
    if (obj == null) {
      Log.info("Resetting because obj null");
      return reset();
    }

    Rock rock = Rock.getBest(obj, self);
    if (rock == null) {
      Log.info("Resetting because rock null");
      return reset();
    }

    Backpack inv = Inventories.backpack();
    Item knife = inv.query().names("Knife").results().first();
    Item logs = inv.query().names("Teak logs", "Mahogany logs").results().first();
    if (knife == null || logs == null) {
      Log.warn("No tick manipulation items");
      return reset();
    }

    if (self.distance(rock.getM1()) > 3) {
      Movement.walkTo(rock.getM1());
      return reset();
    }

    Position target = self.distance(rock.getM1()) > 0 ? rock.getM1() : rock.getM2();

    if (tick == 1) {
      inv.query().nameContains("Uncut").results().forEach(x -> x.interact("Drop"));

      inv.use(knife, logs);

      if (self.distance(target) > 0) {
        Movement.walkTowards(target);
      }

      return true;
    }

    if (tick == 2) {
      obj.interact("Mine");
      return true;
    }

    return true;
  }

  private boolean reset() {
    tick = 0;
    return false;
  }
}
