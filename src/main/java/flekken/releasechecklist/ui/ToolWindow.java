package flekken.releasechecklist.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 2017.03.11..
 */
public class ToolWindow implements ToolWindowFactory {

    private static final int POSITION_DEV_UP_TO_DATE = 0;
    private static final int POSITION_MASTER_UP_TO_DATE = 1;
    private static final int POSITION_MERGED_DEV_TO_MASTER = 2;
    private static final int POSITION_ON_MASTER = 3;
    private static final int POSITION_INCREASE_VERSION = 4;
    private static final int POSITION_COMMIT_VERSION = 5;
    private static final int POSITION_TAG_COMMIT = 6;
    private static final int POSITION_BUILD_RELEASE = 7;
    private static final int POSITION_PUSH_MASTER = 8;
    private static final int POSITION_MERGE_MASTER_TO_DEV = 9;
    private static final int POSITION_PUSH_TO_DEV = 10;

    private final List<JCheckBox> checkBoxList;

    public ToolWindow() {
        checkBoxList = new ArrayList<>(11);
    }

    /**
     * This method is used to initialize the ui.
     * Called once when first opening the toolWindow
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

        JButton newRelease = new JButton("New release");
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        newRelease.addActionListener(e -> {
            for (int i = 0, checkBoxListSize = checkBoxList.size(); i < checkBoxListSize; i++) {
                JCheckBox checkBox = checkBoxList.get(i);
                if (i == POSITION_DEV_UP_TO_DATE) {
                    checkBox.setEnabled(true);
                } else {
                    checkBox.setEnabled(false);
                }
                checkBox.setSelected(false);
            }
        });
        panel.add(newRelease, constraints);

        CheckBoxBuilder checkBoxBuilder = new CheckBoxBuilder(panel, constraints);

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Develop branch up-to-date")
                .enabled(true)
                .listener(l -> setCheckboxesState(POSITION_DEV_UP_TO_DATE))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Master branch up-to-date")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_MASTER_UP_TO_DATE))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Merge develop to master")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_MERGED_DEV_TO_MASTER))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("On master")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_ON_MASTER))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Set version name and code")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_INCREASE_VERSION))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Commit new version")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_COMMIT_VERSION))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Tag commit")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_TAG_COMMIT))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Build release")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_BUILD_RELEASE))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Push changes to master")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_PUSH_MASTER))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Merge master to develop")
                .enabled(false)
                .listener(l -> setCheckboxesState(POSITION_MERGE_MASTER_TO_DEV))
                .build());

        checkBoxList.add(checkBoxBuilder
                .row(checkBoxList.size() + 1)
                .label("Push changes to develop")
                .enabled(false)
                .listener(null)
                .build());

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

    private void setCheckboxesState(int currentPosition) {
        if (checkBoxList.get(currentPosition).isSelected()) {
            checkBoxList.get(currentPosition + 1).setEnabled(true);
        } else {
            for (int i = currentPosition + 1, checkBoxListSize = checkBoxList.size(); i < checkBoxListSize; i++) {
                JCheckBox checkBox = checkBoxList.get(i);
                checkBox.setSelected(false);
                checkBox.setEnabled(false);
            }
        }
    }

    /**
     * Called when project opens and plugin is initialized
     */
    @Override
    public void init(com.intellij.openapi.wm.ToolWindow window) {
        System.out.println("init");
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }


    private static class CheckBoxBuilder {

        public final JPanel panel;
        public final GridBagConstraints constraints;
        private int row;
        private boolean isEnabled = false;
        private String label;
        private ActionListener listener;

        public CheckBoxBuilder(JPanel panel, GridBagConstraints constraints) {
            this.panel = panel;
            this.constraints = constraints;
        }

        public CheckBoxBuilder row(int val) {
            this.row = val;
            return this;
        }

        public CheckBoxBuilder enabled(boolean val) {
            this.isEnabled = val;
            return this;
        }

        public CheckBoxBuilder label(String val) {
            this.label = val;
            return this;
        }

        public CheckBoxBuilder listener(ActionListener val) {
            this.listener = val;
            return this;
        }

        public JCheckBox build() {
            JCheckBox checkbox = new JCheckBox(label);
            constraints.fill = GridBagConstraints.NONE;
            constraints.gridy = row;
            checkbox.setEnabled(isEnabled);
            if (listener != null) {
                checkbox.addActionListener(listener);
            }
            panel.add(checkbox, constraints);
            return checkbox;
        }
    }
}
