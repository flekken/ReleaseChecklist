package flekken.releasechecklist.ui.toolwindow;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import flekken.releasechecklist.bl.aheadbehind.AheadBehindCountFetcher;
import git4idea.GitLocalBranch;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import git4idea.update.GitFetchResult;
import git4idea.update.GitFetcher;
import org.jetbrains.annotations.NotNull;

/**
 * Created by on 2017.03.18..
 */
public class ToolWindowPresenter implements Contract.Presenter {

    private Project project;
    private AheadBehindCountFetcher aheadBehindCountFetcher;
    private GitRepository repository;
    private Contract.View view;

    @Override
    public void attach(Project project, Contract.View view) {
        this.view = view;
        this.project = project;
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        this.repository = repositoryManager.getRepositories().get(0);
        this.aheadBehindCountFetcher = new AheadBehindCountFetcher(project, repository);
    }

    @Override
    public void onReleasePressed() {
        GitVcs.runInBackground(new Task.Backgroundable(project, "Fetching", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitFetcher fetcher = new GitFetcher(project, indicator, true);
                GitFetchResult result = fetcher.fetch(repository);
                if (result.isSuccess()) {
                    checkDevelopUpToDate();
                    checkMasterUpToDate();
                    checkOnMaster();
                    checkMergedDevelopToMaster();
                }
            }
        });
    }

    private void checkMergedDevelopToMaster() {

    }

    private void checkOnMaster() {
        GitLocalBranch currentBranch = repository.getCurrentBranch();
        if (currentBranch != null && currentBranch.getName().equals("master")) {
            view.check(Contract.View.POSITION_ON_MASTER);
        }
    }

    private void checkDevelopUpToDate() {
        GitLocalBranch developBranch = repository.getBranches().findLocalBranch("develop");
        checkBranchUpToDate(developBranch, Contract.View.POSITION_DEV_UP_TO_DATE);
    }

    private void checkMasterUpToDate() {
        GitLocalBranch masterBranch = repository.getBranches().findLocalBranch("master");
        checkBranchUpToDate(masterBranch, Contract.View.POSITION_MASTER_UP_TO_DATE);
    }

    private void checkBranchUpToDate(GitLocalBranch branch, int viewPositionToUpdate) {
        if (branch != null) {
            aheadBehindCountFetcher.get(branch, aheadBehindCount -> {
                if (aheadBehindCount.ahead == 0 && aheadBehindCount.behind == 0) {
                    view.check(viewPositionToUpdate);
                }
            });
        }
    }
}
