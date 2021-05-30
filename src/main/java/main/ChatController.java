package main;

import main.model.Message;
import main.model.User;
import main.repos.MessageRepository;
import main.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import response.AddMessageResponce;
import response.AuthResponse;
import response.MessageResponse;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class ChatController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

    /**
     * Получение статуса авторизации
     * @return возвращает результат true or false
     */
    @GetMapping(path = "/api/auth")
    public AuthResponse auth() {
        AuthResponse response = new AuthResponse();
        String sessionId = getSessionId();
        User user = userRepository.getBySessionId(sessionId);
        response.setResult(user != null);
        if(user != null) response.setName(user.getName());
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

    /**
     * Получение списка сообщений
     * @return возвращает список всех сообщений
     */
    @GetMapping(path = "/api/messages")
    public HashMap<String, List> getMessages() {
        ArrayList<MessageResponse> messagesList =
                new ArrayList<>();
        Iterable<Message> messages = messageRepository.findAll();
        for(Message message : messages) {
            MessageResponse messageItem = new MessageResponse();
            messageItem.setName(message.getUser().getName());
            messageItem.setTime(
                    formatter.format(message.getSendTime())
            );
            messageItem.setText(message.getMessage());
            messagesList.add(messageItem);
        }

        HashMap<String, List> response = new HashMap<>();
        response.put("messages", messagesList);
        return response;
    }


    @PostMapping(path = "/api/messages")
    public AddMessageResponce sendMessages(HttpServletRequest request) {
        String mess = request.getParameter("text");
        String sessionId = getSessionId();
        User user = userRepository.getBySessionId(sessionId);

        Date date = new Date();

        Message message = new Message();
        message.setMessage(mess);
        message.setSendTime(new Date());
        message.setUser(user);
        messageRepository.save(message);

        AddMessageResponce response = new AddMessageResponce();
        response.setResult(true);
        response.setTime(formatter.format(date));
        return response;

    }


    private String getSessionId(){
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }
}
