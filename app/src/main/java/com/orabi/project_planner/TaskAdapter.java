
package com.orabi.project_planner;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.orabi.project_planner.R;
import com.orabi.project_planner.Task;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTaskName.setText(task.getName());
        holder.tvDuration.setText("duration: " + task.getExpected_duration());

        // تطبيق الألوان الجانبية (الأخضر للمكتمل، الأصفر للجاري)
        if ("completed".equals(task.getStatus())) {
            holder.indicator.setBackgroundColor(Color.parseColor("#4CAF50")); // أخضر
        } else {
            holder.indicator.setBackgroundColor(Color.parseColor("#FFC107")); // أصفر
        }
    }

    @Override
    public int getItemCount() { return taskList.size(); }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName, tvDuration;
        View indicator;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName); // تأكد من إضافة الـ IDs في item_task.xml
            tvDuration = itemView.findViewById(R.id.tvDuration);
            indicator = itemView.findViewById(R.id.taskStatusIndicator);
        }
    }
}