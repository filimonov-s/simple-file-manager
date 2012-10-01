package name.ssf.simplefilemanager.filemanager;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListLoader extends AsyncTask<Void, Void, List<ListItem>> {

    public FileListLoader(File mDir, ProgressCallbacks mProgressCallbacks) {
        this.mDir = mDir;
        this.mProgressCallbacks = mProgressCallbacks;
    }

    public interface ProgressCallbacks {
        void workStarted();
        void loadingFinished(File dir, List<ListItem> files);
    }
    @Override
    protected List<ListItem> doInBackground(Void... voids) {
        List<ListItem> loadedFiles = new ArrayList<ListItem>();
        for (File file : mDir.listFiles()) {
            loadedFiles.add(new ListItem(file));
        }

        return loadedFiles;
    }

    @Override
    protected void onPreExecute() {
        mProgressCallbacks.workStarted();
    }

    @Override
    protected void onPostExecute(List<ListItem> listItems) {
        mProgressCallbacks.loadingFinished(mDir, listItems);
    }

    private File mDir;
    private ProgressCallbacks mProgressCallbacks;
}
