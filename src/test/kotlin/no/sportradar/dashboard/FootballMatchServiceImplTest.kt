package no.sportradar.dashboard

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FootballMatchServiceImplTest {

    private lateinit var footballMatchServiceImpl: FootballMatchServiceImpl

    @BeforeEach
    fun setUp() {
        footballMatchServiceImpl = FootballMatchServiceImpl()
    }

    @Test
    fun `should add teams to the dashboard with 0 as starting points` () {
        repeat(6) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        val matchList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(matchList.size).isEqualTo(6)
        matchList.forEach {
            assertThat(it.homeTeamPoints).isEqualTo(0)
            assertThat(it.awayTeamPoints).isEqualTo(0)
        }
    }

    @Test
    fun `should throw error if team name is empty` () {
        repeat(2) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        assertThrows<IllegalArgumentException> {
            footballMatchServiceImpl.startMatch("Home3", "")
        }
        footballMatchServiceImpl.startMatch("Home4", "Away4")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(3)
    }

    @Test
    fun `should throw error if match already exists` () {
        repeat(3) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        assertThrows<MatchAlreadyExistsException> { footballMatchServiceImpl.startMatch("Home1", "Away1") }
        footballMatchServiceImpl.startMatch("Home3", "Away3")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(4)
    }

    @Test
    fun `should throw error if any team is busy in other match` () {
        repeat(2) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        assertThrows<TeamAlreadyBusyException> { footballMatchServiceImpl.startMatch("Home3", "Away1") }
        footballMatchServiceImpl.startMatch("Home4", "Away4")
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(3)
    }

    @Test
    fun `should update a match score` () {
        repeat(2) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
        val modifiedMatch = FootballMatch("Home1", 4, "Away1", 7)
        footballMatchServiceImpl.updateMatchDetails(modifiedMatch)
        val updatedMatchList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(updatedMatchList.size).isEqualTo(2)
        updatedMatchList
            .first { it.homeTeamName == modifiedMatch.homeTeamName && it.awayTeamName == modifiedMatch.awayTeamName }
            .let {
                assertThat(it.homeTeamPoints).isEqualTo(modifiedMatch.homeTeamPoints)
                assertThat(it.awayTeamPoints).isEqualTo(modifiedMatch.awayTeamPoints)
            }
    }

    @Test
    fun `should throw error if attempt to update a non-existed match` () {
        repeat(2) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        val nonExistingMatch = FootballMatch("Home12", 5, "Away1", 7)
        assertThrows<NoMatchFoundException> { footballMatchServiceImpl.updateMatchDetails(nonExistingMatch) }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
    }

    @Test
    fun `should remove a match once it is finished` () {
        repeat(2) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
        footballMatchServiceImpl.finishMatch(FootballMatch(homeTeamName = "Home1", awayTeamName = "Away1"))
        val newMatchList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(newMatchList.size).isEqualTo(1)
        assertThat(newMatchList.first().homeTeamName).isEqualTo("Home0")
        assertThat(newMatchList.first().awayTeamName).isEqualTo("Away0")
    }

    @Test
    fun `should throw error if attempt to finish a non-existed match` () {
        repeat(2) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
        assertThrows<NoMatchFoundException> {
            footballMatchServiceImpl.finishMatch(FootballMatch(homeTeamName = "HomeTeam2", awayTeamName = "Away2"))
        }
        assertThat(footballMatchServiceImpl.getSummaryDashboard().size).isEqualTo(2)
    }

    @Test
    fun `should sort depending on total score` () {
        repeat(6) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        val dashboardList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(dashboardList.first().homeTeamName).isEqualTo("Home5")
        assertThat(dashboardList.last().homeTeamName).isEqualTo("Home0")
        val match1 = FootballMatch("Home0", 3, "Away0", 2) //5
        val match2 = FootballMatch("Home1", 6, "Away1", 7) //13
        val match3 = FootballMatch("Home2", 7, "Away2", 2) //9
        val match4 = FootballMatch("Home3", 2, "Away3", 9) //11
        val match5 = FootballMatch("Home4", 4, "Away4", 2) // 6
        val match6 = FootballMatch("Home5", 1, "Away5", 2) // 3
        footballMatchServiceImpl.updateMatchDetails(match3)
        footballMatchServiceImpl.updateMatchDetails(match5)
        footballMatchServiceImpl.updateMatchDetails(match1)
        footballMatchServiceImpl.updateMatchDetails(match4)
        footballMatchServiceImpl.updateMatchDetails(match2)
        footballMatchServiceImpl.updateMatchDetails(match6)
        val newDashboardList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(newDashboardList.first().homeTeamName).isEqualTo("Home1")
        assertThat(newDashboardList.last().awayTeamName).isEqualTo("Away5")
    }

    @Test
    fun `should sort depending on match start time if all total scores are equal` () {
        repeat(6) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        val dashboardList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(dashboardList.first().homeTeamName).isEqualTo("Home5")
        assertThat(dashboardList.last().homeTeamName).isEqualTo("Home0")
    }

    @Test
    fun `should sort depending on score and thereafter match start time` () {
        repeat(6) { footballMatchServiceImpl.startMatch("Home$it", "Away$it") }
        val match3 = FootballMatch("Home2", 3, "Away2", 2)
        val match5 = FootballMatch("Home4", 2, "Away4", 3)
        val match6 = FootballMatch("Home5", 1, "Away5", 3)
        footballMatchServiceImpl.updateMatchDetails(match3)
        footballMatchServiceImpl.updateMatchDetails(match5)
        footballMatchServiceImpl.updateMatchDetails(match6)
        val newDashboardList = footballMatchServiceImpl.getSummaryDashboard()
        assertThat(newDashboardList.size).isEqualTo(6)
        assertThat(newDashboardList.first().homeTeamName).isEqualTo("Home4")
        assertThat(newDashboardList[1].homeTeamName).isEqualTo("Home2")
        assertThat(newDashboardList[2].homeTeamName).isEqualTo("Home5")
        assertThat(newDashboardList[3].homeTeamName).isEqualTo("Home3")
        assertThat(newDashboardList.last().homeTeamName).isEqualTo("Home0")
    }
}