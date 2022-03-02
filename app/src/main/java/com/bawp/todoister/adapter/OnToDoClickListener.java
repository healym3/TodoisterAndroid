package com.bawp.todoister.adapter;

import com.bawp.todoister.model.Task;

public interface OnToDoClickListener {
    void onToDoClick(Task task);
    void onToDoRadioButtonClick(Task task);
}
