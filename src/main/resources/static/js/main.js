$(function(){
    var userName = 'Юзер';

    let initChat = function() {
        loadUsers();
        loadMessages();
    };

    let loadUsers = function() {
        $.get('/api/users', function(response){
            let users = response.users;
            let usersList = $('.users-list');
            for(let i in users) {
                let userItem = $('<div class="user-item"></div>');
                userItem.text(users[i].name);
                usersList.append(userItem);
            }
        });
    };

    let loadMessages = function(){
        $.get('/api/messages', function (response){
            let messages = response.messages;
            let messagesList = $('.messages-list');
            for(let i in messages) {
                let messageItem = $('<div class="message"><b>' +
                    messages[i].time + "&nbsp;" +
                    messages[i].name +
                    '</b> ' + messages[i].text + '</div>');
                messagesList.append(messageItem);
            }
        })
    }

    let authUser = function() {
        let name = prompt('Введите имя пользователя:');
        userName = name;
        $.post('/api/users', {'name': name}, function(response){
            if(response.result) {
                initChat();
            } else {
                alert('Что-то пошло не так :(');
            }
        });
    };

    let checkAuthStatus = function() {
        $.get('/api/auth', function(response){
            if(response.result) {
                userName = response.name;
                initChat();
            } else {
                authUser();
            }
        });
    };

    checkAuthStatus();

    $('.send-message').on('click', function (){
          let message = $('.message-text').val();
          $.post('/api/message'), {'text': message}, function (responce){
              if(responce.result) {
                  $('.message-text').val('');
              } else {
                  alert('Что-то пошло не так :(');
              }
          }
    });
});