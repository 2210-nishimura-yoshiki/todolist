package com.example.ToDoList.service;

import com.example.ToDoList.controller.form.TaskForm;
import com.example.ToDoList.repository.TaskRepository;
import com.example.ToDoList.repository.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

//    /*
//     * レコード全件取得処理
//     */
//    public List<TaskForm> findAllReport() {
//        List<Task> results = taskRepository.findAllByOrderById();
//        List<TaskForm> tasks = setReportForm(results);
//        return tasks;
//    }

    /*
     * レコード絞り込み取得処理
     */
    public List<TaskForm> findSortTasks(String strStatus, String keyword, String startDate, String endDate) throws ParseException {
        Short status = null;
        Date start = null;
        Date end = null;
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (StringUtils.hasText(startDate)) {
            startDate += " 00:00:00";
            start = sdFormat.parse(startDate);
        } else {
            startDate = "2020-01-01 00:00:00";
            start = sdFormat.parse(startDate);
        }

        if (StringUtils.hasText(endDate)) {
            endDate += " 23:59:59";
            end = sdFormat.parse(endDate);
        } else {
            endDate = "2100-01-01 23:59:59";
            end = sdFormat.parse(endDate);
        }

        List<Task> results = null;
        if (StringUtils.hasText(strStatus) && StringUtils.hasText(keyword)) {
            status = Short.parseShort(strStatus);
            results = taskRepository.findByStatusAndContentContainingAndLimitDateBetweenOrderByLimitDate(status, keyword, start, end);
        } else if (StringUtils.hasText(strStatus) && (!StringUtils.hasText(keyword))) {
            status = Short.parseShort(strStatus);
            results = taskRepository.findByStatusAndLimitDateBetweenOrderByLimitDate(status, start, end);
        } else if (StringUtils.hasText(keyword) && (!StringUtils.hasText(strStatus))) {
            results = taskRepository.findByContentContainingAndLimitDateBetweenOrderByLimitDate(keyword, start, end);
        } else {
            results = taskRepository.findByLimitDateBetweenOrderByLimitDate(start, end);
        }

        List<TaskForm> tasks = setTaskForm(results);
        return tasks;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<TaskForm> setTaskForm(List<Task> results) {
        List<TaskForm> tasks = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            TaskForm task = new TaskForm();
            Task result = results.get(i);
            task.setId(result.getId());
            task.setContent(result.getContent());
            task.setStatus(result.getStatus());

            String strLimitDate = null;
            Date limitDate = result.getLimitDate();
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

            strLimitDate = sdFormat.format(limitDate);

            task.setLimitDate(strLimitDate);
            tasks.add(task);
        }
        return tasks;
    }

    /*
     * タスク追加
     */
    public void saveTask(TaskForm reqTask) throws ParseException {
        Task saveTask = setTaskEntity(reqTask);
        taskRepository.save(saveTask);
    }

    /*
     * ステータス変更
     */
    public boolean saveStatus(TaskForm reqTask){
        Task saveTask = setTaskEntity(reqTask);
        taskRepository.save(saveTask);
        return true;
    }

    /*
     * レコード削除
     */
    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }

    /*
     * レコード1件取得
     */
    public TaskForm editTask(Integer id) {
        List<Task> results = new ArrayList<>();
        results.add((Task) taskRepository.findById(id).orElse(null));
        if (results.get(0) == null) {
            return null;
        }
        List<TaskForm> reports = setTaskForm(results);
        return reports.get(0);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Task setTaskEntity(TaskForm reqTask) {
        Date nowDate = new Date();

        Date limitDate = null;
        String strLimit = reqTask.getLimitDate() + " 00:00:00";
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            limitDate = sdFormat.parse(strLimit);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Task task = new Task();
        task.setId(reqTask.getId());
        task.setContent(reqTask.getContent());
        task.setStatus(reqTask.getStatus());
        task.setLimitDate(limitDate);
        task.setUpdatedDate(nowDate);
        return task;
    }
}
