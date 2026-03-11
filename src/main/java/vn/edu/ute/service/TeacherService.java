package vn.edu.ute.service;

import vn.edu.ute.model.Teacher;
import java.util.List;

public interface TeacherService {
    Teacher createTeacherAccount(Teacher teacherInfo, String username, String initialPassword) throws Exception;
    List<Teacher> getAllTeachers() throws Exception;
    Teacher updateTeacher(Teacher teacher) throws Exception;
    void deleteTeacher(Long id) throws Exception;
    List<Teacher> filterTeachers(String keyword, vn.edu.ute.common.enumeration.Status status, String specialty) throws Exception;
}
