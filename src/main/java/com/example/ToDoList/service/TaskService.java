package com.example.ToDoList.service;

import com.example.ToDoList.controller.form.TaskForm;
import com.example.ToDoList.mapper.TaskMapper;
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
    TaskMapper mapper;

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
            results = mapper.findByStatusAndContentContainingAndLimitDateBetween(status, keyword, start, end);
        } else if (StringUtils.hasText(strStatus) && (!StringUtils.hasText(keyword))) {
            status = Short.parseShort(strStatus);
            results = mapper.findByStatusAndLimitDateBetween(status, start, end);
        } else if (StringUtils.hasText(keyword) && (!StringUtils.hasText(strStatus))) {
            results = mapper.findByContentContainingAndLimitDateBetween(keyword, start, end);
        } else {
            results = mapper.findByLimitDateBetween(start, end);
        }

        return setTaskForm(results);
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<TaskForm> setTaskForm(List<Task> results) {
        List<TaskForm> tasks = new ArrayList<>();

        for (Task value : results) {
            TaskForm task = new TaskForm();
            task.setId(value.getId());
            task.setContent(value.getContent());
            task.setStatus(value.getStatus());

            String strLimitDate = null;
            Date limitDate = value.getLimitDate();
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
        if (saveTask.getId() == 0) {
            mapper.insert(saveTask);
        } else {
            mapper.update(saveTask);
        }
    }

    /*
     * ステータス変更
     */
    public void updateStatus(TaskForm reqTask) {
        Task saveTask = setTaskEntity(reqTask);
        mapper.update(saveTask);
    }

    /*
     * レコード削除
     */
    public void deleteTask(Integer id) {
        mapper.deleteById(id);
    }

    /*
     * レコード1件取得
     */
    public TaskForm editTask(Integer id) {
        Task task = mapper.findById(id);
        if (task == null) {
            return null;
        }
        List<Task> results = new ArrayList<>();
        results.add(task);

        List<TaskForm> reports = setTaskForm(results);

        // 変換後のリストから最初の（そして唯一の）要素を返す
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
