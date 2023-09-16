import no.sportradar.dashboard.FootballMatch

interface FootballMatchService {
    fun startMatch(homeTeamName: String, awayTeamName: String)
    fun updateMatchDetails(football: FootballMatch)
    fun finishMatch(football: FootballMatch)
    fun getSummaryDashboard(): List<FootballMatch>
}