package name.ssf.simplefilemanager.filemanager;

import java.io.File;
import java.io.Serializable;

public class ListItem implements Serializable {
    public ListItem() {
        mIsUpLink = true;
    }

    public ListItem(File mFile) {
        this.mFile = mFile;
    }

    public File getmFile() {
        return mFile;
    }

    public void setmFile(File mFile) {
        this.mFile = mFile;
    }

    public boolean isMIsChecked() {
        return mIsChecked;
    }

    public void setMIsChecked(boolean mIsChecked) {
        if (!mIsUpLink) this.mIsChecked = mIsChecked;
    }

    public boolean isMIsUpLink() {
        return mIsUpLink;
    }

    //XXX code convention
    private File mFile;
    private boolean mIsChecked;
    private boolean mIsUpLink;

}
