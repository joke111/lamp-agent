package lamp.agent.genie.core.install;

import lamp.agent.genie.core.SimpleAppInstanceContext;
import lamp.agent.genie.core.script.ScriptCommand;
import lamp.agent.genie.utils.ExpressionParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface InstallContext {

	SimpleAppInstanceContext getAppInstanceContext();

	ExpressionParser getExpressionParser();

	void transferTo(File dest) throws IOException;

	List<ScriptCommand> getCommands();

	File getInstallLogFile();

}
