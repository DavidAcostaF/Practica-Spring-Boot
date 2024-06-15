package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("")
public class StudentViewController {

    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentViewController(StudentService studentService, PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
    }

    // La clase model se utiliza para transferir objetos del controller a la vista
    @GetMapping("/list")
    public String allStudents(Model model) {
        List<Student> students = studentService.getStudents();
        model.addAttribute("students",students);
        return "students/students_list";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("student",new Student());
        return "register";
    }


    @PostMapping("/register")
    public String registerStudent(@ModelAttribute("student") Student student,
                               Model model) {


//        if (studentService.getStudentByEmail(student.getEmail())!=null) {
//            model.addAttribute("emailError", "Email already exists");
//            return "register";
//        }

        student.setPassword(passwordEncoder.encode(student.getPassword()));

        studentService.addNewStudent(student);
        return "redirect:/login"; // Redirect to login page after successful registration
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("student",new Student());
        return "login";
    }

    @PostMapping("/login")
    public String loginStudent(@ModelAttribute("student") Student student){
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return "redirect:/list";
    }

    @GetMapping("/update_student/{id}")
    public String studentFormEdit(@PathVariable Long id, Model model) {

        Student student = studentService.getStudent(id);
        model.addAttribute("student",student);
        System.out.println(student.getDob());
        model.addAttribute("url","/update_student/"+id);

        return "students/update_student_form";
    }

    @PostMapping("/update_student/{id}")
    public String updateStudent(@PathVariable Long id ,@ModelAttribute Student student) {
        studentService.updateStudent(id,student);
        return "redirect:/list";
    }

    @GetMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/list";
    }
}
