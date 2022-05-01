package finn.academic.voicerecorder.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import finn.academic.voicerecorder.MainActivity;
import finn.academic.voicerecorder.MainStream;
import finn.academic.voicerecorder.R;

import java.io.File;
import java.util.ArrayList;

import finn.academic.voicerecorder.model.Database;
import finn.academic.voicerecorder.model.Folder;
import finn.academic.voicerecorder.util.ListHandler;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Folder> folders;
    Database database;
//    private ShowSelectingListener showSelectingListener;

    public FolderAdapter(Context context, ArrayList<Folder> folders /*, ShowSelectingListener showSelectingListener*/) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        database = new Database(context, "folder.sqlite", null, 1);

        Folder folder = folders.get(position);
        holder.folderName.setText(folder.getName());
        holder.recordQuantity.setText(String.valueOf(folder.getRecords()));

        if (folder.getCanSelect()) {
            holder.selectFolder.setVisibility(View.VISIBLE);
            holder.quantityLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.select_box_enter));
        } else {
                holder.quantityLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.select_box_exit));
                holder.selectFolder.setVisibility(View.GONE);
        }

        if (folder.getSelected()) {
            holder.checkBox();
        } else {
            holder.uncheckBox();
        }

        holder.selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                folder.setSelected(b);

                Button selectBtn = ((Activity) context).findViewById(R.id.select_button);
                for (Folder f : folders) {
                    if (f.getSelected()) {
                        selectBtn.setText(context.getResources().getString(R.string.delete));
                        return;
                    }
                }
                selectBtn.setText(context.getResources().getString(R.string.cancel));
            }
        });

        holder.deleteFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYesNoDialog(holder, position);
            }
        });

        holder.renameFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRenameDialog(position);
            }
        });

        holder.goToFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainStream.class);
                intent.putExtra("key", String.valueOf(folder.getName()));
                String path = view.getContext().getExternalFilesDir("/") + "/" + folder.getName();
                createFolderIfNotExists(path);
                context.startActivity(intent);
                //goToMainStream();
            }
        });
    }

    private void goToMainStream() {
        context.startActivity(new Intent(context, MainStream.class));
    }

    private void showRenameDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.diaglog_make_change);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = dialog.findViewById(R.id.mainTitleDialogMakeChange);
        TextView description = dialog.findViewById(R.id.descriptionDialogMakeChange);
        EditText txtName = dialog.findViewById(R.id.newName);
        txtName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String name = txtName.getText().toString().trim();

                    if (name.equals(folders.get(position).getName())) {
                        description.setText(context.getResources().getString(R.string.namesake));
                        description.setTextColor(context.getResources().getColor(R.color.light_red));
                    } else if (ListHandler.containsName(folders, name)) {
                        description.setText(context.getResources().getString(R.string.existing_name));
                        description.setTextColor(context.getResources().getColor(R.color.light_red));
                    } else {
                        database.queryData("UPDATE Folder " +
                                "SET Name = '" + name + "' " +
                                "WHERE Name = '" + folders.get(position).getName() + "'");

                        folders.get(position).setName(name);
                        notifyDataSetChanged();

                        dialog.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });

        title.setText(context.getResources().getString(R.string.rename_folder));
        description.setText(context.getResources().getString(R.string.input_record_name));
        txtName.setText(folders.get(position).getName());

        Button cancel = dialog.findViewById(R.id.cancelDialogMakeChange);
        Button save = dialog.findViewById(R.id.saveDialogMakeChange);

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty() || charSequence.toString().equals("")) {
                    save.setEnabled(false);
                    save.setAlpha(0.2f);
                } else {
                    int max = charSequence.length() - 1;
                    if (charSequence.charAt(max >= 0 ? max : 0) == '\n') {
                        txtName.setText(charSequence.subSequence(0, charSequence.length() - 1));
                        save.performClick();
                        return;
                    }

                    save.setEnabled(true);
                    save.setAlpha(1.0f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtName.getText().toString().trim();

                if (name.equals(folders.get(position).getName())) {
                    description.setText(context.getResources().getString(R.string.namesake));
                    description.setTextColor(context.getResources().getColor(R.color.light_red));
                } else if (ListHandler.containsName(folders, name)) {
                    description.setText(context.getResources().getString(R.string.existing_name));
                    description.setTextColor(context.getResources().getColor(R.color.light_red));
                } else {
                    database.queryData("UPDATE Folder " +
                            "SET Name = '" + name + "' " +
                            "WHERE Name = '" + folders.get(position).getName() + "'");

                    folders.get(position).setName(name);
                    notifyDataSetChanged();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void showYesNoDialog(ViewHolder holder, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_overwrite_record);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = dialog.findViewById(R.id.mainTitleDialogMakeChange);
        TextView description = dialog.findViewById(R.id.descriptionDialogMakeChange);

        title.setText(context.getResources().getString(R.string.delete_folder));
        description.setText(context.getResources().getString(R.string.alert_delete_folder));

        Button no = dialog.findViewById(R.id.noDialogMakeChange);
        Button yes = dialog.findViewById(R.id.yesDialogMakeChange);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.queryData("DELETE FROM Folder WHERE Name = '" + folders.get(position).getName() + "'");
                Toast.makeText(context, context.getResources().getString(R.string.deleted) + " "
                        + folders.get(position).getName(), Toast.LENGTH_SHORT).show();
                holder.utils.setVisibility(View.GONE);
                folders.remove(position);
                notifyDataSetChanged();
                ((MainActivity) context).updateVisibilityFolderRecyclerView();
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
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

    public static boolean createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (folder.exists())
            return true;
        else
            return folder.mkdirs();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainFolderLayout;
        Button folderName;
        TextView recordQuantity;

        ImageView deleteFolder, renameFolder;

        LinearLayout goToFolder;
        LinearLayout nameLayout;
        LinearLayout utils;

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
            utils = folderView.findViewById(R.id.btnUtils);

            goToFolder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Animation animation;

                    if (utils.getVisibility() == View.GONE) {
                        utils.setVisibility(View.VISIBLE);
                        animation = AnimationUtils.loadAnimation(context, R.anim.trans_name_enter);
                        nameLayout.startAnimation(animation);
                        utils.startAnimation(animation);
                    } else {
                        utils.setVisibility(View.GONE);
                        animation = AnimationUtils.loadAnimation(context, R.anim.exit_full_right);
                        utils.startAnimation(animation);
                        nameLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.trans_name_exit));
                    }

                    return true;
                }
            });

            deleteFolder = folderView.findViewById(R.id.deleteFolder);
            renameFolder = folderView.findViewById(R.id.renameFolder);
        }
    }
}
