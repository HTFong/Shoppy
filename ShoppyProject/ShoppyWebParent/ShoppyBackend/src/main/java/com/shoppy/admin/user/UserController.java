package com.shoppy.admin.user;

import com.shoppy.admin.FileUploadUtil;
import com.shoppy.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("pageTitle", "Create new user");
        return "users";
    }
    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", service.listRoles());
        return "user_form";
    }
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam(name = "fileImg")MultipartFile multipartFile)  throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            String uploadDir = "user-photos/" + service.save(user).getId();
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return "redirect:/users";
    }
    @GetMapping("/users/edit/{id}")
    public String editUser(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = service.getById(id);
            model.addAttribute("user",user);
            model.addAttribute("pageTitle", "Edit user - ID:" + user.getId());
            model.addAttribute("listRoles", service.listRoles());
            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message", "User ID: " + id + " delete successfully");
        }catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }
    @GetMapping("/users/{id}/enabled/{status}")
    public String changeEnableUser(Model model, @PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
        service.changeEnabled(status,id);
        redirectAttributes.addFlashAttribute("message", "User ID: " + id + " change enabled to " + status + " successfully");
        return "redirect:/users";
    }
}