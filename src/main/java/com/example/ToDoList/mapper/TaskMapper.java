package com.example.ToDoList.mapper;

import com.example.ToDoList.repository.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TaskMapper {
    List<Task> getTask();

    /**
     * JPA: findById
     * IDをキーにタスクを1件取得します。
     * @param id タスクID
     * @return 存在すればTaskオブジェクト、なければnull
     */
    Task findById(@Param("id") Integer id);

    /**
     * JPA: save (新規登録用)
     * 新しいタスクをデータベースに登録します。
     *
     * @param task 登録するタスク情報
     */
    void insert(Task task);

    /**
     * JPA: save (更新用)
     * 既存のタスク情報を更新します。
     *
     * @param task 更新するタスク情報
     */
    void update(Task task);

    /**
     * JPA: deleteById
     * IDをキーにタスクを削除します。
     *
     * @param id 削除するタスクのID
     */
    void deleteById(@Param("id") Integer id);

    /**
     * JPA: findByLimitDateBetweenOrderByLimitDate
     * 期間を指定してタスクを検索し、期限日でソートします。
     */
    List<Task> findByLimitDateBetween(@Param("start") Date start, @Param("end") Date end);

    /**
     * JPA: findByStatusAndLimitDateBetweenOrderByLimitDate
     * ステータスと期間を指定してタスクを検索します。
     */
    List<Task> findByStatusAndLimitDateBetween(@Param("status") Short status, @Param("start") Date start, @Param("end") Date end);

    /**
     * JPA: findByContentContainingAndLimitDateBetweenOrderByLimitDate
     * キーワード（部分一致）と期間を指定してタスクを検索します。
     */
    List<Task> findByContentContainingAndLimitDateBetween(@Param("keyword") String keyword, @Param("start") Date start, @Param("end") Date end);

    /**
     * JPA: findByStatusAndContentContainingAndLimitDateBetweenOrderByLimitDate
     * ステータス、キーワード、期間のすべてを指定してタスクを検索します。
     */
    List<Task> findByStatusAndContentContainingAndLimitDateBetween(
            @Param("status") Short status,
            @Param("keyword") String keyword,
            @Param("start") Date start,
            @Param("end") Date end
    );
}
