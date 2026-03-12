package vn.edu.ute.service;

import vn.edu.ute.model.Student;
import java.util.List;

public interface StudentService {
    Student registerStudentAccount(Student studentInfo, String username, String password) throws Exception;
    List<Student> getAllStudents() throws Exception;
    Student updateStudent(Student student) throws Exception;
    void deleteStudent(Long id) throws Exception;
    List<Student> filterStudents(String keyword, vn.edu.ute.common.enumeration.Gender gender, vn.edu.ute.common.enumeration.Status status) throws Exception;
}
