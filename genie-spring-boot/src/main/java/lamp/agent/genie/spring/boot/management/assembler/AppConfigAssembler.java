package lamp.agent.genie.spring.boot.management.assembler;

import lamp.agent.genie.core.AppConfig;
import lamp.agent.genie.core.LampContext;
import lamp.agent.genie.spring.boot.base.assembler.AbstractListAssembler;
import lamp.agent.genie.spring.boot.base.impl.AppConfigImpl;
import lamp.agent.genie.spring.boot.management.model.AppRegisterForm;
import lamp.agent.genie.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AppConfigAssembler extends AbstractListAssembler<AppRegisterForm, AppConfig> {

	@Autowired
	private LampContext lampContext;

	@Override protected AppConfig doAssemble(AppRegisterForm form) {
		AppConfigImpl appConfig = new AppConfigImpl();
		BeanUtils.copyProperties(form, appConfig, AppConfigImpl.class);

		if (StringUtils.isBlank(appConfig.getAppDirectory())) {
			File appDirectory = new File(lampContext.getAppDirectory(), appConfig.getId() + "/app");
			appConfig.setAppDirectory(appDirectory.getAbsolutePath());
		}

		if (StringUtils.isBlank(appConfig.getWorkDirectory())) {
			appConfig.setWorkDirectory("${appDirectory}");
		}

		if (StringUtils.isBlank(appConfig.getPidFile())) {
			appConfig.setPidFile("${workDirectory}/${appId}.pid");
		}

		if (StringUtils.isBlank(appConfig.getLogFile())) {
			appConfig.setLogFile("${appDirectory}/logs/${appId}.log");
		}

		return appConfig;
	}

}
