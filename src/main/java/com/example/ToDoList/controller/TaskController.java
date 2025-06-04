package com.example.ToDoList.controller;

import com.example.ToDoList.controller.form.TaskForm;
import com.example.ToDoList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    HttpSession session;

    /*
     * タスク内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "strStatus", required = false) String strStatus,
                            @RequestParam(name = "keyword", required = false) String keyword,
                            @RequestParam(name = "strStartDate", required = false) String strStartDate,
                            @RequestParam(name = "strEndDate", required = false) String strEndDate) {

        ModelAndView mav = new ModelAndView();
        List<TaskForm> sortDate = null;
        try {
            sortDate = taskService.findSortTasks(strStatus, keyword, strStartDate, strEndDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String errorMessage = (String) session.getAttribute("validationError");
        session.invalidate();

        // 現在の日付を取得（String型）
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strToday = sdf.format(today);

        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("today", strToday);
        mav.addObject("errorMessage", errorMessage);
        mav.addObject("strStatus", strStatus);
        mav.addObject("keyword", keyword);
        mav.addObject("strStartDate", strStartDate);
        mav.addObject("strEndDate", strEndDate);
        mav.addObject("contents", sortDate);
        return mav;
    }

    /*
     * タスク登録画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        TaskForm taskForm = new TaskForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", taskForm);
        return mav;
    }

    /*
     * タスク登録処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@Validated @ModelAttribute("formModel") TaskForm taskForm, BindingResult result) throws ParseException {

        String text = taskForm.getContent();

        List<String> errorList = new ArrayList<String>();
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
        }
        if(StringUtils.hasText(text)){
            String strRemoveNewline = text.replaceAll("\\r\\n|\\r|\\n","");
            if(strRemoveNewline.length() > 140){
                errorList.add("タスクは140文字以内で入力してください");
            }
        }
        if (StringUtils.hasText(taskForm.getLimitDate()) && !checkDate(taskForm.getLimitDate())) {
            errorList.add("無効な日付です");
        }
        if (errorList.size() != 0) {
            ModelAndView mav = new ModelAndView();
            // 画面遷移先を指定
            mav.setViewName("/new");
            mav.addObject("formModel", taskForm);
            mav.addObject("errorMessages", errorList);
            return mav;
        }
        taskForm.setStatus((short) 1);
        taskService.saveTask(taskForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 日時チェック
     */
    private boolean checkDate(String date) throws ParseException {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date cdDate = sdf.parse(date);
        String strToday = sdf.format(today);
        if (!strToday.equals(date) && cdDate.before(today)) {
            return false;
        }
        return true;
    }

    /*
     * タスク削除機能
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return new ModelAndView("redirect:/");
    }

    /*
     * タスク編集画面表示処理
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable String id) {
        Integer intId = null;
        ModelAndView mav = new ModelAndView();
        TaskForm task = null;
        if (StringUtils.hasText(id) && id.matches("^[0-9]+$")) {
            intId = Integer.parseInt(id);
            task = taskService.editTask(intId);
        }
        if (task == null) {
            String errorMessage = "不正なパラメータが入力されました";
            session.setAttribute("validationError", errorMessage);
            // rootへリダイレクト
            return new ModelAndView("redirect:/");
        }
        mav.addObject("formModel", task);
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * タスク編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @Validated @ModelAttribute("formModel") TaskForm task,
                                      BindingResult result) throws ParseException {
        String text = task.getContent();
        List<String> errorList = new ArrayList<String>();
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorList.add(error.getDefaultMessage());
            }
        }
        if(StringUtils.hasText(text)){
            String strRemoveNewline = text.replaceAll("\\r\\n|\\r|\\n","");
            if(strRemoveNewline.length() > 140){
                errorList.add("タスクは140文字以内で入力してください");
            }
        }
        if (StringUtils.hasText(task.getLimitDate()) && !checkDate(task.getLimitDate())) {
            errorList.add("無効な日付です");
        }
        if (errorList.size() != 0) {
            ModelAndView mav = new ModelAndView();
            // 画面遷移先を指定
            mav.setViewName("/edit");
            mav.addObject("formModel", task);
            mav.addObject("errorMessages", errorList);
            return mav;
        }
        TaskForm taskBefore = taskService.editTask(id);
        // UrlParameterのidを更新するentityにセット
        task.setId(id);
        task.setStatus(taskBefore.getStatus());
        // 編集した投稿を更新
        taskService.saveTask(task);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * ステータス変更処理
     */
    @PostMapping("/statusUpdate")
    public ModelAndView updateStatus(@RequestParam(name = "statusValue", required = false) short statusValue,
                                     @RequestParam(name = "id", required = false) int id) throws ParseException {
        TaskForm task = taskService.editTask(id);
        task.setStatus(statusValue);
        // 投稿をテーブルに格納
        taskService.saveStatus(task);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}