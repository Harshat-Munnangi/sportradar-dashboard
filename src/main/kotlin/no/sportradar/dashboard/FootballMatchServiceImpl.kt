package no.sportradar.dashboard

import FootballMatchService

internal class FootballMatchServiceImpl : FootballMatchService {

    private val footballTeams = mutableListOf<FootballMatch>()

    override fun startMatch(homeTeamName: String, awayTeamName: String) {
        val newMatch = FootballMatch(homeTeamName = homeTeamName, awayTeamName = awayTeamName)
        when {
            homeTeamName.isBlank() -> throw IllegalArgumentException("Home team names should not be blank!")
            awayTeamName.isBlank() -> throw IllegalArgumentException("Away team names should not be blank!")
            newMatch.isMatchAlreadyExists() -> throw MatchAlreadyExistsException("Already a match existing with the same team names!")
            newMatch.isAnyTeamBusy() -> throw TeamAlreadyBusyException("A team is busy in another match. Can't start a match until it finished!")
            else -> footballTeams.add(newMatch)
        }
    }

    override fun updateMatchDetails(football: FootballMatch) {
        football.findExistingMatch()
            ?.let {
                footballTeams.remove(it)
                footballTeams.add(it.copy(homeTeamPoints = football.homeTeamPoints, awayTeamPoints = football.awayTeamPoints))
            } ?: throw NoMatchFoundException("No match found to update with the specified details: $football")
    }

    override fun finishMatch(football: FootballMatch) {
        football.findExistingMatch()
            ?.let { footballTeams.remove(it) }
            ?: throw NoMatchFoundException("No processing match found to finish with the specified details: $football")
    }

    override fun getSummaryDashboard(): List<FootballMatch> =
        footballTeams
            .sortedWith(
                compareByDescending<FootballMatch> { it.homeTeamPoints + it.awayTeamPoints }
                    .thenByDescending { it.created }
            )

    private fun FootballMatch.isMatchAlreadyExists(): Boolean =
        footballTeams.any { it.homeTeamName == homeTeamName && it.awayTeamName == awayTeamName }

    private fun FootballMatch.isAnyTeamBusy(): Boolean =
        footballTeams.any {
            it.homeTeamName == homeTeamName || it.homeTeamName == awayTeamName
                    || it.awayTeamName == homeTeamName || it.awayTeamName == awayTeamName
        }

    private fun FootballMatch.findExistingMatch(): FootballMatch? =
        footballTeams.find { it.homeTeamName == homeTeamName && it.awayTeamName == awayTeamName }
}