import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.util.*

fun main(args: Array<String>) {
    //args.set(0,"NjIzOTQ5NjY2MTM1ODM0NjQz.XYJ4dw.ZgORKmgJs8R2PEkPY4uQzhMh27U")
    if (args.isEmpty()) {
        println("No arguments given. Aborting.")
        return
    }

    val builder = JDABuilder(args[0])
    builder.setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE))
    builder.setBulkDeleteSplittingEnabled(false)
    builder.setActivity(Activity.watching("Boss Schedule"))
    builder.addEventListeners(ReadyListener(), MessageListener())
    val jda = builder.build()
    jda.awaitReady()

}