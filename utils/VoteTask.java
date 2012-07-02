package utils;


import java.util.TimerTask;
import java.util.Iterator;


import core.Session;
import core.SessionTask;

public class VoteTask extends TimerTask implements SessionTask {
	String channel;

	Vote vote;

	Session bot;

	private void register() {
		bot.add(this);
	}

	private void deregister() {
		bot.remove(this);
	}

	public VoteTask(Session bot, String channel, Vote vote) {
		this.bot = bot;
		this.channel = channel;
		this.vote = vote;
		register();
	}

	public void run() {
		deregister();
		synchronized (vote) {
			vote.finishVote();
			bot.msg(channel, "Ergebnis der Abstimmung - " + vote.getId()
					+ ": '" + vote.getQuestion() + "'");
			int total = vote.voiceCount();
			bot.msg(channel, "Anzahl Stimmen: " + total);
			for (Iterator<String> i = vote.optionsIterator(); i.hasNext();) {
				String option = i.next();
				int optionCount = vote.voiceCount(option);
				bot.msg(channel, option + " - Stimmen: " + optionCount
						+ " Anteil: " + (optionCount * 100) / total + "%");
			}
			// }
		}
	}

	public boolean cancel() {

		vote.finishVote();
		return super.cancel();

	}

	public String getDescription() {
		return "vote[" + vote.getId() + "]";
	}

	public void kill() {
		deregister();
		cancel();
	}

	public void exit() {
		deregister();
		if (super.cancel()) {
			run();
		}
	}
}