package core;

public interface ReplyObservable {

	public void add(ReplyListener listener);
	public void remove(ReplyListener listener);
}
