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
        holder.tvTaskName.setText(task.taskName);
        holder.tvDurationValue.setText(task.duration);
        holder.tvStartAfterValue.setText(task.startAfterTask != null ? task.startAfterTask : "none");

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

            // ربط العناصر حسب IDs الموجودة في XML
            cardView = (CardView) itemView;
            tvTaskNumber = itemView.findViewById(R.id.tvTaskNumber);
            tvTaskDash = itemView.findViewById(R.id.tvTaskDash);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartAfter = itemView.findViewById(R.id.tvStartAfter);
            tvStartAfterValue = itemView.findViewById(R.id.tvStartAfterValue);

            // ملاحظة: الـ CardView ليس له id في XML، لذا إما:
            // 1. أضف android:id="@+id/cardView" في XML
            // 2. أو استخدم: cardView = (CardView) itemView
        }
    }

    // دالة لإضافة مهمة جديدة
    public void addTask(Task task) {
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }

    // دالة لإزالة مهمة
    public void removeTask(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        }
    }

    // دالة لتحديث جميع البيانات
    public void updateTaskList(List<Task> newList) {
        taskList.clear();
        taskList.addAll(newList);
        notifyDataSetChanged();
    }

    // دالة للحصول على المهام
    public List<Task> getTaskList() {
        return taskList;
    }

    // Interface للنقر على المهام
    public interface OnTaskClickListener {
        void onTaskClick(int position, Task task);
    }

    private OnTaskClickListener itemClickListener;

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.itemClickListener = listener;
    }
}