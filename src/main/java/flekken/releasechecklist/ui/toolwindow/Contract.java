package flekken.releasechecklist.ui.toolwindow;

import com.intellij.openapi.project.Project;

/**
 * Created by on 2017.03.18..
 */
public class Contract {

    public interface Presenter {

        void attach(Project project, View view);

        void onReleasePressed();
    }

    public interface View {

        int POSITION_DEV_UP_TO_DATE = 0;
        int POSITION_MASTER_UP_TO_DATE = 1;
        int POSITION_ON_MASTER = 2;
        int POSITION_MERGED_DEV_TO_MASTER = 3;
        int POSITION_INCREASE_VERSION = 4;
        int POSITION_COMMIT_VERSION = 5;
        int POSITION_TAG_COMMIT = 6;
        int POSITION_BUILD_RELEASE = 7;
        int POSITION_PUSH_MASTER = 8;
        int POSITION_MERGE_MASTER_TO_DEV = 9;
        int POSITION_PUSH_TO_DEV = 10;

        void check(int viewPositionToUpdate);
    }

}
