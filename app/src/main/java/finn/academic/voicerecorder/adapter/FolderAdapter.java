package finn.academic.voicerecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import finn.academic.voicerecorder.R;

import java.util.List;

import finn.academic.voicerecorder.model.Folder;

public class FolderAdapter extends ArrayAdapter<Folder> {
    private Context context;
    private int layout;
    private List<Folder> folderList;

    public FolderAdapter(@NonNull Context context, int resource, @NonNull List<Folder> objects) {
        super(context, resource, objects);

        this.context = context;
        this.layout = resource;
        this.folderList = objects;
    }

    private class ViewHolder {
        Button btnName;
        TextView txtQuantity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder = new ViewHolder();

            holder.btnName = (Button) convertView.findViewById(R.id.folderName);
            holder.txtQuantity = (TextView) convertView.findViewById(R.id.recordQuantity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Folder folder = folderList.get(position);
        holder.btnName.setText(folder.getName());

        return convertView;
    }
}
