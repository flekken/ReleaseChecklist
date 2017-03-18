package flekken.releasechecklist.bl;

import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

/**
 * Created by on 2017.03.15..
 */
public class GitManager {

    /**
     * repository change listener
     *
     * @return listener for initialization
     */
    public static GitRepositoryChangeListener createRepoListener() {
        return new GitRepositoryChangeListener() {
            @Override
            public void repositoryChanged(@NotNull GitRepository repository) {
                System.out.println("Repo changed");
            }
        };
    }

}
