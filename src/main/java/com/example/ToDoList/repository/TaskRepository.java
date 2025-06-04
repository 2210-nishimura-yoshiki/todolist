package com.example.ToDoList.repository;

import com.example.ToDoList.repository.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
//    public List<Task> findAllByOrderById();
    public List<Task> findByLimitDateBetweenOrderByLimitDate(Date start, Date end);

    public List<Task> findByStatusAndLimitDateBetweenOrderByLimitDate(Short status, Date start, Date end);
    public List<Task> findByContentContainingAndLimitDateBetweenOrderByLimitDate(String keyword, Date start, Date end);
    public List<Task> findByStatusAndContentContainingAndLimitDateBetweenOrderByLimitDate(Short status, String keyword, Date start, Date end);
}
