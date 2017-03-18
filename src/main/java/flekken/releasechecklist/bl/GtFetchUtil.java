package flekken.releasechecklist.bl;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.update.GitFetcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class GtFetchUtil {

    private GtFetchUtil() {
    }

    /**
     * Fetch for single repository
     * <p>
     * Taken from {@link git4idea.actions.GitFetch}
     *
     * @param repository
     */
    public static void fetch(GitRepository repository) {
        Project project = repository.getProject();
        GitVcs.runInBackground(new Backgroundable(project, "Fetching", true) {
            public void run(@NotNull ProgressIndicator indicator) {
                (new GitFetcher(project, indicator, true)).fetchRootsAndNotify(Collections.singleton(repository), null, true);
            }
        });
    }
}
