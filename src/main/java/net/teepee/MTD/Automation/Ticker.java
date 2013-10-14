package main.java.net.teepee.MTD.Automation;

import main.java.net.teepee.MTD.MTD;
import main.java.net.teepee.MTD.Automation.Game.Gamestate;
import main.java.net.teepee.MTD.Commands.Msg;

public class Ticker implements Runnable {

	@Override
	public void run() {
		SignUpdater.update();
		for (Game g : MTD.instance.getMaps())
			if (g.getCountdownTime() >= 0) {
				if ((g.getCountdownTime() <= 10 || g.getCountdownTime() % 30 == 0) && g.getState() == Gamestate.WAITING)
					g.broadcast(Msg.format("game_begins_in").replaceAll("<time>", g.getCountdownTime() + ""));
				else if ((g.getCountdownTime() <= 10 || g.getCountdownTime() % 30 == 0) && g.getState() == Gamestate.BUILDING)
					g.broadcast(Msg.format("building_ends_in").replaceAll("<time>", g.getCountdownTime() + ""));
				else if ((g.getCountdownTime() <= 10 || g.getCountdownTime() % 30 == 0 && g.getCountdownTime() <= 120)
						&& g.getState() == Gamestate.STARTED) {
					g.broadcast(Msg.format("game_ends_in").replaceAll("<time>", g.getCountdownTime() + ""));
					g.stop(false);
				}
				g.setCountdownTime(g.getCountdownTime() - 1);
				g.updateTime();
				if (g.getCountdownTime() == -1)
					if (g.getState() == Gamestate.WAITING)
						g.postCountdownStart();
					else if (g.getState() == Gamestate.BUILDING) {
						g.broadcast(Msg.format("mobs_released"));
						g.postBuildingStart();
					}
			}
	}

	public static void beginCountdown(Game game) {
		if (game.getState() == Gamestate.WAITING)
			game.setCountdownTime(MTD.instance.getConfig().getInt("wait_time"));
		else if (game.getState() == Gamestate.BUILDING)
			game.setCountdownTime(MTD.instance.getConfig().getInt("build_time"));
		else if (game.getState() == Gamestate.STARTED)
			game.setCountdownTime(MTD.instance.getConfig().getInt("game_time"));
	}
}
