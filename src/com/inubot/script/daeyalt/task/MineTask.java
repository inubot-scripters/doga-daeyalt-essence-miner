package com.inubot.script.daeyalt.task;

import com.inubot.script.daeyalt.Constant;
import com.inubot.script.daeyalt.Rock;
import org.rspeer.game.adapter.component.inventory.Backpack;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder;
import org.rspeer.game.config.item.loadout.BackpackLoadout;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;
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
    if (self == null) {
      return reset();
    }

    if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 5) {
      Movement.toggleRun(true);
    }

    Backpack inv = Inventories.backpack();
    Item knife = inv.query().names("Knife").results().first();
    Item logs = inv.query().names("Teak logs", "Mahogany logs").results().first();
    if (knife == null || logs == null) {
      bank(self, knife == null, logs == null);
      return reset();
    }

    if (self.getPosition().getRegionId() != Constant.REGION) {
      inv.query().nameContains("Vyre noble").results().forEach(x -> x.interact("Wear"));
      traverseMines();
      return reset();
    }

    SceneObject obj = Rock.getActive();
    if (obj == null) {
      return reset();
    }

    Rock rock = Rock.getBest(obj, self);
    if (rock == null) {
      return reset();
    }

    if (self.distance(rock.getM1()) > 3) {
      Movement.walkTo(rock.getM1());
      return reset();
    }

    Position target = self.distance(rock.getM1()) > 0 ? rock.getM1() : rock.getM2();

    if (tick == 1) {
      inv.query().nameContains("Uncut", " stock").results().forEach(x -> x.interact("Drop"));
      inv.query().nameContains("Prospector").results().forEach(x -> x.interact("Wear"));

      if (self.distance(target) > 0) {
        inv.use(knife, logs);
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

  private void bank(Player self, boolean logs, boolean knife) {
    if (self.getPosition().getRegionId() == Constant.REGION) {
      SceneObjects.query()
          .names("Staircase")
          .actions("Climb-up")
          .results()
          .limit(1)
          .forEach(x -> x.interact("Climb-up"));
      return;
    }

    if (!Bank.isOpen()) {
      Bank.open();
      return;
    }

    Bank bank = Inventories.bank();
    BackpackLoadout loadout = BackpackLoadout.bagged("Inventory");

    if (logs) {
      if (bank.contains(iq -> iq.names("Teak logs").results())) {
        loadout.add(new ItemEntryBuilder()
            .key("Teak logs")
            .quantity(1)
            .build());
      } else {
        loadout.add(new ItemEntryBuilder()
            .key("Mahogany logs")
            .quantity(1)
            .build());
      }
    }

    if (knife) {
      loadout.add(new ItemEntryBuilder()
          .key("Knife")
          .quantity(1)
          .build());
    }

    loadout.withdraw(bank);
  }

  private void traverseMines() {
    SceneObject tunnel = SceneObjects.query()
        .names("Staircase")
        .actions("Climb-down")
        .within(Constant.OUT_MINES_POSITION, 12)
        .results()
        .nearest();
    if (tunnel != null) {
      tunnel.interact("Climb-down");
    } else {
      Movement.walkTo(Constant.OUT_MINES_POSITION);
    }
  }

  private boolean reset() {
    tick = 0;
    return false;
  }
}
