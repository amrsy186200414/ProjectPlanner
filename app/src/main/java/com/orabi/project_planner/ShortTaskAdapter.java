package com.orabi.project_planner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShortTaskAdapter extends RecyclerView.Adapter<ShortTaskAdapter.ShortTaskViewHolder> {
    private List<Task> taskList;

    public ShortTaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ShortTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_short_task, parent, false);
        return new ShortTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortTaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // تعيين البيانات حسب XML الجديد
        holder.tvTaskNumber.setText(String.valueOf(position + 1));
        holder.tvTaskName.setText(task.getName());
        holder.tvDurationValue.setText(task.getExpected_duration().toString());
//        holder.tvStartAfterValue.setText(task.startAfterTask != null ? task.startAfterTask : "none");

        // تغيير لون الخلفية للمهمة
//        if (position % 2 == 0) {
//            holder.cardView.setCardBackgroundColor(
//                    holder.itemView.getContext().getResources().getColor(R.color.light_gray_bg)
//            );
//        } else {
//            holder.cardView.setCardBackgroundColor(
//                    holder.itemView.getContext().getResources().getColor(R.color.white)
//            );
//        }

        // إضافة حدث النقر إذا لزم الأمر
        holder.cardView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onTaskClick(position, task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class ShortTaskViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTaskNumber;
        TextView tvTaskDash;
        TextView tvTaskName;
        TextView tvDuration;
        TextView tvDurationValue;
        TextView tvStartAfter;
        TextView tvStartAfterValue;

        public ShortTaskViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView;
            tvTaskNumber = itemView.findViewById(R.id.tvTaskNumber);
            tvTaskDash = itemView.findViewById(R.id.tvTaskDash);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartAfterValue = itemView.findViewById(R.id.tvStartAfterValue);

        }
    }

    public void addTask(Task task) {
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }

    public void removeTask(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        }
    }

    public void updateTaskList(List<Task> newList) {
        taskList.clear();
        taskList.addAll(newList);
        notifyDataSetChanged();
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public interface OnTaskClickListener {
        void onTaskClick(int position, Task task);
    }

    private OnTaskClickListener itemClickListener;

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.itemClickListener = listener;
    }
}