package flekken.releasechecklist.bl.aheadbehind;

import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitTask;
import git4idea.commands.GitTaskResultHandlerAdapter;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by on 2017.02.11..
 */
public class AheadBehindCountFetcher {

    private final Project myProject;
    private AheadBehindCount aheadBehindCount;
    private GitRepository repository;

    public AheadBehindCountFetcher(Project myProject, @Nullable GitRepository repository) {
        this.aheadBehindCount = new AheadBehindCount(0, 0);
        this.myProject = myProject;
        this.repository = repository;
    }

    public void start(@Nullable GitLocalBranch localBranch, @Nullable AheadBehindCounterListener listener) {
        if (localBranch == null) {
            localBranch = repository.getCurrentBranch();
            if (localBranch == null) {
                return;
            }
        }
        get(localBranch, listener);
    }

    public void get(@NotNull GitLocalBranch localBranch, @Nullable AheadBehindCounterListener listener) {
        assert repository != null;
        final GitLineHandler handler = new GitLineHandler(myProject, repository.getRoot(), GitCommand.REV_LIST);

        String localRef = getLocalBranchHash(repository, localBranch);
        String remoteRef = getRemoteBranchHash(repository, localBranch);
        String branches = localRef + "..." + remoteRef;

        handler.addParameters(branches, "--left-right");

        final GitRevListLeftRightCounter counter = new GitRevListLeftRightCounter();
        handler.addLineListener(counter);

        GitTask task = new GitTask(myProject, handler, branches);
        task.execute(true, false, new GitTaskResultHandlerAdapter() {
            @Override
            protected void onSuccess() {
                aheadBehindCount = new AheadBehindCount(counter.ahead(), counter.behind());
                if (listener != null) {
                    listener.onCompleted(aheadBehindCount);
                }
            }

            @Override
            protected void onCancel() {
            }

            @Override
            protected void onFailure() {

            }
        });
    }

    private String getLocalBranchHash(GitRepository repository, GitLocalBranch localBranch) {
        return repository
                .getInfo()
                .getLocalBranchesWithHashes()
                .get(localBranch)
                .asString();
    }

    private String getRemoteBranchHash(GitRepository repository, GitLocalBranch localBranch) {
        return repository
                .getInfo()
                .getRemoteBranchesWithHashes()
                .get(localBranch.findTrackedBranch(repository))
                .asString();
    }

    public void setRepository(GitRepository repository) {
        this.repository = repository;
    }

    public interface AheadBehindCounterListener {
        void onCompleted(AheadBehindCount aheadBehindCount);
    }

}
