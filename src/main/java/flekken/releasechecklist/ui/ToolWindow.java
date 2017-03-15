package flekken.releasechecklist.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by on 2017.03.11..
 */
public class ToolWindow implements ToolWindowFactory {

    private JButton newRelease;
    private JCheckBox devUpToDate;
    private JCheckBox masterUpToDate;
    private JCheckBox mergeDevToMaster;
    private JCheckBox onMaster;
    private JCheckBox increaseVersion;
    private JCheckBox commitVersion;
    private JCheckBox tagCommit;
    private JCheckBox buildRelease;
    private JCheckBox pushToMaster;
    private JCheckBox mergeToDevelop;
    private JCheckBox pushToDevelop;

    /**
     * This method is used to initialize the ui.
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull com.intellij.openapi.wm.ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(initLayout(), "", true);
        toolWindow.getContentManager().addContent(content);
    }

    private JScrollPane initLayout() {
        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panel);

        panel.setLayout(new GridBagLayout());

        //default gridBagConstraints
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        newRelease = new JButton("New release");
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(newRelease, constraints);

        devUpToDate = createCheckBox(panel, constraints, "Develop branch up-to-date", 1);
        masterUpToDate = createCheckBox(panel, constraints, "Master branch up-to-date", 2);
        mergeDevToMaster = createCheckBox(panel, constraints, "Merge develop to master", 3);
        onMaster = createCheckBox(panel, constraints, "On master", 4);
        increaseVersion = createCheckBox(panel, constraints, "Set version name and code", 5);
        commitVersion = createCheckBox(panel, constraints, "Commit new version", 6);
        tagCommit = createCheckBox(panel, constraints, "Build release", 7);
        buildRelease = createCheckBox(panel, constraints, "Push changes to master", 8);
        pushToMaster = createCheckBox(panel, constraints, "Push changes to master", 9);
        mergeToDevelop = createCheckBox(panel, constraints, "Merge master to develop", 10);
        pushToDevelop = createCheckBox(panel, constraints, "Push changes to develop", 11);

        //Push components to the top left corner
        Component horizontalSpace = Box.createHorizontalGlue();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 12;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 0;
        panel.add(horizontalSpace, constraints);

        Component verticalSpace = Box.createVerticalGlue();
        constraints.gridx = 0;
        constraints.gridy = 12;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.weightx = 0;
        constraints.weighty = 1;
        panel.add(verticalSpace, constraints);

        return scrollPane;
    }

    private JCheckBox createCheckBox(JPanel panel, GridBagConstraints constraints, String label, int row) {
        JCheckBox checkbox = new JCheckBox(label);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = row;
        panel.add(checkbox, constraints);
        return checkbox;
    }

    @Override
    public void init(com.intellij.openapi.wm.ToolWindow window) {

    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }
}
