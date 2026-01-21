package com.orabi.project_planner;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<Plan> planList;

    public PlanAdapter(List<Plan> planList) {
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);
        holder.tvTitle.setText(plan.getTitle());

        // Set the start date (this is what you're missing!)
        holder.tvStartValue.setText(plan.getStartDate());

        // For now, we'll set dummy values for the other fields since your Plan class doesn't have them
        holder.tvDurationValue.setText("Calculating...");
        holder.tvExpectedEndValue.setText("Not set");
        holder.tvEndValue.setText("Not ended");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PlanDetailsActivity.class);
            intent.putExtra("PLAN_ID", plan.getId());
            intent.putExtra("PLAN_TITLE", plan.getTitle());
            v.getContext().startActivity(intent);
        });

        // تغيير لون الشريط الجانبي بناءً على الحالة
        int colorRes;
        switch (plan.getStatus()) {
            case "completed": colorRes = R.color.status_green; break;
            case "late": colorRes = R.color.status_red; break;
            default: colorRes = R.color.status_yellow; break;
        }
        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
    }
    @Override
    public int getItemCount() {
        return planList.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDurationValue;  // Add this
        TextView tvStartValue;     // Add this
        TextView tvExpectedEndValue; // Add this
        TextView tvEndValue;       // Add this
        View statusIndicator;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPlanTitle);
            // Add these lines to connect all the text views
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartValue = itemView.findViewById(R.id.tvStartValue);
            tvExpectedEndValue = itemView.findViewById(R.id.tvExpectedEndValue);
            tvEndValue = itemView.findViewById(R.id.tvEndValue);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }
    }
}
