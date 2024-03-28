package com.inubot.script.daeyalt;

import com.inubot.script.daeyalt.task.MineTask;
import org.rspeer.commons.ArrayUtils;
import org.rspeer.commons.StopWatch;
import org.rspeer.event.Subscribe;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.event.TickEvent;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskScript;
import org.rspeer.game.script.meta.ScriptMeta;
import org.rspeer.game.script.meta.paint.PaintBinding;
import org.rspeer.game.script.meta.paint.PaintScheme;

import java.util.function.IntSupplier;

//TODO gem bag + emptying
@ScriptMeta(
    name = "Daeyalt Essence Miner",
    paint = PaintScheme.class,
    version = 1.12,
    regions = -3
)
public class Daeyalt extends TaskScript {

  @PaintBinding("Runtime")
  private final StopWatch runtime = StopWatch.start();

  @PaintBinding("Mining")
  private final Skill skill = Skill.MINING;

  private int start = -1;

  @PaintBinding(value = "Shards", rate = true)
  private final IntSupplier shards = () -> {
    if (start == -1) {
      return 0;
    }

    return getShardCount() - start;
  };

  @Subscribe
  public void tick(TickEvent event) {
    //in case user didn't start logged in
    if (start == -1) {
      start = getShardCount();
    }
  }

  @Override
  public Class<? extends Task>[] tasks() {
    return ArrayUtils.getTypeSafeArray(
        MineTask.class
    );
  }

  private int getShardCount() {
    return Inventories.backpack().getCount(iq -> iq.names("Daeyalt shard").results(), true);
  }
}
