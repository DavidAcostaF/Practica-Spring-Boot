package com.example.demo.AUTH;

import com.example.demo.dtos.LoginDTO;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    //@Autowired
    private final StudentRepository studentRepository;

    //@Autowired
    private final JWTUtilityService jwtUtilityService;

    @Autowired
    public AuthService(JWTUtilityService jwtUtilityService, StudentRepository studentRepository) {
        this.jwtUtilityService = jwtUtilityService;
        this.studentRepository = studentRepository;
    }

    public HashMap<String,String> login(LoginDTO login) throws Exception {
        try {
            HashMap<String, String> jwt = new HashMap<>();

            Optional<Student> student = studentRepository.findStudentByEmail(login.getEmail());
            if (student.isEmpty()) {
                jwt.put("error", "Student not registered!");
                return jwt;
            }
            if(verifyPassword(login.getPassword(), student.get().getPassword())) {
                jwt.put("jwt", jwtUtilityService.generateJWT(student.get().getId()));

            }else{
                jwt.put("error", "Authentication failed");
            }
            return jwt;

        } catch (Exception e) {
            throw new Exception(e.toString());
        }

    }

    public Student register(Student student)throws Exception{
        try {
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
            if (studentOptional.isPresent()) {
                throw new Exception("Student already registered!");
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            student.setPassword(encoder.encode(student.getPassword()));
            return studentRepository.save(student);

        }catch (Exception e){
            throw new Exception(e.toString());
        }
    }

    private boolean verifyPassword(String enteredPassword, String storedPassword){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(enteredPassword,storedPassword);

    }
}
