package com.footbase.repository;

import com.footbase.entity.EditorYoneticileri;
import com.footbase.entity.EditorYoneticileriId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EditorYoneticileriRepository extends JpaRepository<EditorYoneticileri, EditorYoneticileriId> {

       EditorYoneticileri findByEditorId(Long editorId);

       List<EditorYoneticileri> findByAdminId(Long adminId);

       @Query("SELECT DISTINCT ey FROM EditorYoneticileri ey " +
                     "LEFT JOIN FETCH ey.editor " +
                     "LEFT JOIN FETCH ey.admin " +
                     "WHERE ey.adminId = :adminId")
       List<EditorYoneticileri> findByAdminIdWithDetails(Long adminId);

       @Query("SELECT DISTINCT ey FROM EditorYoneticileri ey " +
                     "LEFT JOIN FETCH ey.editor " +
                     "LEFT JOIN FETCH ey.admin " +
                     "WHERE ey.editorId = :editorId")
       EditorYoneticileri findByEditorIdWithDetails(Long editorId);
}
