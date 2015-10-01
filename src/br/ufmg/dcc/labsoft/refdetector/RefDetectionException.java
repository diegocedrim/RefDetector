package br.ufmg.dcc.labsoft.refdetector;

public class RefDetectionException extends RuntimeException {

	private static final long serialVersionUID = -366569729077905113L;

	public RefDetectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RefDetectionException(String message) {
		super(message);
	}

	public RefDetectionException(Throwable cause) {
		super(cause);
	}

	
}
