package com.example.ToDoList.mapper;

import com.example.ToDoList.repository.entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    List<Task> getTask();
}
