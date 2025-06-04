package com.example.ToDoList.controller.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Date;

@Getter
@Setter
public class TaskForm {

    private int id;
    @NotBlank(message = "タスクを入力してください")
    private String content;
    private short status;
    @NotBlank(message = "期限を設定してください")
    private String limitDate;
}
