package no.sportradar.dashboard

import java.time.LocalDateTime

data class FootballMatch(
    val homeTeamName: String,
    val homeTeamPoints: Int = 0,
    val awayTeamName: String,
    val awayTeamPoints: Int = 0,
    val created: LocalDateTime = LocalDateTime.now()
)
