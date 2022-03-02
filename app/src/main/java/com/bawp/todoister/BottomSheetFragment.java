package com.bawp.todoister;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private EditText enterTodo;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    private Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;
    private RadioButton priorityLow;
    private RadioButton priorityMed;
    private RadioButton priorityHigh;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);
        priorityLow = view.findViewById(R.id.radioButton_low);
        priorityMed = view.findViewById(R.id.radioButton_med);
        priorityHigh = view.findViewById(R.id.radioButton_high);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarButton.setOnClickListener(view1 -> {
            calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            Utils.hideSoftKeyboard(view1);
        });

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            calendar.clear();
            calendar.set(year,month,dayOfMonth);
            dueDate = calendar.getTime();
        });

        priorityButton.setOnClickListener(view1 -> {
            priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

        });
        priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            selectedButtonId = checkedId;
            selectedRadioButton = view.findViewById(selectedButtonId);
            if (selectedRadioButton.getId()==R.id.radioButton_high) {
                priority = Priority.HIGH;
            } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                priority = Priority.MEDIUM;
            } else {
                priority = Priority.LOW;
            }
            //            if (priorityRadioGroup.getVisibility()==View.VISIBLE){
//                selectedButtonId = checkedId;
//                selectedRadioButton = view.findViewById(selectedButtonId);
//                if (selectedRadioButton.getId()==R.id.radioButton_high) {
//                    priority = Priority.HIGH;
//                } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
//                    priority = Priority.MEDIUM;
//                } else {
//                    priority = Priority.LOW;
//                }
//            } else {
//                priority = Priority.LOW;
//            }
        });

        saveButton.setOnClickListener(view1 -> {
            String task = enterTodo.getText().toString().trim();
            if(!TextUtils.isEmpty(task) && dueDate != null){
                Task myTask = new Task(task, priority, dueDate, Calendar.getInstance().getTime(), false);
                if(isEdit) {
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();
                    updateTask.setTask(task);
                    updateTask.setDueDate(dueDate);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);
                } else {
                    TaskViewModel.insert(myTask);
                }

            }
            dismiss();

        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (sharedViewModel.getSelectedItem().getValue() != null){
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
            dueDate = task.getDueDate();
            calendarView.setDate(dueDate.getTime());
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        sharedViewModel.selectItem(null);
        sharedViewModel.setIsEdit(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        isEdit = sharedViewModel.getIsEdit();
        if (sharedViewModel.getSelectedItem().getValue() != null){
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
            dueDate = task.getDueDate();
            calendarView.setDate(dueDate.getTime());
            priority = task.getPriority();
            switch(priority){
                case HIGH:
                    priorityHigh.setChecked(true);
//                    priorityHigh.setSelected(true);
//                    priorityLow.setSelected(false);
//                    priorityMed.setSelected(false);
                    break;
                case MEDIUM:
//                    priorityHigh.setSelected(false);
//                    priorityLow.setSelected(false);
                    priorityMed.setChecked(true);
                    break;

                case LOW:
                    //priorityHigh.setSelected(false);
                    priorityLow.setChecked(true);
                    //priorityMed.setSelected(false);
                    break;

                default:
            }

        } else {
            enterTodo.setText("");
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calendar.getTime();
            calendarView.setDate(calendar.getTimeInMillis());
            priority = Priority.LOW;
            priorityLow.setChecked(true);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.today_chip:
                calendar.add(Calendar.DAY_OF_YEAR, 0);
                dueDate = calendar.getTime();
                calendarView.setDate(calendar.getTimeInMillis());


                break;

            case R.id.tomorrow_chip:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                dueDate = calendar.getTime();
                calendarView.setDate(calendar.getTimeInMillis());

                break;

            case R.id.next_week_chip:
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                dueDate = calendar.getTime();
                calendarView.setDate(calendar.getTimeInMillis());

                break;

            default:

        }
    }
}