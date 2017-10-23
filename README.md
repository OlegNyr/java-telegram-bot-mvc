#Java telegram bot mvc 

Библиотека для разработки [Телеграмм бота](https://core.telegram.org/bots) 
используется фраемворк [Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
для взаимодействия с API телеграмма используется библиотека [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api)

Идея взята из https://habrahabr.ru/post/335490/ спасибо @PqDn, раелизация скопа юзера отсуда https://habrahabr.ru/post/335490/#comment_10359566 спасибо @eugenehr  

Пример использования в папке /sample

```java
@SpringBootApplication
@EnableTelegram
@BotController
public class SampleTelegramBotMvcMain implements TelegramMvcConfiguration {
   private static final Logger logger = LoggerFactory.getLogger(SampleTelegramBotMvcMain.class);

   public static void main(String[] args) {
        SpringApplication.run(SampleTelegramBotMvcMain.class);
    }

    @Override
    public void configuration(TelegramBotBuilder telegramBotBuilder) {
        telegramBotBuilder.token('ТОкен доступа').alias("myFirsBean");
    }

    @BotRequest("/start")
    BaseRequest hello(String text,
                      Long chatId,
                      TelegramRequest telegramRequest,
                      TelegramBot telegramBot,
                      Update update,
                      Message message,
                      Chat chat,
                      User user
    ) {
        logger.info("Text = {}", text);
        logger.info("ChatId or UserId = {}", chatId);
        logger.info("Telegram Request = {}", telegramRequest);
        logger.info("TelegramBot = {}", telegramBot);
        logger.info("Update = {}", update);
        logger.info("Message = {}", message);
        logger.info("Chat = {}", chat);
        logger.info("User = {}", user);

        return new SendMessage(chatId, "I test the bot");
    }
}
```
Что реализованно:
- Аннотация ```@BotController``` используется чтобы пометить класс обработчик
- Аннотация ```@BotRequest``` Принимает параметр 
   1) path - строка фильтрации сообщения понимает запись в форме ant, т.е. ```/run ** service```
   2) messageType - Енум который опредялет тип сообщения, пока реализован MESSAGE, COMMAND и несколько внутренних для провреки возможности       
- возращается строка либо наследник BaseRequest(в терминах API это запрос к телеграм серверу)
- библиотека так же может обрабытывать входящие параметры такие как String text полный текс пользователя к боту, Update, Message, и д.р.   




 
  