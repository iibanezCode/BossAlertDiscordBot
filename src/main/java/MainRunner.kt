
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDABuilder

@Throws(Exception::class)
fun main(args: Array<String>) {
    JDABuilder(AccountType.BOT)
            .setToken("NjIzOTQ5NjY2MTM1ODM0NjQz.XYJ4dw.ZgORKmgJs8R2PEkPY4uQzhMh27U")
            .addEventListeners(VoiceEventHandler())
            .build()
}
