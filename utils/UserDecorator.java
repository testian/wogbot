package utils;
import core.User;

public class UserDecorator {
User user;
public UserDecorator(User user)
{
this.user = user;
}

public boolean equals(Object compare)
{
UserDecorator comparedeco = (UserDecorator)compare;
return (user.getHost().equals(comparedeco.getHost()));
}

public String getHost() {
	return user.getHost();
}

public String getNick() {
	return user.getNick();
}

public String getServername() {
	return user.getServername();
}

public String getUsername() {
	return user.getUsername();
}

public String toString() {
	return user.toString();
}

}
