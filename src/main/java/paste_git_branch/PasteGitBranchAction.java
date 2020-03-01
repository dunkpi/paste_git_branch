package paste_git_branch;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.Refreshable;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by glebk on 2/29/20
 */
public class PasteGitBranchAction extends AnAction {

	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		final CommitMessageI checkinPanel = getCheckinPanel(event);
		if (checkinPanel == null) {
			return;
		}
		Project project = event.getProject();
		checkinPanel.setCommitMessage(getCurrentBranchName(project) + " " + getCurrentCommitMessage(event));
	}

	private static String getCurrentCommitMessage(@Nullable AnActionEvent event) {
		if (event == null) {
			return "";
		}
		Refreshable panel = Refreshable.PANEL_KEY.getData(event.getDataContext());
		if (!(panel instanceof CheckinProjectPanel)) {
			return "";
		}
		CheckinProjectPanel chPan = (CheckinProjectPanel) panel;
		return chPan.getCommitMessage();
	}

	@Nullable
	private static CommitMessageI getCheckinPanel(@Nullable AnActionEvent event) {
		if (event == null) {
			return null;
		}
		Refreshable data = Refreshable.PANEL_KEY.getData(event.getDataContext());
		if (data instanceof CommitMessageI) {
			return (CommitMessageI) data;
		}
		return VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(event.getDataContext());
	}

	private static String getCurrentBranchName(Project project) {
		ProjectLevelVcsManager instance = ProjectLevelVcsManager.getInstance(project);
		if (!instance.checkVcsIsActive("Git")) {
			return "";
		}
		GitRepository repo = GitBranchUtil.getCurrentRepository(project);
		if (repo == null) {
			return "";
		}
		GitLocalBranch currentBranch = repo.getCurrentBranch();
		if (currentBranch == null) {
			return "";
		}
		return currentBranch.getName().trim();
	}
}
