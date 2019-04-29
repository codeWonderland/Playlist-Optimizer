import java.io.File

class PlaylistOptimizer(inFile: String) {
    var capacity = 0
    var numSongs = 0
    private var fullPlaylist: List<Song>? = null
    var optimalPlaylist: Playlist = Playlist()

    init {
        readPlaylist(inFile)

        calcOptimalPlaylist()

        writePlaylist("ult_playlist")
    }

    private fun readPlaylist(inFile: String) {
        File(inFile).readLines()
            .forEachIndexed { index, line ->
                when (index) {
                    0 -> capacity = line.toInt()
                    1 -> numSongs = line.toInt()
                    // item lines
                    else -> {
                        // if fullPlaylist has not been initialized
                        if (fullPlaylist == null) {
                            // we now have the number of fullPlaylist
                            // so we initialize it
                            fullPlaylist = List(numSongs) { Song() }
                        }

                        // split eat item into it's parts
                        val parts = line.split(' ')

                        // we only need parts 1 and 2, the name is irrelevant
                        fullPlaylist!![index - 2].name = parts[0]
                        fullPlaylist!![index - 2].length = parts[1].toInt()
                        fullPlaylist!![index - 2].rating = parts[2].toInt()
                    }
                }
            }
    }

    private fun calcOptimalPlaylist() {
        // initialize grid
        val grid = Array(numSongs) { Array(capacity, init = { Playlist() }) }
        // Index through the fullPlaylist
        fullPlaylist?.forEachIndexed { i, song ->
            // Index through the knapsack sizes
            val songRatings: List<Playlist> = grid[i].mapIndexed { j, _ ->
                // If we are on the first row
                if (i == 0) {
                    // if we can hold the current song in our knapsack
                    if (song.length <= j) {
                        // return the song's rating
                        Playlist(arrayOf(song.name), song.rating)

                    } else {
                        // return 0
                        Playlist()
                    }
                } else {
                    // room left over
                    val spareRoom = j - song.length

                    when {
                        // we have some spare room
                        spareRoom > 0 -> max(
                            // previous rating
                            grid[i - 1][j],
                            // current song rating + rating of remaining space
                            grid[i - 1][j - song.length] + song
                        )
                        // we have filled up our knapsack
                        spareRoom == 0 -> max(
                            // previous rating
                            grid[i - 1][j],
                            // current rating
                            song
                        )
                        // we don't have room for this song
                        else -> grid[i - 1][j]
                    }
                }
            }

            grid[i] = songRatings.toTypedArray()
        }

        // max rating is last cell
        optimalPlaylist = grid.last().last()
    }

    private fun writePlaylist(outFile: String) {
        File(outFile).writeText(optimalPlaylist.export())
    }

    class Song {
        var name: String = ""
        var length: Int = 0
        var rating: Int = 0
    }

    class Playlist(
        var playlist: Array<String> = Array(0, init = {""}),
        var rating: Int = 0
    ) {
        operator fun plus(song: Song): Playlist =
            Playlist(
                playlist.plus(song.name),
                rating + song.rating
            )

        fun export(): String = playlist.joinToString("\n")
    }

    private fun max(left: Playlist, right: Playlist): Playlist {
        if (left.rating >= right.rating) {
            return left
        }

        return right
    }

    private fun max(left: Playlist, right: Song): Playlist {
        if (left.rating >= right.rating) {
            return left
        }

        return Playlist(arrayOf(right.name), right.rating)
    }

}