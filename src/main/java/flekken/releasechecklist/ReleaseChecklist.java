package flekken.releasechecklist;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Adam Vaszil on 2017.01.08..
 */
public class ReleaseChecklist extends AbstractProjectComponent {

    private final Logger LOG = Logger.getInstance(ReleaseChecklist.class.getSimpleName());

    private GitRepository repository;
    private final GitRepositoryChangeListener repoListener = createRepoListener();

    public ReleaseChecklist(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return super.getComponentName();
    }

    @Override
    public void projectOpened() {
        myProject.getMessageBus().connect().subscribe(GitRepository.GIT_REPO_CHANGE, repoListener);
    }

    /**
     * repository change listener
     *
     * @return listener for initialization
     */
    private GitRepositoryChangeListener createRepoListener() {
        return new GitRepositoryChangeListener() {
            @Override
            public void repositoryChanged(@NotNull GitRepository repository) {

            }
        };
    }

    @Override
    public void projectClosed() {

    }
}
