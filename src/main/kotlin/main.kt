
fun main() {
    ChatService.addMessage(1, Message(type = "in", text = "Hello"))
    ChatService.addMessage(1, Message(type = "out", text = "Hi"))
    ChatService.addMessage(1, Message(type = "in", text = "how are you?"))
    ChatService.addMessage(2, Message(type = "out", text = "OK"))
//    ChatService.addMessage(2, Message(type = "in", text = ":)))"))
//    ChatService.addMessage(1, Message(type = "in", text = "OK"))
//    ChatService.print()
    println(ChatService.lastMessages())
    //ChatService.getMessages(1, 1)
    ChatService.deleteMessage(2, 1)
    println(ChatService.getChats())
    println(ChatService.getUnreadChatsCount())
    println(ChatService.getMessages(1, 1))
}

class NotFoundException(message: String) : RuntimeException(message)

data class Message(
        var messageId: Int? = null,
        val type: String,
        var text: String,
        var deleted: Boolean = false,
        var read: Boolean = false
)

data class Chat(
        var chatId: Int? = null,
        var messages: MutableList<Message> = mutableListOf()
)

object ChatService {
    private var lastChatId = 0
    private var lastMessageId = 0
    private var chats = mutableMapOf<Int, Chat>()

    fun addMessage(userId: Int, message: Message) {
        chats.getOrPut(userId) { Chat(chatId = ++lastChatId) }.messages += message.copy(messageId = ++lastMessageId)
    }

    fun lastMessages() = chats.values.asSequence()
            .map { it.messages.last() }
            .filter { !it.deleted }
            .joinToString(separator = "\n") { it.text }
            .ifEmpty { throw NotFoundException("Нет чатов") }

    fun getMessages(userId: Int, messageId: Int): List<Message> =
            chats[userId]
                    .let { it?.messages ?: throw NotFoundException("Нет чатов") }
                    .asSequence()
                    .filter { !it.deleted && it.type == "in" }
                    .drop(messageId)
                    .onEach { it.read = true }
                    .toList()
    fun getUnreadChatsCount() = chats.values
            .count { it -> it.messages.any { !it.read && it.type == "in" } }
    fun getChats(): Map<Int, Chat> {
        return chats
    }

    fun deleteMessage(chatId: Int, messageId: Int): Boolean {
        chats[chatId]
                .let { it?.messages ?: throw NotFoundException("Нет чатов") }
                .take(messageId)
                .onEach { it.deleted = true }
        return true
    }

    //8. Удалить чат, т. е. целиком удалить всю переписку.
    fun deleteChat(chatId: Int): Boolean {
        chats.remove(chatId) ?: throw NotFoundException("Нет чатов")
        return true
    }


    fun editMessage(chatId: Int, messageId: Int, newText: String): Boolean {
        chats[chatId]
                .let { it?.messages ?: throw NotFoundException("Нет чатов") }
                .take(messageId)
                .onEach { it.text = newText }
        return true
    }

    fun print() {
        println(chats)
    }

    fun clear() {
        chats = mutableMapOf<Int, Chat>()
        lastChatId = 0
        lastMessageId = 0
    }

}






