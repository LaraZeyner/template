package net.mmm.mc.template.util.reflections;

public class ReflectionsException extends RuntimeException {
	private static final long serialVersionUID = 4658656590818859731L;

	public ReflectionsException(String message) {
		super(message);
	}

	public ReflectionsException(String message, Throwable cause) {
		super(message, cause);
	}
}
