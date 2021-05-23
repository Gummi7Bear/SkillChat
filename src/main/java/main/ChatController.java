package main;

import main.model.User;
import main.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class ChatController {
    @Autowired
    private UserRepository userRepository;

    /**
     * Получение статуса авторизации
     * @return возвращает результат true or false
     */
    @GetMapping(path = "/api/auth")
    public HashMap<String, Boolean> auth() {
        HashMap<String, Boolean> response = new HashMap<>();
        String sessionId = getSessionId();
        User user = userRepository.getBySessionId(sessionId);
        response.put("result", user != null);
        return response;
    }

    /**
     * Создание пользователя при логине
     * @param request - параметры из запроса, содержат данные о новом пользователе чата
     * @return возвращает результат true
     */
    @PostMapping(path = "/api/users")
    public HashMap <String, Boolean> addUser(HttpServletRequest request) {

        String name = request.getParameter("name");
        String sessionId = getSessionId();
        User user = new User();
        user.setName(name);
        user.setRegTime(new Date());
        user.setSessionId(sessionId);
        userRepository.save(user);
        HashMap<String, Boolean> response = new HashMap<>();
        response.put("result", true);
        return response;
    }

    /**
     * Получение списка пользователей
     * @return возвращает список всех пользователей
     */
    @GetMapping(path = "/api/users")
    public HashMap<String, List<User>> getUsers() {

        List<User> userList = (List<User>) userRepository.findAll();
        HashMap<String, List<User>> response = new HashMap<>();
        response.put("users", userList);
        return response;
    }


    private String getSessionId(){
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }
}
