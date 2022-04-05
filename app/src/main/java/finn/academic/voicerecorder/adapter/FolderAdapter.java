package finn.academic.voicerecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import finn.academic.voicerecorder.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import finn.academic.voicerecorder.listener.ClickListener;
import finn.academic.voicerecorder.model.Folder;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    Context context;
    ArrayList<Folder> folders;

    public FolderAdapter(Context context, ArrayList<Folder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_recyclerview_in_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.folderName.setText(folder.getName());
        holder.recordQuantity.setText(String.valueOf(folder.getRecords()));
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout mainFolderLayout;
        Button folderName;
        TextView recordQuantity;

        ImageView deleteFolder, renameFolder;

        LinearLayout nameLayout;

        public ViewHolder(@NonNull View folderView) {
            super(folderView);

            mainFolderLayout = folderView.findViewById(R.id.mainFolderLayout);
            folderName = folderView.findViewById(R.id.folderName);
            recordQuantity = folderView.findViewById(R.id.recordQuantity);

            nameLayout = folderView.findViewById(R.id.nameLayout);

            nameLayout.setOnClickListener(this);

            nameLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Animation animation;
                    LinearLayout utils = folderView.findViewById(R.id.btnUtils);

                    if (utils.getVisibility() == View.GONE) {
                        utils.setVisibility(View.VISIBLE);
                        animation = AnimationUtils.loadAnimation(context, R.anim.anim_enter);
                        utils.startAnimation(animation);
                        view.startAnimation(animation);
                    } else {
                        animation = AnimationUtils.loadAnimation(context, R.anim.anim_exit);
                        utils.startAnimation(animation);
                        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.trans_name));
                        utils.setVisibility(View.GONE);
                    }
                    return true;
                }
            });

            deleteFolder = folderView.findViewById(R.id.deleteFolder);
            renameFolder = folderView.findViewById(R.id.renameFolder);

            deleteFolder.setOnClickListener(this);
            renameFolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == deleteFolder.getId()) {
                Toast.makeText(context, "Delete " + folders.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            } else if (view.getId() == renameFolder.getId()) {
                Toast.makeText(context, "Rename " + folders.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Go to " + folders.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
