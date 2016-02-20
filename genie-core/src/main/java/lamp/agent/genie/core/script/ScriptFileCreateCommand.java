package lamp.agent.genie.core.script;

import lamp.agent.genie.core.AppContext;
import lamp.agent.genie.core.LampCoreConstants;
import lamp.agent.genie.core.script.exception.CommandExecuteException;
import lamp.agent.genie.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
@Getter
@Setter
public class ScriptFileCreateCommand extends ScriptFileCommand {

	private String filename;
	private String content;

	private boolean readable = true;
	private boolean writable = true;
	private boolean executable = false;

	private String charset = LampCoreConstants.DEFAULT_CHARSET;

	@Override
	public void execute(CommandExecutionContext context) {
		log.info("File Create (0) : {}", filename);
		try {
			File file = getFile(context.getAppContext(), filename);

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileUtils.writeStringToFile(file, content, charset);
			log.info("File Created : {}", file.getAbsolutePath());

			if (executable) {
				file.setExecutable(executable);
			}
		} catch (IOException e) {
			throw new CommandExecuteException(e, "File Create Failed : " + filename);
		}
	}

}
