package finn.academic.voicerecorder.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import finn.academic.voicerecorder.MainStream;
import finn.academic.voicerecorder.R;

import java.util.ArrayList;

import finn.academic.voicerecorder.model.Folder;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Folder> folders;
//    private ShowSelectingListener showSelectingListener;
    private ArrayList<ViewHolder> viewHolders;

    public FolderAdapter(Context context, ArrayList<Folder> folders, ArrayList<ViewHolder> viewHolders /*, ShowSelectingListener showSelectingListener*/) {
        this.context = context;
        this.folders = folders;
        this.viewHolders = viewHolders;
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

        if (folder.getCanSelect()) {
            holder.selectFolder.setVisibility(View.VISIBLE);
            holder.quantityLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.select_box_enter));
        } else {
            holder.selectFolder.setVisibility(View.GONE);
            holder.quantityLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.select_box_exit));
        }

        if (folder.getSelected()) {
            holder.checkBox();
        } else {
            holder.uncheckBox();
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

//    public List<Folder> getSelectedFolder() {
//        List<Folder> folderList = new ArrayList<>();
//        for (Folder folder : folders) {
//            if (folder.getSelected()) {
//                folderList.add(folder);
//            }
//        }
//        return folderList;
//    }

    public void showAllSelecting() {
        for (Folder folder : folders) {
            folder.setCanSelect(true);
        }
        this.notifyDataSetChanged();
    }

    public void hideAllSelecting() {
        for (Folder folder : folders) {
            folder.setCanSelect(false);
        }
        this.notifyDataSetChanged();
    }

    public void setAllChecked() {
        for (Folder folder : folders) {
            folder.setSelected(true);
        }
        this.notifyDataSetChanged();
    }

    public void setAllUnchecked() {
        for (Folder folder : folders) {
            folder.setSelected(false);
        }
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout mainFolderLayout;
        Button folderName;
        TextView recordQuantity;

        ImageView deleteFolder, renameFolder;

        LinearLayout goToFolder;
        LinearLayout nameLayout;

        RelativeLayout selectFolder;

        LinearLayout quantityLayout;

        CheckBox selectBox;

        public RelativeLayout getSelectFolder() {
            return selectFolder;
        }

        public LinearLayout getQuantityLayout() {
            return quantityLayout;
        }

        public void checkBox() {
            selectBox.setChecked(true);
        }

        public void uncheckBox() {
            selectBox.setChecked(false);
        }

        public ViewHolder(@NonNull View folderView) {
            super(folderView);

            mainFolderLayout = folderView.findViewById(R.id.mainFolderLayout);
            folderName = folderView.findViewById(R.id.folderName);
            recordQuantity = folderView.findViewById(R.id.recordQuantity);

            selectFolder = folderView.findViewById(R.id.selectFolder);
            quantityLayout = folderView.findViewById(R.id.quantityLayout);
            selectBox = folderView.findViewById(R.id.selectFolderBox);

            goToFolder = folderView.findViewById(R.id.goToFolder);
            nameLayout = folderView.findViewById(R.id.nameLayout);

            goToFolder.setOnClickListener(this);

            goToFolder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Animation animation;
                    LinearLayout utils = folderView.findViewById(R.id.btnUtils);

                    if (utils.getVisibility() == View.GONE) {
                        utils.setVisibility(View.VISIBLE);
                        animation = AnimationUtils.loadAnimation(context, R.anim.trans_name_enter);
                        nameLayout.startAnimation(animation);
                        utils.startAnimation(animation);
                    } else {
                        animation = AnimationUtils.loadAnimation(context, R.anim.anim_exit);
                        utils.startAnimation(animation);
                        nameLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.trans_name_exit));
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
                showRenameDialog();
            } else {
                goToMainStream();
            }
        }

        private void goToMainStream() {
            context.startActivity(new Intent(context, MainStream.class));
        }

        private void showRenameDialog() {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.diaglog_make_change);
            dialog.setCanceledOnTouchOutside(false);

            TextView title = dialog.findViewById(R.id.mainTitleDialogMakeChange);
            TextView description = dialog.findViewById(R.id.descriptionDialogMakeChange);
            EditText newName = dialog.findViewById(R.id.newName);

            title.setText(context.getResources().getString(R.string.rename_folder));
            description.setText(context.getResources().getString(R.string.input_record_name));
            newName.setText(folders.get(getAdapterPosition()).getName());

            Button cancel = dialog.findViewById(R.id.cancelDialogMakeChange);
            Button save = dialog.findViewById(R.id.saveDialogMakeChange);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            dialog.show();
        }


    }
}
