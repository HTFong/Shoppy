package com.shoppy.admin.user;

import com.shoppy.admin.FileUploadUtil;
import com.shoppy.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
    public String listFirstPage(Model model) {
        return listAllByPage(model, 1,"id", "asc", null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listAllByPage(Model model, @PathVariable("pageNum") int pageNumber,
                                @Param("sortField") String sortField,
                                @Param("sortDir") String sortDir,
                                @Param("keyword") String keyword) {
        Page<User> page = service.listAllByPage(pageNumber, 4, sortField,sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNumber - 1) * 4 + 1;
        long endCount = startCount + 4 - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc":"asc");
        model.addAttribute("keyword",keyword);

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
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam(name = "fileImg") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            String uploadDir = "user-photos/" + service.save(user).getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
                service.save(user);
            }
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + user.getEmail().split("@")[0];
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = service.getById(id);
            model.addAttribute("user", user);
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
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String changeEnableUser(Model model, @PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
        service.changeEnabled(status, id);
        redirectAttributes.addFlashAttribute("message", "User ID: " + id + " change enabled to " + status + " successfully");
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(service.listAll(),response);
    }
    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(service.listAll(),response);
    }
}