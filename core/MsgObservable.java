package core;

public interface MsgObservable {

	
	public void add(MsgListener listener);
	public void remove(MsgListener listener);
}

