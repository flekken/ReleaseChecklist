package flekken.releasechecklist.ui.toolwindow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import flekken.releasechecklist.bl.aheadbehind.AheadBehindCountFetcher;
import git4idea.GitLocalBranch;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitLineHandlerListener;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import git4idea.repo.GitRepositoryManager;
import git4idea.update.GitFetchResult;
import git4idea.update.GitFetcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by on 2017.03.18..
 */
public class ToolWindowPresenter implements Contract.Presenter {

    private static final String BRANCH_NAME_MASTER = "master";
    private static final String BRANCH_NAME_DEVELOP = "develop";

    private Project project;
    private AheadBehindCountFetcher aheadBehindCountFetcher;
    private GitRepository repository;
    private Contract.View view;

    private final GitRepositoryChangeListener changeListener = getRepoChangeListener();

    @Override
    public void attach(Project project, Contract.View view) {
        this.view = view;
        this.project = project;

        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        this.repository = repositoryManager.getRepositories().get(0);
        this.aheadBehindCountFetcher = new AheadBehindCountFetcher(project, repository);

        this.project.getMessageBus().connect().subscribe(GitRepository.GIT_REPO_CHANGE, changeListener);
    }

    @Override
    public void onReleasePressed() {
        GitVcs.runInBackground(new Task.Backgroundable(project, "Fetching", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitFetcher fetcher = new GitFetcher(project, indicator, true);
                GitFetchResult result = fetcher.fetch(repository);
                if (result.isSuccess()) {
                    //run them sequentially
                    //aheadBehind counter lock the thread so they are sequential
                    checkDevelopUpToDate(Contract.View.POSITION_DEV_UP_TO_DATE);
                    if (view.isChecked(Contract.View.POSITION_DEV_UP_TO_DATE)) {
                        checkMasterNotBehind(() -> checkOnMaster(() -> checkMergedDevelopToMaster()));
                    }
                }
            }
        });
    }

    private void checkMergedDevelopToMaster() {
        checkMerge(Contract.View.POSITION_MERGED_DEV_TO_MASTER, BRANCH_NAME_MASTER, BRANCH_NAME_DEVELOP);
    }

    private void checkOnMaster(@Nullable Callback callback) {
        GitLocalBranch currentBranch = repository.getCurrentBranch();
        if (currentBranch != null && currentBranch.getName().equals(BRANCH_NAME_MASTER)) {
            view.check(Contract.View.POSITION_ON_MASTER);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }

    /**
     * Develop branch should not be behind and ahead
     */
    private void checkDevelopUpToDate(int viewPosition) {
        GitLocalBranch developBranch = repository.getBranches().findLocalBranch(BRANCH_NAME_DEVELOP);
        if (developBranch != null) {
            aheadBehindCountFetcher.get(developBranch, aheadBehindCount -> {
                if (aheadBehindCount.ahead == 0 && aheadBehindCount.behind == 0) {
                    view.check(viewPosition);
                }
            });
        }
    }

    /**
     * Master branch should not be behind
     */
    private void checkMasterNotBehind(@Nullable Callback callback) {
        GitLocalBranch masterBranch = repository.getBranches().findLocalBranch(BRANCH_NAME_MASTER);
        if (masterBranch != null) {
            aheadBehindCountFetcher.get(masterBranch, aheadBehindCount -> {
                if (aheadBehindCount.behind == 0) {
                    view.check(Contract.View.POSITION_MASTER_UP_TO_DATE);
                    if (callback != null) {
                        callback.onComplete();
                    }
                }
            });
        }
    }

    private void checkMasterNotAheadNotBehind() {
        GitLocalBranch masterBranch = repository.getBranches().findLocalBranch(BRANCH_NAME_MASTER);
        if (masterBranch != null) {
            aheadBehindCountFetcher.get(masterBranch, aheadBehindCount -> {
                if (aheadBehindCount.behind == 0 && aheadBehindCount.ahead == 0) {
                    view.check(Contract.View.POSITION_PUSH_MASTER);
                }
            });
        }
    }

    private void checkMergedMasterToDevelop() {
        checkMerge(Contract.View.POSITION_MERGE_MASTER_TO_DEV, BRANCH_NAME_DEVELOP, BRANCH_NAME_MASTER);
    }

    private void checkMerge(int viewPosition, String branchToCheck, String branchToFind) {
        Git git = ServiceManager.getService(Git.class);
        git.runCommand(() -> {
            final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(),
                    GitCommand.BRANCH);
            h.setSilent(false);
            h.setStdoutSuppressed(false);
            h.addLineListener(new GitLineHandlerListener() {
                @Override
                public void onLineAvailable(String s, Key key) {
                    if (s.contains(branchToFind)) {
                        view.check(viewPosition);
                    }
                }

                @Override
                public void processTerminated(int exitCode) {

                }

                @Override
                public void startFailed(Throwable exception) {

                }
            });
            h.addParameters("--merged");
            h.addParameters(branchToCheck);
            return h;
        });
    }

    private GitRepositoryChangeListener getRepoChangeListener() {
        return repository -> {
            if (view.isCheckable(Contract.View.POSITION_DEV_UP_TO_DATE)) {
                checkDevelopUpToDate(Contract.View.POSITION_DEV_UP_TO_DATE);
            }
            if (view.isCheckable(Contract.View.POSITION_MASTER_UP_TO_DATE)) {
                checkMasterNotBehind(null);
            }

            //Check if we are on master until we build the release
            if (!view.isChecked(Contract.View.POSITION_BUILD_RELEASE)) {
                checkOnMaster(null);
            }
            if (view.isCheckable(Contract.View.POSITION_MERGED_DEV_TO_MASTER)) {
                checkMergedDevelopToMaster();
            }
            if (view.isCheckable(Contract.View.POSITION_COMMIT_VERSION)) {
                //TODO: check if version was commited
            }
            if (view.isCheckable(Contract.View.POSITION_TAG_COMMIT)) {
                //TODO: check if commit was tagged
            }
            if (view.isCheckable(Contract.View.POSITION_PUSH_MASTER)) {
                checkMasterNotAheadNotBehind();
            }
            if (view.isCheckable(Contract.View.POSITION_MERGE_MASTER_TO_DEV)) {
                checkMergedMasterToDevelop();
            }
            if (view.isCheckable(Contract.View.POSITION_PUSH_TO_DEV)) {
                checkDevelopUpToDate(Contract.View.POSITION_PUSH_TO_DEV);
            }
        };
    }

    private interface Callback {
        void onComplete();
    }
}
