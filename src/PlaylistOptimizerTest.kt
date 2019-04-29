import org.junit.jupiter.api.DisplayName
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test as test

class PlaylistOptimizerTest {

    @DisplayName("Test Input")
    @test fun fillPlaylist() {
        val playlist = PlaylistOptimizer("songlist")

        assertEquals(166, playlist.optimalPlaylist.rating)
    }
}