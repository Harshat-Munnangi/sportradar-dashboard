package no.sportradar.dashboard

import java.lang.IllegalArgumentException
import java.time.LocalDateTime

data class FootballMatch(
    val homeTeamName: String,
    val homeTeamPoints: Int = 0,
    val awayTeamName: String,
    val awayTeamPoints: Int = 0,
    val created: LocalDateTime = LocalDateTime.now()
)

internal class MatchAlreadyExistsException(message: String = "") : IllegalArgumentException(message)

internal class TeamAlreadyBusyException(message: String = "") : IllegalArgumentException(message)

internal class NoMatchFoundException(message: String = "") : IllegalArgumentException(message)