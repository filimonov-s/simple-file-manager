package name.ssf.simplefilemanager.filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import name.ssf.simplefilemanager.R;

import java.util.List;

public class FilesListAdapter extends ArrayAdapter<ListItem> {

    public FilesListAdapter(Context context, int textViewResourceId, List<ListItem> objects) {
        super(context, textViewResourceId, objects);
        mResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout resultView;

        if (convertView == null) {
            resultView = new RelativeLayout(getContext());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(mResourceId, resultView, true);
        } else {
            resultView = (RelativeLayout) convertView;
        }

        ListItem listItem = getItem(position);

        TextView textView = (TextView) resultView.findViewById(R.id.file_manager_item_text_view);
        CheckBox checkBox = (CheckBox) resultView.findViewById(R.id.file_manager_item_check_box);
        ImageView imageView = (ImageView) resultView.findViewById(R.id.file_manager_item_image_view);

        if (listItem.isMIsUpLink()) {
            textView.setText("..");
        } else {
            textView.setText(listItem.getmFile().getName());
        }

        if (mShowCheckBoxes && !listItem.isMIsUpLink()) {
            if (listItem.isMIsChecked()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        if (listItem.isMIsUpLink() || listItem.getmFile().isDirectory()) {
            imageView.setVisibility(View.VISIBLE);
        } else if (listItem.getmFile().isFile()) {
            imageView.setVisibility(View.INVISIBLE);
        }

        return resultView;
    }

    @SuppressWarnings("unused")
    public boolean isMShowCheckBoxes() {
        return mShowCheckBoxes;
    }

    public void setMShowCheckBoxes(boolean mShowCheckBoxes) {
        this.mShowCheckBoxes = mShowCheckBoxes;
    }


    private int mResourceId;
    private boolean mShowCheckBoxes;
}
