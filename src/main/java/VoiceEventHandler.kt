import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.AudioManager
import java.util.*


class VoiceEventHandler : ListenerAdapter() {
    private  var playerManager: AudioPlayerManager
    private  var musicManagers: MutableMap<Long, GuildMusicManager>

    init {
        this.musicManagers = HashMap()

        this.playerManager = DefaultAudioPlayerManager()
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
    }

    @Synchronized
    private fun getGuildAudioPlayer(guild: Guild): GuildMusicManager {
        val guildId = java.lang.Long.parseLong(guild.getId())
        var musicManager: GuildMusicManager? = musicManagers[guildId]

        if (musicManager == null) {
            musicManager = GuildMusicManager(playerManager)
            musicManagers[guildId] = musicManager
        }

        guild.getAudioManager().setSendingHandler(musicManager.sendHandler)
        return musicManager
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val command = event.message.contentRaw.split(" ".toRegex(), 2).toTypedArray()

        if ("~play" == command[0] && command.size == 2) {
            loadAndPlay(event.channel, command[1])
        } else if ("~skip" == command[0]) {
            skipTrack(event.channel)
        } else if ("~join" == command[0]) {
            var channel: VoiceChannel = getChannelToJoin(event.member)
            event.guild.audioManager.openAudioConnection(channel)
        }else if("~bye" == command[0]){
            event.guild.audioManager.closeAudioConnection()
        }

        super.onGuildMessageReceived(event)
    }

    private fun getChannelToJoin(member: Member?): VoiceChannel {
        if (member != null) {
            member.voiceState?.let {
                return if (it.inVoiceChannel()) {
                    it.channel!!
                } else {
                    throw Throwable()
                }
            } ?: run { throw Throwable() }
        } else {
            throw Throwable()
        }
    }


    private fun loadAndPlay(channel: TextChannel, trackUrl: String) {
        val musicManager = getGuildAudioPlayer(channel.guild)

        playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue()

                play(channel.guild, musicManager, track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                var firstTrack = playlist.getSelectedTrack()

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0)
                }

                channel.sendMessage("Adding to queue " + firstTrack!!.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue()

                play(channel.guild, musicManager, firstTrack)
            }

            override fun noMatches() {
                channel.sendMessage("Nothing found by $trackUrl").queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                channel.sendMessage("Could not play: " + exception.message).queue()
            }
        })
    }

    private fun play(guild: Guild, musicManager: GuildMusicManager, track: AudioTrack?) {
        connectToFirstVoiceChannel(guild.getAudioManager())

        musicManager.scheduler.queue(track!!)
    }

    private fun skipTrack(channel: TextChannel) {
        val musicManager = getGuildAudioPlayer(channel.guild)
        musicManager.scheduler.nextTrack()

        channel.sendMessage("Skipped to next track.").queue()
    }

    private fun connectToFirstVoiceChannel(audioManager: AudioManager) {
        if (!audioManager.isConnected && !audioManager.isAttemptingToConnect) {
            for (voiceChannel in audioManager.guild.voiceChannels) {
                audioManager.openAudioConnection(voiceChannel)
                break
            }
        }
    }
}