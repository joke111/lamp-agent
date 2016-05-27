package lamp.agent.genie.spring.boot.management.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.jaxrs.DockerCmdExecFactoryImpl;
import lamp.agent.genie.spring.boot.config.DockerClientProperties;
import lamp.agent.genie.spring.boot.management.model.DockerApp;
import lamp.agent.genie.spring.boot.management.model.DockerContainer;
import lamp.agent.genie.spring.boot.management.model.PortMapping;
import lamp.agent.genie.utils.CollectionUtils;
import lamp.agent.genie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;


@Slf4j
public class DockerClientService {

	private DockerClient dockerClient;

	public DockerClientService(DockerClientProperties dockerClientProperties) {
		log.info("dockerHost : {}", dockerClientProperties.getDockerHost());
		DockerClientConfig.DockerClientConfigBuilder builder = DockerClientConfig.createDefaultConfigBuilder();
		if (StringUtils.isNotEmpty(dockerClientProperties.getDockerHost())) {
			builder.withDockerHost(dockerClientProperties.getDockerHost());
		}
		builder.withDockerTlsVerify(dockerClientProperties.isDockerTlsVerify());
		if (StringUtils.isNotEmpty(dockerClientProperties.getDockerCertPath())) {
			builder.withDockerCertPath(dockerClientProperties.getDockerCertPath());
		}
		if (StringUtils.isNotEmpty(dockerClientProperties.getDockerConfig())) {
			builder.withDockerConfig(dockerClientProperties.getDockerConfig());
		}
		builder.withApiVersion(dockerClientProperties.getApiVersion());

		DockerClientConfig config = builder.build();

		DockerCmdExecFactory dockerCmdExecFactory = new DockerCmdExecFactoryImpl()
			.withReadTimeout(1000)
			.withConnectTimeout(1000)
			.withMaxTotalConnections(100)
			.withMaxPerRouteConnections(10);

		this.dockerClient = DockerClientBuilder.getInstance(config)
			.withDockerCmdExecFactory(dockerCmdExecFactory)
			.build();
	}

	@PreDestroy
	public void close() throws IOException {
		dockerClient.close();
	}

	public List<Container> listContainers(boolean showAll) {
		return dockerClient.listContainersCmd().withShowAll(showAll).exec();
	}

	public InspectContainerResponse getContainer(String containerId) {
		return dockerClient.inspectContainerCmd(containerId).exec();
	}

	public InspectContainerResponse runContainer(DockerApp dockerApp) {
//		String image, String
//	} tag, String containerName, String[] containerCmd) {

		DockerContainer container = dockerApp.getContainer();
		// 1. Pull Image
		PullImageCmd pullImageCmd = dockerClient.pullImageCmd(container.getImage());
		pullImageCmd.exec(new PullImageResultCallback()).awaitSuccess();

		// 2. CreateContainer
		CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(container.getImage());

		if (StringUtils.isNotEmpty(container.getNetwork())) {
			createContainerCmd.withNetworkMode(container.getNetwork());
		}

		if (CollectionUtils.isNotEmpty(container.getPortMappings())) {
			for (PortMapping port : container.getPortMappings()) {
				PortBinding portBinding = PortBinding.parse(port.getContainerPort() + ":" + port.getHostPort());
				createContainerCmd.withExposedPorts(portBinding.getExposedPort());
				createContainerCmd.withPortBindings(portBinding);
			}
		}
		CreateContainerResponse containerResponse = createContainerCmd.exec();
		String containerId  = containerResponse.getId();

		// 3. StartContainer
		dockerClient.startContainerCmd(containerId).exec();

		return dockerClient.inspectContainerCmd(containerId).exec();
	}


	public void stopContainer(String containerId) {
		dockerClient.stopContainerCmd(containerId).exec();
		dockerClient.removeContainerCmd(containerId);
	}

	public Statistics getStats(String containerId) {
		return dockerClient.statsCmd(containerId).exec(new StatsCallback()).awaitSuccess();
	}

	private class StatsCallback extends ResultCallbackTemplate<StatsCallback, Statistics> {

		private Statistics latestStats;

		@Override
		public void onNext(Statistics stats) {
			log.info("Received stats {}", stats);
			if (stats != null) {
				latestStats = stats;
				onComplete();
			}
		}

		public Statistics awaitSuccess() {
			try {
				awaitCompletion();
			} catch (InterruptedException e) {
				throw new DockerClientException("", e);
			}

			if (latestStats == null) {
				new DockerClientException("Could not get stats");
			}
			return latestStats;
		}
	}
}
