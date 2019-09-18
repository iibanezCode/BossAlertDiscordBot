import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.AudioManager
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import sun.audio.AudioPlayer
import java.nio.ByteBuffer


class ReadyListener() : EventListener {

    override fun onEvent(event: GenericEvent) {

        if (event is ReadyEvent)
            System.out.println("API is ready!");
    }
}

class MessageListener() : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        // This makes sure we only execute our code when someone sends a message with "!play"
        if (event.message.contentRaw.startsWith(".join")) {
            // Now we want to exclude messages from bots since we want to avoid command loops in chat!
            // this will include own messages as well for bot accounts
            // if this is not a bot make sure to check if this message is sent by yourself!
            if (event.author.isBot) return
            val guild = event.guild
            val channel: VoiceChannel = event.member!!.voiceState!!.channel!!
            val manager = guild.audioManager

            // MySendHandler should be your AudioSendHandler implementation
            manager.sendingHandler = CustomAudioSendHandler()
            // Here we finally connect to the target voice channel
            // and it will automatically start pulling the audio from the MySendHandler instance
            manager.openAudioConnection(channel)
        }else if (event.message.contentRaw.startsWith(".leave")){
            event.guild.audioManager.closeAudioConnection()
        }else return
    }

}

class CustomAudioSendHandler : AudioSendHandler{
    
    override fun provide20MsAudio(): ByteBuffer? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canProvide(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
