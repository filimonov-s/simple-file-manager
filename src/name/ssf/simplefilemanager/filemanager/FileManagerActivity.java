package name.ssf.simplefilemanager.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import name.ssf.simplefilemanager.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagerActivity extends Activity implements FileListLoader.ProgressCallbacks, FileRemover.ProgressCallbacks {

    private final static File ROOT = Environment.getExternalStorageDirectory();
    private final static String CURRENT_DIRECTORY = "CD";
    private final static String CURRENT_DIRECTORY_ITEMS = "CDI";
    private final static String CURRENT_ACTIVITY_MODE = "CDM";


    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);

        LinearLayout mLoadingLayout = (LinearLayout) findViewById(R.id.file_manager_loading_layout);
        ListView mFilesListView = (ListView) findViewById(R.id.file_manager_files_list_view);

        mFilesListAdapter = new FilesListAdapter(this, R.layout.file_manager_item_view, mFilesList);
        mFilesListView.setAdapter(mFilesListAdapter);
        mFilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListItem listItem = mFilesListAdapter.getItem(position);
                if (mIsSelectMode) {
                    listItem.setMIsChecked(!listItem.isMIsChecked());
                    mFilesListAdapter.notifyDataSetChanged();
                } else {
                    if (listItem.isMIsUpLink()) {
                        loadDirectory(mCurrentDir.getParentFile());
                    } else {
                        loadDirectory(listItem.getmFile());
                    }
                }
            }
        });
        mFilesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        File file = new File(ROOT, "delete.txt");

        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (savedInstanceState != null) {
            String restoredDir = savedInstanceState.getString(CURRENT_DIRECTORY);
            ArrayList<ListItem> data =
                    (ArrayList<ListItem>) savedInstanceState.getSerializable(CURRENT_DIRECTORY_ITEMS);
            boolean restoredSelectMode = savedInstanceState.getBoolean(CURRENT_ACTIVITY_MODE);
            if (restoredDir != null && data != null) {
                mCurrentDir = new File(restoredDir);

                for (ListItem item : data) {
                    mFilesListAdapter.add(item);
                }

                mFilesListAdapter.notifyDataSetChanged();

                if (restoredSelectMode) {
                    enableSelectMode();
                }

                mLoadingLayout.setVisibility(View.GONE);
                mFilesListView.setVisibility(View.VISIBLE);
            } else {
                mCurrentDir = ROOT;
                loadDirectory(mCurrentDir);
            }
        } else {
            mCurrentDir = ROOT;
            loadDirectory(mCurrentDir);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_DIRECTORY, mCurrentDir.toString());
        outState.putSerializable(CURRENT_DIRECTORY_ITEMS, (ArrayList<ListItem>) mFilesList);
        outState.putBoolean(CURRENT_ACTIVITY_MODE, mIsSelectMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.file_manager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file_manager_menu_select:
                enableSelectMode();
                break;
            case R.id.file_manager_menu_select_all:
                selectAll();
                break;
            case R.id.file_manager_menu_clear_selection:
                clearSelection();
                break;
            case R.id.file_manager_menu_delete:
                deleteCheckedFiles();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem selectAll = menu.findItem(R.id.file_manager_menu_select_all);
        MenuItem clearSelection = menu.findItem(R.id.file_manager_menu_clear_selection);
        MenuItem select = menu.findItem(R.id.file_manager_menu_select);
        MenuItem delete = menu.findItem(R.id.file_manager_menu_delete);

        if (mIsSelectMode) {
            select.setVisible(false);
            selectAll.setVisible(true);
            clearSelection.setVisible(true);
            delete.setVisible(true);
        } else {
            select.setVisible(true);
            clearSelection.setVisible(false);
            selectAll.setVisible(false);
            delete.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mIsSelectMode) {
            disableSelectMode();
        } else {
            super.onBackPressed();
        }
    }

    // My state methods

    private void enableSelectMode() {
        mIsSelectMode = true;
        mFilesListAdapter.setMShowCheckBoxes(true);
        mFilesListAdapter.notifyDataSetChanged();
    }

    private void disableSelectMode() {
        clearSelection();
        mIsSelectMode = false;
        mFilesListAdapter.setMShowCheckBoxes(false);
        mFilesListAdapter.notifyDataSetChanged();
    }



    // List operations

    @SuppressWarnings("unchecked")
    private void loadDirectory(File file) {
        if (file.isDirectory()) {
            new FileListLoader(file, this).execute();
        } else if (file.isFile()) {
            openFile(file);
        } else {
            Log.wtf(File.class.getSimpleName(), "Loading file does not exist");
        }
    }

    @SuppressWarnings("unused")
    private void openFile(File file) {
        //  TODO : Create method
    }

    @SuppressWarnings("unchecked")
    private void deleteCheckedFiles() {
        new FileRemover(mFilesList, this).execute();
    }

    private void selectAll() {

        for (ListItem listItem : mFilesList) {
            listItem.setMIsChecked(true);
        }
        mFilesListAdapter.notifyDataSetChanged();
    }

    private void clearSelection() {
        for (ListItem listItem : mFilesList) {
            listItem.setMIsChecked(false);
        }
        mFilesListAdapter.notifyDataSetChanged();
    }


    // Loader and remover callbacks

    @Override
    public void workStarted() {
        LinearLayout mLoadingLayout = (LinearLayout) findViewById(R.id.file_manager_loading_layout);
        ListView mFilesListView = (ListView) findViewById(R.id.file_manager_files_list_view);

        mFilesListView.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadingFinished(File dir, List<ListItem> files) {
        mFilesListAdapter.clear();

        mCurrentDir = dir;

        if (!mCurrentDir.equals(ROOT)) {
            mFilesListAdapter.add(new ListItem());
        }

        for (ListItem item : files) {
            mFilesListAdapter.add(item);
        }

        LinearLayout mLoadingLayout = (LinearLayout) findViewById(R.id.file_manager_loading_layout);
        ListView mFilesListView = (ListView) findViewById(R.id.file_manager_files_list_view);

        mLoadingLayout.setVisibility(View.GONE);
        mFilesListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void removingFinished() {


        LinearLayout mLoadingLayout = (LinearLayout) findViewById(R.id.file_manager_loading_layout);
        ListView mFilesListView = (ListView) findViewById(R.id.file_manager_files_list_view);

        mLoadingLayout.setVisibility(View.GONE);
        mFilesListView.setVisibility(View.VISIBLE);

        mFilesListAdapter.notifyDataSetChanged();
        disableSelectMode();
    }

    private ArrayList<ListItem> mFilesList = new ArrayList<ListItem>();
    private FilesListAdapter mFilesListAdapter;
    private File mCurrentDir = ROOT;
    private boolean mIsSelectMode;
}
