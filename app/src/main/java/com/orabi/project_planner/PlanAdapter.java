package com.orabi.project_planner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {
    private List<Plan> planList;
    private Context context;
    private Timer timer;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private Map<Integer, PlanViewHolder> viewHolders = new HashMap<>();

    public PlanAdapter(List<Plan> planList, Context context) {
        this.planList = planList;
        this.context = context;
        startTimer();
    }

    public void updatePlans(List<Plan> newList) {
        this.planList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }
    private String formatDurationForPlanItem(Duration duration) {
        if (duration == null) return "Not set";

        StringBuilder sb = new StringBuilder();
        if (duration.getDays() > 0) {
            sb.append(duration.getDays()).append("d");
            if (duration.getHours() > 0) {
                sb.append(" ").append(duration.getHours()).append("h");
            }
        } else if (duration.getHours() > 0) {
            sb.append(duration.getHours()).append("h");
        } else if (duration.getMinutes() > 0) {
            sb.append(duration.getMinutes()).append("m");
        } else {
            sb.append("0d");
        }

        return sb.toString();
    }
    private String calculateExpectedEndDate(String startDate, Duration duration) {
        if (startDate == null || startDate.isEmpty() || duration == null) {
            return "--";
        }

        try {
            SimpleDateFormat inputFormat;

            if (startDate.contains("/")) {
                if (startDate.length() == 10) { // dd/MM/yyyy
                    inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                } else if (startDate.length() == 8) { // dd/MM/yy
                    inputFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                }
            } else if (startDate.contains("-")) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            } else {
                return "Invalid Date";
            }

            Date start = inputFormat.parse(startDate);
            if (start == null) {
                return "--";
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            calendar.add(Calendar.MONTH, duration.getMonths());
            calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());
            calendar.add(Calendar.HOUR_OF_DAY, duration.getHours());
            calendar.add(Calendar.MINUTE, duration.getMinutes());

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
            return outputFormat.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }

    private String formatDateToYYYYMMDD(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equals("--")) {
            return "--";
        }

        try {
            Date date = null;

            String[] possibleFormats = {
                    "dd/MM/yyyy",
                    "dd/MM/yy",
                    "yyyy-MM-dd",
                    "MM/dd/yyyy",
                    "dd-MM-yyyy",
                    "dd MMM yyyy",
                    "dd MMM",
                    "MMM dd",
                    "yyyy/MM/dd"
            };

            for (String format : possibleFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                    date = sdf.parse(dateStr);
                    if (date != null) break;
                } catch (Exception e) {
                }
            }

            if (date == null) {
                if (dateStr.matches(".*\\d+.*")) {
                    try {
                        if (dateStr.contains("/")) {
                            String[] parts = dateStr.split("/");
                            if (parts.length >= 3) {
                                int year = Integer.parseInt(parts[2]);
                                if (year < 100) {
                                    year += 2000;
                                }
                                dateStr = year + "-" + parts[1] + "-" + parts[0];
                                return dateStr;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                return dateStr;
            }

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    private String calculateExpectedEndDateYYYYMMDD(String startDate, Duration duration) {
        if (startDate == null || startDate.isEmpty() || duration == null) {
            return "--";
        }

        try {
            String formattedStartDate = formatDateToYYYYMMDD(startDate);
            if (formattedStartDate.equals("--") || formattedStartDate.equals(startDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start;
                try {
                    start = sdf.parse(startDate);
                } catch (Exception e) {
                    sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    start = sdf.parse(startDate);
                }

                if (start == null) return "--";

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);

                calendar.add(Calendar.MONTH, duration.getMonths());
                calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());
                calendar.add(Calendar.HOUR_OF_DAY, duration.getHours());
                calendar.add(Calendar.MINUTE, duration.getMinutes());

                return sdf.format(calendar.getTime());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = sdf.parse(formattedStartDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            calendar.add(Calendar.MONTH, duration.getMonths());
            calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());
            calendar.add(Calendar.HOUR_OF_DAY, duration.getHours());
            calendar.add(Calendar.MINUTE, duration.getMinutes());

            return sdf.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }
    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);

        viewHolders.put(plan.getId(), holder);

        holder.tvTitle.setText(plan.getTitle());

        String formattedStartDate = formatDateToYYYYMMDD(plan.getStartDate());
        holder.tvStartValue.setText(formattedStartDate);

        String expectedEndDate = calculateExpectedEndDateYYYYMMDD(plan.getStartDate(), plan.getDuration());
        holder.tvExpectedEndValue.setText(expectedEndDate);

        if (plan.getEndDate() != null && !plan.getEndDate().isEmpty()) {
            String formattedEndDate = formatDateToYYYYMMDD(plan.getEndDate());
            holder.tvEndValue.setText(formattedEndDate);
        } else {
            holder.tvEndValue.setText("--");
        }

        if (plan.getDuration() != null) {
            holder.tvDurationValue.setText(formatDurationForPlanItem(plan.getDuration()));
        } else {
            holder.tvDurationValue.setText("Not set");
        }

        if ("in_progress".equals(plan.getStatus())) {
            holder.btnStart.setText("In Progress");
            holder.btnStart.setEnabled(false);
            holder.btnStart.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
        } else if ("completed".equals(plan.getStatus())) {
            holder.btnStart.setText("Completed");
            holder.btnStart.setEnabled(false);
            holder.btnStart.setBackgroundResource(R.drawable.rounded_button_bg_disabled);
        } else {
            holder.btnStart.setText("Start");
            holder.btnStart.setEnabled(true);
            holder.btnStart.setBackgroundResource(R.drawable.rounded_button_bg);
        }

        updatePlanTimeRemaining(holder, plan);

        int colorRes;
        switch (plan.getStatus()) {
            case "completed":
                colorRes = R.color.status_green;
                break;
            case "late":
                colorRes = R.color.status_red;
                break;
            case "in_progress":
                colorRes = R.color.status_yellow;
                break;
            default:
                colorRes = R.color.status_yellow;
                break;
        }
        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(context, colorRes));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlanDetailsActivity.class);
            intent.putExtra("PLAN_ID", plan.getId());
            intent.putExtra("PLAN_TITLE", plan.getTitle());
            context.startActivity(intent);
        });

        holder.btnStart.setOnClickListener(v -> {
            startPlan(plan.getId());
        });
    }

    private void updatePlanTimeRemaining(PlanViewHolder holder, Plan plan) {
        if ("in_progress".equals(plan.getStatus()) && plan.getStartDate() != null && !plan.getStartDate().isEmpty()) {
            try {
                Date startDate = dateFormat.parse(plan.getStartDate());
                if (startDate != null && plan.getDuration() != null) {
                    long totalMillis = plan.getDuration().toMillis();
                    long currentTime = System.currentTimeMillis();
                    long startTime = startDate.getTime();
                    long elapsedTime = currentTime - startTime;
                    long remainingMillis = totalMillis - elapsedTime;

                    if (remainingMillis > 0) {
                        String remainingText = formatTimeRemaining(remainingMillis);
                        holder.tvTimeRemaining.setText(remainingText);
                        holder.tvTimeRemaining.setTextColor(ContextCompat.getColor(context, R.color.status_green));
                    } else {
                        holder.tvTimeRemaining.setText("Overdue!");
                        holder.tvTimeRemaining.setTextColor(ContextCompat.getColor(context, R.color.status_red));
                        plan.setStatus("late");
                    }
                } else {
                    holder.tvTimeRemaining.setText(plan.getDuration() != null ? formatDuration(plan.getDuration()) : "Not set");
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvTimeRemaining.setText(plan.getDuration() != null ? formatDuration(plan.getDuration()) : "Not set");
            }
        } else {
            holder.tvTimeRemaining.setText(plan.getDuration() != null ? formatDuration(plan.getDuration()) : "Not set");
            holder.tvTimeRemaining.setTextColor(ContextCompat.getColor(context, R.color.secondary_text));
        }
    }

    /**
     * Format time remaining in a countdown format: Xd Yh Zm
     */
    private String formatTimeRemaining(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }
        sb.append(minutes).append("m");

        return sb.toString().trim();
    }

    /**
     * Format duration for display
     */
    private String formatDuration(Duration duration) {
        if (duration == null) return "Not set";

        StringBuilder sb = new StringBuilder();
        if (duration.getMonths() > 0) {
            sb.append(duration.getMonths()).append("mo ");
        }
        if (duration.getDays() > 0) {
            sb.append(duration.getDays()).append("d ");
        }
        if (duration.getHours() > 0) {
            sb.append(duration.getHours()).append("h ");
        }
        if (duration.getMinutes() > 0 || sb.length() == 0) {
            sb.append(duration.getMinutes()).append("m");
        }

        return sb.toString().trim();
    }

    private void startPlan(int planId) {
        DBHelperPlan dbHelper = new DBHelperPlan(context);
        DBHelperTask dbHelperTask = new DBHelperTask(context);

        Plan plan = dbHelper.getPlanByID(planId);
        if (plan != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            plan.setStartDate(sdf.format(new Date()));
            plan.setStatus("in_progress");
            dbHelper.updateStudentDetails(plan);

            List<Task> tasks = dbHelperTask.getTasksByPlanId(planId);
            for (Task task : tasks) {
                if (task.getPrevious_task() == null || task.getPrevious_task().getId() == -1) {
                    task.setStart_date(new Date());
                    task.setStatus("in_progress");
                    dbHelperTask.updateTaskDetails(task);
                }
            }

            if (context instanceof MainActivity) {
                ((MainActivity) context).onResume();
            }
        }
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Update time remaining every minute for in-progress plans
                if (context instanceof MainActivity) {
                    ((MainActivity) context).runOnUiThread(() -> {
                        for (Plan plan : planList) {
                            if ("in_progress".equals(plan.getStatus())) {
                                PlanViewHolder holder = viewHolders.get(plan.getId());
                                if (holder != null) {
                                    updatePlanTimeRemaining(holder, plan);
                                }
                            }
                        }
                    });
                }
            }
        }, 0, 60000); // Update every minute
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        viewHolders.clear();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stopTimer();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDurationValue, tvStartValue, tvExpectedEndValue, tvTimeRemaining, tvEndValue;
        View statusIndicator;
        Button btnStart;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPlanTitle);
            tvDurationValue = itemView.findViewById(R.id.tvDurationValue);
            tvStartValue = itemView.findViewById(R.id.tvStartValue);
            tvExpectedEndValue = itemView.findViewById(R.id.tvExpectedEndValue);
            tvTimeRemaining = itemView.findViewById(R.id.tvTimeRemaining);
            tvEndValue = itemView.findViewById(R.id.tvEndValue);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }

    private void calculateAndSetExpectedEndDate(Plan plan) {
        if (plan.getStartDate() != null && !plan.getStartDate().isEmpty() &&
                plan.getDuration() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date startDate = sdf.parse(plan.getStartDate());

                if (startDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);

                    Duration duration = plan.getDuration();
                    calendar.add(Calendar.MONTH, duration.getMonths());
                    calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());

                    String expectedEndDate = sdf.format(calendar.getTime());
                    plan.setExpectedEndDate(expectedEndDate);

                    DBHelperPlan dbHelper = new DBHelperPlan(context);
                    dbHelper.updateStudentDetails(plan);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}