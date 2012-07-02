package core;
public class PermissionDeniedException extends SessionException {
static final long serialVersionUID=1;

public PermissionDeniedException() {
	super("Admin-Rechte benötigt");
	// TODO Auto-generated constructor stub
}
public PermissionDeniedException(String message) {
	super(message);
	// TODO Auto-generated constructor stub
}


}
