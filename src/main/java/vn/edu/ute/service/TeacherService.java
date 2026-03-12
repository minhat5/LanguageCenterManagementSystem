package vn.edu.ute.service;

import vn.edu.ute.model.Teacher;
import vn.edu.ute.common.enumeration.Status;
import java.util.List;

public interface TeacherService {
    List<Teacher> getAll() throws Exception;
    Teacher createTeacherAccount(Teacher teacherInfo, String username, String initialPassword) throws Exception;
    List<Teacher> getAllTeachers() throws Exception;
    Teacher updateTeacher(Teacher teacher) throws Exception;
    void deleteTeacher(Long id) throws Exception;
    List<Teacher> filterTeachers(String keyword, Status status, String specialty) throws Exception;
}