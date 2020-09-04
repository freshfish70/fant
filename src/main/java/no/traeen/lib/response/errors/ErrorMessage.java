package no.traeen.lib.response.errors;

public class ErrorMessage {

	private String message;

	public ErrorMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
