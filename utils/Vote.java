package utils;

import java.util.*;



import core.User;



public class Vote {

	public static class AlreadyVotedException extends Exception {
		static final long serialVersionUID = 1;
	}

	private static Map<Integer, Vote> runningVotes = new Hashtable<Integer, Vote>();

	public static Vote getVote(int id) {
		return runningVotes.get(id);

	}

	/*
	 * public Map<Integer, Vote> getRunningVotes() { return runningVotes; }
	 */
	String question;

	Map<String, Integer> voices;

	User initiator;

	int id;

	HashSet<UserDecorator> voters;

	synchronized private static int addVote(Vote vote) {
		boolean noid = true;
		int id = 0;
		while (noid) {
			if (runningVotes.containsKey(id)) {
				id++;
			} else {
				noid = false;
			}
		}
		runningVotes.put(id, vote);
		return id;
	}

	public Vote(User initiator, String question) {
		this.initiator = initiator;
		this.question = question;
		voices = new Hashtable<String, Integer>();
		voters = new HashSet<UserDecorator>();
		id = addVote(this);
	}

	/*
	 * public boolean equals(Vote compare) { return
	 * getQuestion().equals(compare.getQuestion()); }
	 */
	public String getQuestion() {
		return question;
	}

	synchronized public void vote(User voter, String option) throws AlreadyVotedException {
		UserDecorator deco = new UserDecorator(voter);

		if (putVoter(deco)) {
			if (voices.containsKey(option)) {
				voices.put(option, voices.get(option).intValue() + 1);
			} else {
				voices.put(option, 1);
			}
		} else {
			throw new AlreadyVotedException();
		}

	}
	private boolean putVoter(UserDecorator deco)
	{
	for (Iterator<UserDecorator> i = voters.iterator(); i.hasNext();)
	{
	
		if (i.next().equals(deco)){return false;}
	}
	voters.add(deco);
	return true;
	}
	public int getId(){return id;}
	public int voiceCount()
	{return voters.size();}
	public int voiceCount(String option) {
		if (voices.containsKey(option)) {
			return voices.get(option);
		}
		return 0;
	}
	public Iterator<String> optionsIterator()
	{
	return voices.keySet().iterator();
	}
	synchronized public void finishVote()
	{
		runningVotes.remove(id);
	}

}
