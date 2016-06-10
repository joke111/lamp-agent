package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;

public enum  AppResourceType {
	ARTIFACT(Values.ARTIFACT), URL(Values.URL);

	@Getter
	private String value;

	AppResourceType(String value) {
		this.value = value;
	}

	public static final class Values {
		public static final String ARTIFACT = "ARTIFACT";
		public static final String URL = "URL";

		private Values() {
		}
	}
}
