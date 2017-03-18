package flekken.releasechecklist.bl.aheadbehind;

import com.intellij.openapi.util.Key;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.impl.HashImpl;
import git4idea.commands.GitLineHandlerListener;
import org.jetbrains.annotations.Nullable;

public class GitRevListLeftRightCounter implements GitLineHandlerListener {
    private int ahead = 0;
    private Hash aheadHash;
    private int behind = 0;
    private Hash behindHash;

    private int exitCode;
    @Nullable
    private Throwable error;

    @Override
    public void onLineAvailable(String line, Key outputType) {
        if (aheadLine(line)) {
            ahead++;
            if (aheadHash == null) {
                aheadHash = hashFromLine(line);
            }
        } else if (behindLine(line)) {
            behind++;
            if (behindHash == null) {
                behindHash = hashFromLine(line);
            }
        }
    }

    private boolean aheadLine(String line) {
        return line.startsWith("<");
    }

    private boolean behindLine(String line) {
        return line.startsWith(">");
    }

    private Hash hashFromLine(String line) {
        return HashImpl.build(line.substring(1));
    }

    public int ahead() {
        return ahead;
    }

    public int behind() {
        return behind;
    }

    @Nullable
    public Hash aheadTop() {
        return aheadHash;
    }

    @Nullable
    public Hash behindTop() {
        return behindHash;
    }

    public boolean isSuccess() {
        return exitCode == 0 && error == null;
    }

    @Override
    public void processTerminated(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public void startFailed(Throwable throwable) {
        error = throwable;
    }
}
