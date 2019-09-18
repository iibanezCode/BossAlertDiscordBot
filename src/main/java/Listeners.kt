import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter


class ReadyListener() : EventListener {

    override fun onEvent(event: GenericEvent) {

        if (event is ReadyEvent)
            System.out.println("API is ready!");
    }
}

class MessageListener() : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msg = event.message
        if (msg.contentRaw.equals("")) {
            val channel = event.channel
            val time = System.currentTimeMillis()
            channel.sendMessage("")
                    .queue { response /* => Message */ -> response.editMessageFormat(": %d ms", System.currentTimeMillis() - time).queue() }
        }
    }
}
