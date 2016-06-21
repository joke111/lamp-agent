package lamp.agent.genie.core;


import java.io.File;
import java.util.Date;

public interface AppInstance<T extends AppInstanceContext> {

	String getId();

	AppInstanceSpec getSpec();

	T getContext();

	AppInstanceStatus getStatus();

	AppInstanceStatus getCorrectStatus();

	void start();

	void stop();

	boolean isRunning();

	boolean monitored();

	File getStdOutFile();

	File getStdErrFile();

}
