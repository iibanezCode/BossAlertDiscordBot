import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.nio.ByteBuffer
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import sun.audio.AudioPlayer.player






class ReadyListener : EventListener {

    override fun onEvent(event: GenericEvent) {

        if (event is ReadyEvent)
            System.out.println("API is ready!")
    }
}

class MessageListener : ListenerAdapter() {
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
            val playerManager = DefaultAudioPlayerManager()
            AudioSourceManagers.registerRemoteSources(playerManager)
            val player = playerManager.createPlayer()
            val trackScheduler = TrackScheduler(player)
            player.addListener(trackScheduler)

            // MySendHandler should be your AudioSendHandler implementation
            manager.sendingHandler = CustomAudioSendHandler(player)
            // Here we finally connect to the target voice channel
            // and it will automatically start pulling the audio from the MySendHandler instance
            manager.openAudioConnection(channel)
        }else if (event.message.contentRaw.startsWith(".leave")){
            event.guild.audioManager.closeAudioConnection()
        }else return
    }

}

class CustomAudioSendHandler(audioPlayer: AudioPlayer) : AudioSendHandler{

    private val audioPlayer: AudioPlayer? = audioPlayer
    private var lastFrame: AudioFrame? = null

    override fun provide20MsAudio(): ByteBuffer? {
        return ByteBuffer.wrap(lastFrame?.data)
    }

    override fun canProvide(): Boolean {
        if (audioPlayer != null) {
            lastFrame = audioPlayer.provide()
        }
        return lastFrame != null
    }

    override fun isOpus(): Boolean {
        return true
    }
}
