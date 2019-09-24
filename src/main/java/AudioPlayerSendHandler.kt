
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer


class AudioPlayerSendHandler
/**
 * @param audioPlayer Audio player to wrap.
 */
(private val audioPlayer: AudioPlayer) : AudioSendHandler {
    private val buffer: ByteBuffer
    private val frame: MutableAudioFrame

    init {
        this.buffer = ByteBuffer.allocate(1024)
        this.frame = MutableAudioFrame()
        this.frame.setBuffer(buffer)
    }

    override fun canProvide(): Boolean {
        // returns true if audio was provided
        return audioPlayer.provide(frame)
    }

    override fun provide20MsAudio(): ByteBuffer {
        // flip to make it a read buffer
        return buffer.flip() as ByteBuffer
    }

    override fun isOpus(): Boolean {
        return true
    }
}