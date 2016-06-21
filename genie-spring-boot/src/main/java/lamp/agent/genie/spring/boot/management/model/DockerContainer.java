package lamp.agent.genie.spring.boot.management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class DockerContainer {

	private String type = "docker";

	private String image;
	private String network;

	private boolean forcePullImage;
	private boolean privileged;

	private List<String> portMappings;

	private List<String> volumes;

	private List<String> env;

//	private List<Parameter> parameters = new ArrayList<>();



}
