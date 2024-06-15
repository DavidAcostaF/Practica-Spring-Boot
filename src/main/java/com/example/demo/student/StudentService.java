package com.example.demo.student;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentService(StudentRepository studentRepository,PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (!exists) {
            throw new IllegalStateException("student does not exist");
        }
        studentRepository.deleteById(studentId);
    }
    @Transactional
    public void updateStudent(Long studentId,String name,String email) {
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new IllegalStateException("student not found"));

        if (name!=null &&!name.isEmpty()&& Objects.equals(name,student.getName())) {
            student.setName(name);
        }

        if (email!=null && !email.isEmpty()&& Objects.equals(email,student.getEmail())) {
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
            if (studentOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            student.setEmail(email);
        }
        studentRepository.save(student);
    }

    public Student getStudent(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(()-> new IllegalStateException("student not found"));
    }


    public void updateStudent(Long studentId,Student student){
        Student studentObtained = studentRepository.findById(studentId).orElseThrow(()-> new IllegalStateException("student not found"));
        studentObtained.setName(student.getName());
        studentObtained.setEmail(student.getEmail());
        studentObtained.setDob(student.getDob());
        studentRepository.save(studentObtained);
    }

    public Student getStudentByEmail(String email) {
        return studentRepository.findStudentByEmail(email)
                .orElse(null); // Manejar el caso en que el usuario no sea encontrado
    }

//    public Student login(Student student) {
//        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
//        if (studentOptional.isPresent()) {
//            Student studentFound = studentOptional.get();
//            // Verificar si la contraseña proporcionada coincide con la contraseña almacenada codificada
//            if (passwordEncoder.matches(student.getPassword(), studentFound.getPassword())) {
//                return student; // Credenciales válidas, devolver el objeto Student
//            }
//        }
//        return null; // Credenciales inválidas o usuario no encontrado
//    }
}
