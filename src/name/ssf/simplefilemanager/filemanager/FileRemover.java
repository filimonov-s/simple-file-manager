package name.ssf.simplefilemanager.filemanager;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;

public class FileRemover extends AsyncTask<Void, Void, Void> {

    public FileRemover(ArrayList<ListItem> mFilesList, ProgressCallbacks mProgressCallbacks) {
        this.mFilesList = mFilesList;
        this.mProgressCallbacks = mProgressCallbacks;
    }

    public interface ProgressCallbacks {
        void removingFinished();
        void workStarted();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 0; i < mFilesList.size(); i++) {
            ListItem item = mFilesList.get(i);
            if (item.isMIsChecked()) {
                File file = item.getmFile();
                if (file.isFile()) {
                    if (file.delete()) {
                        mFilesList.remove(i);
                    } else if (file.isDirectory()) {
                        // TODO: Remove directories
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressCallbacks.workStarted();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressCallbacks.removingFinished();
    }

    private ArrayList<ListItem> mFilesList;
    private ProgressCallbacks mProgressCallbacks;

}
