package lamp.agent.genie.core.install;

import lamp.agent.genie.core.AppContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(staticName = "of")
public class SimpleUninstallContext implements UninstallContext {

	@NonNull
	private AppContext appContext;

}
